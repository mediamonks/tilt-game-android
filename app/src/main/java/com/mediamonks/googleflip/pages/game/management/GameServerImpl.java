package com.mediamonks.googleflip.pages.game.management;

import android.util.Log;

import com.mediamonks.googleflip.GoogleFlipGameApplication;
import com.mediamonks.googleflip.data.constants.LevelColor;
import com.mediamonks.googleflip.data.constants.LevelDifficulty;
import com.mediamonks.googleflip.data.constants.PlayerState;
import com.mediamonks.googleflip.data.vo.ClientVO;
import com.mediamonks.googleflip.data.vo.LevelResultVO;
import com.mediamonks.googleflip.data.vo.LevelVO;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of the GameServer interface
 */
public class GameServerImpl implements GameServer {
    private static final String TAG = GameServerImpl.class.getSimpleName();

    private static int sCurrentId = 0;

    private final List<Player> _players = new ArrayList<>();
    private boolean _debug;
    private List<Long> _rounds;
    private int _roundIndex;
    private List<LevelColor> _backgroundColors = new ArrayList<>();
    private HashMap<Integer, List<LevelVO>> _levelDifficultyMap;

    public GameServerImpl() {
    }

    public void initBackgroundColors() {
        LevelColor[] colors = {LevelColor.BLUE, LevelColor.CYAN, LevelColor.PINK, LevelColor.PURPLE};
        List<LevelColor> colorList = Arrays.asList(colors);
        Collections.shuffle(colorList);

        _backgroundColors = new LinkedList<>();
        _backgroundColors.addAll(colorList);
    }

    @Override
    public void addPlayer(final Player player) {
        if (_debug) Log.d(TAG, "addPlayer: ");

        // initialize player with unique ID & background color
        player.setPlayerId(getUniqueId());
        player.setPlayerLevelColor(getLevelColor());

        player.getConnection().setMessageHandler(
                new Connection.MessageHandler() {
                    @Override
                    public void onMessageReceived(String message) {
                        GameServerImpl.this.onMessageReceived(player, message);
                    }
                }
        );
        player.getConnection().setConnectionHandler(
                new Connection.ConnectionHandler() {
                    @Override
                    public void onConnectionLost() {
                        removePlayer(player);
                    }
                }
        );

        _players.add(player);

        // send this now, even though the client may not be ready to receive it
        sendClientAck(player);
    }

    private void sendClientAck(Player player) {
        ClientVO clientVO = player.getClientVO();
        player.getConnection().writeMessage(GameMessageConverter.writeMessage(new S2CClientAckMessage(clientVO.id, clientVO.levelColor, clientVO.name)));
    }

    private LevelColor getLevelColor() {
        if (_backgroundColors.size() == 0) {
            initBackgroundColors();
        }

        return _backgroundColors.remove(0);
    }

    private void onConnectedClientsChanged() {
        List<ClientVO> clients = new ArrayList<>();
        for (Player player : _players) {
            clients.add(player.getClientVO());
        }

        broadcastMessage(new S2CConnectedClientsChangedMessage(clients));
    }

    @Override
    public void removePlayer(Player player) {
        if (_debug) Log.d(TAG, "removePlayer: player = " + player);

        _backgroundColors.add(player.getClientVO().levelColor);

        disconnectPlayer(player);

        _players.remove(player);

        onConnectedClientsChanged();
    }

    private void disconnectPlayer(Player player) {
        player.getConnection().setMessageHandler(null);
        player.getConnection().setConnectionHandler(null);
        player.getConnection().disconnect();
    }

    @Override
    public void removePlayer(String deviceAddress) {
        if (_debug) Log.d(TAG, "removePlayer: deviceAddress = " + deviceAddress);

        for (Player player : _players) {
            if (player.getConnection().getDeviceAddress().equals(deviceAddress)) {
                removePlayer(player);

                return;
            }
        }
    }

    private void onMessageReceived(Player player, String message) {
        GameMessage gameMessage = GameMessageConverter.readMessage(message);
        if (gameMessage == null) {
            Log.e(TAG, "onMessageReceived: couldn't convert message " + message);
            return;
        }

        switch (gameMessage.getType()) {
            case C2S_CLIENT_NAME:
                onClientNameMessageReceived(player, (C2SClientNameMessage) gameMessage);
                break;
            case C2S_ROUND_FINISHED:
                onRoundFinishedMessageReceived(player, (C2SRoundFinishedMessage) gameMessage);
                break;
            default:
                if (_debug) Log.d(TAG, "onMessageReceived: unhandled message type " + gameMessage.getType());
                break;
        }
    }

    private void onRoundFinishedMessageReceived(Player player, C2SRoundFinishedMessage message) {
        player.addLevelResult(message.levelResultVO);

        player.setPlayerState(PlayerState.FINISHED);

        updateScores();

        checkRoundFinished();
    }

    /**
     * Check if all players are done with the current round
     */
    private void checkRoundFinished() {
        for (Player player : _players) {
            if (player.getClientVO().playerState.equals(PlayerState.PLAYING)) {
                return;
            }
        }

        _roundIndex++;

        if (_debug) Log.d(TAG, "checkRoundFinished: " + _roundIndex);

        // check if round is finished, or whole game is finished
        if (_roundIndex < _rounds.size()) {
            broadcastMessage(new S2CRoundFinishedMessage());
        } else {
            broadcastMessage(new S2CGameFinishedMessage(getWinnerId()));
        }
    }

    /**
     * Determine the ID of the winner
     */
    private int getWinnerId() {
        int id = 0;
        float minTime = Float.MAX_VALUE;

        for (Player player : _players) {
            float totalTime = 0;
            for (LevelResultVO resultVO : player.getLevelResults()) {
                totalTime += resultVO.seconds;
            }

            if (totalTime < minTime) {
                minTime = totalTime;
                id = player.getClientVO().id;
            }
        }

        return id;
    }

    private void clearScores() {
        for (Player player : _players) {
            player.clearLevelResults();
        }
    }

    private void updateScores() {
        List<PlayerScoreVO> playerScores = new ArrayList<>();

        for (Player player : _players) {
            ClientVO clientVO = player.getClientVO();
            PlayerScoreVO scoreVO = new PlayerScoreVO(clientVO, _roundIndex);

            float totalTime = 0;
            for (LevelResultVO resultVO : player.getLevelResults()) {
                scoreVO.roundScores.add(resultVO.seconds);

                totalTime += resultVO.seconds;
            }
            scoreVO.totalTime = totalTime;

            scoreVO.isPlaying = (player.getClientVO().playerState.equals(PlayerState.PLAYING));

            playerScores.add(scoreVO);
        }

        // update order
        Collections.sort(playerScores);
        for (int i = 0; i < playerScores.size(); i++) {
            playerScores.get(i).order = i + 1;
        }

        broadcastMessage(new S2CClientsScoreChangedMessage(playerScores));
    }

    private void onClientNameMessageReceived(Player player, C2SClientNameMessage message) {
        String playerName = player.getClientVO().name;
        if (playerName != null && playerName.equals(message.name) && player.getClientVO().id == message.id) {
            // onClientNameMessageReceived: player already known
        } else {
            player.setPlayerName(message.name);

            sendClientAck(player);

            onConnectedClientsChanged();
        }
    }

    private static int getUniqueId() {
        return sCurrentId++;
    }

    public void setDebug(boolean debug) {
        _debug = debug;
    }

    @Override
    public void setLevels(List<LevelVO> levels) {
        // create map with shuffled lists of levels per difficulty
        _levelDifficultyMap = new HashMap<>();
        for (int difficulty : LevelDifficulty.DIFFICULTIES) {
            _levelDifficultyMap.put(difficulty, new ArrayList<LevelVO>());
        }
        for (LevelVO levelVO : levels) {
            _levelDifficultyMap.get(levelVO.difficulty).add(levelVO);
        }
        for (int difficulty : LevelDifficulty.DIFFICULTIES) {
            Collections.shuffle(_levelDifficultyMap.get(difficulty));
        }
    }

    @Override
    public void stop() {
        while (_players.size() > 0) {
            Player player = _players.remove(0);

            disconnectPlayer(player);
        }
    }

    @Override
    public void startGame() {
        clearScores();

        initRounds();

        _roundIndex = 0;

        startRound();
    }

    private void initRounds() {
        _rounds = new ArrayList<>();

        for (int difficulty : LevelDifficulty.DIFFICULTIES) {
            List<LevelVO> levels = _levelDifficultyMap.get(difficulty);
            int index = (int)(Math.floor(levels.size() * Math.random()));
            _rounds.add(levels.get(index).id);
        }

        Log.d(TAG, "initRounds: " + _rounds);
    }

    public void startRound() {
        Long currentLevel = _rounds.get(_roundIndex);

        for (Player player : _players) {
            player.setPlayerState(PlayerState.PLAYING);
        }

        GoogleFlipGameApplication.getUserModel().selectLevelById(currentLevel);
        broadcastMessage(new S2CRoundStartedMessage(currentLevel, _roundIndex));
    }

    private void broadcastMessage(GameMessage gameMessage) {
        String message = GameMessageConverter.writeMessage(gameMessage);

        for (Player player : _players) {
            player.getConnection().writeMessage(message);
        }
    }
}
