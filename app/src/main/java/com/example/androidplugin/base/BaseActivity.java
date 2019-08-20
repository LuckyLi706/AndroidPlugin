package com.example.androidplugin.base;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.androidplugin.ui.adpater.MainAdapter;

/**
 * 作者：jacky on 2019/8/17 14:18
 * 邮箱：jackyli706@gmail.com
 */
public abstract class BaseActivity extends Activity implements AdapterView.OnItemClickListener {

    public String[] mainValue;
    public ListView listView;
    public MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initData();
    }

    public abstract void initData();

    public abstract void initView();

    public abstract int getLayoutId();
}
