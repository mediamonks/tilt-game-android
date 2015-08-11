package com.mediamonks.googleflip.pages.game.management.gamemessages.s2c;

import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessageType;
import com.mediamonks.googleflip.data.vo.PlayerScoreVO;
import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessage;

import java.util.List;

/**
 * Message sent from server to client when the score of one or more clients has changed
 */
public class S2CClientsScoreChangedMessage implements GameMessage {
    public List<PlayerScoreVO> playerScores;

    public S2CClientsScoreChangedMessage(List<PlayerScoreVO> playerScores) {
        this.playerScores = playerScores;
    }

    @Override
    public GameMessageType getType() {
        return GameMessageType.S2C_CLIENTS_SCORE_CHANGED;
    }
}
