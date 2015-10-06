package com.mediamonks.googleflip.pages.game_flow.multiplayer.fragments.gameover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.pages.game.management.GameClient;
import com.mediamonks.googleflip.ui.BaseFragment;
import com.mediamonks.googleflip.ui.animation.LargeAnimatedTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Page showing current player's result when multiplayer game is over
 */
public class GameOverResultPage extends BaseFragment {
    private static final String TAG = GameOverResultPage.class.getSimpleName();

    @Bind(R.id.result_label1)
    protected LargeAnimatedTextView _resultLabel1;
    @Bind(R.id.result_label2)
    protected LargeAnimatedTextView _resultLabel2;

    public static GameOverResultPage newInstance() {
        return new GameOverResultPage();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_game_over, container, false);
        ButterKnife.bind(this, view);

        GameClient gameClient = GoogleFlipGameApplication.getGameClient();
        String str;

        if (gameClient.isWinner()) {
            str = getString(R.string.you_win);

            _resultLabel1.setText(str.split("\n")[0]);
            _resultLabel2.setText(str.split("\n")[1]);
        } else if (gameClient.getConnectedClients().size() == gameClient.getPlayerScore().order) {
            if (gameClient.getConnectedClients().size() <= 2) {
                _resultLabel1.setText(getString(R.string.you_lose));
            } else {
                _resultLabel1.setText(getString(R.string.last) + " " + getString(R.string.place));
            }
            _resultLabel2.setText(getString(R.string.womp));
        } else {
            switch (gameClient.getPlayerScore().order) {
                case 2:
                    _resultLabel1.setText(getString(R.string.second));
                    break;
                case 3:
                    _resultLabel1.setText(getString(R.string.third));
                    break;
            }

            _resultLabel2.setText(getString(R.string.place));
        }

        _resultLabel1.show();
        _resultLabel2.show(200);

        return view;
    }
}
