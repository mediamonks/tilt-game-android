package com.mediamonks.googleflip.pages.game_flow.multiplayer.fragments.lobby;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.ActivityRequestCode;
import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.pages.game.management.GameServer;
import com.mediamonks.googleflip.pages.game.management.Player;
import com.mediamonks.googleflip.pages.game.management.PlayerImpl;
import com.mediamonks.googleflip.util.SoundManager;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.List;

import butterknife.OnClick;
import temple.core.net.BroadcastReceiver;
import temple.multiplayer.net.bluetooth.connection.BluetoothConnection;
import temple.multiplayer.net.bluetooth.service.BluetoothServerService;
import temple.multiplayer.net.common.connection.LoopBackConnection;
import temple.multiplayer.net.common.device.DeviceChangeListener;
import temple.multiplayer.net.common.device.DeviceChangeListenerAdapter;

/**
 * Server lobby, wait for players to connect
 */
public class ServerLobbyFragment extends AbstractLobbyFragment {
    private static final String TAG = ServerLobbyFragment.class.getSimpleName();

    private BluetoothServerService _serverService;
    private GameServer _gameServer;
    private DeviceChangeListener _serverDeviceChangedListener;
    private BroadcastReceiver _broadcastReceiver;

    public static ServerLobbyFragment newInstance() {
        return new ServerLobbyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _broadcastReceiver = new BroadcastReceiver(getActivity());
        _broadcastReceiver.addActionHandler(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED,
                new BroadcastReceiver.ActionHandler() {
                    @Override
                    public void onAction(String action, Intent intent) {
                        Log.d(TAG, "onAction: " + action);

                        GoogleFlipGameApplication.restoreBluetoothDeviceName();

                        View view = getView();
                        TextView textView = new TextView(getActivity());
                        textView.setTextAppearance(getActivity(), R.style.dialog_label);
                        textView.setText(R.string.server_broadcast_restart);
                        textView.setPadding(25, 25, 25, 25);

                        if (view != null) {
                            new AlertDialog.Builder(getActivity(), R.style.ConnectAlertDialog)
                                    .setCustomTitle(textView)
                                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.d(TAG, "onClick: starting discoverability");
                                            setDiscoverable();
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }

    @Override
    protected void setupUI() {
        _serverService = GoogleFlipGameApplication.getBluetoothServerService();

        _gameServer = GoogleFlipGameApplication.getGameServer();
        _gameServer.stop();
        _gameServer.initBackgroundColors();
        _gameServer.setDebug(true);

        _gameClient.stop();

        Player player = new PlayerImpl(new LoopBackConnection());
        player.setPlayerName(Prefs.getString(PrefKeys.PLAYER_NAME, ""));

        _gameClient.setPlayer(player);
        _gameServer.addPlayer(player);

        _buttons.setVisibility(View.VISIBLE);
        _nextButton.setVisibility(View.GONE);

        _lookingForHostText.setVisibility(View.GONE);

        _serverDeviceChangedListener = new DeviceChangeListenerAdapter() {
            @Override
            public void onDeviceAdded(String deviceName, String address) {
                if (_gameServer.hasRoomForMorePlayers()) {
                    _gameServer.addPlayer(new PlayerImpl(new BluetoothConnection(_serverService, address)));
                }
            }

            @Override
            public void onDeviceRemoved(String deviceName, String address) {
                _gameServer.removePlayer(address);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();

        _serverService.getHandler().addDeviceChangeListener(_serverDeviceChangedListener);
        _serverService.start();
    }

    @Override
    public void onResume() {
        super.onResume();

        _broadcastReceiver.onResume();

        Log.d(TAG, "onResume: resuming");

        setDiscoverable();
    }

    @Override
    public void onPause() {
        super.onPause();

        _broadcastReceiver.onPause();

        GoogleFlipGameApplication.restoreBluetoothDeviceName();
    }

    private void setDiscoverable() {
        Log.d(TAG, "setDiscoverable: ");

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return;
        }

        // change name if necessary
        String originalName = adapter.getName();
        if (originalName.lastIndexOf(GoogleFlipGameApplication.DEVICE_POSTFIX) != originalName.length() - GoogleFlipGameApplication.DEVICE_POSTFIX.length()) {
            GoogleFlipGameApplication.setOriginalBluetoothDeviceName(originalName);

            Log.d(TAG, "setDiscoverable: Bluetooth device name set to " + (originalName + GoogleFlipGameApplication.DEVICE_POSTFIX));
            adapter.setName(originalName + GoogleFlipGameApplication.DEVICE_POSTFIX);
        }

        // set discoverable if necessary
        if (adapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Log.d(TAG, "setDiscoverable: scan mode is not discoverable, starting activity with code " + ActivityRequestCode.REQUEST_ENABLE_SCAN);
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
            getActivity().startActivityForResult(discoverableIntent, ActivityRequestCode.REQUEST_ENABLE_SCAN);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        _serverService.getHandler().removeDeviceChangeListener(_serverDeviceChangedListener);
        _serverService.stopListening();
    }

    @OnClick(R.id.btn_next)
    protected void onNextButtonClick() {
        SoundManager.getInstance().play(R.raw.tap);

        _gameServer.startGame();
    }

    @Override
    protected void updateClients(List<ClientVO> clients) {
        super.updateClients(clients);

        int clientCount = 0;
        if (clients != null) {
            clientCount = clients.size();
        }

        _waitingForPlayersText.setVisibility(clientCount > 1 ? View.GONE : View.VISIBLE);
    }
}
