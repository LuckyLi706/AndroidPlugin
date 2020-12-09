package com.lucky.commplugin.wifi;

import com.lucky.commplugin.listener.ClientConnectListener;
import com.lucky.commplugin.listener.ReadListener;
import com.lucky.commplugin.listener.ServerAcceptListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <permission>
 * <!--允许应用程序改变网络状态-->
 * <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE"/>
 * <!--允许应用程序改变WIFI连接状态-->
 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
 * <!--允许应用程序访问有关的网络信息-->
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
 * <!--允许应用程序访问WIFI网卡的网络信息-->
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
 * <!--允许应用程序完全使用网络-->
 * <uses-permission android:name="android.permission.INTERNET"/>
 * </permission>
 */
public abstract class WifiCommManager {
    protected ExecutorService executorService;

    public void init() {
        executorService = Executors.newCachedThreadPool();
    }

    //读取数据
    public abstract void read(ReadListener readListener);

    //写入数据
    public abstract void write(String message) throws Exception;

    //客户端连接
    public void connect(ClientConnectListener clientConnectListener) {

    }

    //服务端接收
    public void accept(ServerAcceptListener serverAcceptListener) {

    }

    //关闭连接
    public abstract void close() throws Exception;

    //释放资源
    public abstract void release();
}
