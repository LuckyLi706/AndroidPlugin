package com.example.androidplugin.ui.activity.comm;

import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidplugin.Constants;
import com.example.androidplugin.R;
import com.example.androidplugin.ui.adpater.BlueScanAdapter;
import com.lucky.commplugin.CommConfig;
import com.lucky.commplugin.bluetooth.client.ClassicClient;
import com.lucky.commplugin.listener.BlueScanListener;

import java.util.ArrayList;
import java.util.List;

public class BlueScanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private BlueScanAdapter blueScanAdapter;

    private List<BluetoothDevice> bluetoothDevices = new ArrayList<>();

    private String name;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_scan);

        name = getIntent().getStringExtra("name");
        initView();
        initData();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initView() {
        recyclerView = findViewById(R.id.rv_scan_result);
        blueScanAdapter = new BlueScanAdapter(this, bluetoothDevices, name);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  //默认条目垂直展示
        recyclerView.setAdapter(blueScanAdapter);
        findViewById(R.id.btn_start_scan).setOnClickListener(v -> {
            if (name.equals(Constants.CLASSIC_BLUE_CLIENT)) {
                ClassicClient.getInstance().startScan_1(new BlueScanListener() {
                    @Override
                    public void onScanResult(BluetoothDevice bluetoothDevice) {
                        bluetoothDevices.add(bluetoothDevice);
                        blueScanAdapter.notifyDataSetChanged();
                    }
                });
            } else {
                ClassicClient.getInstance().startScan_3(new BlueScanListener() {
                    @Override
                    public void onScanResult(BluetoothDevice bluetoothDevice) {
                        bluetoothDevices.add(bluetoothDevice);
                        blueScanAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        findViewById(R.id.btn_stop_scan).setOnClickListener(v -> {
            if (name.equals(Constants.CLASSIC_BLUE_CLIENT)) {
                ClassicClient.getInstance().stopScan_1();
            } else {
                ClassicClient.getInstance().stopScan_3();
            }
        });
    }

    private void initData() {
        CommConfig commConfig = new CommConfig.Builder().classicUUID("").builder();
        ClassicClient.getInstance().initBluetooth(this, commConfig);
    }
}
