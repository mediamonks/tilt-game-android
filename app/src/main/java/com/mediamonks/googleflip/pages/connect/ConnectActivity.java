package com.mediamonks.googleflip.pages.connect;

import android.os.Bundle;

import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.Fragments;
import com.mediamonks.googleflip.pages.connect.fragments.ConnectClientServerFragment;
import com.mediamonks.googleflip.pages.connect.fragments.ConnectJoinGameFragment;
import com.mediamonks.googleflip.pages.connect.fragments.ConnectPlayerNameFragment;
import com.mediamonks.googleflip.pages.connect.fragments.ConnectProtocolFragment;
import com.mediamonks.googleflip.ui.BaseFragment;
import com.mediamonks.googleflip.ui.RegisteredFragmentActivity;
import com.mediamonks.googleflip.util.Navigator;

import butterknife.ButterKnife;

/**
 * Activity for connecting in multiplayer mode
 */
public class ConnectActivity extends RegisteredFragmentActivity implements Navigator {
    private static final String TAG = ConnectActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_frame_container);
        ButterKnife.bind(this);

        navigateTo(Fragments.CONNECT_PLAYER_NAME);
    }

    public boolean navigateTo(String name) {
        BaseFragment newFragment = null;

        switch (name) {
            case Fragments.CONNECT_PLAYER_NAME:
                newFragment = ConnectPlayerNameFragment.newInstance();
                break;
            case Fragments.CONNECT_PROTOCOL:
                newFragment = ConnectProtocolFragment.newInstance();
                break;
            case Fragments.CONNECT_CLIENTSERVER:
                newFragment = ConnectClientServerFragment.newInstance();
                break;
            case Fragments.CONNECT_JOIN_GAME:
                newFragment = ConnectJoinGameFragment.newInstance();
                break;
        }

        if (newFragment != null) {
            newFragment.setNavigator(this);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFragment).commit();
        }

        return newFragment != null;
    }
}
