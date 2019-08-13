package com.example.androidplugin;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.lucky.cacheplugin.internal.sp.SPUtil;
import com.lucky.danagerinfoplugin.DangerProtector;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DangerProtector.isEmulator(this);

        SPUtil spUtil = SPUtil.getInstance(this, "");

        SPUtil spUtil1= SPUtil.getInstance(this, "username");

        spUtil.deleteFile(this);
        spUtil1.deleteFile(this);

        Log.d("ddd", spUtil.filename + "-" + spUtil1.filename);
    }
}
