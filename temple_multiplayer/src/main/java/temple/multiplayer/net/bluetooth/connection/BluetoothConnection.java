package temple.multiplayer.net.bluetooth.connection;

import android.util.Log;

import temple.multiplayer.net.bluetooth.service.AbstractBluetoothService;
import temple.multiplayer.net.common.connection.AbstractConnection;
import temple.multiplayer.net.common.connection.Connection;
import temple.multiplayer.net.common.device.DeviceChangeListener;
import temple.multiplayer.net.common.service.ServiceMessageHandler;


/**
 * Created by stephan on 19-5-2015.
 */
public class BluetoothConnection extends AbstractConnection implements Connection, ServiceMessageHandler.MessageListener, DeviceChangeListener {
    private static final String TAG = BluetoothConnection.class.getSimpleName();

    private AbstractBluetoothService _service;
    private String _deviceAddress;

    public BluetoothConnection(AbstractBluetoothService service, String deviceAddress, boolean debug) {
        _debug = debug;

        if (_debug) Log.d(TAG, "BluetoothConnection: created");

        _service = service;
        _deviceAddress = deviceAddress;

        _service.getHandler().addMessageListener(this);
        _service.getHandler().addDeviceChangeListener(this);
    }

    public BluetoothConnection(AbstractBluetoothService service, String deviceAddress) {
        this(service, deviceAddress, false);
    }

    @Override
    public void writeMessage(String message) {
        if (_debug) Log.d(TAG, "writeMessage: " + message);

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
        if (_debug) Log.d(TAG, "onMessageReceived: message = " + message + ", meantForMe = " + meantForMe + ", messageHandler = " + _messageHandler);

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
