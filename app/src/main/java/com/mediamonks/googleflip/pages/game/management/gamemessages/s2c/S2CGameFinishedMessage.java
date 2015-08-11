package com.mediamonks.googleflip.pages.game.management.gamemessages.s2c;

import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessageType;

/**
 * Message sent from server to client when the game is finished
 */
public class S2CGameFinishedMessage implements GameMessage {
    public int winnerId;

    public S2CGameFinishedMessage(int winnerId) {
        this.winnerId = winnerId;
    }

    @Override
    public GameMessageType getType() {
        return GameMessageType.S2C_GAME_FINISHED;
    }
}
