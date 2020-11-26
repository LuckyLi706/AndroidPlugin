package com.example.androidplugin.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidplugin.R;
import com.lucky.commplugin.CommConfig;
import com.lucky.commplugin.bluetooth.BluetoothManagerClient;
import com.lucky.commplugin.listener.UsbConnectListener;
import com.lucky.commplugin.listener.UsbStateListener;
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

        findViewById(R.id.btn_connect_bluetooth).setOnClickListener((v -> {
            BluetoothManagerClient.getInstance().initBluetooth(this);
        }));
    }

    @Override
    public void onNewData(byte[] data) {
        LogUtil.d(HexDump.bytesToHexString(data));
        // runOnUiThread(() -> Toast.makeText(CommActivity.this, HexDump.bytesToHexString(data), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRunError(Exception e) {

    }
}
