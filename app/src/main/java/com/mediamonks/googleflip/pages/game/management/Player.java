package com.mediamonks.googleflip.pages.game.management;

import com.mediamonks.googleflip.data.constants.LevelColor;
import com.mediamonks.googleflip.data.constants.PlayerState;
import com.mediamonks.googleflip.data.vo.LevelResultVO;
import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.net.common.Connection;

import java.util.List;

/**
 * Interface for player
 */
public interface Player {
    /**
     * Set connection object for sending & receiving messages
     */
    void setConnection (Connection connection);

    Connection getConnection ();

    /**
     * Get player data
     */
    ClientVO getClientVO();

    void setPlayerName (String playerName);

    void setPlayerId (int id);

    void setPlayerState (PlayerState playerState);

    void setPlayerLevelColor (LevelColor levelColor);

    /**
     * Add result for single round
     */
    void addLevelResult (LevelResultVO levelResultVO);

    /**
     * Get results for all rounds so far
     */
    List<LevelResultVO> getLevelResults ();

    void clearLevelResults ();
}
