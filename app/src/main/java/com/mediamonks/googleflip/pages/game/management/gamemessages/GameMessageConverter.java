package com.mediamonks.googleflip.pages.game.management.gamemessages;

import com.google.gson.Gson;
import com.mediamonks.googleflip.pages.game.management.gamemessages.c2s.C2SClientNameMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.c2s.C2SRoundFinishedMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.s2c.S2CClientAckMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.s2c.S2CClientsScoreChangedMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.s2c.S2CConnectedClientsChangedMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.s2c.S2CGameFinishedMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.s2c.S2CRoundFinishedMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.s2c.S2CRoundStartedMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for converting game messages to and from a textual format to be sent between clients and server
 * All supported game messages need to be present in the class map
 */
public class GameMessageConverter {
    // special character separating game message type identifier from game message content
    private static final String SEPARATOR = "|";

    private static Gson sGson;
    private static Map<GameMessageType, Class> sClassMap;

    public static GameMessage readMessage(String message) {
        initGson();
        initClassMap();

        String[] messageParts = message.split("\\" + SEPARATOR);
        String typePart = messageParts[0];
        String jsonPart = messageParts[1];

        int typeIndex = Integer.parseInt(typePart);
        GameMessageType messageType = GameMessageType.values()[typeIndex];

        if (!sClassMap.containsKey(messageType)) {
            throw new Error("No class found for message type " + messageType);
        }

        return (GameMessage) sGson.fromJson(jsonPart, sClassMap.get(messageType));
    }

    public static String writeMessage(GameMessage gameMessage) {
        initGson();

        return gameMessage.getType().ordinal() + SEPARATOR + sGson.toJson(gameMessage) + "\n";
    }

    private static void initGson() {
        if (sGson == null) {
            sGson = new Gson();
        }
    }

    private static void initClassMap() {
        if (sClassMap == null) {
            sClassMap = new HashMap<>();
            sClassMap.put(GameMessageType.S2C_CLIENT_ACK, S2CClientAckMessage.class);
            sClassMap.put(GameMessageType.S2C_ROUND_STARTED, S2CRoundStartedMessage.class);
            sClassMap.put(GameMessageType.S2C_CLIENTS_SCORE_CHANGED, S2CClientsScoreChangedMessage.class);
            sClassMap.put(GameMessageType.S2C_CONNECTED_CLIENTS_CHANGED, S2CConnectedClientsChangedMessage.class);
            sClassMap.put(GameMessageType.S2C_ROUND_FINISHED, S2CRoundFinishedMessage.class);
            sClassMap.put(GameMessageType.S2C_GAME_FINISHED, S2CGameFinishedMessage.class);

            sClassMap.put(GameMessageType.C2S_CLIENT_NAME, C2SClientNameMessage.class);
            sClassMap.put(GameMessageType.C2S_ROUND_FINISHED, C2SRoundFinishedMessage.class);
        }
    }
}
