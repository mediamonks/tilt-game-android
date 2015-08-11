package com.mediamonks.googleflip.pages.connect.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.Fragments;
import com.mediamonks.googleflip.data.constants.IntentKeys;
import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.mediamonks.googleflip.net.bluetooth.AbstractBluetoothService;
import com.mediamonks.googleflip.net.bluetooth.BluetoothClientService;
import com.mediamonks.googleflip.net.bluetooth.BluetoothConnection;
import com.mediamonks.googleflip.net.common.DeviceChangeListener;
import com.mediamonks.googleflip.net.common.DeviceChangeListenerAdapter;
import com.mediamonks.googleflip.net.common.ServiceMessageHandler;
import com.mediamonks.googleflip.pages.game.management.Player;
import com.mediamonks.googleflip.pages.game.management.PlayerImpl;
import com.mediamonks.googleflip.pages.game_flow.multiplayer.MultiPlayerGameFlowActivity;
import com.mediamonks.googleflip.ui.BaseFragment;
import com.mediamonks.googleflip.ui.ProgressDialog;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import temple.core.net.BroadcastReceiver;

/**
 * Join a game as client
 */
public class ConnectJoinGameFragment extends BaseFragment {
    private static final String TAG = ConnectJoinGameFragment.class.getSimpleName();

    @InjectView(R.id.tv_looking_for_host)
    protected TextView _lookingForHostText;
    @InjectView(R.id.btn_retry)
    protected Button _retryButton;

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

    public static ConnectJoinGameFragment newInstance() {
        return new ConnectJoinGameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _receiver = new BroadcastReceiver(getActivity());
        _receiver.addActionHandler(BluetoothDevice.ACTION_FOUND, // action triggered when the system has found a new Blutooth device
                new BroadcastReceiver.ActionHandler() {
                    @Override
                    public void onAction(String action, Intent intent) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                        if (device.getName() == null) {
                            return;
                        }

                        // if device name ends with specified postfix, select immediately
                        if (device.getName().lastIndexOf(GoogleFlipGameApplication.DEVICE_POSTFIX) > 0) {
                            selectDevice(device);
                        }
                    }
                });
        _receiver.addActionHandler(BluetoothAdapter.ACTION_DISCOVERY_FINISHED,  // action triggered when the system stops discovering Bluetooth devices
                new BroadcastReceiver.ActionHandler() {
                    @Override
                    public void onAction(String action, Intent intent) {
                        _lookingForHostText.setText(R.string.no_host_found);

                        _retryButton.setVisibility(View.VISIBLE);

                        _isDiscoveringDevices = false;
                    }
                });
        _receiver.addActionHandler(BluetoothDevice.ACTION_UUID,   // action triggered when the system has received the UUIDS of a requested Bluetooth device
                new BroadcastReceiver.ActionHandler() {
                    @Override
                    public void onAction(String action, Intent intent) {
                        if (!_isCheckingPairedDevices || _isConnecting) {
                            // Connecting or not checking paired devices, ignoring action
                            return;
                        }

                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (!device.getAddress().equals(_currentCheckingDevice.getAddress())) {
                            // Not current device, ignoring action
                            return;
                        }

                        Parcelable[] uuids = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);

                        if (uuids == null) {
                            // No UUIDs for device, checking next
                            checkNextPairedDevice();
                            return;
                        }

                        for (Parcelable uuid : uuids) {
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
                // create new Player instance for current user, set as player for GameClient
                Player player = new PlayerImpl(new BluetoothConnection(_clientService, address));
                player.setPlayerName(Prefs.getString(PrefKeys.PLAYER_NAME, ""));
                GoogleFlipGameApplication.getGameClient().setPlayer(player);

                // navigate to lobby to wait for server to start game
                Intent intent = new Intent(getActivity(), MultiPlayerGameFlowActivity.class);
                intent.putExtra(IntentKeys.FRAGMENT, Fragments.GAME_FLOW_LOBBY);
                startActivity(intent);

                getActivity().finish();
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
        _clientService = GoogleFlipGameApplication.getBluetoothClientService();
        _clientService.getHandler().setStatusChangedListener(
                new ServiceMessageHandler.StatusChangedListener() {
                    @Override
                    public void onStatusChanged(int status) {
                        handleServiceStatusChanged(status);
                    }
                }
        );
        _clientService.getHandler().addDeviceChangeListener(_deviceChangeListener);
        _clientService.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(R.layout.fragment_connect_join_game, inflater, container);

        _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        _pairedDevices = new ArrayList<>();
        for (BluetoothDevice device : _bluetoothAdapter.getBondedDevices()) {
            _pairedDevices.add(device);
        }

        // check paired devices first
        if (_pairedDevices.size() > 0) {
            _currentPairedDeviceIndex = -1;
            checkNextPairedDevice();
        } else {
            startDiscovery();
        }

        _retryButton.setVisibility(View.GONE);

        return view;
    }

    private void startDiscovery() {
        if (_isDiscoveringDevices) {
            return;
        }

        _isCheckingPairedDevices = false;
        _isDiscoveringDevices = true;

        _retryButton.setVisibility(View.GONE);

        _lookingForHostText.setText(R.string.looking_for_host);

        _bluetoothAdapter.startDiscovery();
    }

    private void checkNextPairedDevice() {
        _currentPairedDeviceIndex++;

        if (_currentPairedDeviceIndex >= _pairedDevices.size()) {
            // checkNextPairedDevice: Done checking paired devices, now starting discovery
            startDiscovery();

            return;
        }

        _isCheckingPairedDevices = true;

        // fetch UUIDs of paired device
        _currentCheckingDevice = _pairedDevices.get(_currentPairedDeviceIndex);
        _currentCheckingDevice.fetchUuidsWithSdp();
    }

    @Override
    public void onResume() {
        super.onResume();

        _receiver.onResume();

        if (_isDiscoveringDevices) {
            _bluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        _bluetoothAdapter.cancelDiscovery();

        _receiver.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

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
        _isConnecting = true;

        _bluetoothAdapter.cancelDiscovery();
        _isDiscoveringDevices = false;

        _clientService.connect(device, true);
    }

    private void handleServiceStatusChanged(int status) {
        switch (status) {
            case AbstractBluetoothService.STATE_CONNECTING:
                ProgressDialog.showInstance(getChildFragmentManager());
                break;
            case AbstractBluetoothService.STATE_CONNECTED:
                ProgressDialog.dismissInstance();
                break;
            case AbstractBluetoothService.STATE_IDLE:
                ProgressDialog.dismissInstance();
                break;
        }
    }

    @OnClick(R.id.btn_retry)
    protected void onRetryButtonClick() {
        startDiscovery();
    }
}
