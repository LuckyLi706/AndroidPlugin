package com.lucky.commplugin;

public class CommConfig {

    final int baudRate;   //波特率
    final int dataBits;   //数据位
    final int stopBits;   //停止位
    final int parity;     //校验位
    final String classicUUID; //经典蓝牙的UUID
    final String serverIP;  //局域网通信的ip
    final int serverPort;   //局域网通信的port
    final String bleServiceUUID;
    final String bleWriteUUID;
    final String bleNotifyUUID;
    final String blueDescNotifyUUID;

    public String getBlueDescNotifyUUID() {
        return blueDescNotifyUUID;
    }

    public String getBleServiceUUID() {
        return bleServiceUUID;
    }

    public String getBleWriteUUID() {
        return bleWriteUUID;
    }

    public String getBleNotifyUUID() {
        return bleNotifyUUID;
    }

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
        bleNotifyUUID = builder.bleNotifyUUID;
        bleServiceUUID = builder.bleServiceUUID;
        bleWriteUUID = builder.bleWriteUUID;
        serverIP = builder.serverIP;
        serverPort = builder.serverPort;
        blueDescNotifyUUID = builder.blueDescNotifyUUID;
    }

    public static final class Builder {

        int baudRate;   //波特率
        int dataBits;   //数据位
        int stopBits;   //停止位
        int parity;     //校验位
        String classicUUID;  //经典蓝牙的UUID
        String serverIP;  //局域网通信的ip
        int serverPort;   //局域网通信的port
        String bleServiceUUID; //低功耗蓝牙主UUID
        String bleWriteUUID;  //低功耗蓝牙写入的UUID
        String bleNotifyUUID; //低功耗蓝牙读取的UUID
        String blueDescNotifyUUID; //低功耗蓝牙的描述信息

        public Builder() {
            baudRate = 115200;
            dataBits = 8;
            stopBits = 1;
            parity = 0;
            classicUUID = Constants.CLASSIC_BLUE_UUID;
            bleNotifyUUID = Constants.BLE_BLUE_NOTIFY_UUID;
            bleServiceUUID = Constants.BLE_BLUE_SERVICE_UUID;
            bleWriteUUID = Constants.BLE_BLUE_WRITE_UUID;
            serverIP = Constants.SERVER_IP;
            serverPort = Constants.SERVER_PORT;
            blueDescNotifyUUID = Constants.BLE_BLUE_DESC_NOTIFY_UUID;
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

        public Builder bleNotifyUUID(String bleNotifyUUID) {
            this.bleNotifyUUID = bleNotifyUUID;
            return this;
        }

        public Builder bleWriteUUID(String bleWriteUUID) {
            this.bleWriteUUID = bleWriteUUID;
            return this;
        }

        public Builder bleServiceUUID(String bleServiceUUID) {
            this.bleServiceUUID = bleServiceUUID;
            return this;
        }

        public Builder blueDescNotifyUUID(String blueDescNotifyUUID) {
            this.blueDescNotifyUUID = blueDescNotifyUUID;
            return this;
        }

        public CommConfig builder() {
            return new CommConfig(this);
        }
    }
}
