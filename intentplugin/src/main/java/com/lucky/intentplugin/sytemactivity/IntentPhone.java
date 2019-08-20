package com.lucky.intentplugin.sytemactivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.lucky.intentplugin.utils.LogUtil;
import com.lucky.intentplugin.utils.PermissionUtil;

/**
 * 作者：jacky on 2019/8/20 08:40
 * 邮箱：jackyli706@gmail.com
 */
public class IntentPhone {
    private static final IntentPhone ourInstance = new IntentPhone();

    public static IntentPhone getInstance() {
        return ourInstance;
    }

    private IntentPhone() {
    }

    //携带电话号码调转到系统电话
    public void startPhone(Context context, String phoneNumber) {
        Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));//跳转到拨号界面，同时传递电话号码
        if (intentPhone.resolveActivity(context.getPackageManager()) != null) {
            if (context instanceof Activity) {
                ((Activity) context).startActivity(intentPhone);
            }
        } else {
            LogUtil.w(this, "系统电话不存在");
        }
    }

    //直接拨打电话号码
    @SuppressLint("MissingPermission")
    public void startDirectPhone(Context context, String phoneNumber) {
        if (PermissionUtil.CALL_PHONE(context)) {
            Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));//直接拨打电话
            if (intentPhone.resolveActivity(context.getPackageManager()) != null) {
                if (context instanceof Activity) {
                    ((Activity) context).startActivity(intentPhone);
                }
            } else {
                LogUtil.w(this, "系统电话不存在");
            }
        } else {
            LogUtil.e(this, "CALL_PHONE权限不存在");
        }
    }
}
