package temple.multiplayer.net.bluetooth.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import temple.core.common.interfaces.IDebuggable;
import temple.core.net.BroadcastReceiver;
import temple.multiplayer.net.bluetooth.service.BluetoothClientService;
import temple.multiplayer.net.common.connection.Connection;
import temple.multiplayer.net.common.device.DeviceChangeListener;
import temple.multiplayer.net.common.device.DeviceChangeListenerAdapter;
import temple.multiplayer.net.common.service.ServiceMessageHandler;

/**
 * Created by stephan on 14-8-2015.
 */
public class BluetoothClientConnectionHelper implements IDebuggable {
    private static final String TAG = BluetoothClientConnectionHelper.class.getSimpleName();

    private BluetoothAdapter _bluetoothAdapter;
    private BroadcastReceiver _receiver;
    private BluetoothClientService _clientService;
    private DeviceChangeListener _deviceChangeListener;
    private boolean _isConnecting;
    private List<BluetoothDevice> _pairedDevices;
    private int _currentPairedDeviceIndex;
    private BluetoothDevice _currentCheckingDevice;
    private boolean _isCheckingPairedDevices;
    private boolean _isDiscoveringDevices;
    private ConnectionHelperListener _connectionHelperListener;
    private String _deviceNamePostFix;
    private boolean _debug;

    /**
     * Constructor
     *
     * @param context
     * @param clientService BluetoothClientService instance for dealing with Bluetooth sockets
     */
    public BluetoothClientConnectionHelper(Context context, BluetoothClientService clientService, String deviceNamePostFix) {
        _clientService = clientService;
        _deviceNamePostFix = deviceNamePostFix;

        _receiver = new BroadcastReceiver(context);
        _receiver.addActionHandler(BluetoothDevice.ACTION_FOUND, // action triggered when the system has found a new Blutooth device
                new BroadcastReceiver.ActionHandler() {
                    @Override
                    public void onAction(String action, Intent intent) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (_debug) Log.d(TAG, "onAction: " + action + " : " + device.getName());

                        if (device.getName() == null) {
                            return;
                        }

                        // if device name ends with specified postfix, select immediately
                        if (device.getName().lastIndexOf(_deviceNamePostFix) > 0) {
                            selectDevice(device);
                        }
                    }
                });
        _receiver.addActionHandler(BluetoothAdapter.ACTION_DISCOVERY_FINISHED,  // action triggered when the system stops discovering Bluetooth devices
                new BroadcastReceiver.ActionHandler() {
                    @Override
                    public void onAction(String action, Intent intent) {
                        if (_debug) Log.d(TAG, "onAction: " + action + ", _isConnecting = " + _isConnecting);
                        _isDiscoveringDevices = false;

                        if (!_isConnecting && _connectionHelperListener != null) {
                            _connectionHelperListener.onDiscoveryFinished();
                        }
                    }
                });
        _receiver.addActionHandler(BluetoothDevice.ACTION_UUID,   // action triggered when the system has received the UUIDS of a requested Bluetooth device
                new BroadcastReceiver.ActionHandler() {
                    @Override
                    public void onAction(String action, Intent intent) {
                        if (_debug) Log.d(TAG, "onAction: " + action);

                        if (!_isCheckingPairedDevices || _isConnecting) {
                            // Connecting or not checking paired devices, ignoring action
                            if (_debug) Log.d(TAG, "onAction: ignored, " + (_isConnecting ? "connecting" : "not checking devices"));
                            return;
                        }

                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (!device.getAddress().equals(_currentCheckingDevice.getAddress())) {
                            // Not current device, ignoring action
                            if (_debug) Log.d(TAG, "onAction: ignored, not current device");
                            return;
                        }

                        Parcelable[] uuids = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);

                        if (uuids == null) {
                            // No UUIDs for device, checking next
                            if (_debug) Log.d(TAG, "onAction: no uuids found");
                            checkNextPairedDevice();
                            return;
                        }

                        for (Parcelable uuid : uuids) {
                            if (_debug) Log.d(TAG, "onAction: testing uuid " + ((ParcelUuid) uuid).getUuid());

                            if (_clientService.acceptServerUUID(((ParcelUuid) uuid).getUuid())) {
                                // UUID accepted, connecting to device
                                selectDevice(device);

                                return;
                            }
                        }

                        // no UUIDs accepted, checking next
                        checkNextPairedDevice();
                    }
                });

        _deviceChangeListener = new DeviceChangeListenerAdapter() {
            @Override
            public void onDeviceConnected(String deviceName, String address) {
                if (_debug) Log.d(TAG, "onDeviceConnected: Connected to other device!");

                if (_connectionHelperListener != null) {
                    _connectionHelperListener.onConnected(new BluetoothConnection(_clientService, address));
                }
            }

            @Override
            public void onConnectFailed(String deviceName, String deviceAddress) {
                _isConnecting = false;

                if (_isCheckingPairedDevices) {
                    // onConnectFailed: checking next
                    checkNextPairedDevice();
                } else {
                    // start discovery
                    startDiscovery();
                }
            }
        };

        // retrieve client service, initialize handlers,
        _clientService.getHandler().addDeviceChangeListener(_deviceChangeListener);
        try {
            _clientService.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        _pairedDevices = new ArrayList<>();
        for (BluetoothDevice device : _bluetoothAdapter.getBondedDevices()) {
            _pairedDevices.add(device);
        }
        Collections.sort(_pairedDevices, new CompairedDevices());
    }

    /**
     * Start searching for devices
     */
    public void start() {
        if (_pairedDevices.size() > 0) {
            _currentPairedDeviceIndex = -1;
            checkNextPairedDevice();
        } else {
            startDiscovery();
        }
    }

    private void startDiscovery() {
        if (_debug) Log.d(TAG, "startDiscovery: ");

        if (_isDiscoveringDevices) {
            return;
        }

        _isCheckingPairedDevices = false;
        _isDiscoveringDevices = true;

        _bluetoothAdapter.startDiscovery();

        if (_connectionHelperListener != null) {
            _connectionHelperListener.onDiscoveryStarted();
        }
    }

    private void checkNextPairedDevice() {
        if (_debug) Log.d(TAG, "checkNextPairedDevice: ");

        _currentPairedDeviceIndex++;

        if (_currentPairedDeviceIndex >= _pairedDevices.size()) {
            // checkNextPairedDevice: Done checking paired devices, now starting discovery
            if (_debug) Log.d(TAG, "checkNextPairedDevice: no more paired devices, starting discovery");
            startDiscovery();

            return;
        }

        _isCheckingPairedDevices = true;

        // fetch UUIDs of paired device
        _currentCheckingDevice = _pairedDevices.get(_currentPairedDeviceIndex);
        if (_debug) Log.d(TAG, "checkNextPairedDevice: checking " + _currentCheckingDevice.getName());

        selectDevice(_currentCheckingDevice);
    }

    public void onResume() {
        _receiver.onResume();

        if (_isDiscoveringDevices) {
            _bluetoothAdapter.startDiscovery();
        }
    }

    public void onPause() {
        _bluetoothAdapter.cancelDiscovery();

        _receiver.onPause();
    }

    public void onStop() {
        if (_clientService != null) {
            _clientService.getHandler().removeDeviceChangeListener(_deviceChangeListener);
            _clientService.getHandler().setStatusChangedListener(null);
        }

        if (_bluetoothAdapter != null) {
            _bluetoothAdapter.cancelDiscovery();
        }
    }

    /**
     * Try to connect to specified device
     */
    private void selectDevice(BluetoothDevice device) {
        if (_debug) Log.d(TAG, "selectDevice: " + device.getName());

        _isConnecting = true;

        _bluetoothAdapter.cancelDiscovery();
        _isDiscoveringDevices = false;

        _clientService.connect(device, true);
    }

    public void setDeviceNamePostFix(String deviceNamePostFix) {
        _deviceNamePostFix = deviceNamePostFix;
    }

    @Override
    public void setDebug(boolean value) {
        _debug = value;
    }

    @Override
    public boolean getDebug() {
        return _debug;
    }

    /**
     * Interface for process events
     */
    public interface ConnectionHelperListener extends ServiceMessageHandler.StatusChangedListener {
        /**
         * method called when device starts to discover other devices
         */
        void onDiscoveryStarted();

        /**
         * method called when discovering other devices has stopped
         */
        void onDiscoveryFinished();

        /**
         * Method called when a connection has been made to another device
         * @param connection the connection instance
         */
        void onConnected(Connection connection);
    }

    public void setConnectionHelperListener(ConnectionHelperListener listener) {
        _connectionHelperListener = listener;

        _clientService.getHandler().setStatusChangedListener(listener);
    }

    private class CompairedDevices implements Comparator<BluetoothDevice> {

        @Override
        public int compare(BluetoothDevice lhs, BluetoothDevice rhs) {
            String lname = lhs.getName();
            String rname = rhs.getName();
            boolean lcontains = lname.contains(_deviceNamePostFix);
            boolean rcontains = rname.contains(_deviceNamePostFix);

            int retval;
            if (lcontains) {
                retval = rcontains ? 0 : -1;
            } else {
                retval = rcontains ? 1 : 0;
            }

            return retval;
        }
    }
}
