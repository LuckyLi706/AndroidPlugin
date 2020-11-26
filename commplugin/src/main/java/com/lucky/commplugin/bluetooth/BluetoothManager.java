package com.lucky.commplugin.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.lucky.commplugin.listener.BleBlueToothListener;
import com.lucky.commplugin.utils.LogUtil;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.lucky.commplugin.utils.HexDump.hexStringToByteArray;

public abstract class BluetoothManager {

    private BluetoothDiscoveryReceiver bluetoothDiscoveryReceiver;
    private BluetoothAdapter bluetoothAdapter;
    protected Context context;


    public void initBluetooth(Context context) {
        this.context = context;
        registerReceiver();
        registerDiscovery();
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (bluetoothAdapter == null) {
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
    }

    public void openBluetooth(Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, 1);
    }

    public Set<BluetoothDevice> getPairDevice() {
        return bluetoothAdapter.getBondedDevices();

    }


    private void registerDiscovery() {
        if (bluetoothDiscoveryReceiver == null) {
            bluetoothDiscoveryReceiver = new BluetoothDiscoveryReceiver();
        }
        // 注册用以接收到已搜索到的蓝牙设备的receiver
        IntentFilter mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mFilter.addAction(BluetoothDevice.ACTION_FOUND);     //发现蓝牙
        mFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  //扫描完成
        context.registerReceiver(bluetoothDiscoveryReceiver, mFilter);
    }

    private void unregisterDiscovery() {
        if (bluetoothDiscoveryReceiver != null) {
            context.unregisterReceiver(bluetoothDiscoveryReceiver);
        }
    }

    // 广播接收发现蓝牙设备
    private class BluetoothDiscoveryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //发现蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //蓝牙搜索完成
                // 蓝牙搜索是非常消耗系统资源开销的过程，搜索完毕后应该及时取消搜索
                bluetoothAdapter.cancelDiscovery();
            }
        }
    }

    private BluetoothReceiver bluetoothReceiver;

    /**
     * 注册广播
     */
    public void registerReceiver() {
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

    private void stopScanBle() {
        bluetoothAdapter.stopLeScan(mBLEScanCallback);
        bluetoothAdapter.startLeScan(mBLEScanCallback);
        bluetoothAdapter.startDiscovery();
    }

    //mBLEScanCallback回调函数
    private BluetoothAdapter.LeScanCallback mBLEScanCallback = (device, rssi, scanRecord) -> {
        //打印蓝牙mac地址
        stopScanBle();
    };


    public static class BluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        LogUtil.d("STATE_OFF 手机蓝牙关闭");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        LogUtil.d("STATE_TURNING_OFF 手机蓝牙正在关闭");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        LogUtil.d("STATE_ON 手机蓝牙开启");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        LogUtil.d("STATE_TURNING_ON 手机蓝牙正在开启");
                        break;
                }
            }
        }
    }
}
