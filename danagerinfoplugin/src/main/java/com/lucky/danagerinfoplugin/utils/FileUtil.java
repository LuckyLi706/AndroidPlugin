package com.lucky.danagerinfoplugin.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class FileUtil {

    public static String checkPackageName(String packageName, Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(intent, 0);
        //for循环遍历ResolveInfo对象获取包名和类名
        for (int i = 0; i < apps.size(); i++) {
            ResolveInfo info = apps.get(i);
            String pkgName = info.activityInfo.packageName;
            if (pkgName.equals(packageName)) {
                return "1";
            }
        }
        return "0";
    }

    /**
     * 判断路径是否存在
     * @param path
     * @return
     */
    public static String checkPath(String path) {
        File file = new File(path);
        if (file.exists()) {
            return "1";
        } else {
            if (file.isDirectory()) {
                return "1";
            }
            return "0";
        }
    }

    /**
     * 获取文件内容
     * @param path
     * @return
     */
    public static String getFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return "notExist";
            }
            if (file.isDirectory()) {
                StringBuilder builder = new StringBuilder();
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    builder.append(files[i].getName());
                }
                return builder.toString();
            }
            StringBuilder builder = new StringBuilder();
            String data = null;
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            while ((data = br.readLine()) != null) {
                builder.append(data);
            }
            br.close();
            return builder.toString();
        } catch (Throwable e) {
            // BSLog.w(str + "collect error");
        }
        return "";
    }
}
