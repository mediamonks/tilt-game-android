package temple.multiplayer.net.common.connection;

/**
 * Created by stephan on 19-5-2015.
 */
public abstract class AbstractConnection implements Connection{
    protected MessageHandler _messageHandler;
    protected ConnectionHandler _connectionHandler;
    protected boolean _debug;

    @Override
    public void setMessageHandler(MessageHandler messageHandler) {
        _messageHandler = messageHandler;
    }

    @Override
    public void disconnect() {
        setMessageHandler(null);
    }

    @Override
    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        _connectionHandler = connectionHandler;
    }

    public void setDebug (boolean debug) {
        _debug = debug;
    }
}
