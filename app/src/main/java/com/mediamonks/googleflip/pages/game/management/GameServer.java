package com.mediamonks.googleflip.pages.game.management;

import com.mediamonks.googleflip.data.vo.LevelVO;

import java.util.List;

/**
 * Interface for game server
 */
public interface GameServer {
    /**
     * Add player
     */
    void addPlayer (Player player);

    /**
     * remove player by player object
     */
    void removePlayer (Player player);

    /**
     * remove player by device address
     */
    void removePlayer (String deviceAddress);

    /**
     * Start a new game
     */
    void startGame();

    /**
     * start the next round
     */
    void startRound ();

    void setDebug(boolean debug);

    /**
     * set available list of levels
     */
    void setLevels (List<LevelVO> levels);

    /**
     * stop the game server
     */
    void stop ();

    void initBackgroundColors();

    boolean hasRoomForMorePlayers ();
}
