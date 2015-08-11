package com.mediamonks.googleflip.pages.game_flow.multiplayer.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.data.vo.PlayerScoreVO;
import com.mediamonks.googleflip.pages.game.management.GameClient;
import com.mediamonks.googleflip.pages.game.management.GameClientListener;
import com.mediamonks.googleflip.pages.game.management.GameClientListenerAdapter;
import com.mediamonks.googleflip.ui.BaseFragment;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Base class for page showing scoreboard
 */
public abstract class AbstractScoreboardPage extends BaseFragment {
    private static final String TAG = AbstractScoreboardPage.class.getSimpleName();

    private GameClient _gameClient;
    private GameClientListener _gameClientListener;

    protected View createView (int resId, LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(resId, container, false);
        ButterKnife.inject(this, view);

        _gameClient = GoogleFlipGameApplication.getGameClient();
        _gameClientListener = new GameClientListenerAdapter() {
            @Override
            public void onPlayerScoresChanged(List<PlayerScoreVO> playerScores) {
                updatePlayerScores(playerScores);
            }
        };

        List<ClientVO> clients = _gameClient.getConnectedClients();
        if (clients != null) {
            createClientRows((ViewGroup)view, clients);

            updatePlayerScores(_gameClient.getPlayerScores());
        }

        return view;
    }

    protected abstract void createClientRows(ViewGroup container, List<ClientVO> clients);

    protected abstract void updatePlayerScores(List<PlayerScoreVO> playerScores);

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
}
