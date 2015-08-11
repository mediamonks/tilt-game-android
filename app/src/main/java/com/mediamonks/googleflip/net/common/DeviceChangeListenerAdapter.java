package com.mediamonks.googleflip.net.common;

/**
 * Created by stephan on 10-6-2015.
 */
public abstract class DeviceChangeListenerAdapter implements DeviceChangeListener {

    @Override
    public void onDeviceAdded(String deviceName, String address) {
    }

    @Override
    public void onDeviceRemoved(String deviceName, String address) {
    }

    @Override
    public void onDeviceConnected(String deviceName, String address) {
    }

    @Override
    public void onConnectFailed(String deviceName, String deviceAddress) {
    }
}
