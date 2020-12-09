package com.lucky.commplugin.wifi;

import com.lucky.commplugin.Constants;
import com.lucky.commplugin.listener.ClientConnectListener;
import com.lucky.commplugin.listener.ReadListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class WifiClient extends WifiCommManager {
    private static final WifiClient wifiClient = new WifiClient();
    private Socket mSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean isRunning = true;


    public static WifiClient getInstance() {
        return wifiClient;
    }

    public void connect(ClientConnectListener clientConnectListener) {
        if (!mSocket.isConnected() || mSocket == null) {
            ConnectRunnable connectRunnable = new ConnectRunnable(clientConnectListener);
            executorService.submit(connectRunnable);
        } else {
            clientConnectListener.connectFail(new Exception(Constants.EXCEPTION_CLIENT_CONNECT));
        }
    }

    public void read(ReadListener readListener) {
        if (!mSocket.isConnected() || mSocket == null) {
            readListener.readError(new Exception(Constants.EXCEPTION_CLIENT_CONNECT_FAIL));
        } else {
            ReadRunnable readRunnable = new ReadRunnable(readListener);
            executorService.submit(readRunnable);
        }
    }

    public void write(String message) throws Exception {
        if (!mSocket.isConnected() || mSocket == null) {
            throw new Exception(Constants.EXCEPTION_CLIENT_CONNECT_FAIL);
        } else {
            DataOutputStream writer = new DataOutputStream(outputStream);
            writer.writeUTF(message); // 写一个UTF-8的信息
        }
    }

    @Override
    public void close() throws Exception {
        isRunning = false;
        if (mSocket != null) {
            mSocket.close();
        }
    }

    @Override
    public void release() {
        executorService.shutdown();
    }

    private class ConnectRunnable implements Runnable {

        private final ClientConnectListener clientConnectListener;

        public ConnectRunnable(ClientConnectListener clientConnectListener) {
            this.clientConnectListener = clientConnectListener;
        }

        @Override
        public void run() {
            try {
                //指定ip地址和端口号
                mSocket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
                //获取输出流、输入流
                outputStream = mSocket.getOutputStream();
                inputStream = mSocket.getInputStream();
                clientConnectListener.connectSuccess();
            } catch (Exception e) {
                clientConnectListener.connectFail(e);
            }
        }
    }

    private class ReadRunnable implements Runnable {

        private final ReadListener readListener;

        public ReadRunnable(ReadListener readListener) {
            this.readListener = readListener;
        }

        @Override
        public void run() {
            DataInputStream reader = new DataInputStream(inputStream);
            while (isRunning) {
                try {
                    // 读取数据
                    String msg = reader.readUTF();
                    readListener.readData(msg.getBytes());
                } catch (Exception e) {
                    readListener.readError(e);
                    if (mSocket == null || !mSocket.isConnected()) {
                        isRunning = false;
                    }
                }
            }
        }
    }
}