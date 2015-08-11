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

package com.mediamonks.googleflip.net.common;

import com.mediamonks.googleflip.net.bluetooth.AbstractBluetoothService;

/**
 * Defines several constants used between {@link AbstractBluetoothService} and the UI.
 */
public enum ServiceMessageType {

    // Message types sent from the BluetoothService Handler
    MESSAGE_STATE_CHANGE,
    MESSAGE_READ,
    MESSAGE_DEVICE_ADDED,
    MESSAGE_DEVICE_CONNECTED,
    MESSAGE_DEVICE_REMOVED,
    MESSAGE_CONNECT_FAILED;
}
