package com.mediamonks.googleflip.pages.connect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.Fragments;
import com.mediamonks.googleflip.data.constants.IntentKeys;
import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.mediamonks.googleflip.pages.game.management.Player;
import com.mediamonks.googleflip.pages.game.management.PlayerImpl;
import com.mediamonks.googleflip.pages.game_flow.multiplayer.MultiPlayerGameFlowActivity;
import com.mediamonks.googleflip.ui.BaseFragment;
import com.mediamonks.googleflip.ui.ProgressDialog;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.Bind;
import butterknife.OnClick;
import temple.multiplayer.net.bluetooth.connection.BluetoothClientConnectionHelper;
import temple.multiplayer.net.bluetooth.service.AbstractBluetoothService;
import temple.multiplayer.net.common.connection.Connection;

/**
 * Join a game as client
 */
public class ConnectJoinGameFragment extends BaseFragment implements BluetoothClientConnectionHelper.ConnectionHelperListener {
    private static final String TAG = ConnectJoinGameFragment.class.getSimpleName();

    @Bind(R.id.tv_looking_for_host)
    protected TextView _lookingForHostText;
    @Bind(R.id.btn_retry)
    protected Button _retryButton;

    private BluetoothClientConnectionHelper _connectionHelper;

    public static ConnectJoinGameFragment newInstance() {
        return new ConnectJoinGameFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(R.layout.fragment_connect_join_game, inflater, container);

        _connectionHelper = new BluetoothClientConnectionHelper(getActivity(), GoogleFlipGameApplication.getBluetoothClientService(),
                GoogleFlipGameApplication.DEVICE_POSTFIX);
        _connectionHelper.setDebug(true);
        _connectionHelper.setConnectionHelperListener(this);
        _connectionHelper.start();

        _retryButton.setVisibility(View.GONE);

        return view;
    }

    @OnClick(R.id.btn_retry)
    protected void onRetryButtonClick() {
        _connectionHelper.start();
    }

    @Override
    public void onDiscoveryStarted() {
        _retryButton.setVisibility(View.GONE);

        _lookingForHostText.setText(R.string.looking_for_host);
    }

    @Override
    public void onDiscoveryFinished() {
        _lookingForHostText.setText(R.string.no_host_found);

        _retryButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnected(Connection connection) {
        // create new Player instance for current user, set as player for GameClient
        Player player = new PlayerImpl(connection);
        player.setPlayerName(Prefs.getString(PrefKeys.PLAYER_NAME, ""));
        GoogleFlipGameApplication.getGameClient().setPlayer(player);

        // navigate to lobby to wait for server to start game
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), MultiPlayerGameFlowActivity.class);
            intent.putExtra(IntentKeys.FRAGMENT, Fragments.GAME_FLOW_LOBBY);
            startActivity(intent);

            getActivity().finish();
        }
    }

    @Override
    public void onStatusChanged(int status) {
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

    @Override
    public void onResume() {
        super.onResume();

        _connectionHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        _connectionHelper.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        _connectionHelper.onStop();
    }
}
