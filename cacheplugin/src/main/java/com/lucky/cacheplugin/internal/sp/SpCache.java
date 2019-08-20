package com.lucky.cacheplugin.internal.sp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * 作者：jacky on 2019/8/12 19:51
 * 邮箱：jackyli706@gmail.com
 */
public class SpCache {
    private SharedPreferences.Editor editor;
    private SharedPreferences sp;
    private static Map<String, SpCache> map = new HashMap<>();
    public String filename;


    public static SpCache getInstance(Context context, String fileName) {
        synchronized (SpCache.class) {
            if (!map.containsKey(fileName)) {
                SpCache sInstance = new SpCache(context, fileName);
                if (fileName == null || fileName.equals("")) {
                    sInstance.filename = context.getPackageName() + "_preferences";
                } else {
                    sInstance.filename = fileName;
                }
                map.put(fileName, sInstance);
            }
        }
        return map.get(fileName);
    }


    @SuppressLint("CommitPrefEdits")
    private SpCache(Context context, String filename) {
        if (filename == null || filename.equals("")) {
            //文件名为空,
            // 该SharedPreferences MODE默认为MODE_PRIVATE，而且生成文件名为
            //<包名>_preferences.xml
            sp = PreferenceManager.getDefaultSharedPreferences(context);
            editor = sp.edit();
        } else {
            sp = context.getSharedPreferences(filename, MODE_PRIVATE);
            editor = sp.edit();
        }
    }

    /**
     * 存储数据
     */
    public void putValue(String key, Object object) {
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 获取数据
     */
    public Object getValue(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        } else {
            return sp.getString(key, null);
        }
    }

    /**
     * 移除某个key值已经对应的值
     */
    public void removeValue(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除文件的数据,并不删除数据
     */
    public void clearAll() {
        editor.clear();
        editor.commit();
    }

    /**
     * 删除当前文件
     */
    public void deleteFile(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            String absolutePath = context.getDataDir().getAbsoluteFile().toString();
            File file = new File(absolutePath + "/" + context.getPackageName() + "/shared_prefs", this.filename);
            if (file.exists()) {
                file.delete();
            }
        } else {
            String absolutePath = Environment.getDataDirectory().getAbsolutePath();
            File file = new File(absolutePath + "/data/" + context.getPackageName() + "/shared_prefs", this.filename + ".xml");
            Log.d("ddd", file.getAbsolutePath());
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
