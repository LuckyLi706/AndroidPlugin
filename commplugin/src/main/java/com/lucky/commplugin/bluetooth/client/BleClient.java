package com.lucky.commplugin.bluetooth.client;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.lucky.commplugin.bluetooth.BluetoothManager;
import com.lucky.commplugin.listener.ClientConnectListener;
import com.lucky.commplugin.listener.ReadListener;
import com.lucky.commplugin.utils.LogUtil;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import static com.lucky.commplugin.utils.HexDump.hexStringToByteArray;

/**
 * 呼叫者（Android应用程序）是GATT客户端。连接状态，以及GATT的数据变化等通过BluetoothGattCallback接口回调给客户端（APP）
 * <p>
 * 一个BLE设备可能有多个服务BluetoothGattService，同样每个服务可以有多个BluetoothGattCharacteristic特性。
 * <p>
 * 我们一般只会与某个特定BluetoothGattService中的某个特性BluetoothGattCharacteristic进行数据读写。
 * 判断条件就是这里的UUID_SERVICE和UUID_CHARACTERISTIC，这两个UUID一般提供BLE设备的时候会一并提供给我们
 */
public class BleClient extends BluetoothManager {

    /**
     * Ble蓝牙连接方式
     */
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic writeCharacteristic;   //写的特征值
    private BluetoothGattCharacteristic notifyCharacteristic;  //读取的特征值
    private ReadListener readListener;


    private ClientConnectListener clientConnectListener;

    @SuppressLint("StaticFieldLeak")
    private static final BleClient client = new BleClient();

    private BleClient() {

    }

    public static BleClient getInstance() {
        return client;
    }


    @Override
    public void read(ReadListener readListener) {
        this.readListener = readListener;
    }

    @Override
    public void write(String data) throws Exception {
        if (writeCharacteristic != null) {
            byte[] b = hexStringToByteArray(data);
            writeCharacteristic(b);
        } else {
            throw new Exception("writeCharacteristic is NULL");
        }
    }

    @Override
    public void connect(BluetoothDevice bluetoothDevice, ClientConnectListener clientConnectListener) {
        this.clientConnectListener = clientConnectListener;
        bluetoothDevice.connectGatt(context, false, bluetoothGattCallback);  //第二个参数表示为 true 时，如果设备断开了连接将会不断的尝试连接
    }

    @Override
    public void write(byte[] b) throws Exception {
        if (writeCharacteristic != null) {
            writeCharacteristic(b);
        } else {
            throw new Exception("writeCharacteristic is NULL");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void writeCharacteristic(byte[] value) {
        if (!writeBleDataCheck()) {
            return;
        }
        writeCharacteristic.setValue(value);
        bluetoothGatt.writeCharacteristic(writeCharacteristic);
    }

    private boolean callBackDiscovered = false;

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
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


            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                bluetoothGatt = gatt;
                gatt.discoverServices(); //启动服务发现
                clientConnectListener.connectSuccess();
            } else {
                close();
                clientConnectListener.connectFail(new Exception(gatt.toString()));
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
                List<BluetoothGattService> services = bluetoothGatt.getServices();
                for (int i = 0; i < services.size(); i++) {
                    BluetoothGattService bluetoothGattService = services.get(i);
                    String serviceUuid = bluetoothGattService.getUuid().toString();
                    List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
                    for (int j = 0; j < characteristics.size(); j++) {
                        LogUtil.d("特征服务-----" + serviceUuid + "," + "特征值:" + characteristics.get(j).getUuid().toString());
                    }
                }

                //设置serviceUUID,原型是：BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(SERVICESUUID));
                BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(commConfig.getBleServiceUUID()));
                //设置写入特征UUID,原型是：BluetoothGattCharacteristic writeCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(WRITEUUID));
                writeCharacteristic = bluetoothGattService.getCharacteristic((UUID.fromString(commConfig.getBleWriteUUID())));
                //设置监听特征UUID,原型是：BluetoothGattCharacteristic notifyCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(NOTIFYUUID));
                notifyCharacteristic = bluetoothGattService.getCharacteristic((UUID.fromString(commConfig.getBleNotifyUUID())));
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
            }
        }

        //数据返回的回调（此处接收BLE设备返回数据）
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            readListener.readData(characteristic.getValue());
        }
    };

    //处理频繁写数据的问题
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean writeBleDataCheck() {
        long enterTime = System.currentTimeMillis();
        long HONEY_CMD_TIMEOUT = 500;
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
        return false;
    }

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


    @Override
    public void close() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
            writeCharacteristic = null;
            notifyCharacteristic = null;
        }
    }
}
