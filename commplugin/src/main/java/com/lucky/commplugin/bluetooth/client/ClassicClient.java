package com.lucky.commplugin.bluetooth.client;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.lucky.commplugin.Constants;
import com.lucky.commplugin.bluetooth.BlueConnectState;
import com.lucky.commplugin.bluetooth.BluetoothManager;
import com.lucky.commplugin.listener.ClassBlueListener;
import com.lucky.commplugin.listener.ClientConnectListener;
import com.lucky.commplugin.utils.HexDump;
import com.lucky.commplugin.utils.LogUtil;

import java.io.IOException;
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
                    ClientConnectListener.connectFail(new Exception("bluetoothDevice is null"));
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
                blueConnectState = BlueConnectState.CONNECT_SUCCESS;
                ClientConnectListener.connectSuccess();
            } catch (Exception e) {
                blueConnectState = BlueConnectState.CONNECT_FAIL;
                ClientConnectListener.connectFail(e);
            }
        }

        public void close() {
            try {
                blueConnectState = BlueConnectState.CONNECT_UNKNOWN;
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
                executorService.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
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
                } catch (IOException e) {
                    blueConnectState = BlueConnectState.CONNECT_UNKNOWN;
                    classBlueListener.readClassicError(e);
                }
            }
        }
    }

    /**
     * socket连接
     */
    public void connect(BluetoothDevice bluetoothDevice, ClientConnectListener ClientConnectListener) {
        if (blueConnectState == BlueConnectState.CONNECT_UNKNOWN || blueConnectState == BlueConnectState.CONNECT_FAIL) {
            if (connectRunnable == null) {
                connectRunnable = new ConnectRunnable(bluetoothDevice, ClientConnectListener);
            }
            executorService.submit(connectRunnable);
        } else {
            LogUtil.e("蓝牙正在连接或者已经连接上了");
        }
    }

    @Override
    public void close() {
        if (connectRunnable != null) {
            connectRunnable.close();
        }
    }

    @Override
    public void read(ClassBlueListener classBlueListener) {
        if (readRunnable == null) {
            readRunnable = new ReadRunnable(inputStream, classBlueListener);
            readRunnable.start();
            executorService.submit(readRunnable);
        } else {
            LogUtil.w("正在读取数据");
        }
    }

    @Override
    public void write(String data) {
        try {
            if (blueConnectState == BlueConnectState.CONNECT_SUCCESS) {
                blueConnectState = BlueConnectState.CONNECT_UNKNOWN;
                outputStream.write(HexDump.hexStringToByteArray(data));
            } else {
                LogUtil.w("蓝牙连接失败,不可以读取数据");
            }
        } catch (IOException e) {
            LogUtil.e(this, e.getMessage());
        }
    }

    @Override
    public void write(byte[] b) {
        try {
            if (blueConnectState == BlueConnectState.CONNECT_SUCCESS) {
                outputStream.write(b);
            } else {
                blueConnectState = BlueConnectState.CONNECT_UNKNOWN;
                LogUtil.w("蓝牙连接失败,不可以读取数据");
            }
        } catch (IOException e) {
            LogUtil.e(this, e.getMessage());
        }
    }
}
