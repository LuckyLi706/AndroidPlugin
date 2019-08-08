package com.example.androidplugin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lucky.baseinfoplugin.BaseInfo;
import com.lucky.danagerinfoplugin.DangerProtector;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BaseInfo.printAllMessage(this);

        DangerProtector.isDebug(this);
        DangerProtector.isEmulator(this);
        DangerProtector.isRoot(this);
        DangerProtector.isXposedExsit(this);


    }
}
