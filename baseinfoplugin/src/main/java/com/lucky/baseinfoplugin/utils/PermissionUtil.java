package com.lucky.baseinfoplugin.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public class PermissionUtil {

    /**
     * 对网络权限做一些判断
     */
    public static boolean INTERNET(Context context) {
        return PackageManager.PERMISSION_GRANTED == context.getPackageManager().checkPermission("android" +
                ".permission.INTERNET", context.getPackageName());
    }

    public static boolean ACCESS_NETWORK_STATE(Context context) {
        return PackageManager.PERMISSION_GRANTED == context.getPackageManager().checkPermission("android" +
                ".permission.ACCESS_NETWORK_STATE", context.getPackageName());
    }

    public static boolean ACCESS_WIFI_STATE(Context context) {
        return PackageManager.PERMISSION_GRANTED == context.getPackageManager().checkPermission("android" +
                ".permission.ACCESS_WIFI_STATE", context.getPackageName());
    }

    public static boolean CHANGE_WIFI_STATE(Context context) {
        return PackageManager.PERMISSION_GRANTED == context.getPackageManager().checkPermission("android" +
                ".permission.CHANGE_WIFI_STATE", context.getPackageName());
    }

    public static boolean READ_PHONE_STATE(Context context) {
        return PackageManager.PERMISSION_GRANTED == context.getPackageManager().checkPermission("android" +
                ".permission.READ_PHONE_STATE", context.getPackageName());
    }
}
