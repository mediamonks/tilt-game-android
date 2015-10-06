package com.mediamonks.googleflip.pages.game.management;

import com.mediamonks.googleflip.data.constants.LevelColor;
import com.mediamonks.googleflip.data.constants.PlayerState;
import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.data.vo.LevelResultVO;

import java.util.ArrayList;
import java.util.List;

import temple.multiplayer.net.common.connection.Connection;

/**
 * Implementation of game player interface
 */
public class PlayerImpl implements Player {
    private static final String TAG = PlayerImpl.class.getSimpleName();

    private Connection _connection;
    private ClientVO _clientVO = new ClientVO();
    private List<LevelResultVO> _results = new ArrayList<>();

    public PlayerImpl(Connection connection) {
        _connection = connection;
    }

    @Override
    public void setConnection(Connection connection) {
        _connection = connection;
    }

    @Override
    public Connection getConnection() {
        return _connection;
    }

    @Override
    public ClientVO getClientVO() {
        return _clientVO;
    }

    @Override
    public void setPlayerName(String playerName) {
        _clientVO.name = playerName;
    }

    @Override
    public void setPlayerId(int id) {
        _clientVO.id = id;
    }

    @Override
    public void setPlayerState(PlayerState playerState) {
        _clientVO.playerState = playerState;
    }

    @Override
    public void setPlayerLevelColor(LevelColor levelColor) {
        _clientVO.levelColor = levelColor;
    }

    @Override
    public void addLevelResult(LevelResultVO levelResultVO) {
        _results.add(levelResultVO);
    }

    @Override
    public List<LevelResultVO> getLevelResults() {
        return _results;
    }

    @Override
    public void clearLevelResults() {
        _results = new ArrayList<>();
    }
}
