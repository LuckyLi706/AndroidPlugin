package com.example.androidplugin.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidplugin.Constants;
import com.example.androidplugin.R;
import com.example.androidplugin.ui.activity.comm.BlueMessageActivity;
import com.example.androidplugin.ui.activity.comm.BlueScanActivity;

public class CommActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm);

        findViewById(R.id.classic_blue_client).setOnClickListener(v -> {
            Intent intent = new Intent(this, BlueScanActivity.class);
            intent.putExtra("name", Constants.CLASSIC_BLUE_CLIENT);
            startActivity(intent);
        });

        findViewById(R.id.classic_blue_server).setOnClickListener(v -> {
            Intent intent = new Intent(this, BlueMessageActivity.class);
            intent.putExtra("name", Constants.BLE_BLUE_CLIENT);
            startActivity(intent);
        });

        findViewById(R.id.ble_blue_client).setOnClickListener(v -> {
            Intent intent = new Intent(this, BlueScanActivity.class);
            intent.putExtra("name", Constants.CLASSIC_BLUE_SERVER);
            startActivity(intent);
        });

        findViewById(R.id.ble_blue_server).setOnClickListener(v -> {
            Intent intent = new Intent(this, BlueScanActivity.class);
            intent.putExtra("name", Constants.BLE_BLUE_SERVER);
            startActivity(intent);
        });

        findViewById(R.id.usb).setOnClickListener(v -> {

        });
    }
}
