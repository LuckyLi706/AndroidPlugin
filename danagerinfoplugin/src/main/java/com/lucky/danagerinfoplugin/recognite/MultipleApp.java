package com.lucky.danagerinfoplugin.recognite;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * 能够多开的应用
 * 平行空间、VirtualApp、双开助手、DualSpace、Go双开、双开精灵
 */
public class MultipleApp {



    private static final MultipleApp ourInstance = new MultipleApp();

    public static MultipleApp getInstance() {
        return ourInstance;
    }

    private MultipleApp() {
    }


}
