package com.lucky.commplugin.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.lucky.commplugin.Constants;
import com.lucky.commplugin.listener.ClassBlueListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ClassicClient {

    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    /**
     * socket连接
     *
     * @throws IOException
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

    /**
     * 关闭socket连接
     */
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

    public void sendBuffer(String b) {
        try {
            outputStream.write(b.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ClassicBlueOutput classicBlueOutput;

    public void receiverBlue(ClassBlueListener classBlueListener) {
        classicBlueOutput = new ClassicBlueOutput(inputStream, classBlueListener);
        classicBlueOutput.start();
        Executors.newSingleThreadExecutor().submit(classicBlueOutput);
    }
}
