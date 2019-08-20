package com.example.androidplugin.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;

import com.example.androidplugin.R;
import com.lucky.cameraplugin.camera1.CameraHelper;

/**
 * 作者：jacky on 2019/8/20 10:45
 * 邮箱：jackyli706@gmail.com
 */
public class CameraActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        SurfaceView surfaceView = findViewById(R.id.surfaceview);
        CameraHelper cameraHelper = new CameraHelper(surfaceView, this);
    }
}
