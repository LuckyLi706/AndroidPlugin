package com.lucky.commplugin.usb;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.lucky.commplugin.CommConfig;
import com.lucky.commplugin.listener.UsbConnectListener;
import com.lucky.commplugin.listener.UsbStateListener;
import com.lucky.commplugin.usb.usbserial.driver.UsbSerialDriver;
import com.lucky.commplugin.usb.usbserial.driver.UsbSerialPort;
import com.lucky.commplugin.usb.usbserial.driver.UsbSerialProber;
import com.lucky.commplugin.usb.usbserial.util.SerialInputOutputManager;
import com.lucky.commplugin.utils.LogUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

import static com.lucky.commplugin.utils.HexDump.hexStringToByteArray;

/**
 * usb library {https://github.com/mik3y/usb-serial-for-android  v3.3.0}
 * <code>
 * 关联某个activity,连接usb设备就启动activity
 * <intent-filter>
 * <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
 * </intent-filter>
 * <meta-data
 * android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"
 * android:resource="@xml/device_filter" />
 * <p>
 * 根据xml下关联的配置文件进行匹配
 * </code>
 */
public class UsbManagerClient {

    private UsbManager usbManager;      //USB管理类

    private Context context;            //上下文对象
    private CommConfig commConfig;      //配置信息
    private UsbReceiver usbReceiver;   //广播
    private PendingIntent mPermissionIntent;  //需要申请的权限
    private UsbStateListener usbStateListener;  //usb状态
    private UsbConnectListener usbConnectListener;  //usb建立通信的连接状态
    private UsbSerialPort sPort;
    private SerialInputOutputManager mSerialIoManager;
    private UsbSerialDriver usbSerialDriver;
    private boolean isConnect;

    @SuppressLint("StaticFieldLeak")
    private static UsbManagerClient usbManagerClient;

    private UsbManagerClient() {
    }

    public static UsbManagerClient getInstance() {
        if (usbManagerClient == null) {
            usbManagerClient = new UsbManagerClient();
        }
        return usbManagerClient;
    }

    /**
     * 初始化usb数据
     *
     * @param context    上下文对象
     * @param commConfig 配置信息
     */
    public void init(Context context, CommConfig commConfig, UsbStateListener usbStateListener) {
        this.context = context.getApplicationContext();
        this.commConfig = commConfig;
        this.usbStateListener = usbStateListener;
        registerReceiver();
    }

    /**
     * 判断对应 USB 设备是否有权限
     */
    private boolean hasPermission(UsbDevice device) {
        return usbManager.hasPermission(device);
    }

    /**
     * 请求获取指定 USB 设备的权限
     */
    private void requestPermission(UsbDevice device) {
        if (device != null) {
            usbManager.requestPermission(device, mPermissionIntent);
        } else {
            LogUtil.w(this, "设备未连接");
        }
    }

    public List<UsbSerialDriver> getConnectDevice() {
        if (usbManager == null) {
            usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        }
        return UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
    }

    /**
     * 打开Usb连接
     * 检测权限等
     */
    public void openUsbConnection(UsbSerialDriver driver, UsbConnectListener usbConnectListener) {
        this.usbConnectListener = usbConnectListener;
        this.usbSerialDriver = driver;
        UsbDevice device = driver.getDevice();
        if (hasPermission(driver.getDevice())) {
            openUsbDevice(driver);
            return;
        }
        requestPermission(device);
    }


    /**
     * 建立连接
     *
     * @param driver
     */
    private void openUsbDevice(UsbSerialDriver driver) {
        if (usbManager == null) {
            return;
        }
        UsbDeviceConnection connection = usbManager.openDevice(driver.getDevice());
        if (driver.getPorts().isEmpty() || connection == null) {
            LogUtil.e(this, "设备接口为空或者获取连接失败");
            if (usbConnectListener != null) {
                usbConnectListener.connectFail("设备接口为空或者获取连接失败");
            }
            return;
        }
        //获取设备接口，一般只有一个
        sPort = driver.getPorts().get(0);
        try {
            sPort.open(connection);
            sPort.setParameters(commConfig.getBaudRate(), commConfig.getDataBits(), commConfig.getStopBits(), commConfig.getParity());
            isConnect = true;
            if (usbConnectListener != null) {
                usbConnectListener.connectSuccess();
            }
        } catch (IOException e) {
            try {
                sPort.close();
            } catch (IOException e2) {
                LogUtil.e(this, e2.getMessage());
            }
        }
    }

    /**
     * 注册广播
     */
    private void registerReceiver() {
        if (usbReceiver == null) {
            usbReceiver = new UsbReceiver();
        }
        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(UsbReceiver.ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(UsbReceiver.ACTION_USB_PERMISSION);
        filter.addAction(android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED);
        context.registerReceiver(usbReceiver, filter);
    }

    /**
     * 取消广播
     */
    private void unregisterReceiver() {
        context.unregisterReceiver(usbReceiver);
    }

    /**
     * 异步读取数据
     */
    public void readDataAsync(SerialInputOutputManager.Listener mListener) {
        stopIoManager();
        if (sPort != null && isConnect) {
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            Executors.newSingleThreadExecutor().submit(mSerialIoManager);
        } else {
            LogUtil.w(this, "设备未连接");
        }
    }


    /**
     * 同步读取数据
     *
     * @param b 接收的字节数组
     * @return
     */
    protected int readDataSync(byte[] b) {
        if (sPort != null) {
            try {
                return sPort.read(b, 1000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 发送string类型的数据
     *
     * @param data 数据
     */
    public void sendBuffer(String data) {
        if (sPort != null && isConnect) {
            try {
                sPort.write(hexStringToByteArray(data), 500);
            } catch (IOException e) {
                LogUtil.d("发送数据失败" + e.getMessage());
            }
        } else {
            LogUtil.w(this, "设备未连接");
        }
    }

    /**
     * 发送字节数据
     *
     * @param b 数据
     */
    public void sendBuffer(byte[] b) {
        if (sPort != null && isConnect) {
            try {
                sPort.write(b, 500);
            } catch (IOException e) {
                LogUtil.e(this, "发送数据失败" + e.getMessage());
            }
        } else {
            LogUtil.w(this, "设备未连接");
        }
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    //关闭usb连接
    public void closeUsbConnection() {
        stopIoManager();
        isConnect = false;
        if (sPort != null) {
            try {
                sPort.close();
                sPort = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //释放usb
    public void releaseUsb() {
        closeUsbConnection();
        unregisterReceiver();
    }


    //广播
    private class UsbReceiver extends BroadcastReceiver {

        public static final String ACTION_USB_PERMISSION = "com.lucky.usb.USB_PERMISSION";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                // 获取权限结果的广播
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (usbSerialDriver != null) {
                                openUsbConnection(usbSerialDriver, usbConnectListener);
                            }
                            LogUtil.d("权限获取成功");
                        } else {
                            if (usbConnectListener != null) {
                                usbConnectListener.connectFail("权限获取失败");
                            }
                            LogUtil.w(this, "权限获取失败");
                        }
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                // 有新的设备插入了，在这里一般会判断这个设备是不是我们想要的，是的话就去请求权限
                if (usbStateListener != null) {
                    usbStateListener.attach();
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                // 有设备拔出了
                isConnect = false;
                if (usbStateListener != null) {
                    usbStateListener.detach();
                }
            }
        }
    }
}
