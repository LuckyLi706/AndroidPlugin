package com.lucky.commplugin.utils;

import android.util.Log;

public class LogUtil {
    private static final String TAG = "lucky";

    private static boolean isDebug = true;

    public static void setDebug(boolean flag) {
        isDebug = flag;
    }

    public static void e(Object object, String info) {
        if (isDebug) {
            Log.e(TAG, "---------当前类名:" + object.getClass().getSimpleName() + "---------");
            Log.e(TAG, "---------当前方法名:" + new Exception().getStackTrace()[1].getMethodName() + "--------");
            Log.e(TAG, "---------当前异常:" + info + "--------");
        }
    }

    public static void w(Object object, String info) {
        if (isDebug) {
            Log.w(TAG, "---------当前类名:" + object.getClass().getSimpleName() + "---------");
            Log.w(TAG, "---------当前方法名:" + new Exception().getStackTrace()[1].getMethodName() + "--------");
            Log.w(TAG, "---------当前警告:" + info + "--------");
            Log.w(TAG, "         -----------------------          ");
        }
    }

    public static void w(String warn) {
        if (isDebug) {
            Log.d(TAG, "--------" + warn + "--------");
        }
    }

    public static void e(String error) {
        if (isDebug) {
            Log.d(TAG, "--------" + error + "--------");
        }
    }

    public static void d(String info) {
        if (isDebug) {
            Log.d(TAG, "--------" + info + "--------");
        }
    }
}
