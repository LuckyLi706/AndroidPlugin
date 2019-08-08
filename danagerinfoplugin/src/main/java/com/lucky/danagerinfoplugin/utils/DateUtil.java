package com.lucky.danagerinfoplugin.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /* //时间戳转换日期 */
    public static String stampToTime(long stamp) {
        String sd = "";
        //Date d = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sd = sdf.format(new Date(stamp)); // 时间戳转换日期
        return sd;
    }
}
