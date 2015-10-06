package temple.multiplayer.net.common.connection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephan on 19-5-2015.
 */
public class LoopBackConnection extends AbstractConnection implements Connection {

    private List<MessageHandler> _messageHandlers = new ArrayList<>();

    @Override
    public void setMessageHandler(MessageHandler messageHandler) {
        if (messageHandler != null) {
            _messageHandlers.add(messageHandler);
        }
    }

    @Override
    public void writeMessage(String message) {
        for (MessageHandler handler : _messageHandlers) {
            handler.onMessageReceived(message);
        }
    }

    @Override
    public void setDeviceAddress(String deviceAddress) {

    }

    @Override
    public String getDeviceAddress() {
        return "local device";
    }
}
