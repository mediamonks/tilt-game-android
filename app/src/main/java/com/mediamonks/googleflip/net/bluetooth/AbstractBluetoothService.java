/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mediamonks.googleflip.net.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mediamonks.googleflip.BuildConfig;
import com.mediamonks.googleflip.net.common.ServiceMessageHandler;
import com.mediamonks.googleflip.net.common.ServiceMessageKeys;
import com.mediamonks.googleflip.net.common.ServiceMessageType;

import java.util.UUID;

public abstract class AbstractBluetoothService {
    private static final String TAG = AbstractBluetoothService.class.getSimpleName();

    // Constants that indicate the current connection state
    public static final int STATE_IDLE = 0;       // idle
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    // Name for the SDP record when creating server socket
    protected static final String NAME_SECURE = BuildConfig.APPLICATION_ID + "Secure";
    protected static final String NAME_INSECURE = BuildConfig.APPLICATION_ID + "Insecure";

    // Unique UUID for this application
    protected static final UUID MY_UUID_SECURE = UUID.fromString("39455500-fb09-11e4-b939-0800200c9a66");
    protected static final UUID MY_UUID_INSECURE = UUID.fromString("50242980-fd6c-11e4-b939-0800200c9a66");

    // Member fields
    protected final BluetoothAdapter _adapter;
    protected final Handler _handler;
    protected int _state = STATE_IDLE;

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
        Log.d(TAG, "setState() " + _state + " -> " + state);

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
        BluetoothCommunicationThread communicationThread = new BluetoothCommunicationThread(socket, device.getAddress(), _handler);
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
}
