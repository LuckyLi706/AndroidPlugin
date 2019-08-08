package com.lucky.baseinfoplugin.info;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import com.lucky.baseinfoplugin.utils.LogUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class OtherInfo {
    private static final OtherInfo ourInstance = new OtherInfo();

    public static OtherInfo getInstance() {
        return ourInstance;
    }

    private OtherInfo() {
    }


    public Map<String, String> getInfo(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("字体hash值", getFontHash());
        map.put("当前语言", getUseLanguage());
        map.put("时区", timeZone());
        return map;
    }

    public Map<String, String> getData(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("fontHash", getFontHash());
        map.put("useLanguage", getUseLanguage());
        map.put("timeZone", timeZone());
        return map;
    }


    public void printLog(Context context) {
        Map<String, String> map = getInfo(context);
        LogUtil.d("其他信息");
        Set<String> set = map.keySet();
        for (String s : set) {
            LogUtil.d(s + ":" + map.get(s));
        }
    }

    private String getFontHash() {
        try {
            StringBuilder builder = new StringBuilder();
            File temp = new File("/system/fonts/");
            String fontSuffix = ".ttf";
            for (File font : temp.listFiles()) {
                String fontName = font.getName();
                builder.append(fontName);
            }
            return builder.toString();
        } catch (Throwable e) {

        }
        return "";
    }

    private String getUseLanguage() {
        String lan = Locale.getDefault().getLanguage();
        return lan;
    }

    private String timeZone() {
        try {
            TimeZone timeZone = TimeZone.getDefault();
            return ("[" + timeZone.getDisplayName(false, TimeZone.SHORT) + "," + timeZone.getID() + "]").replace("=", "").replace("&", "");
        } catch (Throwable e) {
            LogUtil.w(this, "Timezone Collect Error");
        }
        return "";
    }

}
