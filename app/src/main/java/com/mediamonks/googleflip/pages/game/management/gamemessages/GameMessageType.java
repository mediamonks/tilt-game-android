package com.mediamonks.googleflip.pages.game.management.gamemessages;

/**
 * Supported message types
 */
public enum GameMessageType {
    S2C_CLIENT_ACK,
    S2C_CONNECTED_CLIENTS_CHANGED,
    S2C_ROUND_STARTED,
    S2C_ROUND_FINISHED,
    S2C_GAME_FINISHED,
    S2C_CLIENTS_SCORE_CHANGED,

    C2S_CLIENT_NAME,
    C2S_ROUND_FINISHED
}
