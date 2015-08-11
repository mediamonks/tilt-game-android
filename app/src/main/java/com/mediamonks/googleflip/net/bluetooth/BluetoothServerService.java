package com.mediamonks.googleflip.net.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.mediamonks.googleflip.net.common.ServiceMessageKeys;
import com.mediamonks.googleflip.net.common.ServiceMessageType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stephan on 22-5-2015.
 */
public class BluetoothServerService extends AbstractBluetoothService {
    private static final String TAG = BluetoothServerService.class.getSimpleName();

    private AcceptThread _secureAcceptThread;
    private AcceptThread _insecureAcceptThread;
    private Map<String, BluetoothCommunicationThread> _communicationThreads = new HashMap<>();
    private Boolean isStopping = false;

    /**
     * Constructor. Prepares a new BluetoothChat session.
     *
     * @param handler A Handler to send messages back to the UI Activity
     */
    public BluetoothServerService(Handler handler) {
        super(handler);
    }

    @Override
    public synchronized void start() {
        super.start();

        // Start the thread to listen on a BluetoothServerSocket
        if (_secureAcceptThread == null) {
            _secureAcceptThread = new AcceptThread(true);
            _secureAcceptThread.start();
        }
        if (_insecureAcceptThread == null) {
            _insecureAcceptThread = new AcceptThread(false);
            _insecureAcceptThread.start();
        }

        setState(STATE_LISTEN);
    }

    @Override
    public synchronized void stop() {
        super.stop();

        stopListening();

        cancelCommunicationThreads();
    }

    public synchronized void stopListening () {
        if (_secureAcceptThread != null) {
            _secureAcceptThread.cancel();
            _secureAcceptThread = null;
        }

        if (_insecureAcceptThread != null) {
            _insecureAcceptThread.cancel();
            _insecureAcceptThread = null;
        }
    }

    public void write(String deviceAddress, byte[] out) {
        // Create temporary object
        BluetoothCommunicationThread communicationThread;

        // Synchronize a copy of all communcation threads
        synchronized (this) {
            communicationThread = _communicationThreads.get(deviceAddress);
        }
        // Perform the write unsynchronized
        if(communicationThread != null) {
            communicationThread.write(out);
        }
    }

    private synchronized void accepted(BluetoothSocket socket, BluetoothDevice device, String socketType) {
        Log.d(TAG, "accepted, Socket Type:" + socketType);

        // Start the thread to manage the connection and perform transmissions
        createCommunicationThread(socket, device);

        // Send the name of the connected device back to the UI Activity
        Bundle bundle = new Bundle();
        bundle.putString(ServiceMessageKeys.DEVICE_NAME, device.getName());
        bundle.putString(ServiceMessageKeys.DEVICE_ADDRESS, device.getAddress());
        sendMessage(ServiceMessageType.MESSAGE_DEVICE_ADDED, bundle);
    }

    @Override
    protected void connectionLost(BluetoothDevice device) {
        if(isStopping) return;

        super.connectionLost(device);

		BluetoothCommunicationThread communicationThread = _communicationThreads.get(device.getAddress());
		if (communicationThread != null) {
			cancelCommunicationThread(communicationThread);

			_communicationThreads.remove(device.getAddress());
		}
	}

    protected void cancelCommunicationThreads() {
        isStopping = true;

        for (Map.Entry<String, BluetoothCommunicationThread> entry : _communicationThreads.entrySet()) {
            BluetoothCommunicationThread communicationThread = entry.getValue();

            cancelCommunicationThread(communicationThread);
        }

        _communicationThreads.clear();

        isStopping = false;
    }

    @Override
    protected BluetoothCommunicationThread createCommunicationThread(BluetoothSocket socket, BluetoothDevice device) {
        BluetoothCommunicationThread communicationThread = super.createCommunicationThread(socket, device);

        _communicationThreads.put(device.getAddress(), communicationThread);

        return communicationThread;
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket _serverSocket;
        private final String _socketType;
        private boolean _isListening;

        public AcceptThread(boolean secure) {
            _socketType = secure ? "Secure" : "Insecure";

            // Create a new listening server socket
            BluetoothServerSocket serverSocket = null;
            try {
                if (secure) {
                    serverSocket = _adapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
                } else {
                    serverSocket = _adapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: listen failed");
                e.printStackTrace();
            }

            _serverSocket = serverSocket;

            _isListening = true;
        }

        public void run() {
            Log.i(TAG, "run: START AcceptThread " + this);

            setName("AcceptThread " + _socketType);

            BluetoothSocket socket = null;

            // Listen to the server socket
            while (_isListening) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = _serverSocket.accept();
                } catch (IOException e) {
                    if (_isListening) {
                        Log.e(TAG, "run: accept failed");
                        e.printStackTrace();
                    }
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothServerService.this) {
                        switch (_state) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                accepted(socket, socket.getRemoteDevice(), _socketType);
                                break;
                            case STATE_IDLE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }

            Log.i(TAG, "run: END " + this);
        }

        public void cancel() {
            Log.d(TAG, "cancel: " + this);

            _isListening = false;

            try {
                _serverSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close failed");
                e.printStackTrace();
            }
        }
    }
}
