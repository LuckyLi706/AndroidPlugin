package com.lucky.commplugin.listener;

import android.bluetooth.BluetoothDevice;

public interface BlueScanListener {

    void onScanResult(BluetoothDevice bluetoothDevice);
}
