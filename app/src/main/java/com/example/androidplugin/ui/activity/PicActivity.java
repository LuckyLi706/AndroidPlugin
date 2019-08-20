package com.example.androidplugin.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.androidplugin.R;

/**
 * 作者：jacky on 2019/8/18 12:00
 * 邮箱：jackyli706@gmail.com
 */
public class PicActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);
        ImageView imageView = findViewById(R.id.im);

        Intent intent = getIntent();
        if (intent != null) {
            String path = intent.getStringExtra("path");
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bitmap);
        }
    }
}
