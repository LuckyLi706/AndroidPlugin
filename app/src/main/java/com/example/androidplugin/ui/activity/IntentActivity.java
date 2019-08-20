package com.example.androidplugin.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;

import com.example.androidplugin.R;
import com.example.androidplugin.base.BaseActivity;
import com.example.androidplugin.ui.adpater.MainAdapter;
import com.lucky.intentplugin.Constants;
import com.lucky.intentplugin.sytemactivity.IntentCamera;
import com.lucky.intentplugin.sytemactivity.IntentPhone;
import com.lucky.intentplugin.sytemactivity.IntentPhoto;
import com.lucky.intentplugin.sytemactivity.IntentSettings;
import com.lucky.intentplugin.sytemactivity.IntentSms;

/**
 * 作者：jacky on 2019/8/17 14:15
 * 邮箱：jackyli706@gmail.com
 */
public class IntentActivity extends BaseActivity {

    //private ImageView imageView;

    @Override
    public void initData() {
        mainValue = new String[]{"Camera", "Photo", "Phone", "SMS", "Settings"};
        mainAdapter = new MainAdapter(this, mainValue);
        listView.setAdapter(mainAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void initView() {
        listView = findViewById(R.id.lv_intent);
        // imageView = findViewById(R.id.im);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_intent;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            IntentCamera.getInstance().startCamera(this, IntentCamera.getInstance().getFilePath(this));
        } else if (i == 1) {
            IntentPhoto.getInstance().startPhoto(this);
        } else if (i == 2) {
            IntentPhone.getInstance().startDirectPhone(this, "123");
        } else if (i == 3) {
            IntentSms.getInstance().startSMS(this, "123", "123");
        } else if (i == 4) {
            IntentSettings.getInstance().startSettings(this, Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.INTENT_CAMERA) {
            //Bitmap bitmap = IntentCamera.getInstance().getPic(requestCode, resultCode, data);
            String path = IntentCamera.getInstance().getFilePath(this).getAbsolutePath();
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                Intent intent = new Intent(this, PicActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        } else if (requestCode == Constants.INTENT_PHOTO) {
            String realPathFromUri = IntentPhoto.getInstance().getRealPathFromUri(this, data.getData());
            Intent intent = new Intent(this, PicActivity.class);
            intent.putExtra("path", realPathFromUri);
            startActivity(intent);
        }
    }
}
