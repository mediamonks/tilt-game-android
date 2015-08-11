package com.mediamonks.googleflip.pages.game_flow.multiplayer.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.data.vo.PlayerScoreVO;
import com.mediamonks.googleflip.util.LevelColorUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

/**
 * Single row with player round times
 */
public class PlayerRoundTimesView {
    private static final String TAG = PlayerRoundTimesView.class.getSimpleName();

    @InjectViews({R.id.tv_result_round1, R.id.tv_result_round2, R.id.tv_result_round3})
    protected TextView[] _roundResultTexts;

    private Context _context;
    private ClientVO _clientVO;
    private View _view;

    public static PlayerRoundTimesView newInstance(Context context, LayoutInflater inflater, ViewGroup parent, ClientVO clientVO) {
        View row = inflater.inflate(R.layout.item_sb_player_roundtimes, parent, false);

        return new PlayerRoundTimesView(context, clientVO, row);
    }

    public PlayerRoundTimesView(Context context, ClientVO clientVO, View view) {
        _context = context;
        _clientVO = clientVO;
        _view = view;

        ButterKnife.inject(this, _view);

        _view.setBackgroundColor(LevelColorUtil.fromLevelColor(clientVO.levelColor));
    }

    public void updateResults(PlayerScoreVO playerScoreVO) {
        for (int index = 0; index < playerScoreVO.roundScores.size(); index++) {
            _roundResultTexts[index].setText(_context.getString(R.string.result_time, playerScoreVO.roundScores.get(index)));
        }
        for (int index = playerScoreVO.roundScores.size(); index < _roundResultTexts.length; index++) {
            _roundResultTexts[index].setText((index == playerScoreVO.roundIndex && playerScoreVO.isPlaying) ? "..." : "x");
        }
    }

    public int getPlayerId() {
        return _clientVO.id;
    }

    public View getView() {
        return _view;
    }
}
