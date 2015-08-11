package com.mediamonks.googleflip.pages.game.management.gamemessages.c2s;

import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessageType;
import com.mediamonks.googleflip.data.vo.LevelResultVO;
import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessage;

/**
 * Message sent from client to server when the player has finished a round
 */
public class C2SRoundFinishedMessage implements GameMessage {
    public LevelResultVO levelResultVO;

    public C2SRoundFinishedMessage(Long levelId, float seconds, boolean success) {
        levelResultVO = new LevelResultVO(levelId, seconds, success);
    }

    @Override
    public GameMessageType getType() {
        return GameMessageType.C2S_ROUND_FINISHED;
    }
}
