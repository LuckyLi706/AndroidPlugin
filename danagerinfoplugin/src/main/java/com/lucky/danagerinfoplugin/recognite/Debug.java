package com.lucky.danagerinfoplugin.recognite;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.lucky.danagerinfoplugin.utils.LogUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Debug {
    private static final Debug ourInstance = new Debug();

    public static Debug getInstance() {
        return ourInstance;
    }

    private Debug() {
    }

    private String debugTag;

    public boolean isDebug(Context context) {
        debugTag = antiDebug(context) + hasTracerPid();
        if (debugTag.contains("1")) {
            printLog();
            return true;
        }
        return false;
    }

    private void printLog() {
        LogUtil.d("Debug信息");
        //LogUtil.d("是否Debug:" + debugTag.contains("1"));
        LogUtil.d("模拟器识别的tag:" + debugTag);
        // return ourInstance;
    }


    //1.直接调用系统的android.os.Debug.isDebuggerConnected()方法
    //2.查看配置文件是否存在android:debuggable="true"
    //返回1表示处于开启debuggle模式
    //返回0则没有
    private String antiDebug(Context context) {
        try {
            boolean debug =
                    (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            return debug + "";
        } catch (Throwable throwable) {
        }
        return "0";
    }

    private static String tracerpid = "TracerPid";


    private String hasTracerPid() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/self" +
                    "/status")), 1000);
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.length() > tracerpid.length()) {
                    if (line.substring(0, tracerpid.length()).equalsIgnoreCase(tracerpid)) {
                        if (Integer.decode(line.substring(tracerpid.length() + 1).trim()) > 0) {
                            return "1";
                        }
                        break;
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return "0";
    }
}
