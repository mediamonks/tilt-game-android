package com.mediamonks.googleflip.pages.game_flow.multiplayer.fragments.scoreboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.data.vo.PlayerScoreVO;
import com.mediamonks.googleflip.pages.game_flow.multiplayer.ui.AbstractScoreboardPage;
import com.mediamonks.googleflip.pages.game_flow.multiplayer.ui.PlayerRoundTimesView;

import java.util.HashMap;
import java.util.List;

/**
 * Scoreboard page showing player times
 */
public class ScoreboardPlayerTimesPage extends AbstractScoreboardPage {
    private static final String TAG = ScoreboardPlayerTimesPage.class.getSimpleName();

    private HashMap<Integer, PlayerRoundTimesView> _rowMap;

    public static ScoreboardPlayerTimesPage newInstance() {
        return new ScoreboardPlayerTimesPage();
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
            PlayerRoundTimesView row = PlayerRoundTimesView.newInstance(getActivity(), getActivity().getLayoutInflater(), container, clients.get(i));

            _rowMap.put(row.getPlayerId(), row);

            container.addView(row.getView());
        }
    }

    @Override
    protected void updatePlayerScores(List<PlayerScoreVO> playerScores) {
        if (playerScores == null) return;

        for (int index = 0; index < playerScores.size(); index++) {
            PlayerScoreVO playerScore = playerScores.get(index);
            PlayerRoundTimesView roundTimesView = _rowMap.get(playerScore.clientVO.id);
            if (roundTimesView != null) {
                roundTimesView.updateResults(playerScore);
            }
        }
    }

    @Override
    public void onDestroy() {
        if(_rowMap != null)
        {
            _rowMap.clear();
            _rowMap= null;
        }

        super.onDestroy();
    }
}
