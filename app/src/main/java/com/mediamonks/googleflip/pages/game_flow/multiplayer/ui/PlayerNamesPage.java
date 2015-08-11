package com.mediamonks.googleflip.pages.game_flow.multiplayer.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.data.vo.PlayerScoreVO;

import java.util.HashMap;
import java.util.List;

/**
 * Page showing player names
 */
public class PlayerNamesPage extends AbstractScoreboardPage {
    private static final String TAG = PlayerNamesPage.class.getSimpleName();

    private HashMap<Integer, PlayerNameView> _rowMap;

    public static PlayerNamesPage newInstance() {
        return new PlayerNamesPage();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return createView(R.layout.page_scoreboard, inflater, container);
    }

    @Override
    protected void createClientRows(ViewGroup container, List<ClientVO> clients) {
        _rowMap = new HashMap<>();

        for (int i = 0; i < clients.size(); i++) {
            PlayerNameView row = PlayerNameView.newInstance(getActivity(), getActivity().getLayoutInflater(), container, clients.get(i));

            _rowMap.put(row.getPlayerId(), row);

            container.addView(row.getView());
        }
    }

    @Override
    protected void updatePlayerScores(List<PlayerScoreVO> playerScores) {
        if (playerScores == null) return;

        for (int index = 0; index < playerScores.size(); index++) {
            PlayerScoreVO playerScore = playerScores.get(index);
            PlayerNameView nameView = _rowMap.get(playerScore.clientVO.id);
            if (nameView != null) {
                nameView.updateResults(playerScore);
            }
        }
    }
}
