package com.mediamonks.googleflip.pages.game.management.gamemessages.s2c;

import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessageType;
import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessage;

import java.util.List;

/**
 * Message sent from server to client when one or more clients have connected or disconnected
 */
public class S2CConnectedClientsChangedMessage implements GameMessage {
    public List<ClientVO> clients;

    public S2CConnectedClientsChangedMessage(List<ClientVO> clients) {
        this.clients = clients;
    }

    @Override
    public GameMessageType getType() {
        return GameMessageType.S2C_CONNECTED_CLIENTS_CHANGED;
    }
}
