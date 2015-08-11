package com.mediamonks.googleflip.pages.game.management;

import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.data.vo.PlayerScoreVO;

import java.util.List;

/**
 * Interface for game client event listeners
 */
public interface GameClientListener {
    /**
     * method called when clients have been connected or disconnected
     */
    void onClientsChanged(List<ClientVO> clients);

    /**
     * method called when player scores have changed
     */
    void onPlayerScoresChanged(List<PlayerScoreVO> playerScores);

    /**
     * method called when a game round is started
     * @param levelId id of level to be started
     */
    void onRoundStarted(Long levelId);

    /**
     * method called when round is finished
     */
    void onRoundFinished();

    /**
     * method called when game is finished
     */
    void onGameFinished();

    /**
     * method called when connection to server is lost
     */
    void onConnectionLost ();
}
