package com.lucky.commplugin.listener;

public interface ReadListener {

    void readData(byte[] b);

    void readError(Exception e);
}
