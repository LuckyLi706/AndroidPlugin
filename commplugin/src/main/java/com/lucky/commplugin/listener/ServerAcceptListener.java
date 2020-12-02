package com.lucky.commplugin.listener;

import android.bluetooth.BluetoothSocket;

public interface ServerAcceptListener {

    public void connectSuccess(BluetoothSocket bluetoothSocket);

    public void connectFail(Exception e);
}
