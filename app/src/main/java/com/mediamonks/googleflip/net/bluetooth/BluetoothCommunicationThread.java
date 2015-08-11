package com.mediamonks.googleflip.net.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mediamonks.googleflip.net.common.ServiceMessageKeys;
import com.mediamonks.googleflip.net.common.ServiceMessageType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This thread runs during a connection with a remote device.
 * It handles all incoming and outgoing transmissions.
 */
class BluetoothCommunicationThread extends Thread {
    private static final String TAG = BluetoothCommunicationThread.class.getSimpleName();

    private final BluetoothSocket _socket;
    private final InputStream _inputStream;
    private final OutputStream _outputStream;
    private String _deviceAddress;
    private final Handler _handler;
    private ConnectionLostListener _connectionLostListener;

    public BluetoothCommunicationThread(BluetoothSocket socket, String deviceAddress, Handler handler) {
        Log.d(TAG, "BluetoothCommunicationThread: created");

        _deviceAddress = deviceAddress;
        _handler = handler;
        _socket = socket;

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

        Log.d(TAG, "run: starting to listen to socket");

        // Keep listening to the InputStream while connected
        while (true) {
            try {
                // Read from the InputStream
                byteCount = _inputStream.read(buffer);
                Log.d(TAG, "run: " + byteCount + " bytes received");

                String messages = new String(buffer, 0, byteCount);
                String[] messageParts = messages.split("\\n");
                for (String messagePart : messageParts) {
                    if (messagePart.length() > 0) {
                        // Send the obtained bytes to the UI Activity
                        Message message = _handler.obtainMessage(ServiceMessageType.MESSAGE_READ.ordinal(), messagePart.length(), -1, messagePart.getBytes());
                        Bundle bundle = new Bundle();
                        bundle.putString(ServiceMessageKeys.DEVICE_ADDRESS, _deviceAddress);
                        message.setData(bundle);
                        message.sendToTarget();
                    }
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
        Log.d(TAG, "write: " + buffer.length + " bytes to write");

        try {
            _outputStream.write(buffer);
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

    public interface ConnectionLostListener {
        void onConnectionLost ();
    }

    public void setConnectionLostListener (ConnectionLostListener listener) {
        _connectionLostListener = listener;
    }
}
