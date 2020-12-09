package com.lucky.commplugin.listener;

public interface ServerAcceptListener {

    public void connectSuccess(Object object);

    public void connectFail(Exception e);
}
