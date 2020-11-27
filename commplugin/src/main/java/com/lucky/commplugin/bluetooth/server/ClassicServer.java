package com.lucky.commplugin.bluetooth.server;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
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

public class ClassicServer extends BluetoothManager {

    @SuppressLint("StaticFieldLeak")
    private static final ClassicServer server = new ClassicServer();

    private ClassicServer() {

    }

    public static ClassicServer getInstance() {
        return server;
    }

    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean isRunning;
    private ClassBlueListener classBlueListener;
    private BluetoothSocket bluetoothSocket;

    @Override
    public void read(ClassBlueListener classBlueListener) {
        this.classBlueListener = classBlueListener;
    }

    @Override
    public void write(String data) {
        try {
            if (outputStream != null) {
                outputStream.write(HexDump.hexStringToByteArray(data));
            }
        } catch (IOException e) {
            LogUtil.e(this, e.getMessage());
        }
    }

    @Override
    public void write(byte[] b) {
        try {
            if (outputStream != null) {
                outputStream.write(b);
            }
        } catch (IOException e) {
            LogUtil.e(this, e.getMessage());
        }
    }

    @Override
    public void connect(BluetoothDevice bluetoothDevice) {
        LogUtil.d("server 请调用accept方法");
    }

    @Override
    public void accept() {
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
        }
        acceptThread.start();
    }

    @Override
    public void close() {
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private AcceptThread acceptThread;

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("blue", UUID.fromString(Constants.CLASSIC_BLUE_UUID));
            } catch (IOException e) {
                LogUtil.e(this, "Socket's listen() method failed" + e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                    LogUtil.d("客户端连接了");
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                } catch (IOException e) {
                    LogUtil.e(this, "Socket's accept() method failed" + e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    manageMyConnectedSocket(socket);
                    //mmServerSocket.close();
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                LogUtil.e(this, "Could not close the connect socket" + e);
            }
        }
    }

    private ClassicBlueOutput classicBlueOutput;

    private void manageMyConnectedSocket(BluetoothSocket socket) {
        bluetoothSocket = socket;
        if (classicBlueOutput == null) {
            classicBlueOutput = new ClassicBlueOutput(inputStream, classBlueListener);
        }
        classicBlueOutput.start();
        Executors.newSingleThreadExecutor().submit(classicBlueOutput);
    }
}
