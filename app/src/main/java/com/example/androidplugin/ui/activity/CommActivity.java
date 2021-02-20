package com.example.androidplugin.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidplugin.R;

public class CommActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm);

        findViewById(R.id.classic_blue_client).setOnClickListener(v -> {

        });

        findViewById(R.id.classic_blue_server).setOnClickListener(v -> {

        });

        findViewById(R.id.ble_blue_client).setOnClickListener(v -> {

        });

        findViewById(R.id.ble_blue_server).setOnClickListener(v -> {

        });

        findViewById(R.id.usb).setOnClickListener(v -> {

        });
    }
}
