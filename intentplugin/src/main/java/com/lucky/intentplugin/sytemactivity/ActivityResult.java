package com.lucky.intentplugin.sytemactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lucky.intentplugin.Constants;
import com.lucky.intentplugin.utils.LogUtil;

/**
 * 作者：jacky on 2019/8/20 09:25
 * 邮箱：jackyli706@gmail.com
 */
public class ActivityResult {
    private static final ActivityResult ourInstance = new ActivityResult();

    public static ActivityResult getInstance() {
        return ourInstance;
    }

    private ActivityResult() {
    }

    public String getResult(Context context, int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.INTENT_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                String path = IntentCamera.getInstance().getFilePath(context).getAbsolutePath();
                return null;
            } else {
                LogUtil.w(this, "拍照被取消");
            }
        } else if (requestCode == Constants.INTENT_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                String realPathFromUri = IntentPhoto.getInstance().getRealPathFromUri(context, data.getData());
                return realPathFromUri;
            } else {
                LogUtil.w(this, "选择图片被取消");
            }
        }
        return null;
    }
}
