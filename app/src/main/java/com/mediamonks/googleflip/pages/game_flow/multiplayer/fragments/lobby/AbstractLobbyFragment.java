package com.mediamonks.googleflip.pages.game_flow.multiplayer.fragments.lobby;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.pages.game.FlipGameActivity;
import com.mediamonks.googleflip.pages.game.management.GameClient;
import com.mediamonks.googleflip.pages.game.management.GameClientListener;
import com.mediamonks.googleflip.pages.game.management.GameClientListenerAdapter;
import com.mediamonks.googleflip.ui.BaseFragment;
import com.mediamonks.googleflip.util.LevelColorUtil;

import java.util.List;

import butterknife.Bind;

/**
 * Base fragment for lobby fragments
 */
public abstract class AbstractLobbyFragment extends BaseFragment {
    private static final String TAG = AbstractLobbyFragment.class.getSimpleName();

    @Bind(R.id.buttons)
    protected LinearLayout _buttons;
    @Bind(R.id.tv_waiting_for_players)
    protected TextView _waitingForPlayersText;
    @Bind(R.id.tv_looking_for_host)
    protected TextView _lookingForHostText;
    @Bind(R.id.btn_next)
    protected Button _nextButton;
    @Bind({R.id.player_name_1, R.id.player_name_2, R.id.player_name_3, R.id.player_name_4})
    protected TextView[] _playerViews;

    protected GameClient _gameClient;

    private GameClientListener _gameClientListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return createView(R.layout.fragment_lobby, inflater, container);
    }

    @Override
    protected View createView(int layoutId, LayoutInflater inflater, ViewGroup container) {
        View view = super.createView(layoutId, inflater, container);

        _gameClient = GoogleFlipGameApplication.getGameClient();

        _gameClientListener = new GameClientListenerAdapter() {
            @Override
            public void onRoundStarted(Long levelId) {
                GoogleFlipGameApplication.getUserModel().selectLevelById(levelId);

                startActivity(new Intent(getActivity(), FlipGameActivity.class));

                getActivity().finish();
            }

            @Override
            public void onClientsChanged(List<ClientVO> clients) {
                updateClients(clients);
            }
        };

        setupUI();

        return view;
    }

    protected abstract void setupUI();

    protected void updateClients(List<ClientVO> clients) {
        int clientCount = 0;
        if (clients != null) {
            clientCount = clients.size();
        }

        for (int index = 0; index < clientCount && index < _playerViews.length; index++) {
            ClientVO clientVO = clients.get(index);
            TextView playerView = _playerViews[index];

            playerView.setText(clientVO.name);
            playerView.setBackgroundColor(LevelColorUtil.fromLevelColor(clientVO.levelColor));
            playerView.setVisibility(View.VISIBLE);
        }

        for (int index = clientCount; index < _playerViews.length; index++) {
            _playerViews[index].setVisibility(View.GONE);
        }

        _nextButton.setVisibility(clientCount > 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();

        _gameClient.addGameClientListener(_gameClientListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateClients(_gameClient.getConnectedClients());
    }

    @Override
    public void onStop() {
        super.onStop();

        _gameClient.removeGameClientListener(_gameClientListener);
    }
}
