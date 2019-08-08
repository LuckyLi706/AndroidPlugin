package com.lucky.baseinfoplugin.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public class PermissonUtil {

//    public static void validatePermission(Context context) {
//        BSLog.d("verifyPermission");
//        PackageManager packageManager = context.getPackageManager();
//        //check internet
//        String packageName = context.getPackageName();
//        boolean PERMISSION_GIVEN =
//                (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android" +
//                        ".permission.INTERNET", packageName));
//        if (!PERMISSION_GIVEN) {
//            BSLog.w("Lack necessary permission : android.permission.INTERNET");
//        }
//        //检测GSM权限
//        PERMISSION_GIVEN = (PackageManager.PERMISSION_GRANTED == packageManager.
//                checkPermission("android.permission.ACCESS_NETWORK_STATE", packageName));
//        if (!PERMISSION_GIVEN) {
//            BSLog.w("Lack permission : android.permission.ACCESS_NETWORK_STATE");
//        }
//        //检测WIFI网络权限
//        PERMISSION_GIVEN = (PackageManager.PERMISSION_GRANTED == packageManager.
//                checkPermission("android.permission.ACCESS_WIFI_STATE", packageName));
//        if (!PERMISSION_GIVEN) {
//            BSLog.w("Lack permission : android.permission.ACCESS_WIFI_STATE");
//        }
//        //检测连接一配对蓝牙的权限
//        PERMISSION_GIVEN = (PackageManager.PERMISSION_GRANTED == packageManager.
//                checkPermission("android.permission.BLUETOOTH", packageName));
//        if (!PERMISSION_GIVEN) {
//            BSLog.w("Lack permission : android.permission.BLUETOOTH");
//        }
//        //检测发现和配对蓝牙的权限
//        PERMISSION_GIVEN = (PackageManager.PERMISSION_GRANTED == packageManager.
//                checkPermission("android.permission.BLUETOOTH_ADMIN", packageName));
//        if (!PERMISSION_GIVEN) {
//            BSLog.w("Lack permission : android.permission.BLUETOOTH_ADMIN");
//        }
//        //检测手机状态权限
//        PERMISSION_GIVEN = (PackageManager.PERMISSION_GRANTED == packageManager.
//                checkPermission("android.permission.READ_PHONE_STATE", packageName));
//        if (!PERMISSION_GIVEN) {
//            BSLog.w("Lack permission : android.permission.READ_PHONE_STATE");
//        }
//        //检测手机的的写权限
//        PERMISSION_GIVEN = (PackageManager.PERMISSION_GRANTED == packageManager.
//                checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", packageName));
//        if (!PERMISSION_GIVEN) {
//            BSLog.w("Lack permission : android.permission.WRITE_EXTERNAL_STORAGE");
//        }
//        //检测手机的读权限
//        PERMISSION_GIVEN = (PackageManager.PERMISSION_GRANTED == packageManager.
//                checkPermission("android.permission.READ_EXTERNAL_STORAGE", packageName));
//        if (!PERMISSION_GIVEN) {
//            BSLog.w("Lack permission : android.permission.READ_EXTERNAL_STORAGE");
//        }
//        //检测手机允许改变wifi状态的权限
//        PERMISSION_GIVEN = (PackageManager.PERMISSION_GRANTED == packageManager.
//                checkPermission("android.permission.CHANGE_WIFI_STATE", packageName));
//        if (!PERMISSION_GIVEN) {
//            BSLog.w("Lack permission : android.permission.CHANGE_WIFI_STATE");
//        }
//        //允许一个程序访问CellID或WiFi热点来获取粗略的位置
//        PERMISSION_GIVEN = (PackageManager.PERMISSION_GRANTED == packageManager.
//                checkPermission("android.permission.ACCESS_COARSE_LOCATION", packageName));
//        if (!PERMISSION_GIVEN) {
//            BSLog.w("Lack permission : android.permission.ACCESS_COARSE_LOCATION");
//        }
//    }

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

//    public static boolean ACCESS_NETWORK_STATE(Context context) {
//        return PackageManager.PERMISSION_GRANTED == context.getPackageManager().checkPermission("android" +
//                ".permission.ACCESS_NETWORK_STATE", context.getOpPackageName());
//    }
}
