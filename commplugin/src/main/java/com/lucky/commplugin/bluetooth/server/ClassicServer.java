package com.lucky.commplugin.bluetooth.server;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.lucky.commplugin.Constants;
import com.lucky.commplugin.bluetooth.BluetoothManager;
import com.lucky.commplugin.listener.ClassBlueListener;
import com.lucky.commplugin.listener.ServerAcceptListener;
import com.lucky.commplugin.utils.HexDump;
import com.lucky.commplugin.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

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
    private BluetoothSocket bluetoothSocket;

    @Override
    public void read(ClassBlueListener classBlueListener) {
        if (bluetoothSocket == null || !bluetoothSocket.isConnected()) {
            classBlueListener.readClassicError(new Exception(Constants.BLUE_EXCEPTION_NO_CLIENT_CONNECT));
            return;
        }
        if (readRunnable == null) {
            readRunnable = new ReadRunnable(inputStream, classBlueListener);
        }
        readRunnable.start();
        executorService.submit(readRunnable);
    }

    @Override
    public void write(String data) throws Exception {
        if (bluetoothSocket != null && bluetoothSocket.isConnected() && outputStream != null) {
            outputStream.write(HexDump.hexStringToByteArray(data));
        } else {
            throw new Exception(Constants.BLUE_EXCEPTION_NO_CLIENT_CONNECT);
        }
    }

    @Override
    public void write(byte[] b) throws Exception {
        if (bluetoothSocket != null && bluetoothSocket.isConnected() && outputStream != null) {
            outputStream.write(b);
        } else {
            throw new Exception(Constants.BLUE_EXCEPTION_NO_CLIENT_CONNECT);
        }
    }

    @Override
    public void accept(ServerAcceptListener serverAcceptListener) {
        if (bluetoothSocket == null || !bluetoothSocket.isConnected()) {
            if (acceptRunnable == null) {
                acceptRunnable = new AcceptRunnable(serverAcceptListener);
            }
            executorService.submit(acceptRunnable);
        } else {
            serverAcceptListener.connectFail(new Exception(Constants.BLUE_EXCEPTION_SERVER_CONNECT));
        }
    }

    @Override
    public void close() {
        if (acceptRunnable != null) {
            acceptRunnable.cancel();
            acceptRunnable = null;
        }
        if (readRunnable != null) {
            readRunnable.stop();
            readRunnable = null;
        }
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void release() {
        executorService.shutdown();
    }

    private AcceptRunnable acceptRunnable;

    private class AcceptRunnable implements Runnable {
        private final BluetoothServerSocket mmServerSocket;
        private boolean isRunning = true;
        private ServerAcceptListener serverAcceptListener;

        public AcceptRunnable(ServerAcceptListener serverAcceptListener) {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                this.serverAcceptListener = serverAcceptListener;
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("blue", UUID.fromString(Constants.CLASSIC_BLUE_UUID));
            } catch (IOException e) {
                serverAcceptListener.connectFail(e);
                LogUtil.e(this, "Socket's listen() method failed" + e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (isRunning) {
                try {
                    socket = mmServerSocket.accept();
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                } catch (IOException e) {
                    serverAcceptListener.connectFail(e);
                    LogUtil.e(this, "Socket's accept() method failed" + e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    manageMyConnectedSocket(socket);
                    serverAcceptListener.connectSuccess(socket);
                    //mmServerSocket.close();
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                isRunning = false;
                if (mmServerSocket != null) {
                    mmServerSocket.close();
                }
            } catch (IOException e) {
                LogUtil.e(this, "Could not close the connect socket" + e);
            }
        }
    }

    private class ReadRunnable implements Runnable {

        private final InputStream inputStream;
        private final ClassBlueListener classBlueListener;

        public ReadRunnable(InputStream inputStream, ClassBlueListener classBlueListener) {
            this.inputStream = inputStream;
            this.classBlueListener = classBlueListener;
        }

        private boolean isRunning = true;

        public void start() {
            isRunning = true;
        }

        public void stop() {
            isRunning = false;
        }

        @Override
        public void run() {
            while (isRunning) {
                try {
                    byte[] date = new byte[128];//每次可以读取的最大数量
                    int len = inputStream.read(date);//此时数据读取到数组中
                    byte[] date1 = new byte[len];
                    System.arraycopy(date, 0, date1, 0, len);
                    classBlueListener.readClassicData(date1);
                } catch (Exception e) {
                    classBlueListener.readClassicError(e);
                    if (bluetoothSocket != null && !bluetoothSocket.isConnected()) {
                        isRunning = false;
                    }
                }
            }
        }
    }

    private ReadRunnable readRunnable;

    private void manageMyConnectedSocket(BluetoothSocket socket) {
        bluetoothSocket = socket;
    }
}
