package com.lucky.danagerinfoplugin;

import android.content.Context;

import com.lucky.danagerinfoplugin.recognite.Debug;
import com.lucky.danagerinfoplugin.recognite.Emulator;
import com.lucky.danagerinfoplugin.recognite.Root;
import com.lucky.danagerinfoplugin.recognite.Xposed;

public class DangerProtector {


    public static boolean isEmulator(Context context) {
        return Emulator.getInstance().distinguishVM(context).getVM();
    }

    public static boolean isRoot(Context context) {
        return Root.getInstance().isRoot(context);
    }

    public static boolean isXposedExsit(Context context) {
        return Xposed.getInstance().isXposed(context);
    }

    public static boolean isMultipleApp(Context context) {
        //return MultipleApp.getInstance().isMultiApp(context);
        return false;
    }

    public static boolean isDebug(Context context) {
        return Debug.getInstance().isDebug(context);
    }
}
