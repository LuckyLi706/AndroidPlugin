package com.example.androidplugin.ui.activity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.androidplugin.R;
import com.lucky.commplugin.CommConfig;
import com.lucky.commplugin.bluetooth.BluetoothManager;
import com.lucky.commplugin.bluetooth.client.ClassicClient;
import com.lucky.commplugin.listener.BlueScanListener;
import com.lucky.commplugin.listener.UsbConnectListener;
import com.lucky.commplugin.usb.UsbManagerClient;
import com.lucky.commplugin.usb.usbserial.driver.UsbSerialDriver;
import com.lucky.commplugin.usb.usbserial.util.SerialInputOutputManager;
import com.lucky.commplugin.utils.HexDump;
import com.lucky.commplugin.utils.LogUtil;

import java.util.List;

public class CommActivity extends AppCompatActivity implements SerialInputOutputManager.Listener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm);


        findViewById(R.id.btn_connect_usb).setOnClickListener((v -> {
            CommConfig commConfig = new CommConfig.Builder().builder();
            UsbManagerClient.getInstance().init(this, commConfig, null);
            List<UsbSerialDriver> connectDevice = UsbManagerClient.getInstance().getConnectDevice();
            if (connectDevice.size() > 0) {
                UsbManagerClient.getInstance().openUsbConnection(connectDevice.get(0), new UsbConnectListener() {
                    @Override
                    public void connectSuccess() {
                        Toast.makeText(CommActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                        UsbManagerClient.getInstance().readDataAsync(CommActivity.this);
                    }

                    @Override
                    public void connectFail(String message) {
                        Toast.makeText(CommActivity.this, "连接失败：" + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }));

        findViewById(R.id.btn_usb_send_data).setOnClickListener((v -> {
            UsbManagerClient.getInstance().sendBuffer("EEEEFF020000");
        }));

        findViewById(R.id.btn_start_scan_bluetooth).setOnClickListener((v -> {
            BluetoothManager bluetoothManager = ClassicClient.getInstance();
            bluetoothManager.initBluetooth(this);
            bluetoothManager.startScan_2(new BlueScanListener() {
                @Override
                public void onScanResult(BluetoothDevice bluetoothDevice) {
                    LogUtil.d(bluetoothDevice.getName() + "," + bluetoothDevice.getAddress());
                }
            });
        }));

        findViewById(R.id.btn_stop_scan_bluetooth).setOnClickListener((v -> {
            BluetoothManager bluetoothManager = ClassicClient.getInstance();
            bluetoothManager.stopScan_2();
        }));

        //判断是否有权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
            //判断是否需要 向用户解释，为什么要申请该权限
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this,"shouldShowRequestPermissionRationale",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onNewData(byte[] data) {
        LogUtil.d(HexDump.bytesToHexString(data));
        // runOnUiThread(() -> Toast.makeText(CommActivity.this, HexDump.bytesToHexString(data), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRunError(Exception e) {

    }

    //权限申请结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
