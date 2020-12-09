package com.lucky.commplugin.wifi;

import com.lucky.commplugin.Constants;
import com.lucky.commplugin.listener.ReadListener;
import com.lucky.commplugin.listener.ServerAcceptListener;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class WifiServer extends WifiCommManager {

    private static final WifiServer wifiServer = new WifiServer();

    private ServerSocket mServerSocket;
    private Socket mSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private AcceptRunnable acceptRunnable;
    private ReadRunnable readRunnable;

    public static WifiServer getInstance() {
        return wifiServer;
    }

    public void accept(ServerAcceptListener serverAcceptListener) {
        try {
            if (mServerSocket == null) {
                mServerSocket = new ServerSocket(Constants.SERVER_PORT);
            } else {
                if (mSocket == null || !mSocket.isConnected()) {
                    acceptRunnable = new AcceptRunnable(serverAcceptListener);
                    executorService.submit(acceptRunnable);
                } else {
                    serverAcceptListener.connectFail(new Exception(Constants.EXCEPTION_SERVER_CONNECT));
                }
            }
        } catch (IOException e) {
            serverAcceptListener.connectFail(e);
        }
    }

    @Override
    public void read(ReadListener readListener) {
        if (mSocket != null && mSocket.isConnected()) {
            readRunnable = new ReadRunnable(readListener);
            executorService.submit(readRunnable);
        } else {
            readListener.readError(new Exception(Constants.EXCEPTION_NO_CLIENT_CONNECT));
        }
    }

    @Override
    public void write(String message) throws Exception {
        if (mSocket != null && mSocket.isConnected()) {
            outputStream.write(message.getBytes());
        } else {
            throw new Exception(Constants.EXCEPTION_NO_CLIENT_CONNECT);
        }
    }

    @Override
    public void close() throws Exception {
        if (readRunnable != null) {
            readRunnable.close();
        }
        if (acceptRunnable != null) {
            acceptRunnable.close();
        }
        if (mSocket != null) {
            mSocket.close();
        }
        if (mServerSocket != null) {
            mServerSocket.close();
        }
    }

    @Override
    public void release() {
        executorService.shutdown();
    }

    private class AcceptRunnable implements Runnable {

        private final ServerAcceptListener serverAcceptListener;
        private boolean isRunning = true;

        public AcceptRunnable(ServerAcceptListener serverAcceptListener) {
            this.serverAcceptListener = serverAcceptListener;
        }

        @Override
        public void run() {
            while (isRunning) {
                try {
                    //指定ip地址和端口号
                    mSocket = mServerSocket.accept();
                    //获取输出流、输入流
                    outputStream = mSocket.getOutputStream();
                    inputStream = mSocket.getInputStream();
                    serverAcceptListener.connectSuccess(mSocket);
                } catch (Exception e) {
                    serverAcceptListener.connectFail(e);
                }
            }
        }

        public void close() {
            isRunning = false;
        }
    }

    private class ReadRunnable implements Runnable {

        private final ReadListener readListener;
        private boolean isRunning = true;

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

        public void close() throws Exception {
            isRunning = false;
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
