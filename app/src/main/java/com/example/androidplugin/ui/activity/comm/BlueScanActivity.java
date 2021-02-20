package com.example.androidplugin.ui.activity.comm;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidplugin.R;

public class BlueScanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_scan);

        recyclerView = findViewById(R.id.rv_scan_result);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  //默认条目垂直展示


        findViewById(R.id.btn_scan).setOnClickListener(v -> {

        });
    }
}
