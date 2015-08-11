package com.mediamonks.googleflip.data.vo;

import com.mediamonks.googleflip.data.constants.LevelColor;
import com.mediamonks.googleflip.data.constants.PlayerState;

/**
 * VO class for storing multiplayer client data
 */
public class ClientVO {
    public int id = -1;
    public String name;
    public PlayerState playerState;
    public LevelColor levelColor;

    public ClientVO() {
    }

    @Override
    public String toString() {
        return "ClientVO{" +
                "id=" + id +
                ", playerState=" + playerState +
                ", name='" + name + '\'' +
                ", levelColor='" + levelColor + '\'' +
                '}';
    }
}
