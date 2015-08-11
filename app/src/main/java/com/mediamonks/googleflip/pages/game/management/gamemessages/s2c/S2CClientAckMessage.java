package com.mediamonks.googleflip.pages.game.management.gamemessages.s2c;

import com.mediamonks.googleflip.data.constants.LevelColor;
import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessageType;
import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessage;

/**
 * Message sent from server to client when the server has connected to the client
 */
public class S2CClientAckMessage implements GameMessage {
    public int id;
    public LevelColor levelColor;
    public String name;

    public S2CClientAckMessage(int id, LevelColor levelColor, String name) {
        this.id = id;
        this.levelColor = levelColor;
        this.name = name;
    }

    @Override
    public GameMessageType getType() {
        return GameMessageType.S2C_CLIENT_ACK;
    }
}
