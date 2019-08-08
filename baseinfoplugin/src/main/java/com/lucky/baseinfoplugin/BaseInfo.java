package com.lucky.baseinfoplugin;

import android.content.Context;

import com.lucky.baseinfoplugin.info.HardwareInfo;
import com.lucky.baseinfoplugin.info.NetworkInfo;
import com.lucky.baseinfoplugin.info.OtherInfo;
import com.lucky.baseinfoplugin.info.PhoneInfo;
import com.lucky.baseinfoplugin.info.SimInfo;

import java.util.Map;

public class BaseInfo {

    public static Map<String, String> getPhoneInfo() {
        return PhoneInfo.getInstance().getData();
    }

    public static Map<String, String> getNetworkInfo(Context context) {
        return NetworkInfo.getInstance().getData(context);
    }

    public static Map<String, String> getSimInfo(Context context) {
        return SimInfo.getInstance().getData(context);
    }

    public static Map<String, String> getHardwareInfo(Context context) {
        return HardwareInfo.getInstance().getData(context);
    }

    public static Map<String, String> getOtherInfo(Context context) {
        return OtherInfo.getInstance().getData(context);
    }

    public static void printAllMessage(Context context) {
        PhoneInfo.getInstance().printLog();
        NetworkInfo.getInstance().printLog(context);
        SimInfo.getInstance().printLog(context);
        HardwareInfo.getInstance().printLog(context);
        OtherInfo.getInstance().printLog(context);
    }
}
