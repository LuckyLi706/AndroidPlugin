package com.lucky.commplugin.listener;

/**
 * USB通信建立的连接是否成功
 */
public interface UsbConnectListener {

    void connectSuccess();

    void connectFail(String message);
}
