package com.example.androidplugin.ui.activity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
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
import com.lucky.commplugin.bluetooth.server.ClassicServer;
import com.lucky.commplugin.listener.BlueScanListener;
import com.lucky.commplugin.listener.ClassBlueListener;
import com.lucky.commplugin.listener.ClientConnectListener;
import com.lucky.commplugin.listener.ServerAcceptListener;
import com.lucky.commplugin.listener.UsbConnectListener;
import com.lucky.commplugin.usb.UsbManagerClient;
import com.lucky.commplugin.usb.usbserial.driver.UsbSerialDriver;
import com.lucky.commplugin.usb.usbserial.util.SerialInputOutputManager;
import com.lucky.commplugin.utils.HexDump;
import com.lucky.commplugin.utils.LogUtil;

import java.util.List;

public class CommActivity extends AppCompatActivity implements SerialInputOutputManager.Listener {

    private BluetoothDevice bluetoothDevice;
    private EditText et_data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm);

        et_data = findViewById(R.id.et_data);
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

            BluetoothManager bluetoothManager1 = ClassicServer.getInstance();
            bluetoothManager1.initBluetooth(this);
            bluetoothManager.startScan_1(new BlueScanListener() {
                @Override
                public void onScanResult(BluetoothDevice bluetoothDevice) {
                    if (bluetoothDevice.getAddress().equals("AC:37:43:76:8D:34")) {
                        CommActivity.this.bluetoothDevice = bluetoothDevice;
                        bluetoothManager.stopScan_1();

                        Toast.makeText(CommActivity.this, "扫描成功", Toast.LENGTH_SHORT).show();
                    }
                    //LogUtil.d(bluetoothDevice.getName() + "," + bluetoothDevice.getAddress());
                }
            });
        }));

        findViewById(R.id.btn_scaned_bluetooth).setOnClickListener(v -> {
            BluetoothManager bluetoothManager = ClassicClient.getInstance();
            bluetoothManager.enableDiscovery();
        });

        findViewById(R.id.btn_stop_scan_bluetooth).setOnClickListener((v -> {
            BluetoothManager bluetoothManager = ClassicClient.getInstance();
            bluetoothManager.stopScan_2();
        }));

        findViewById(R.id.btn_start_client).setOnClickListener(v -> {
            if (bluetoothDevice == null) {
                runOnUiThread(() -> Toast.makeText(CommActivity.this, "设备未发现", Toast.LENGTH_SHORT).show());
                return;
            }
            ClassicClient.getInstance().connect(bluetoothDevice, new ClientConnectListener() {
                @Override
                public void connectSuccess() {
                    runOnUiThread(() -> Toast.makeText(CommActivity.this, "连接了", Toast.LENGTH_SHORT).show());
                    ClassicClient.getInstance().read(new ClassBlueListener() {
                        @Override
                        public void readClassicData(byte[] b) {
                            runOnUiThread(() -> Toast.makeText(CommActivity.this, new String(b), Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void readClassicError(Exception e) {
                          //  runOnUiThread(() -> Toast.makeText(CommActivity.this, "读取异常:" + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    });
                }

                @Override
                public void connectFail(Exception e) {
                    runOnUiThread(() -> Toast.makeText(CommActivity.this, "连接异常" + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });

        });

        findViewById(R.id.btn_stop_client).setOnClickListener(v -> {
            ClassicClient.getInstance().close();
        });

        findViewById(R.id.btn_client_send_data).setOnClickListener(v -> {
            ClassicClient.getInstance().write(et_data.getText().toString().getBytes());
        });

        findViewById(R.id.btn_start_server).setOnClickListener(v -> {

            ClassicServer.getInstance().accept(new ServerAcceptListener() {
                @Override
                public void connectSuccess(BluetoothSocket bluetoothSocket) {

                    runOnUiThread(() -> Toast.makeText(CommActivity.this, bluetoothSocket.getRemoteDevice().getAddress() + "连接了", Toast.LENGTH_SHORT).show());
                    ClassicServer.getInstance().read(new ClassBlueListener() {
                        @Override
                        public void readClassicData(byte[] b) {
                            runOnUiThread(() -> Toast.makeText(CommActivity.this, new String(b), Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void readClassicError(Exception e) {
                            runOnUiThread(() -> Toast.makeText(CommActivity.this, "读取异常:" + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    });
                }

                @Override
                public void connectFail(Exception e) {
                    runOnUiThread(() -> Toast.makeText(CommActivity.this, "连接异常" + e.getMessage(), Toast.LENGTH_SHORT).show());

                }
            });
        });

        findViewById(R.id.btn_stop_server).setOnClickListener(v -> {
            ClassicServer.getInstance().close();
        });

        findViewById(R.id.btn_server_send_data).setOnClickListener(v -> {
            ClassicServer.getInstance().write(et_data.getText().toString().getBytes());
        });


        //判断是否有权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            //判断是否需要 向用户解释，为什么要申请该权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this, "shouldShowRequestPermissionRationale",
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
