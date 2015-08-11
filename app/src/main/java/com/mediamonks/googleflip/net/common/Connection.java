package com.mediamonks.googleflip.net.common;

/**
 * Created by stephan on 19-5-2015.
 */
public interface Connection {
    void setMessageHandler(MessageHandler messageHandler);

    void setConnectionHandler (ConnectionHandler connectionHandler);

    void writeMessage (String message);

    void setDeviceAddress (String deviceAddress);

    String getDeviceAddress ();

    void disconnect ();

    interface MessageHandler {
        void onMessageReceived(String message);
    }

    interface ConnectionHandler {
        void onConnectionLost ();
    }
}
