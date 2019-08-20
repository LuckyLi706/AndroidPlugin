package com.lucky.cacheplugin.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

/**
 * 作者：jacky on 2019/8/18 17:00
 * 邮箱：jackyli706@gmail.com
 */
public class PathUtil {

    public static String DIRECTORY_ALARMS = "Alarms";
    public static String DIRECTORY_AUDIOBOOKS = "Audiobooks";
    public static String DIRECTORY_DCIM = "DCIM";
    public static String DIRECTORY_DOCUMENTS = "Documents";
    public static String DIRECTORY_DOWNLOADS = "Download";
    public static String DIRECTORY_MOVIES = "Movies";
    public static String DIRECTORY_MUSIC = "Music";
    public static String DIRECTORY_NOTIFICATIONS = "Notifications";
    public static String DIRECTORY_PICTURES = "Pictures";
    public static String DIRECTORY_PODCASTS = "Podcasts";
    public static String DIRECTORY_RINGTONES = "Ringtones";
    public static String DIRECTORY_SCREENSHOTS = "Screenshots";

    //  /data目录
    public static String getDataDir() {
        return Environment.getDataDirectory().getAbsolutePath();
    }

    //  /system目录
    public static String getSystemDir() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    //  /cache目录
    public static String getCacheDir() {
        return Environment.getDownloadCacheDirectory().getAbsolutePath();
    }

    /**
     * @param name 都存储在SD卡公共目录下（也可以自己写自定义的文件夹名字,表示创建文件夹）
     *             DIRECTORY_DCIM  存储在/storage/emulated/0/DCIM
     *             DIRECTORY_ALARMS 存储在/storage/emulated/0/ALARMS
     * @return
     */
    public static String getPublicDir(String name) {
        //该方法9.0上已经过时,暂时还没有解决方案
        return Environment.getExternalStoragePublicDirectory(name).getAbsolutePath();
    }

    public static String getExternalStorageDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getExternalFilesDir(Context context) {
        return getExternalFilesDir(context, null);
    }

    /**
     * 4.4以下不存在该方法
     * 4.4位置在/storage/sdcard/Android/data/包名/files，
     * 5.0以上位置在/storage/emulated/0/Android/data/包名/files
     * 表示app外部私有目录，不需要任何权限
     *
     * @param context 上下文对象
     * @param name    为null获取上面的路径。不为null表示在该目录下创建该路径
     * @return
     */
    public static String getExternalFilesDir(Context context, String name) {
        File file = context.getExternalFilesDir(name);
        if (file != null) {
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     * 4.4以下不存在该方法
     * 4.4位置在/storage/sdcard/Android/data/包名/cache，
     * 5.0以上位置在/storage/emulated/0/Android/data/包名/cache
     * 表示app外部私有目录，不需要任何权限
     *
     * @param context 上下文对象
     * @return
     */
    public static String getExternalCacheDir(Context context) {
        File file = context.getExternalCacheDir();
        if (file != null) {
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     * 存储位置在/data/data
     *
     * @param context
     * @return
     */
    public static String getAppDir(Context context) {
        String path = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            path = context.getDataDir().getAbsolutePath();
        } else {
            path = context.getDir("name", Context.MODE_PRIVATE).getParent();
        }
        return path;
    }

    /**
     * 存储位置在/data/data/包名/files
     * 单用户模式（/data/user/0/包名/files）软链接到/data/data/包名/files
     * 多用户（第二个用户 /data/user/10/包名/files）
     *
     * @param context
     * @return
     */
    public static String getInternalFileDir(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    /**
     * 存储位置在/data/data/包名/app_文件夹
     * 多用户同上
     *
     * @param context 上下文对象
     * @param name    文件夹名字,有一个前缀app_
     * @return
     */
    public static String getInternalDir(Context context, String name) {
        return context.getDir(name, Context.MODE_PRIVATE).getAbsolutePath();
    }

    /**
     * 存储位置在data/data/包名/cache
     * 多用户同上
     *
     * @param context
     * @return
     */
    public static String getInternalCacheDir(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    /**
     * 存储位置在data/data/包名/databases
     * 中间的字符串不能写null和空，表示建立的文件夹）多用户同上
     *
     * @param context
     * @return
     */
    public static String getInternalDatabaseDir(Context context) {
        return context.getDatabasePath("12").getParent();
    }

    /**
     * 版本大于等于5.0
     * 存储位置在/data/data/包名/code_cache
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String getInternalCodeCacheDir(Context context) {
        return context.getCodeCacheDir().getAbsolutePath();
    }

    public static String getInternalSharePreDir(Context context) {
        return getAppDir(context) + File.separator + "/shared_prefs";
    }
}
