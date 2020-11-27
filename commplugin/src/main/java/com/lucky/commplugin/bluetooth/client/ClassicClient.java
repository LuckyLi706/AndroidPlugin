package com.lucky.commplugin.bluetooth.client;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.lucky.commplugin.Constants;
import com.lucky.commplugin.bluetooth.BluetoothManager;
import com.lucky.commplugin.bluetooth.ClassicBlueOutput;
import com.lucky.commplugin.listener.ClassBlueListener;
import com.lucky.commplugin.utils.HexDump;
import com.lucky.commplugin.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ClassicClient extends BluetoothManager {

    @SuppressLint("StaticFieldLeak")
    private static final ClassicClient client = new ClassicClient();

    private ClassicClient() {

    }

    public static ClassicClient getInstance() {
        return client;
    }

    private ClassicBlueOutput classicBlueOutput;


    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    @Override
    public void read(ClassBlueListener classBlueListener) {
        if (classicBlueOutput == null) {
            classicBlueOutput = new ClassicBlueOutput(inputStream, classBlueListener);
        }
        classicBlueOutput.start();
        Executors.newSingleThreadExecutor().submit(classicBlueOutput);
    }

    @Override
    public void write(String data) {
        try {
            outputStream.write(HexDump.hexStringToByteArray(data));
        } catch (IOException e) {
            LogUtil.e(this, e.getMessage());
        }
    }

    @Override
    public void write(byte[] b) {
        try {
            outputStream.write(b);
        } catch (IOException e) {
            LogUtil.e(this, e.getMessage());
        }
    }

    /**
     * socket连接
     */
    public void connect(BluetoothDevice bluetoothDevice) throws Exception {
        if (bluetoothDevice == null) {
            return;
        }
        socket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(Constants.CLASSIC_BLUE_UUID));
        if (socket != null) {
            // 连接
            if (!socket.isConnected()) {
                socket.connect();
            }
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        }
    }

    @Override
    public void accept() {
        LogUtil.d("client 请调用connect方法");
    }

    @Override
    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (classicBlueOutput != null) {
                classicBlueOutput.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
