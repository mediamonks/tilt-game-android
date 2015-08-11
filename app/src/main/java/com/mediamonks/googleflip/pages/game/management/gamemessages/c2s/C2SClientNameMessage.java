package com.mediamonks.googleflip.pages.game.management.gamemessages.c2s;

import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessageType;
import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessage;

/**
 * Message sent from client to server with the player name
 */
public class C2SClientNameMessage implements GameMessage {
    public String name;
    public int id;

    public C2SClientNameMessage(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public GameMessageType getType() {
        return GameMessageType.C2S_CLIENT_NAME;
    }
}
