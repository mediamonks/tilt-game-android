package com.mediamonks.googleflip.net.common;

/**
 * Created by stephan on 19-5-2015.
 */
public abstract class AbstractConnection implements Connection{
    protected MessageHandler _messageHandler;
    protected ConnectionHandler _connectionHandler;

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
}
