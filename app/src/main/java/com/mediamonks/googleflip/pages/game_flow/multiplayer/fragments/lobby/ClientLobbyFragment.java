package com.mediamonks.googleflip.pages.game_flow.multiplayer.fragments.lobby;

import android.view.View;

import com.mediamonks.googleflip.R;
import com.mediamonks.googleflip.data.vo.ClientVO;

import java.util.List;

/**
 * Client lobby, wait for server to start game
 */
public class ClientLobbyFragment extends AbstractLobbyFragment {
    private static final String TAG = ClientLobbyFragment.class.getSimpleName();

    public static ClientLobbyFragment newInstance() {
        return new ClientLobbyFragment();
    }

    @Override
    protected void setupUI() {
        _waitingForPlayersText.setVisibility(View.GONE);
        _buttons.setVisibility(View.GONE);
    }

    @Override
    protected void updateClients(List<ClientVO> clients) {
        super.updateClients(clients);

        int clientCount = 0;
        if (clients != null) {
            clientCount = clients.size();
        }

        _lookingForHostText.setText(clientCount > 1 ? R.string.waiting_to_start_game : R.string.looking_for_host);
        _lookingForHostText.setVisibility(clientCount == 0 ? View.GONE : View.VISIBLE);

    }
}
