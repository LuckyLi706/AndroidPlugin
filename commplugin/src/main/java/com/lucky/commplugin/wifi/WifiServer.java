package com.lucky.commplugin.wifi;

import com.lucky.commplugin.Constants;
import com.lucky.commplugin.listener.ServerAcceptListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class WifiServer {

    private static final WifiServer wifiServer = new WifiServer();

    private ServerSocket mServerSocket;
    private Socket mSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private ExecutorService executorService;

    public static WifiServer getInstance() {
        return wifiServer;
    }

    public void accept(ServerAcceptListener serverAcceptListener) {
        try {
            if (mServerSocket == null) {
                mServerSocket = new ServerSocket(Constants.SERVER_PORT);
            } else {
                if (mSocket == null || !mSocket.isConnected()) {

                }
            }
        } catch (IOException e) {
            serverAcceptListener.connectFail(e);
        }
    }

    private class AcceptRunnable implements Runnable {

        private final ServerAcceptListener serverAcceptListener;

        public AcceptRunnable(ServerAcceptListener serverAcceptListener) {
            this.serverAcceptListener = serverAcceptListener;
        }

        @Override
        public void run() {
            try {
                // mServerSocket.
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
}
