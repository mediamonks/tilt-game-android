package temple.multiplayer.net.common.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import temple.multiplayer.net.common.device.DeviceChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephan on 18-5-2015.
 */
public class ServiceMessageHandler extends Handler{
    private static final String TAG = ServiceMessageHandler.class.getSimpleName();

    private StatusChangedListener _statusChangedListener;
    private List<MessageListener> _messageListeners;
    private List<DeviceChangeListener> _deviceChangeListeners;

    @Override
    public void handleMessage(Message msg) {
        ServiceMessageType messageType = ServiceMessageType.values()[msg.what];
        switch (messageType) {
            case MESSAGE_STATE_CHANGE:
                if (_statusChangedListener != null) {
                    _statusChangedListener.onStatusChanged(msg.arg1);
                }
                break;
            case MESSAGE_CONNECT_FAILED:
                if (_deviceChangeListeners != null) {
                    List<DeviceChangeListener> listeners = new ArrayList<>();
                    listeners.addAll(_deviceChangeListeners);

                    String deviceName = msg.getData().getString(ServiceMessageKeys.DEVICE_NAME);
                    String deviceAddress = msg.getData().getString(ServiceMessageKeys.DEVICE_ADDRESS);
                    for (DeviceChangeListener listener : listeners) {
                        listener.onConnectFailed(deviceName, deviceAddress);
                    }
                }
                break;
            case MESSAGE_DEVICE_ADDED:
                if (_deviceChangeListeners != null) {
                    List<DeviceChangeListener> listeners = new ArrayList<>();
                    listeners.addAll(_deviceChangeListeners);

                    String deviceName = msg.getData().getString(ServiceMessageKeys.DEVICE_NAME);
                    String deviceAddress = msg.getData().getString(ServiceMessageKeys.DEVICE_ADDRESS);
                    for (DeviceChangeListener listener : listeners) {
                        listener.onDeviceAdded(deviceName, deviceAddress);
                    }
                }
                break;
            case MESSAGE_DEVICE_REMOVED:
                if (_deviceChangeListeners != null) {
                    List<DeviceChangeListener> listeners = new ArrayList<>();
                    listeners.addAll(_deviceChangeListeners);

                    String deviceName = msg.getData().getString(ServiceMessageKeys.DEVICE_NAME);
                    String deviceAddress = msg.getData().getString(ServiceMessageKeys.DEVICE_ADDRESS);
                    for (DeviceChangeListener listener : listeners) {
                        listener.onDeviceRemoved(deviceName, deviceAddress);
                    }
                }
                break;
            case MESSAGE_DEVICE_CONNECTED:
                if (_deviceChangeListeners != null) {
                    String deviceName = msg.getData().getString(ServiceMessageKeys.DEVICE_NAME);
                    String deviceAddress = msg.getData().getString(ServiceMessageKeys.DEVICE_ADDRESS);
                    for (DeviceChangeListener listener : _deviceChangeListeners) {
                        listener.onDeviceConnected(deviceName, deviceAddress);
                    }
                }
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);

                Bundle bundle = msg.getData();
                String deviceAddress = bundle.getString(ServiceMessageKeys.DEVICE_ADDRESS);

                if (_messageListeners != null) {
                    for (MessageListener listener : _messageListeners) {
                        listener.onMessageReceived(readMessage, deviceAddress);
                    }
                }
                break;
        }
    }

    public interface StatusChangedListener {
        void onStatusChanged(int status);
    }

    public void setStatusChangedListener(StatusChangedListener listener) {
        _statusChangedListener = listener;
    }

    public interface MessageListener {
        void onMessageReceived(String message, String deviceAddress);
    }

    public void addMessageListener(MessageListener listener) {
        if (_messageListeners == null) {
            _messageListeners = new ArrayList<>();
        }
        _messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        _messageListeners.remove(listener);
    }

    public void addDeviceChangeListener(DeviceChangeListener listener) {
        if (_deviceChangeListeners == null) {
            _deviceChangeListeners = new ArrayList<>();
        }
        _deviceChangeListeners.add(listener);
    }

    public void removeDeviceChangeListener (DeviceChangeListener listener) {
        _deviceChangeListeners.remove(listener);
    }

}
