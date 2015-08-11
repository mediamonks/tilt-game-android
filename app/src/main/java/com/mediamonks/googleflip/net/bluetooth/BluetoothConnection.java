package com.mediamonks.googleflip.net.bluetooth;

import android.util.Log;

import com.mediamonks.googleflip.net.common.AbstractConnection;
import com.mediamonks.googleflip.net.common.Connection;
import com.mediamonks.googleflip.net.common.DeviceChangeListener;
import com.mediamonks.googleflip.net.common.ServiceMessageHandler;

/**
 * Created by stephan on 19-5-2015.
 */
public class BluetoothConnection extends AbstractConnection implements Connection, ServiceMessageHandler.MessageListener, DeviceChangeListener {
    private static final String TAG = BluetoothConnection.class.getSimpleName();

    private AbstractBluetoothService _service;
    private String _deviceAddress;

    public BluetoothConnection(AbstractBluetoothService service, String deviceAddress) {
        Log.d(TAG, "BluetoothConnection: created");

        _service = service;
        _deviceAddress = deviceAddress;

        _service.getHandler().addMessageListener(this);
        _service.getHandler().addDeviceChangeListener(this);
    }

    @Override
    public void writeMessage(String message) {
        Log.d(TAG, "writeMessage: " + message);

        _service.write(_deviceAddress, message.getBytes());
    }

    @Override
    public void setDeviceAddress(String deviceAddress) {
        _deviceAddress = deviceAddress;
    }

    @Override
    public String getDeviceAddress() {
        return _deviceAddress;
    }

    /**
     * Handle messages from BluetoothMessageHandler in BluetootService
     * @param message message received from outside
     */
    @Override
    public void onMessageReceived(String message, String deviceAddress) {
        boolean meantForMe = _deviceAddress.equals(deviceAddress);
        Log.d(TAG, "onMessageReceived: message = " + message + ", meantForMe = " + meantForMe + ", messageHandler = " + _messageHandler);

        if (meantForMe && (_messageHandler != null)) {
            _messageHandler.onMessageReceived(message);
        }
    }

    @Override
    public void disconnect() {
        super.disconnect();

        _service.getHandler().removeMessageListener(this);
        _service.getHandler().removeDeviceChangeListener(this);
    }

    @Override
    public void onDeviceAdded(String deviceName, String address) {
    }

    @Override
    public void onDeviceRemoved(String deviceName, String address) {
        if (_deviceAddress.equals(address) && _connectionHandler != null) {
            _connectionHandler.onConnectionLost();
        }
    }

    @Override
    public void onDeviceConnected(String deviceName, String address) {
    }

    @Override
    public void onConnectFailed(String deviceName, String deviceAddress) {
    }
}
