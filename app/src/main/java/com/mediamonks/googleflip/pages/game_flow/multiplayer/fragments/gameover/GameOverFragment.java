package com.mediamonks.googleflip.pages.game_flow.multiplayer.fragments.gameover;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.constants.MultiplayerMode;
import com.mediamonks.googleflip.data.constants.PrefKeys;
import com.mediamonks.googleflip.pages.game.FlipGameActivity;
import com.mediamonks.googleflip.pages.game.management.GameClient;
import com.mediamonks.googleflip.pages.game.management.GameClientListener;
import com.mediamonks.googleflip.pages.game.management.GameClientListenerAdapter;
import com.mediamonks.googleflip.pages.home.HomeActivity;
import com.mediamonks.googleflip.ui.BaseFragment;
import com.mediamonks.googleflip.ui.paging.PageIndicator;
import com.mediamonks.googleflip.util.SoundManager;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Fragment shown when multiplayer game is over
 */
public class GameOverFragment extends BaseFragment {
    private static final String TAG = GameOverFragment.class.getSimpleName();

    @Bind(R.id.buttons)
    protected LinearLayout _buttons;
    @Bind(R.id.tv_waiting_for_players)
    protected TextView _waitingForPlayersText;
    @Bind(R.id.btn_restart)
    protected Button _restartButton;
    @Bind(R.id.exit_button)
    protected Button _exitButton;
    @Bind(R.id.viewpager)
    protected ViewPager _viewPager;
    @Bind(R.id.page_indicator)
    protected PageIndicator _pageIndicator;

    private ViewPager.OnPageChangeListener _pageChangeListener;
    private GameClient _gameClient;
    private GameClientListener _gameClientListener;

    public static GameOverFragment newInstance() {
        return new GameOverFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(R.layout.fragment_game_over, inflater, container);

        _exitButton.setVisibility(View.GONE);

        _gameClient = GoogleFlipGameApplication.getGameClient();

        _gameClientListener = new GameClientListenerAdapter() {
            @Override
            public void onRoundStarted(Long levelId) {
                GoogleFlipGameApplication.getUserModel().selectLevelById(levelId);

                startActivity(new Intent(getActivity(), FlipGameActivity.class));

                getActivity().finish();
            }
        };

        _buttons.setVisibility(View.VISIBLE);
        _exitButton.setVisibility(View.VISIBLE);

        if (MultiplayerMode.values()[Prefs.getInt(PrefKeys.MULTIPLAYER_MODE, 0)].equals(MultiplayerMode.SERVER)) {
            _restartButton.setText(getString(R.string.restart));
            _restartButton.setEnabled(true);
            _waitingForPlayersText.setVisibility(View.GONE);
        } else {
            _restartButton.setVisibility(View.GONE);
            _waitingForPlayersText.setText(R.string.waiting_for_game_restart);
            _waitingForPlayersText.setVisibility(View.VISIBLE);
        }

        _viewPager.setAdapter(new GameOverPagerAdapter(getChildFragmentManager()));

        _pageIndicator.setActivePage(0);
        _pageIndicator.setNumPages(_viewPager.getAdapter().getCount());

        _pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                _pageIndicator.setActivePage(position);
            }
        };

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

        _viewPager.addOnPageChangeListener(_pageChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        _viewPager.removeOnPageChangeListener(_pageChangeListener);
    }

    @OnClick(R.id.btn_restart)
    protected void onRestartButtonClick() {
        SoundManager.getInstance().play(R.raw.tap);

        GoogleFlipGameApplication.getGameServer().startGame();
    }

    @OnClick(R.id.exit_button)
    protected void onExitButtonClick() {
        SoundManager.getInstance().play(R.raw.tap);

        GoogleFlipGameApplication.stopGame();

        startActivity(new Intent(getActivity(), HomeActivity.class));
    }
}
