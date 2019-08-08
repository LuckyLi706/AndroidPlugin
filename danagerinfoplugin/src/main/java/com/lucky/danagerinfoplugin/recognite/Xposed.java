package com.lucky.danagerinfoplugin.recognite;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.lucky.danagerinfoplugin.utils.LogUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 检测Xposed等作弊软件框架
 * 参考http://blog.csdn.net/u012417380/article/details/56675547
 */
public class Xposed {
    private static final Xposed ourInstance = new Xposed();

    public static Xposed getInstance() {
        return ourInstance;
    }

    private Xposed() {
    }

    private String xposedTag;

    public boolean isXposed(Context context) {
        xposedTag = checkPackage(context) + checkProc() + checkStack();
        try {
            if (xposedTag.contains("1")) {
                printLog();
                return true;
            } else {
                return false;
            }
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return false;
    }

    private void printLog() {
        LogUtil.d("Xposed信息");
        //LogUtil.d("是否Debug:" + debugTag.contains("1"));
        LogUtil.d("Xposed的tag:" + xposedTag);
        // return ourInstance;
    }


    /**
     * 用PakageManager类来检测包名来判断是否安装了Xposed框架和CydiaSubstrate框架。
     * 1表示Xposed
     * 2表示CydiaSubstrate
     * 0表示上面两种框架都没装
     *
     * @param context
     * @return
     */
    private String checkPackage(Context context) {
        try {
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            List<ApplicationInfo> appliacationInfoList =
                    packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo item : appliacationInfoList) {
                if (item.packageName.equals("de.robv.android.xposed.installer")) {
                    return "1";
                }
                if (item.packageName.equals("com.saurik.substrate")) {
                    return "2";
                }
                if (item.packageName.equals("io.va.exposed")) {
                    return "3";
                }
            }
        } catch (Throwable e) {
            //BSLog.w("Collect cheatCheck1 Error");
        }
        return "0";
    }

    /**
     * 1表示Substrate is active on the device.
     * 2表示A method on the stack trace has been hooked using Substrate.
     * 3表示Xposed is active on the device.
     * 4表示A method on the stack trace has been hooked using Xposed.
     *
     * @return
     */
    private String checkStack() {
        try {
            throw new Exception("Deteck hook");
        } catch (Exception e) {
            //StringBuffer buffer = new StringBuffer("");
            int zygoteInitCallCount = 0;
            for (StackTraceElement item : e.getStackTrace()) {
                // 检测"com.android.internal.os.ZygoteInit"是否出现两次，如果出现两次，则表明Substrate框架已经安装
                if (item.getClassName().equals("com.android.internal.os.ZygoteInit")) {
                    zygoteInitCallCount++;
                    if (zygoteInitCallCount == 2) {
                        //BSLog.w("Substrate is active on the device.");
                        //buffer.append("1");
                        return "1";
                    }
                }
                if (item.getClassName().equals("com.saurik.substrate.MS$2") && item.getMethodName().equals("invoke")) {
                    //BSLog.w("A method on the stack trace has been hooked using Substrate.");
                    return "2";
                }
                if (item.getClassName().equals("de.robv.android.xposed.XposedBridge") && item.getMethodName().equals("main")) {
                    //BSLog.w("Xposed is active on the device.");
                    return "3";
                }
                if (item.getClassName().equals("de.robv.android.xposed.XposedBridge") && item.getMethodName().equals("handleHookedMethod")) {
                    //BSLog.w("A method on the stack trace has been hooked using Xposed.");
                    return "4";
                }
            }
            return "0";
        }
    }

    //4.用 /proc/[pid]/maps来探测内存中可疑的对象和JARs对象。
    private String checkProc() {
        Set<String> libraries = new HashSet<String>();
        String mapsFilename = "/proc/" + android.os.Process.myPid() + "/maps";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mapsFilename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.endsWith(".so") || line.endsWith(".jar")) {
                    int n = line.lastIndexOf(" ");
                    libraries.add(line.substring(n + 1));
                }
            }
            for (String library : libraries) {
                if (library.contains("com.saurik.substrate")) {
                    //BSLog.w("Substrate shared object found: " + library);
                    return "1";
                }
                if (library.contains("XposedBridge.jar")) {
                    //BSLog.w("Xposed JAR found: " + library);
                    return "2";
                }
            }
            reader.close();
        } catch (Exception e) {
            //BSLog.w("Collect cheatCheck3 Error");
        }
        return "0";
    }
}
