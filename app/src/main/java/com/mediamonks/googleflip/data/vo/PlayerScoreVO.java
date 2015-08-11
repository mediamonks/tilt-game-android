package com.mediamonks.googleflip.data.vo;

import android.support.annotation.NonNull;

import com.mediamonks.googleflip.data.constants.LevelColor;

import org.andengine.util.adt.array.ArrayUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * VO class for storing multiplayer player score
 */
public class PlayerScoreVO implements Comparable<PlayerScoreVO> {
    public List<Float> roundScores = new ArrayList<>();
    public float totalTime;
    public boolean isPlaying;
    public ClientVO clientVO;
    public int roundIndex;
    public int order;

    public PlayerScoreVO(ClientVO clientVO, int roundIndex) {
        this.clientVO = clientVO;
        this.roundIndex = roundIndex;
    }

    @Override
    public String toString() {
        return "PlayerScoreVO{" +
                "clientVO=" + clientVO +
                ", totalTime=" + totalTime +
                ", isPlaying=" + isPlaying +
                ", roundIndex=" + roundIndex +
                ", roundScores=" + roundScores +
                ", order=" + order +
                '}';
    }

    @Override
    public int compareTo(@NonNull PlayerScoreVO another) {
        if (totalTime < another.totalTime) return -1;
        else if (totalTime > another.totalTime) return 1;
        else return 0;
    }
}
