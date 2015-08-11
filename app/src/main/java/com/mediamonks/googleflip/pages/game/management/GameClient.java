package com.mediamonks.googleflip.pages.game.management;

import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.data.vo.PlayerScoreVO;
import com.mediamonks.googleflip.util.Navigator;

import java.util.List;

/**
 * Interface for game client. Each user has one.
 */
public interface GameClient {
    /**
     * Initialize GameClient with Player instance, which provides the connection to the server
     */
    void setPlayer (Player player);

    Player getPlayer ();

    /**
     * Indicate player is done with current round
     * @param levelId id of round level
     * @param seconds seconds to complete round
     * @param success whether the round was completed successfully
     */
    void setRoundComplete (Long levelId, float seconds, boolean success);

    /**
     * Add listener for game client events
     */
    void addGameClientListener (GameClientListener listener);

    void removeGameClientListener (GameClientListener listener);

    /**
     * retrieve all connected clients (all players in the game)
     */
    List<ClientVO> getConnectedClients ();

    /**
     * get scores for all players
     */
    List<PlayerScoreVO> getPlayerScores ();

    /**
     * Get score for current player
     */
    PlayerScoreVO getPlayerScore();

    /**
     * retrieve state of game
     * @return true if game is finished
     */
    boolean isGameFinished ();

    /**
     * retrieve state of round
     * @return true if round is finished
     */
    boolean isRoundFinished ();

    /**
     * stop game client
     */
    void stop();

    /**
     * Retrieve index of current round
     */
    int getCurrentRoundIndex();

    /**
     * retrieve whether current player has won the game
     * @return true if current player has won
     */
    boolean isWinner ();

    /**
     * retrieve whether current player is connected to the server
     */
    boolean isConnected ();
}
