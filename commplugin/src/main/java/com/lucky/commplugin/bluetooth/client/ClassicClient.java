package com.lucky.commplugin.bluetooth.client;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.lucky.commplugin.Constants;
import com.lucky.commplugin.bluetooth.BluetoothManager;
import com.lucky.commplugin.listener.ReadListener;
import com.lucky.commplugin.listener.ClientConnectListener;
import com.lucky.commplugin.utils.HexDump;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ClassicClient extends BluetoothManager {

    @SuppressLint("StaticFieldLeak")
    private static final ClassicClient client = new ClassicClient();

    private ClassicClient() {

    }

    public static ClassicClient getInstance() {
        return client;
    }

    private ReadRunnable readRunnable;


    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private ConnectRunnable connectRunnable;

    private class ConnectRunnable implements Runnable {

        private final BluetoothDevice bluetoothDevice;
        private final ClientConnectListener ClientConnectListener;

        public ConnectRunnable(BluetoothDevice bluetoothDevice, ClientConnectListener ClientConnectListener) {
            this.ClientConnectListener = ClientConnectListener;
            this.bluetoothDevice = bluetoothDevice;
        }

        @Override
        public void run() {
            try {
                if (bluetoothDevice == null) {
                    ClientConnectListener.connectFail(new Exception(Constants.EXCEPTION_CLIENT_IS_NULL));
                    return;
                }
                socket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(commConfig.getClassicUUID()));
                if (socket != null) {
                    // 连接
                    if (!socket.isConnected()) {
                        socket.connect();
                    }
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                }
                ClientConnectListener.connectSuccess();
            } catch (Exception e) {
                ClientConnectListener.connectFail(e);
            }
        }

        public void close() {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (readRunnable != null) {
                    readRunnable.stop();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                readRunnable = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void release() {
        executorService.shutdown();
    }

    private class ReadRunnable implements Runnable {

        private final InputStream inputStream;
        private final ReadListener readListener;

        public ReadRunnable(InputStream inputStream, ReadListener readListener) {
            this.inputStream = inputStream;
            this.readListener = readListener;
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
                    readListener.readData(date1);
                } catch (Exception e) {
                    readListener.readError(e);
                    //断开连接取消接收数据线程
                    if (socket != null && !socket.isConnected()) {
                        isRunning = false;
                    }
                }
            }
        }
    }

    /**
     * socket连接
     */
    public void connect(BluetoothDevice bluetoothDevice, ClientConnectListener ClientConnectListener) {
        if (socket == null || !socket.isConnected()) {
            if (connectRunnable == null) {
                connectRunnable = new ConnectRunnable(bluetoothDevice, ClientConnectListener);
            }
            executorService.submit(connectRunnable);
        } else {
            ClientConnectListener.connectFail(new Exception(Constants.EXCEPTION_CLIENT_CONNECT));
        }
    }

    @Override
    public void close() {
        if (connectRunnable != null) {
            connectRunnable.close();
        }
    }

    @Override
    public void read(ReadListener readListener) {
        if (socket != null && socket.isConnected() && outputStream != null) {
            readRunnable = new ReadRunnable(inputStream, readListener);
            readRunnable.start();
            executorService.submit(readRunnable);
        } else {
            readListener.readError(new Exception(Constants.EXCEPTION_CLIENT_CONNECT_FAIL));
        }
    }

    @Override
    public void write(String data) throws Exception {
        if (socket != null && socket.isConnected()) {
            outputStream.write(HexDump.hexStringToByteArray(data));
        } else {
            throw new Exception(Constants.EXCEPTION_CLIENT_CONNECT_FAIL);
        }
    }

    @Override
    public void write(byte[] b) throws Exception {
        if (socket != null && socket.isConnected()) {
            outputStream.write(b);
        } else {
            throw new Exception(Constants.EXCEPTION_CLIENT_CONNECT_FAIL);
        }
    }
}
