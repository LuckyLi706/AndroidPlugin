package com.example.androidplugin.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.androidplugin.R;
import com.example.androidplugin.base.BaseActivity;
import com.example.androidplugin.ui.adpater.MainAdapter;

public class MainActivity extends BaseActivity {

    @Override
    public void initData() {
        mainValue = new String[]{"Intent", "Cache", "自定义Camera", "危险信息检测"};
        mainAdapter = new MainAdapter(this, mainValue);
        listView.setAdapter(mainAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void initView() {
        listView = findViewById(R.id.listView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(this, "" + i, Toast.LENGTH_SHORT).show();
        if (i == 0) {
            startActivity(new Intent(this, IntentActivity.class));
        } else if (i == 1) {
            startActivity(new Intent(this, CacheActivity.class));
        } else if (i == 2) {
            startActivity(new Intent(this, CameraActivity.class));
        } else if (i == 3) {
            startActivity(new Intent(this, DangerInfoActivity.class));
        }
    }
}
