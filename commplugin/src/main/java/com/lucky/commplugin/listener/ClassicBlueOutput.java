package com.lucky.commplugin.listener;

import java.io.IOException;
import java.io.InputStream;

public class ClassicBlueOutput implements Runnable {

    private InputStream inputStream;
    private ClassBlueListener classBlueListener;

    ClassicBlueOutput(InputStream inputStream, ClassBlueListener classBlueListener) {
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
                //e.printStackTrace();
                //if (e instanceof IOException) {
                classBlueListener.readClassicError(e);
                //}
            }
        }
    }
}