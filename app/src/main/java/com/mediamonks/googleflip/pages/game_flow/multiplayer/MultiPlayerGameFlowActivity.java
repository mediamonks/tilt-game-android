package com.mediamonks.googleflip.pages.game_flow.multiplayer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.Fragments;
import com.mediamonks.googleflip.data.constants.IntentKeys;
import com.mediamonks.googleflip.data.constants.MultiplayerMode;
import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.net.bluetooth.BluetoothClientService;
import com.mediamonks.googleflip.net.common.DeviceChangeListener;
import com.mediamonks.googleflip.net.common.DeviceChangeListenerAdapter;
import com.mediamonks.googleflip.pages.game.management.GameClient;
import com.mediamonks.googleflip.pages.game.management.GameClientListener;
import com.mediamonks.googleflip.pages.game.management.GameClientListenerAdapter;
import com.mediamonks.googleflip.pages.game_flow.multiplayer.fragments.gameover.GameOverFragment;
import com.mediamonks.googleflip.pages.game_flow.multiplayer.fragments.lobby.ServerLobbyFragment;
import com.mediamonks.googleflip.pages.game_flow.multiplayer.fragments.scoreboard.ScoreboardFragment;
import com.mediamonks.googleflip.pages.game_flow.multiplayer.fragments.lobby.ClientLobbyFragment;
import com.mediamonks.googleflip.pages.home.HomeActivity;
import com.mediamonks.googleflip.ui.BaseFragment;
import com.mediamonks.googleflip.ui.RegisteredFragmentActivity;
import com.mediamonks.googleflip.util.Navigator;
import com.pixplicity.easyprefs.library.Prefs;

import org.andengine.util.ActivityUtils;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Activity for multiplayer game flow
 */
public class MultiPlayerGameFlowActivity extends RegisteredFragmentActivity implements Navigator {
    private static final String TAG = MultiPlayerGameFlowActivity.class.getSimpleName();

    private BluetoothClientService _clientService;
    private DeviceChangeListener _clientDeviceChangedListener;
    private MultiplayerMode _multiplayerMode;
    private GameClient _gameClient;
    private GameClientListener _gameClientListener;
    private String _currentFragmentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_frame_container);
        ButterKnife.inject(this);
        ActivityUtils.keepScreenOn(this);

        _gameClient = GoogleFlipGameApplication.getGameClient();

        _multiplayerMode = MultiplayerMode.values()[Prefs.getInt(PrefKeys.MULTIPLAYER_MODE, 0)];
        if (_multiplayerMode.equals(MultiplayerMode.CLIENT)) {
            _clientService = GoogleFlipGameApplication.getBluetoothClientService();

            _clientDeviceChangedListener = new DeviceChangeListenerAdapter() {
                @Override
                public void onDeviceRemoved(String deviceName, String address) {
                    handleConnectionLost();
                }
            };
        }

        _gameClientListener = new GameClientListenerAdapter() {
            @Override
            public void onClientsChanged(List<ClientVO> clients) {
                checkConnectionLost();
            }
        };

        String fragment;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fragment = extras.getString(IntentKeys.FRAGMENT);
        } else {
            fragment = Fragments.GAME_FLOW_SCOREBOARD;
        }

        navigateTo(fragment);
    }

    private void handleConnectionLost() {
        Log.d(TAG, "handleConnectionLost: ");
        Toast.makeText(MultiPlayerGameFlowActivity.this, R.string.connection_lost, Toast.LENGTH_SHORT).show();

        GoogleFlipGameApplication.stopGame();

        startActivity(new Intent(MultiPlayerGameFlowActivity.this, HomeActivity.class));
    }

    public boolean navigateTo(String name) {
        BaseFragment newFragment = null;

        switch (name) {
            case Fragments.GAME_FLOW_LOBBY:
                newFragment = createLobby();
                break;
            case Fragments.GAME_FLOW_SCOREBOARD:
                newFragment = ScoreboardFragment.newInstance();
                break;
            case Fragments.GAME_FLOW_GAME_OVER:
                newFragment = GameOverFragment.newInstance();
                break;
        }

        if (newFragment != null) {
            _currentFragmentName = name;
            newFragment.setNavigator(this);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFragment).commit();
        }

        return newFragment != null;
    }

    private BaseFragment createLobby() {
        switch (MultiplayerMode.values()[Prefs.getInt(PrefKeys.MULTIPLAYER_MODE, 0)]) {
            case CLIENT:
                return ClientLobbyFragment.newInstance();
            case SERVER:
                return ServerLobbyFragment.newInstance();
        }
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (_multiplayerMode.equals(MultiplayerMode.CLIENT)) {
            _clientService.getHandler().addDeviceChangeListener(_clientDeviceChangedListener);
        }

        _gameClient.addGameClientListener(_gameClientListener);

        checkConnectionLost();
    }

    private void checkConnectionLost() {
        // don't check in the lobby, it has its own logic
        if (_currentFragmentName.equals(Fragments.GAME_FLOW_LOBBY)) {
            return;
        }

        // we've lost connection if the game client isn't connected, or there are 1 or less clients connected
        List<ClientVO> clients = _gameClient.getConnectedClients();
        int clientCount = clients == null ? 0 : clients.size();
        if (!_gameClient.isConnected() || (clientCount <= 1)) {
            handleConnectionLost();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (_multiplayerMode.equals(MultiplayerMode.CLIENT)) {
            _clientService.getHandler().removeDeviceChangeListener(_clientDeviceChangedListener);
        }

        _gameClient.removeGameClientListener(_gameClientListener);
    }

    @Override
    public void onBackPressed() {
        GoogleFlipGameApplication.stopGame();

        Intent intent = new Intent(MultiPlayerGameFlowActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
        //super.onBackPressed();
    }
}
