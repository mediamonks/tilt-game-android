package temple.multiplayer.net.bluetooth.device;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import temple.multiplayer.net.common.service.ServiceMessageKeys;
import temple.multiplayer.net.common.service.ServiceMessageType;

/**
 * This thread runs during a connection with a remote device.
 * It handles all incoming and outgoing transmissions.
 */
public class BluetoothCommunicationThread extends Thread {
    private static final String TAG = BluetoothCommunicationThread.class.getSimpleName();

    private final BluetoothSocket _socket;
    private final InputStream _inputStream;
    private final OutputStream _outputStream;
    private String _deviceAddress;
    private final Handler _handler;
    private ConnectionLostListener _connectionLostListener;
    private boolean _debug;
    private String _splitter;

    public BluetoothCommunicationThread(BluetoothSocket socket, String deviceAddress, Handler handler) {
        this(socket, deviceAddress, handler, false);
    }

    public BluetoothCommunicationThread(BluetoothSocket socket, String deviceAddress, Handler handler, boolean debug) {
        _debug = debug;

        if (_debug) Log.d(TAG, "BluetoothCommunicationThread: created");

        _deviceAddress = deviceAddress;
        _handler = handler;
        _socket = socket;

        byte[] splitChar = {0};
        _splitter = new String(splitChar, 0, 1);

        InputStream inputStream = null;
        OutputStream outputStream = null;

        // Get the BluetoothSocket input and output streams
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "BluetoothCommunicationThread: streams not created");
            e.printStackTrace();
        }

        _inputStream = inputStream;
        _outputStream = outputStream;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int byteCount;
        String messages = "";
        boolean eof;

        if (_debug) Log.d(TAG, "run: starting to listen to socket");

        // Keep listening to the InputStream while connected
        while (true) {
            try {
                if (_debug) Log.d(TAG, "run: starting to read, messages = '" + messages + "'");

                // Read from the InputStream
                byteCount = _inputStream.read(buffer);
                eof = (buffer[byteCount - 1] == 0);
                if (_debug) Log.d(TAG, "run: first read, " + byteCount + " bytes received, last char = " + buffer[byteCount - 1] + ", eof = " + eof);

                int size = eof ? byteCount - 1 : byteCount;
                if (size > 0) {
                    messages += new String(buffer, 0, eof ? byteCount - 1 : byteCount);
                }
                if (_debug) Log.d(TAG, "run: first: messages = " + messages);

                if (eof) {
                    if (_debug) Log.d(TAG, "run: eof, sending messages");

                    String[] messageParts = messages.split(_splitter);
                    if (_debug) Log.d(TAG, "run: " + messageParts.length + " message parts found");

                    for (String messagePart : messageParts) {
                        if (messagePart.length() > 0) {
                            if (_debug) Log.d(TAG, "run: sending message: " + messagePart);

                            // Send the obtained bytes to the UI Activity
                            Message message = _handler.obtainMessage(ServiceMessageType.MESSAGE_READ.ordinal(), messagePart.length(), -1, messagePart.getBytes());
                            Bundle bundle = new Bundle();
                            bundle.putString(ServiceMessageKeys.DEVICE_ADDRESS, _deviceAddress);
                            message.setData(bundle);
                            message.sendToTarget();
                        }
                    }

                    if (_debug) Log.d(TAG, "run: clearing messages");
                    messages = "";
                }
            } catch (IOException e) {
                Log.e(TAG, "run: failed to read, disconnected");

                if (_connectionLostListener != null) {
                    _connectionLostListener.onConnectionLost();
                }
                break;
            }
        }
    }

    /**
     * Write to the connected OutStream.
     *
     * @param buffer The bytes to write
     */
    public void write(byte[] buffer) {
        if (_debug) Log.d(TAG, "write: " + buffer.length + " bytes to write");

        try {
            _outputStream.write(buffer);
            _outputStream.write(0);

        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }

    public void cancel() {
        try {
            _socket.close();
        } catch (IOException e) {
            Log.e(TAG, "cancel: close failed");
            e.printStackTrace();
        }
    }

    public void setDebug(boolean debug) {
        _debug = debug;
    }

    public interface ConnectionLostListener {
        void onConnectionLost();
    }

    public void setConnectionLostListener(ConnectionLostListener listener) {
        _connectionLostListener = listener;
    }
}
