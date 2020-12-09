package com.lucky.commplugin;

public class CommConfig {

    final int baudRate;   //波特率
    final int dataBits;   //数据位
    final int stopBits;   //停止位
    final int parity;     //校验位
    final String classicUUID; //经典蓝牙的UUID
    final String serverIP;  //局域网通信的ip
    final int serverPort;   //局域网通信的port

    public String getServerIP() {
        return serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public int getDataBits() {
        return dataBits;
    }

    public int getStopBits() {
        return stopBits;
    }

    public int getParity() {
        return parity;
    }

    public String getClassicUUID() {
        return classicUUID;
    }

    public CommConfig() {
        this(new Builder());
    }

    public CommConfig(Builder builder) {
        baudRate = builder.baudRate;
        dataBits = builder.dataBits;
        stopBits = builder.stopBits;
        parity = builder.parity;
        classicUUID = builder.classicUUID;
        serverIP = builder.serverIP;
        serverPort = builder.serverPort;
    }

    public static final class Builder {

        int baudRate;   //波特率
        int dataBits;   //数据位
        int stopBits;   //停止位
        int parity;     //校验位
        String classicUUID;  //经典蓝牙的UUID
        String serverIP;  //局域网通信的ip
        int serverPort;   //局域网通信的port

        public Builder() {
            baudRate = 115200;
            dataBits = 8;
            stopBits = 1;
            parity = 0;
            classicUUID = Constants.CLASSIC_BLUE_UUID;
            serverIP = Constants.SERVER_IP;
            serverPort = Constants.SERVER_PORT;
        }

        public Builder baudRate(int baudRate) {
            this.baudRate = baudRate;
            return this;
        }

        public Builder dataBits(int dataBits) {
            this.dataBits = dataBits;
            return this;
        }

        public Builder stopBits(int stopBits) {
            this.stopBits = stopBits;
            return this;
        }

        public Builder parity(int parity) {
            this.parity = parity;
            return this;
        }

        public Builder classicUUID(String uuid) {
            this.classicUUID = uuid;
            return this;
        }

        public Builder serverIP(String ip) {
            this.serverIP = ip;
            return this;
        }

        public Builder serverPort(int port) {
            this.serverPort = port;
            return this;
        }

        public CommConfig builder() {
            return new CommConfig(this);
        }
    }
}
