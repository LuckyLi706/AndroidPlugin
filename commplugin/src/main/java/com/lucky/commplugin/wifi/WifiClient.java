package com.lucky.commplugin.wifi;

import com.lucky.commplugin.Constants;
import com.lucky.commplugin.listener.ClientConnectListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class WifiClient {
    private static final WifiClient wifiClient = new WifiClient();
    private Socket mSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private ExecutorService executorService;
    private ConnectRunnable connectRunnable;

    public static WifiClient getInstance() {
        return wifiClient;
    }

    public void connect(ClientConnectListener clientConnectListener) {
        if (!mSocket.isConnected() || mSocket == null) {
            connectRunnable = new ConnectRunnable(clientConnectListener);
            executorService.submit(connectRunnable);
        } else {
            clientConnectListener.connectFail(new Exception("已经连接"));
        }
    }

    public void read() {
        if (!mSocket.isConnected() || mSocket == null) {

        } else {
            ReadRunnable readRunnable = new ReadRunnable();
            executorService.submit(readRunnable);
        }
    }

    public void write(String message) throws Exception {
        if (!mSocket.isConnected() || mSocket == null) {
            throw new Exception("未连接");
        } else {
            DataOutputStream writer = new DataOutputStream(outputStream);
            writer.writeUTF(message); // 写一个UTF-8的信息
        }
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
        @Override
        public void run() {
            DataInputStream reader;
            boolean isRunning = true;
            try {
                // 获取读取流
                reader = new DataInputStream(inputStream);
                while (isRunning) {
                    System.out.println("*等待客户端输入*");
                    // 读取数据
                    String msg = reader.readUTF();
                    System.out.println("获取到客户端的信息：=" + msg);
                }
            } catch (Exception e) {

            }
        }
    }
}