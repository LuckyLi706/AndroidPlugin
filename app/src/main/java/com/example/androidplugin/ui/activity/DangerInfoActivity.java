package com.example.androidplugin.ui.activity;

import android.view.View;
import android.widget.AdapterView;

import com.example.androidplugin.R;
import com.example.androidplugin.base.BaseActivity;
import com.example.androidplugin.ui.adpater.MainAdapter;
import com.lucky.danagerinfoplugin.DangerProtector;

/**
 * 作者：jacky on 2019/8/20 16:09
 * 邮箱：jackyli706@gmail.com
 */
public class DangerInfoActivity extends BaseActivity {

    @Override
    public void initData() {
        boolean emulator = DangerProtector.isEmulator(this);
        boolean root = DangerProtector.isRoot(this);
        boolean debug = DangerProtector.isDebug(this);
        boolean xposedExsit = DangerProtector.isXposedExsit(this);
        boolean multipleApp = DangerProtector.isMultipleApp(this);
        mainValue = new String[]{"模拟器信息:" + emulator, "Root信息:" + root, "Debug信息:" + debug, "Xposed信息:" + xposedExsit, "多开信息:" + multipleApp};
        mainAdapter = new MainAdapter(this, mainValue);
        listView.setAdapter(mainAdapter);
    }

    @Override
    public void initView() {
        listView = findViewById(R.id.listview_dangerinfo);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_dangerinfo;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
