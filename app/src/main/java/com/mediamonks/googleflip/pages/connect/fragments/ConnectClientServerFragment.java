package com.mediamonks.googleflip.pages.connect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.Fragments;
import com.mediamonks.googleflip.data.constants.IntentKeys;
import com.mediamonks.googleflip.data.constants.MultiplayerMode;
import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.mediamonks.googleflip.pages.game_flow.multiplayer.MultiPlayerGameFlowActivity;
import com.mediamonks.googleflip.ui.BaseFragment;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.OnClick;

/**
 * Allow selection between joining a game as client, or starting a new game as server
 */
public class ConnectClientServerFragment extends BaseFragment {
    private static final String TAG = ConnectClientServerFragment.class.getSimpleName();

    public static ConnectClientServerFragment newInstance() {
        return new ConnectClientServerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return createView(R.layout.fragment_select_client_server, inflater, container);
    }

    @OnClick(R.id.btn_start_new_game)
    protected void onStartNewGameButtonClick() {
        Prefs.putInt(PrefKeys.MULTIPLAYER_MODE, MultiplayerMode.SERVER.ordinal());

        Intent intent = new Intent(getActivity(), MultiPlayerGameFlowActivity.class);
        intent.putExtra(IntentKeys.FRAGMENT, Fragments.GAME_FLOW_LOBBY);
        startActivity(intent);
    }

    @OnClick(R.id.btn_join_game)
    protected void onJoinGameButtonClick() {
        Prefs.putInt(PrefKeys.MULTIPLAYER_MODE, MultiplayerMode.CLIENT.ordinal());

        navigateTo(Fragments.CONNECT_JOIN_GAME);
    }
}
