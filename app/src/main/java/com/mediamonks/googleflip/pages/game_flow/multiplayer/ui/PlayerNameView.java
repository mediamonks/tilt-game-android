package com.mediamonks.googleflip.pages.game_flow.multiplayer.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.data.vo.PlayerScoreVO;
import com.mediamonks.googleflip.util.LevelColorUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Single player name view
 */
public class PlayerNameView {
    private static final String TAG = PlayerNameView.class.getSimpleName();

    @InjectView(R.id.tv_player_name)
    protected TextView _playerNameText;
    @InjectView(R.id.tv_player_result)
    protected TextView _playerResultText;

    private Context _context;
    private ClientVO _clientVO;
    private View _view;

    public static PlayerNameView newInstance(Context context, LayoutInflater inflater, ViewGroup parent, ClientVO clientVO) {
        View row = inflater.inflate(R.layout.item_sb_player_name, parent, false);

        return new PlayerNameView(context, clientVO, row);
    }

    public PlayerNameView(Context context, ClientVO clientVO, View view) {
        _context = context;
        _clientVO = clientVO;
        _view = view;

        ButterKnife.inject(this, _view);

        _playerNameText.setText(_clientVO.name);

        _view.setBackgroundColor(LevelColorUtil.fromLevelColor(clientVO.levelColor));
    }

    public void updateResults(PlayerScoreVO playerScoreVO) {
        int currentRound = playerScoreVO.roundIndex;

        if (playerScoreVO.roundScores.size() <= currentRound || playerScoreVO.isPlaying) {
            _playerResultText.setText("...");
        } else {
            _playerResultText.setText(_context.getString(R.string.result_time, playerScoreVO.totalTime));
        }
    }

    public int getPlayerId() {
        return _clientVO.id;
    }

    public View getView() {
        return _view;
    }
}
