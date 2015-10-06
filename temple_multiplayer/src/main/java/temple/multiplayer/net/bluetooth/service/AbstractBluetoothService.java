package temple.multiplayer.net.bluetooth.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.UUID;

import temple.multiplayer.net.bluetooth.device.BluetoothCommunicationThread;
import temple.multiplayer.net.common.service.ServiceMessageHandler;
import temple.multiplayer.net.common.service.ServiceMessageKeys;
import temple.multiplayer.net.common.service.ServiceMessageType;

public abstract class AbstractBluetoothService {
    private static final String TAG = AbstractBluetoothService.class.getSimpleName();

    // Constants that indicate the current connection state
    public static final int STATE_IDLE = 0;       // idle
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    protected final BluetoothAdapter _adapter;
    protected final Handler _handler;
    protected int _state = STATE_IDLE;
    protected UUID _secureUuid;
    protected UUID _insecureUuid;
    protected String _secureSPDName;
    protected String _insecureSPDName;
    protected boolean _debug;

    /**
     * @param handler A Handler to send messages back to the UI Activity
     */
    public AbstractBluetoothService(Handler handler) {
        _handler = handler;

        _adapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Set the current state of the connection
     *
     * @param state An integer defining the current connection state
     */
    protected synchronized void setState(int state) {
        if (_debug) Log.d(TAG, "setState() " + _state + " -> " + state);

        _state = state;

        // Give the new state to the Handler so the UI Activity can update
        _handler.obtainMessage(ServiceMessageType.MESSAGE_STATE_CHANGE.ordinal(), state, -1).sendToTarget();
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return _state;
    }

    /**
     * Start the service. Specifically start AcceptThread to begin a
     * session in listening (server) mode.
     */
    public synchronized void start() {
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        setState(STATE_IDLE);
    }

    public abstract void write(String deviceAddress, byte[] out);

    protected BluetoothCommunicationThread createCommunicationThread(BluetoothSocket socket, final BluetoothDevice device) {
        BluetoothCommunicationThread communicationThread = new BluetoothCommunicationThread(socket, device.getAddress(), _handler, _debug);
        communicationThread.setDebug(_debug);
        communicationThread.setConnectionLostListener(new BluetoothCommunicationThread.ConnectionLostListener() {
            @Override
            public void onConnectionLost() {
                connectionLost(device);
            }
        });
        communicationThread.start();

        return communicationThread;
    }

    protected void cancelCommunicationThread(BluetoothCommunicationThread communicationThread) {
        if (communicationThread != null) {
            communicationThread.cancel();
            communicationThread.setConnectionLostListener(null);
        }
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     *
     * @param device the device for which the connection was lost
     */
    protected void connectionLost(BluetoothDevice device) {
        Bundle bundle = new Bundle();
        bundle.putString(ServiceMessageKeys.DEVICE_NAME, device.getName());
        bundle.putString(ServiceMessageKeys.DEVICE_ADDRESS, device.getAddress());
        sendMessage(ServiceMessageType.MESSAGE_DEVICE_REMOVED, bundle);
    }

    protected void sendMessage(ServiceMessageType messageType, Bundle bundle) {
        Message message = _handler.obtainMessage(messageType.ordinal());
        message.setData(bundle);
        _handler.sendMessage(message);
    }

    protected void sendMessage(ServiceMessageType messageType) {
        _handler.sendMessage(_handler.obtainMessage(messageType.ordinal()));
    }

    public ServiceMessageHandler getHandler() {
        return (ServiceMessageHandler) _handler;
    }

    public void setSecureUuid(String uuid) {
        _secureUuid = UUID.fromString(uuid);
    }

    public void setInsecureUuid(String uuid) {
        _insecureUuid = UUID.fromString(uuid);
    }

    public void setApplicationId(String id) {
        _secureSPDName = id + "Secure";
        _insecureSPDName = id + "Insecure";
    }

    public void setDebug (boolean debug) {
        _debug = debug;
    }
}
