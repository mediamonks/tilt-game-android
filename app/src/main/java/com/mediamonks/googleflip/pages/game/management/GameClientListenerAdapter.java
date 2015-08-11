package com.mediamonks.googleflip.pages.game.management;

import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.data.vo.PlayerScoreVO;

import java.util.List;

/**
 * Helper class for partial GameClientListener implementations
 */
public abstract class GameClientListenerAdapter implements GameClientListener {
    @Override
    public void onClientsChanged(List<ClientVO> clients) {
    }

    @Override
    public void onPlayerScoresChanged(List<PlayerScoreVO> playerScores) {
    }

    @Override
    public void onRoundStarted(Long levelId) {
    }

    @Override
    public void onRoundFinished() {
    }

    @Override
    public void onGameFinished() {
    }

    @Override
    public void onConnectionLost() {
    }
}
