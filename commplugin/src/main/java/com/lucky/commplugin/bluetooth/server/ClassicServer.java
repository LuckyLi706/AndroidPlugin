package com.lucky.commplugin.bluetooth.server;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.lucky.commplugin.bluetooth.BluetoothManager;

import java.io.InputStream;
import java.io.OutputStream;

public class ClassicServer extends BluetoothManager {

    private static final ClassicServer client=new ClassicServer();

    private ClassicServer(){

    }

    public static ClassicServer getInstance(){
        return client;
    }

    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    @Override
    public void read() {

    }

    @Override
    public void write(String data) {

    }

    @Override
    public void write(byte[] b) {

    }

    @Override
    public void connect(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void accept() {

    }
}
