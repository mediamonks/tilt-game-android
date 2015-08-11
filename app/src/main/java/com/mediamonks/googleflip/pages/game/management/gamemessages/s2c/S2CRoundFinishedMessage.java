package com.mediamonks.googleflip.pages.game.management.gamemessages.s2c;

import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessageType;

/**
 * Message sent from server to client when a round is finished
 */
public class S2CRoundFinishedMessage implements GameMessage {
    @Override
    public GameMessageType getType() {
        return GameMessageType.S2C_ROUND_FINISHED;
    }
}
