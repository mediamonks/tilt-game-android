package com.mediamonks.googleflip.pages.game.management;

import android.util.Log;

import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.data.vo.PlayerScoreVO;
import com.mediamonks.googleflip.net.common.Connection;
import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.GameMessageConverter;
import com.mediamonks.googleflip.pages.game.management.gamemessages.c2s.C2SClientNameMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.c2s.C2SRoundFinishedMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.s2c.S2CClientAckMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.s2c.S2CClientsScoreChangedMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.s2c.S2CConnectedClientsChangedMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.s2c.S2CGameFinishedMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.s2c.S2CRoundFinishedMessage;
import com.mediamonks.googleflip.pages.game.management.gamemessages.s2c.S2CRoundStartedMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of GameClient interface
 */
public class GameClientImpl implements GameClient, Connection.ConnectionHandler {
    private static final String TAG = GameClientImpl.class.getSimpleName();

    private Player _player;
    private boolean _debug;
    private List<ClientVO> _clients;
    private List<PlayerScoreVO> _playerScores;
    private GameClientListenerProxy _listenerProxy = new GameClientListenerProxy();
    private Connection.MessageHandler _messageHandler = new Connection.MessageHandler() {
        @Override
        public void onMessageReceived(String message) {
            GameClientImpl.this.onMessageReceived(message);
        }
    };
    private boolean _isGameFinished;
    private int _currentRoundIndex;
    private boolean _isWinner;
    private boolean _isRoundFinished;
    private boolean _isConnected;

    public GameClientImpl() {
    }

    @Override
    public void setPlayer(Player player) {
        if (_debug) Log.d(TAG, "setPlayer: " + player.getClientVO().name);

        _player = player;

        _player.getConnection().setMessageHandler(_messageHandler);
        _player.getConnection().setConnectionHandler(this);

        _isConnected = true;

        // send this now, even though the server may not be ready to receive it
        writeGameMessage(new C2SClientNameMessage(_player.getClientVO().name, _player.getClientVO().id));
    }

    private void writeGameMessage(GameMessage gameMessage) {
        if (_isConnected) {
            _player.getConnection().writeMessage(GameMessageConverter.writeMessage(gameMessage));
        }
    }

    private void onMessageReceived(String message) {
        if (_debug) Log.d(TAG, "onMessageReceived: message = " + message);

        GameMessage gameMessage = GameMessageConverter.readMessage(message);
        if (gameMessage == null) {
            Log.e(TAG, "onMessageReceived: couldn't convert message " + message);
            return;
        }

        switch (gameMessage.getType()) {
            case S2C_CLIENT_ACK:
                onClientAck((S2CClientAckMessage) gameMessage);
                break;
            case S2C_ROUND_STARTED:
                onRoundStarted((S2CRoundStartedMessage) gameMessage);
                break;
            case S2C_CONNECTED_CLIENTS_CHANGED:
                onConnectedClientsChanged((S2CConnectedClientsChangedMessage) gameMessage);
                break;
            case S2C_CLIENTS_SCORE_CHANGED:
                onClientsScoresChanged((S2CClientsScoreChangedMessage) gameMessage);
                break;
            case S2C_ROUND_FINISHED:
                onRoundFinished((S2CRoundFinishedMessage) gameMessage);
                break;
            case S2C_GAME_FINISHED:
                onGameFinished((S2CGameFinishedMessage) gameMessage);
                break;
            default:
                if (_debug) Log.d(TAG, "onMessageReceived: unhandled message type " + gameMessage.getType());
                break;
        }
    }

    private void onGameFinished(S2CGameFinishedMessage message) {
        if (_debug) Log.d(TAG, "onGameFinished: winner = " + message.winnerId);

        _isGameFinished = true;
        _isWinner = (message.winnerId == _player.getClientVO().id);

        _listenerProxy.onGameFinished();
    }

    private void onRoundFinished(S2CRoundFinishedMessage message) {
        if (_debug) Log.d(TAG, "onRoundFinished: ");

        _isRoundFinished = true;

        _listenerProxy.onRoundFinished();
    }

    private void onClientsScoresChanged(S2CClientsScoreChangedMessage message) {
        if (_debug) Log.d(TAG, "onClientsScoresChanged: " + message.playerScores);

        _playerScores = message.playerScores;

        _listenerProxy.onPlayerScoresChanged(message.playerScores);
    }

    private void onConnectedClientsChanged(S2CConnectedClientsChangedMessage message) {
        if (_debug) Log.d(TAG, "onConnectedClientsChanged: " + message.clients);

        _clients = message.clients;

        _listenerProxy.onClientsChanged(message.clients);
    }

    private void onRoundStarted(S2CRoundStartedMessage message) {
        _isGameFinished = false;
        _isRoundFinished = false;

        if (_debug) Log.d(TAG, "onRoundStarted: id = " + message.levelId);

        _currentRoundIndex = message.index;

        _listenerProxy.onRoundStarted(message.levelId);
    }

    private void onClientAck(S2CClientAckMessage message) {
        if (_debug) Log.d(TAG, "onClientAck: id = " + message.id);

        if (_player.getClientVO().id == message.id && _player.getClientVO().name.equals(message.name)) {
            if (_debug) Log.d(TAG, "onClientAck: player already known to server");
        } else {
            _player.setPlayerId(message.id);
            _player.setPlayerLevelColor(message.levelColor);

            writeGameMessage(new C2SClientNameMessage(_player.getClientVO().name, _player.getClientVO().id));
        }
    }

    @Override
    public Player getPlayer() {
        return _player;
    }

    @Override
    public void setRoundComplete(Long levelId, float seconds, boolean success) {
        writeGameMessage(new C2SRoundFinishedMessage(levelId, seconds, success));
    }

    @Override
    public void addGameClientListener(GameClientListener listener) {
        _listenerProxy.addGameClientListener(listener);
    }

    @Override
    public void removeGameClientListener(GameClientListener listener) {
        _listenerProxy.removeGameClientListener(listener);
    }

    @Override
    public List<ClientVO> getConnectedClients() {
        return _clients;
    }

    @Override
    public List<PlayerScoreVO> getPlayerScores() {
        return _playerScores;
    }

    @Override
    public PlayerScoreVO getPlayerScore() {
        if (_playerScores == null) {
            return null;
        }

        for (PlayerScoreVO playerScoreVO : _playerScores) {
            if (playerScoreVO.clientVO.id == _player.getClientVO().id) {
                return playerScoreVO;
            }
        }

        return null;
    }

    @Override
    public boolean isGameFinished() {
        return _isGameFinished;
    }

    @Override
    public boolean isRoundFinished() {
        return _isRoundFinished;
    }

    @Override
    public void stop() {
        if (_player == null) {
            return;
        }
        // disconnect player
        _player.getConnection().disconnect();

        _clients = null;
        _player = null;
        _playerScores = null;

        _isConnected = false;
    }

    @Override
    public int getCurrentRoundIndex() {
        return _currentRoundIndex;
    }

    @Override
    public boolean isWinner() {
        return _isWinner;
    }

    @Override
    public boolean isConnected() {
        return _isConnected;
    }

    public void setDebug(boolean debug) {
        _debug = debug;
    }

    @Override
    public void onConnectionLost() {
        _listenerProxy.onConnectionLost();

        _isConnected = false;
    }

    /**
     * Helper class for serving multiple listeners
     */
    private static class GameClientListenerProxy implements GameClientListener {
        private ArrayList<GameClientListener> _gameClientListeners;

        private void addGameClientListener(GameClientListener listener) {
            if (_gameClientListeners == null) {
                _gameClientListeners = new ArrayList<>();
            }
            _gameClientListeners.add(listener);
        }

        private void removeGameClientListener(GameClientListener listener) {
            _gameClientListeners.remove(listener);
        }

        @Override
        public void onClientsChanged(List<ClientVO> clients) {
            if (_gameClientListeners != null) {
                for (GameClientListener listener : _gameClientListeners) {
                    listener.onClientsChanged(clients);
                }
            }
        }

        @Override
        public void onPlayerScoresChanged(List<PlayerScoreVO> playerScores) {
            if (_gameClientListeners != null) {
                for (GameClientListener listener : _gameClientListeners) {
                    listener.onPlayerScoresChanged(playerScores);
                }
            }
        }

        @Override
        public void onRoundStarted(Long levelId) {
            if (_gameClientListeners != null) {
                for (GameClientListener listener : _gameClientListeners) {
                    listener.onRoundStarted(levelId);
                }
            }
        }

        @Override
        public void onRoundFinished() {
            if (_gameClientListeners != null) {
                for (GameClientListener listener : _gameClientListeners) {
                    listener.onRoundFinished();
                }
            }
        }

        @Override
        public void onGameFinished() {
            if (_gameClientListeners != null) {
                for (GameClientListener listener : _gameClientListeners) {
                    listener.onGameFinished();
                }
            }
        }

        @Override
        public void onConnectionLost() {
            if (_gameClientListeners != null) {
                for (GameClientListener listener : _gameClientListeners) {
                    listener.onConnectionLost();
                }
            }
        }
    }
}
