package com.lucky.commplugin.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.lucky.commplugin.listener.ClientConnectListener;
import com.lucky.commplugin.listener.BlueScanListener;
import com.lucky.commplugin.listener.ClassBlueListener;
import com.lucky.commplugin.listener.ServerAcceptListener;
import com.lucky.commplugin.utils.LogUtil;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 扫描方案
 * <code>
 * BluetoothAdapter.startDiscovery()//可以扫描经典蓝牙和ble蓝牙两种
 * BluetoothAdapter.startLeScan()//扫描低功耗蓝牙，在api21已经弃用，不过还是可以使用
 * BluetoothLeScanner.startScan()//新的ble扫描方法
 * </code>
 * <p>
 * 官方地址：https://developer.android.com/guide/topics/connectivity/bluetooth
 */
public abstract class BluetoothManager {


    private BluetoothDiscoveryReceiver bluetoothDiscoveryReceiver;
    protected BluetoothAdapter bluetoothAdapter;
    protected Context context;
    protected ExecutorService executorService = Executors.newFixedThreadPool(6);
    private BlueScanListener blueScanListener;
    protected BlueConnectState blueConnectState = BlueConnectState.CONNECT_UNKNOWN;


    public void initBluetooth(Context context) {
        this.context = context.getApplicationContext();
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (bluetoothAdapter == null) {
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        registerReceiver();
    }

    /**
     * 打开蓝牙
     *
     * @param activity 当前activity对象
     */
    public void openBluetooth(Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, 1);
    }

    /**
     * 获取已经配对的蓝牙对象
     *
     * @return 配对的蓝牙集合
     */
    public Set<BluetoothDevice> getPairDevice() {
        return bluetoothAdapter.getBondedDevices();
    }

    /**
     * 注册扫描蓝牙广播
     * 比较耗时
     */
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

    /**
     * 取消注册蓝牙广播
     */
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
                blueScanListener.onScanResult(device);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //蓝牙搜索完成
                // 蓝牙搜索是非常消耗系统资源开销的过程，搜索完毕后应该及时取消搜索
                bluetoothAdapter.cancelDiscovery();
            }
        }
    }

    private BluetoothStateReceiver bluetoothReceiver;

    /**
     * 注册广播（蓝牙的开启和关闭状态检测）
     */
    public void registerReceiver() {
        bluetoothReceiver = new BluetoothStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(bluetoothReceiver, filter);
    }

    /**
     * 取消广播
     */
    public void unregisterReceiver() {
        context.unregisterReceiver(bluetoothReceiver);
    }

    /**
     * 开启可以当前设备被扫描状态
     */
    public void enableDiscovery() {
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);   //设备处于可检测到模式的时间设置为 5 分钟(300 秒)
        discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(discoverableIntent);
    }

    public void startScan_1(BlueScanListener blueScanListener) {
        this.blueScanListener = blueScanListener;
        registerDiscovery();
        bluetoothAdapter.startDiscovery();
    }

    public void stopScan_1() {
        bluetoothAdapter.cancelDiscovery();
        unregisterDiscovery();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startScan_2(BlueScanListener blueScanListener) {
        this.blueScanListener = blueScanListener;
        bluetoothAdapter.startLeScan(leScanCallback);

    }

    public void stopScan_2() {
        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startScan_3(BlueScanListener blueScanListener) {
        this.blueScanListener = blueScanListener;
        bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stopScan_3() {
        bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
    }

    //读取数据
    public void read(ClassBlueListener classBlueListener) {

    }

    //发送十六进制字符串
    public abstract void write(String data) throws Exception;

    //发送字节数据
    public abstract void write(byte[] b) throws Exception;

    //客户端连接
    public void connect(BluetoothDevice bluetoothDevice, ClientConnectListener ClientConnectListener) throws Exception {

    }

    //服务端等待连接
    public void accept(ServerAcceptListener serverAcceptListener) {

    }

    //关闭连接蓝牙连接
    public abstract void close();

    public void release() {

    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            blueScanListener.onScanResult(device);
        }
    };

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            blueScanListener.onScanResult(result.getDevice());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


    private static class BluetoothStateReceiver extends BroadcastReceiver {

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
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                /**
                 * int BOND_NONE = 10; //配对没有成功
                 * int BOND_BONDING = 11; //配对中
                 * int BOND_BONDED = 12; //配对成功
                 */
                int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE); //当前的配对的状态
                int state2 = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.BOND_NONE); //前一次的配对状态
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); //配对的设备信息
                LogUtil.d(state + "," + state2);
            }
        }
    }
}
