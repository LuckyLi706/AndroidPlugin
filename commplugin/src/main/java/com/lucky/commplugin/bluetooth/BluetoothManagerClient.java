package com.lucky.commplugin.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.lucky.commplugin.listener.BleBlueToothListener;
import com.lucky.commplugin.listener.ClassBlueListener;
import com.lucky.commplugin.listener.ClassicBlueOutput;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;

import static com.lucky.commplugin.utils.HexDump.hexStringToByteArray;

public class BluetoothManagerClient {

    private static final BluetoothManagerClient ourInstance = new BluetoothManagerClient();

    public static BluetoothManagerClient getInstance() {
        return ourInstance;
    }

    private BluetoothManagerClient() {
    }

    private InputStream inputStream;
    private OutputStream outputStream;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothDevice bluetoothDeviceBle;
    private BluetoothSocket socket = null;   //蓝牙设备Socket客户端
    private static final String BLUE_UUID = "00001101-0000-1000-8000-00805F9B34FB";


    public void initBluetooth() {
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (bluetoothAdapter == null) {
            return;
        }
        //bluetoothAdapter.isEnabled();
    }

    public void openBluetooth(Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, 1);
    }

    /**
     * 获取已经配对的设备
     * 返回0 表示没有配对
     * 返回1 表示有一个配对
     * 返回2 表示有多个配对
     *
     * @return
     */
    public int getPairDevice() {
        @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() == 1) {
            for (BluetoothDevice device : pairedDevices) {
                bluetoothDevice = device;
            }
            return 1;
        } else if (pairedDevices.size() >= 1) {
            return 2;
        } else {
            return 0;
        }
    }

    private BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    private BluetoothDevice getBluetoothDeviceBle() {
        return bluetoothDeviceBle;
    }

    public void setBluetoothDevice(BluetoothDevice mBluetoothDevice) {
        this.bluetoothDeviceBle = mBluetoothDevice;
    }

    /**
     * socket连接
     *
     * @throws IOException
     */
    @SuppressLint("MissingPermission")
    public void connect() throws Exception {
        socket = getBluetoothDevice().createRfcommSocketToServiceRecord(UUID.fromString(BLUE_UUID));
        if (socket != null) {
            // 连接
            if (!socket.isConnected()) {
                socket.connect();
            }
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        }
    }

    /**
     * 关闭socket连接
     */
    public void closeClassicBlue() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (classicBlueOutput != null) {
                classicBlueOutput.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ClassBlueListener classBlueListener;

    public void setClassicBlueListener(ClassBlueListener classBlueListener) {
        this.classBlueListener = classBlueListener;
    }

    public void sendBuffer(String b) {
        try {
            outputStream.write(b.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ClassicBlueOutput classicBlueOutput;

    public void receiverBlue() {
        classicBlueOutput = new ClassicBlueOutput(inputStream, classBlueListener);
        classicBlueOutput.start();
        Executors.newSingleThreadExecutor().submit(classicBlueOutput);
    }

    private BluetoothReceiver bluetoothReceiver;

    /**
     * 注册广播
     *
     * @param context
     */
    public void registerReceiver(Context context) {
        bluetoothReceiver = new BluetoothReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(bluetoothReceiver, filter);
    }

    /**
     * 取消广播
     *
     * @param context
     */
    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(bluetoothReceiver);
    }


    /**
     * Ble蓝牙连接方式
     */
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic notifyCharacteristic;

    private UUID SERVICE_UUID;    //主服务的UUID
    private UUID RX_UUID;         //写的UUID
    private UUID NOTIFY_UUID;     //读的UUID

    private String currentBleDevice;   //当前的蓝牙设备


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void connectBle() {
        closeBleBlue();
        bluetoothGatt = getBluetoothDeviceBle().connectGatt(context, false, callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void closeBleBlue() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
            writeCharacteristic = null;
            notifyCharacteristic = null;
        }
    }

    private long currentTime;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("MissingPermission")
    public void startScanBle(Context context) {


        this.context = context;
        if (getBluetoothDeviceBle() == null) {
            bluetoothAdapter.startLeScan(mBLEScanCallback);
            currentTime = System.currentTimeMillis();
        } else {
            connectBle();
        }
    }

    private Context context;

    private void stopScanBle() {
        bluetoothAdapter.stopLeScan(mBLEScanCallback);
    }

    //mBLEScanCallback回调函数
    private BluetoothAdapter.LeScanCallback mBLEScanCallback = (device, rssi, scanRecord) -> {

        if (device.getName() != null) {
        }

        if (System.currentTimeMillis() - currentTime > 10000) {
            stopScanBle();
            //BluetoothManagerClient.getInstance().blueToothListener.connect(false);
        }
        //打印蓝牙mac地址
        else if (device.getName() != null && device.getName().equals(currentBleDevice)) {
            setBluetoothDevice(device);
            connectBle();
            stopScanBle();
        }
    };


    public void sendBleOrder(String str) {
        if (writeCharacteristic != null) {
            byte[] b = hexStringToByteArray(str);
            //将指令放置进特征中
//            writeCharacteristic.setValue(b);
//            //设置回复形式
//            writeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
//            //开始写数据
//            boolean isSuccess = bluetoothGatt.writeCharacteristic(writeCharacteristic);
//            LogUtil.d("isSuccess:" + isSuccess);
            writeCharacteristic(b);
        } else {
        }
    }


    private static long HONEY_CMD_TIMEOUT = 500;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean isDeviceBusy() {
        boolean state = false;
        try {
            state = (boolean) readField(bluetoothGatt, "mDeviceBusy");
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return state;
    }


    private Object readField(Object object, String name) throws IllegalAccessException, NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(object);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void writeCharacteristic(byte[] value) {
        if (bluetoothGatt == null) {
            return;
        }
        if (!writeBleDataCheck()) {
            return;
        }
        try {
            writeCharacteristic.setValue(value);
            boolean status = bluetoothGatt.writeCharacteristic(writeCharacteristic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean writeBleDataCheck() {
        long enterTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - enterTime) < HONEY_CMD_TIMEOUT) {
            if (isDeviceBusy()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                return true;
            }
        }
        if ((System.currentTimeMillis() - enterTime) >= HONEY_CMD_TIMEOUT) {
        }
        return false;
    }

    private boolean callBackDiscovered = false;
    private long connectTime;

    private BluetoothGattCallback callback = new BluetoothGattCallback() {
        /**
         * 133 ：连接超时或未找到设备.
         * 8 ： 设备超出范围
         * 22 ：表示本地设备终止了连接
         * @param gatt
         * @param status    用于返回操作是否成功,会返回异常码
         * @param newState  返回连接状态，如BluetoothProfile#STATE_DISCONNECTED、BluetoothProfile#STATE_CONNECTED
         */
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {


            if (status != BluetoothGatt.GATT_SUCCESS) {

            } else {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    connectTime = System.currentTimeMillis();
                    callBackDiscovered = false;
                    //count = 0;
                    // 开始扫描服务，安卓蓝牙开发重要步骤之一

                    gatt.discoverServices();
                    startDiscoverThread();
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    // 连接断开
                    /*连接断开后的相应处理*/
                    //blueToothListener.connect(false);
                    // bluetoothGatt = null;
                    bluetoothGatt.close();
                }
            }
        }

        //写的通知
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (callBackDiscovered) {
                return;
            }
            callBackDiscovered = true;
            //获取服务列表
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //设置serviceUUID,原型是：BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(SERVICESUUID));
                bluetoothGattService = bluetoothGatt.getService(SERVICE_UUID);
                //设置写入特征UUID,原型是：BluetoothGattCharacteristic writeCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(WRITEUUID));
                writeCharacteristic = bluetoothGattService.getCharacteristic(RX_UUID);
                //设置监听特征UUID,原型是：BluetoothGattCharacteristic notifyCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(NOTIFYUUID));
                notifyCharacteristic = bluetoothGattService.getCharacteristic(NOTIFY_UUID);
                //开启监听
                boolean result = gatt.setCharacteristicNotification(notifyCharacteristic, true);
                if (result) {
                    List<BluetoothGattDescriptor> descriptorList = notifyCharacteristic.getDescriptors();
                    if (descriptorList != null && descriptorList.size() > 0) {
                        for (BluetoothGattDescriptor descriptor : descriptorList) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            bluetoothGatt.writeDescriptor(descriptor);
                        }
                    }
                }
            } else {
            }
        }

        //数据返回的回调（此处接收BLE设备返回数据）
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            blueToothListener.readBleData(characteristic.getValue());
        }
    };

    private BleBlueToothListener blueToothListener;

    public void setBleListener(BleBlueToothListener blueToothListener) {
        this.blueToothListener = blueToothListener;
    }

    private void startDiscoverThread() {
        Thread mConnectThread = new Thread(() -> {
            while (!callBackDiscovered) {
                if (System.currentTimeMillis() - connectTime > 15000) {
                    blueToothListener.connect(false);
                    callBackDiscovered = true;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mConnectThread.start();
    }

}
