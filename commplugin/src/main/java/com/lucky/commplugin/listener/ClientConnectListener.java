package com.lucky.commplugin.listener;

public interface ClientConnectListener {

    public void connectSuccess();

    public void connectFail(Exception e);
}
