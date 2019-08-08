package com.lucky.danagerinfoplugin.recognite;

import android.content.Context;

import com.lucky.danagerinfoplugin.utils.CommandUtils;
import com.lucky.danagerinfoplugin.utils.FileUtil;
import com.lucky.danagerinfoplugin.utils.LogUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Root {

    private static Root root;

    public static Root getInstance() {
        if (root == null) {
            root = new Root();
        }
        return root;
    }

    private String rootTag;

    private void printLog() {
        LogUtil.d("Root信息");
        LogUtil.d("Root的tag:" + rootTag);
       // return root;
    }

    public boolean isRoot(Context context) {
        rootTag = checkRootPackage(context) + checkSecure() + checksuFile() + checkRootWhichSU();
        if (rootTag.contains("1")) {
            printLog();
            return true;
        } else {
            return false;
        }
    }

    private String[] rootPackage = {"com.qihoo.permmgr", "com.noshufou.android.su", "eu.chainfire"
            + ".supersu", "com.kingroot.kinguser", "com.kingouser.com", "com.koushikdutta" +
            ".superuser", "com.dianxinos.superuser", "com.lbe.security.shuame", "com.geohot" +
            ".towelroot", "com.genymotion.superuser", "com.speedsoftware.superuser", "com" +
            ".thirdparty.superuser", "com.topjohnwu.magisk"};

    private String checkRootPackage(Context context) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < rootPackage.length; i++) {
            String value = FileUtil.checkPackageName(rootPackage[i], context);
            buffer.append(value);
        }
        return buffer.toString();
    }

    private String checkSecure() {
        int secureProp = getroSecureProp();
        if (secureProp == 0) {
            return "1";
        }//eng/userdebug版本，自带root权限
        return "0";
    }

    private String checksuFile() {
        String[] paths = {"/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su",
                "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data" +
                "/local/su"};
        StringBuffer buffer = new StringBuffer();
        for (String path : paths) {
            buffer.append(FileUtil.checkPath(path));
        }
        return buffer.toString();
    }

    private int getroSecureProp() {
        int secureProp;
        String roSecureObj = CommandUtils.getInstance().getProperty("ro.secure");
        if (roSecureObj == null) secureProp = 1;
        else {
            if ("0".equals(roSecureObj)) secureProp = 0;
            else secureProp = 1;
        }
        return secureProp;
    }

    private static String checkRootWhichSU() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) {
                return "1";
            }
            return "0";
        } catch (Throwable t) {
            return "0";
        }
    }
}
