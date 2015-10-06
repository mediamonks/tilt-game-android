package com.mediamonks.googleflip.pages.game_flow.multiplayer.fragments.scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.Fragments;
import com.mediamonks.googleflip.data.constants.MultiplayerMode;
import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.mediamonks.googleflip.pages.game.FlipGameActivity;
import com.mediamonks.googleflip.pages.game.management.GameClient;
import com.mediamonks.googleflip.pages.game.management.GameClientListener;
import com.mediamonks.googleflip.pages.game.management.GameClientListenerAdapter;
import com.mediamonks.googleflip.ui.BaseFragment;
import com.mediamonks.googleflip.ui.paging.PageIndicator;
import com.mediamonks.googleflip.util.SoundManager;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Fragment shown during multiplayer games between rounds
 */
public class ScoreboardFragment extends BaseFragment {
    private static final String TAG = ScoreboardFragment.class.getSimpleName();

    @Bind(R.id.buttons)
    protected LinearLayout _buttons;
    @Bind(R.id.tv_waiting_for_players)
    protected TextView _waitingForPlayersText;
    @Bind(R.id.viewpager)
    protected ViewPager _viewPager;
    @Bind(R.id.page_indicator)
    protected PageIndicator _pageIndicator;

    private ViewPager.OnPageChangeListener _pageChangeListener;
    private GameClient _gameClient;
    private GameClientListener _gameClientListener;

    public static ScoreboardFragment newInstance() {
        return new ScoreboardFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(R.layout.fragment_scoreboard, inflater, container);

        _gameClient = GoogleFlipGameApplication.getGameClient();

        _gameClientListener = new GameClientListenerAdapter() {
            @Override
            public void onRoundStarted(Long levelId) {
                GoogleFlipGameApplication.getUserModel().selectLevelById(levelId);

                startActivity(new Intent(getActivity(), FlipGameActivity.class));

                getActivity().finish();
            }

            @Override
            public void onRoundFinished() {
                checkRoundFinished();
            }

            @Override
            public void onGameFinished() {
                _buttons.setVisibility(View.VISIBLE);
                _waitingForPlayersText.setVisibility(View.GONE);
            }
        };

        _viewPager.setAdapter(new ScoreboardPagerAdapter(getChildFragmentManager()));

        _pageIndicator.setActivePage(0);
        _pageIndicator.setNumPages(_viewPager.getAdapter().getCount());

        _pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                _pageIndicator.setActivePage(position);
            }
        };

        _buttons.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        _gameClient.addGameClientListener(_gameClientListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        _gameClient.removeGameClientListener(_gameClientListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        checkRoundFinished();

        // doublecheck if game isn't over in the meantime
        if (_gameClient.isGameFinished()) {
            _buttons.setVisibility(View.VISIBLE);
            _waitingForPlayersText.setVisibility(View.GONE);
        }

        _viewPager.addOnPageChangeListener(_pageChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        _viewPager.removeOnPageChangeListener(_pageChangeListener);
    }

    private void checkRoundFinished() {
        if (_gameClient.isRoundFinished()) {
            MultiplayerMode multiplayerMode = MultiplayerMode.values()[Prefs.getInt(PrefKeys.MULTIPLAYER_MODE, 0)];

            if (multiplayerMode.equals(MultiplayerMode.SERVER)) {
                _buttons.setVisibility(View.VISIBLE);
                _waitingForPlayersText.setVisibility(View.GONE);
            } else {
                _waitingForPlayersText.setText(R.string.waiting_to_start_round);
            }
        }
    }

    @OnClick(R.id.btn_next)
    protected void onNextButtonClick() {
        SoundManager.getInstance().play(R.raw.tap);

        if (_gameClient.isGameFinished()) {
            navigateTo(Fragments.GAME_FLOW_GAME_OVER);
        } else {
            GoogleFlipGameApplication.getGameServer().startRound();
        }
    }
}
