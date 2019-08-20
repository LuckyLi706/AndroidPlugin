package com.lucky.intentplugin.sytemactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.lucky.intentplugin.utils.LogUtil;

/**
 * 作者：jacky on 2019/8/20 08:54
 * 邮箱：jackyli706@gmail.com
 */
public class IntentSms {
    private static final IntentSms ourInstance = new IntentSms();

    public static IntentSms getInstance() {
        return ourInstance;
    }

    private IntentSms() {
    }

    /**
     * 跳转到系统短信页面
     *
     * @param context     上下文对象
     * @param phoneNumber 电话号码
     * @param message     短信内容
     */
    public void startSMS(Context context, String phoneNumber, String message) {
        Intent intentSMS = new Intent();                        //创建 Intent 实例
        intentSMS.setAction(Intent.ACTION_SENDTO);             //设置动作为发送短信
        if (intentSMS.resolveActivity(context.getPackageManager()) != null) {
            intentSMS.setData(Uri.parse("smsto:" + phoneNumber));           //设置发送的号码
            intentSMS.putExtra("sms_body", message);              //设置发送的内容
            ((Activity) context).startActivity(intentSMS);                            //启动 Activity
        } else {
            LogUtil.w(this, "系统短信应用不存在");
        }
    }
}
