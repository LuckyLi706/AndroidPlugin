package com.example.androidplugin.ui.activity;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.androidplugin.R;
import com.example.androidplugin.base.BaseActivity;
import com.lucky.cacheplugin.utils.PathUtil;

/**
 * 作者：jacky on 2019/8/18 18:49
 * 邮箱：jackyli706@gmail.com
 */
public class CacheActivity extends BaseActivity {

    private TextView tv;

    @Override
    public void initData() {
        tv.setText(getFilePath(this));
    }

    @Override
    public void initView() {
        tv = findViewById(R.id.tv);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_cache;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public String getFilePath(Context context) {
        StringBuilder sb = new StringBuilder("");
        sb.append(PathUtil.getCacheDir()).append("\n").append(PathUtil.getDataDir()).append("\n").append(PathUtil.getSystemDir()).append("\n").append(PathUtil.getExternalStorageDir())
                .append("\n").append(PathUtil.getExternalCacheDir(context)).append("\n").append(PathUtil.getAppDir(context)).append("\n").append(PathUtil.getInternalDir(context,"name")).append("\n")
                .append(PathUtil.getInternalCacheDir(context)).append("\n").append(PathUtil.getInternalFileDir(context)).append("\n").append(PathUtil.getInternalCodeCacheDir(context)).append("\n")
                .append(PathUtil.getInternalDatabaseDir(context)).append("\n").append(PathUtil.getInternalSharePreDir(context));
        return sb.toString();
    }
}
