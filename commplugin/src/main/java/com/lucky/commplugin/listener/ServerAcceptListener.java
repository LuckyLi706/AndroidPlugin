package com.lucky.commplugin.listener;

import android.bluetooth.BluetoothSocket;

public interface ServerAcceptListener {

    public void connectSuccess(Object object);

    public void connectFail(Exception e);
}
