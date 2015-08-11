package com.mediamonks.googleflip.pages.game_flow.singleplayer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.Fragments;
import com.mediamonks.googleflip.data.constants.IntentKeys;
import com.mediamonks.googleflip.pages.game_flow.singleplayer.fragments.ResultLevelFragment;
import com.mediamonks.googleflip.pages.game_flow.singleplayer.fragments.SelectLevelFragment;
import com.mediamonks.googleflip.pages.home.HomeActivity;
import com.mediamonks.googleflip.ui.BaseFragment;
import com.mediamonks.googleflip.ui.RegisteredFragmentActivity;
import com.mediamonks.googleflip.util.Navigator;
import com.mediamonks.googleflip.util.ScreenUtil;

import org.andengine.util.ActivityUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Activity for single player game flow
 */
public class SinglePlayerGameFlowActivity extends RegisteredFragmentActivity implements Navigator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_frame_container);
        ButterKnife.inject(this);
        ActivityUtils.keepScreenOn(this);

        String fragment;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fragment = extras.getString(IntentKeys.FRAGMENT);
        } else {
            fragment = Fragments.GAME_FLOW_SELECT_LEVEL;
        }

        navigateTo(fragment);
    }

    public boolean navigateTo(String name) {
        BaseFragment newFragment = null;

        switch (name) {
            case Fragments.GAME_FLOW_SELECT_LEVEL:
                newFragment = SelectLevelFragment.newInstance();
                break;
            case Fragments.GAME_FLOW_RESULT_LEVEL:
                ScreenUtil.setFullScreen(getWindow().getDecorView());
                newFragment = ResultLevelFragment.newInstance();
                break;
        }

        if (newFragment != null) {
            newFragment.setNavigator(this);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFragment).commit();
        }

        return newFragment != null;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SinglePlayerGameFlowActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
        super.onBackPressed();
    }
}
