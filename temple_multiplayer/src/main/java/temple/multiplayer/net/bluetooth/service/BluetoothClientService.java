package temple.multiplayer.net.bluetooth.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import temple.multiplayer.net.bluetooth.device.BluetoothCommunicationThread;
import temple.multiplayer.net.common.service.ServiceMessageKeys;
import temple.multiplayer.net.common.service.ServiceMessageType;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by stephan on 22-5-2015.
 */
public class BluetoothClientService extends AbstractBluetoothService {
    private static final String TAG = BluetoothClientService.class.getSimpleName();

    private ConnectThread _connectThread;
    private BluetoothCommunicationThread _communicationThread;


    /**
     * Constructor. Prepares a new BluetoothChat session.
     *
     * @param handler A Handler to send messages back to the UI Activity
     */
    public BluetoothClientService(Handler handler) {
        super(handler);
    }

    @Override
    public synchronized void start() {
        super.start();

        cancelConnectThread();
    }

    @Override
    public synchronized void stop() {
        if (_debug) Log.d(TAG, "stop");
        super.stop();

        cancelConnectThread();
        cancelCommunicationThread(_communicationThread);
    }

    public boolean acceptServerUUID (UUID uuid) {
        return uuid.equals(_insecureUuid) || uuid.equals(_secureUuid);
    }

    @Override
    public void write(String deviceAddress, byte[] out) {
        if (_debug) Log.d(TAG, "write: " + out.length + " bytes to write");

        // Create temporary object
        BluetoothCommunicationThread communicationThread;

        // Synchronize a copy of the communcation thread
        synchronized (this) {
            if (_state != STATE_CONNECTED) return;
            communicationThread = _communicationThread;
        }
        // Perform the write unsynchronized
        communicationThread.write(out);
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        if (_debug) Log.d(TAG, "connect to: " + device.getName());

        // Cancel any thread attempting to make a connection
        if (_state == STATE_CONNECTING) {
            cancelConnectThread();
        }
        cancelCommunicationThread(_communicationThread);

        // Start the thread to connect with the given device
        _connectThread = new ConnectThread(device, secure);
        _connectThread.start();

        setState(STATE_CONNECTING);
    }

    private void cancelConnectThread() {
        if (_connectThread != null) {
            _connectThread.cancel();
        }
        _connectThread = null;
    }

    @Override
    protected void cancelCommunicationThread(BluetoothCommunicationThread communicationThread) {
        super.cancelCommunicationThread(communicationThread);

        _communicationThread = null;
    }

    /**
     * Start the CommunicationThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
        if (_debug) Log.d(TAG, "connected: ");

        cancelConnectThread();
        cancelCommunicationThread(_communicationThread);

        _communicationThread = createCommunicationThread(socket, device);

        // Send the name of the connected device back to the UI Activity
        Bundle bundle = new Bundle();
        bundle.putString(ServiceMessageKeys.DEVICE_NAME, device.getName());
        bundle.putString(ServiceMessageKeys.DEVICE_ADDRESS, device.getAddress());
        sendMessage(ServiceMessageType.MESSAGE_DEVICE_CONNECTED, bundle);

        setState(STATE_CONNECTED);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed(BluetoothDevice device) {
        // Send a failure message back to the Activity
        Bundle bundle = new Bundle();
        bundle.putString(ServiceMessageKeys.DEVICE_NAME, device.getName());
        bundle.putString(ServiceMessageKeys.DEVICE_ADDRESS, device.getAddress());
        sendMessage(ServiceMessageType.MESSAGE_CONNECT_FAILED);

        if (_debug) Log.d(TAG, "connectionFailed: restarting");

        setState(STATE_IDLE);

        // Start the service over to restart listening mode
        start();
    }

    @Override
    protected void connectionLost(BluetoothDevice device) {
        super.connectionLost(device);

        if (_debug) Log.d(TAG, "connectionLost: ");

        stop();
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket _socket;
        private final BluetoothDevice _device;
        private String _socketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            _device = device;
            _socketType = secure ? "Secure" : "Insecure";

            BluetoothSocket socket = null;
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (secure) {
                    socket = device.createRfcommSocketToServiceRecord(_secureUuid);
                } else {
                    socket = device.createInsecureRfcommSocketToServiceRecord(_insecureUuid);
                }
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: create failed");
                e.printStackTrace();
            }
            _socket = socket;
        }

        public void run() {
            Log.i(TAG, "run: _connectThread SocketType:" + _socketType);

            setName("ConnectThread " + _socketType);

            // Always cancel discovery because it will slow down a connection
            _adapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                _socket.connect();
            } catch (IOException e) {
                Log.e(TAG, "run: failed to connect");
                e.printStackTrace();

                // Close the socket
                try {
                    _socket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "run: unable to close socket ");
                    e.printStackTrace();
                }
                connectionFailed(_device);
                return;
            }

            if (_debug) Log.d(TAG, "run: socket connected");

            // Clear the ConnectThread because we're done
            synchronized (BluetoothClientService.this) {
                _connectThread = null;
            }

            // Start the connected thread
            connected(_socket, _device, _socketType);
        }

        public void cancel() {
            try {
                _socket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close failed");
                e.printStackTrace();
            }
        }
    }
}
