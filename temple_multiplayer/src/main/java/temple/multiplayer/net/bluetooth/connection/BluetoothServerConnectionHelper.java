package temple.multiplayer.net.bluetooth.connection;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;

import temple.core.net.BroadcastReceiver;
import temple.multiplayer.net.bluetooth.service.BluetoothServerService;
import temple.multiplayer.net.common.connection.Connection;
import temple.multiplayer.net.common.device.DeviceChangeListener;
import temple.multiplayer.net.common.device.DeviceChangeListenerAdapter;

/**
 * Created by stephan on 14-8-2015.
 */
public class BluetoothServerConnectionHelper {
    private static final String TAG = BluetoothServerConnectionHelper.class.getSimpleName();

    private BluetoothServerService _serverService;
    private DeviceChangeListener _serverDeviceChangedListener;
    private BroadcastReceiver _broadcastReceiver;
    private ConnectionHelperListener _connectionHelperListener;
    private String _deviceNamePostFix;
    private Activity _activity;

    public BluetoothServerConnectionHelper(Activity activity, BluetoothServerService serverService, final boolean isSingleClient) {
        _serverService = serverService;
        _activity = activity;

        _broadcastReceiver = new BroadcastReceiver(activity);
        _broadcastReceiver.addActionHandler(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED,
                new BroadcastReceiver.ActionHandler() {
                    @Override
                    public void onAction(String action, Intent intent) {
                        if (_connectionHelperListener != null) {
                            _connectionHelperListener.onScanningEnded();
                        }
                    }
                });

        _serverDeviceChangedListener = new DeviceChangeListenerAdapter() {
            @Override
            public void onDeviceAdded(String deviceName, String address) {
                Log.d(TAG, "onDeviceAdded: we have a connection!");

                if (_connectionHelperListener != null) {
                    _connectionHelperListener.onConnected(new BluetoothConnection(_serverService, address));
                }

                if (isSingleClient) {
                    stopListening();
                }
            }

            @Override
            public void onDeviceRemoved(String deviceName, String address) {
                Log.d(TAG, "onDeviceRemoved: we've lost the device");
            }
        };
    }

    private void stopListening() {
        _serverService.getHandler().removeDeviceChangeListener(_serverDeviceChangedListener);
        _serverService.stopListening();
    }

    public interface ConnectionHelperListener {
        void onScanningEnded();

        void onConnected(Connection connection);

        void onAdapterNameChange(String originalName);
    }

    public void setConnectionHelperListener(ConnectionHelperListener listener) {
        _connectionHelperListener = listener;
    }

    public void onStart() {
        _serverService.getHandler().addDeviceChangeListener(_serverDeviceChangedListener);
        _serverService.start();
    }

    public void onResume() {
        _broadcastReceiver.onResume();

        setDiscoverable();

    }

    public void onPause() {
        _broadcastReceiver.onPause();
    }

    public void onStop() {
        stopListening();
    }

    public void setDiscoverable() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return;
        }

        // change name if necessary
        String originalName = adapter.getName();
        if (originalName.lastIndexOf(_deviceNamePostFix) != originalName.length() - _deviceNamePostFix.length()) {
            if (_connectionHelperListener != null) {
                _connectionHelperListener.onAdapterNameChange(originalName);
            }

            Log.d(TAG, "setDiscoverable: Bluetooth device name set to " + (originalName + _deviceNamePostFix));
            adapter.setName(originalName + _deviceNamePostFix);
        }

        // set discoverable if necessary
        if (adapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            _activity.startActivity(discoverableIntent);
        }
    }

    public void setDeviceNamePostFix(String deviceNamePostFix) {
        _deviceNamePostFix = deviceNamePostFix;
    }
}
