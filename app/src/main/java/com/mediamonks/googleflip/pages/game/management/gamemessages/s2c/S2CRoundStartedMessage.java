package com.mediamonks.googleflip.pages.game.management.gamemessages.s2c;

import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessageType;
import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessage;

/**
 * Message sent from server to client when the round has started
 */
public class S2CRoundStartedMessage implements GameMessage {
    public Long levelId;
    public int index;

    public S2CRoundStartedMessage(Long levelId, int index) {
        this.levelId = levelId;
        this.index = index;
    }

    @Override
    public GameMessageType getType() {
        return GameMessageType.S2C_ROUND_STARTED;
    }
}
