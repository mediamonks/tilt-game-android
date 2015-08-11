package com.mediamonks.googleflip.net.common;

/**
 * Created by stephan on 10-6-2015.
 */
public interface DeviceChangeListener {
    void onDeviceAdded(String deviceName, String deviceAddress);

    void onDeviceRemoved(String deviceName, String deviceAddress);

    void onDeviceConnected(String deviceName, String deviceAddress);

    void onConnectFailed (String deviceName, String deviceAddress);
}
