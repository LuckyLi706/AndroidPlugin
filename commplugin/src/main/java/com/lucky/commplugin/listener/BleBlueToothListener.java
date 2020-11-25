package com.lucky.commplugin.listener;

public interface BleBlueToothListener {

    void connect(boolean isSuccess);

    void readBleData(byte[] b);
}
