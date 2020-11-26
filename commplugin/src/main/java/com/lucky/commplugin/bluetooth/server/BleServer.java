package com.lucky.commplugin.bluetooth.server;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.lucky.commplugin.bluetooth.BluetoothManager;
import com.lucky.commplugin.listener.BleBlueToothListener;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import static com.lucky.commplugin.utils.HexDump.hexStringToByteArray;

public class BleServer extends BluetoothManager {

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
    private void connectBle(BluetoothDevice bluetoothDevice) {
        closeBleBlue();
        bluetoothGatt = bluetoothDevice.connectGatt(context, false, callback);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
                    //startDiscoverThread();
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

    @Override
    public void read() {

    }

    @Override
    public void write(String data) {
        if (writeCharacteristic != null) {
            byte[] b = hexStringToByteArray(data);
            writeCharacteristic(b);
        } else {
        }
    }

    @Override
    public void write(byte[] b) {

    }

    @Override
    public void connect(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void accept() {

    }
}
