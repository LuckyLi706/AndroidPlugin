package com.lucky.baseinfoplugin.info;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.lucky.baseinfoplugin.utils.LogUtil;
import com.lucky.baseinfoplugin.utils.PermissonUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimInfo {
    private static final SimInfo ourInstance = new SimInfo();

    public static SimInfo getInstance() {
        return ourInstance;
    }

    private SimInfo() {
    }

    public Map<String, String> getInfo(Context context) {
        Map<String, String> map = new HashMap<>();
        if (getSIMState(context)) {
            map.put("IMSI", getIMSI(context));
            map.put("SIM序列号", getSimSerialNumber(context));
            // map.put("电话号码", getn(context));
            // map.put("运营商", getProvidersName(context));
        }
        map.put("IMEI", getIMEI(context));
        map.put("语音号码", getVoiceMailNumber(context));

        //map.put("手机信息", getPhoneInfo(context));
        return map;
    }

    public Map<String, String> getData(Context context) {
        Map<String, String> map = new HashMap<>();
        if (getSIMState(context)) {
            map.put("IMSI", getIMSI(context));
            map.put("serialNumber", getSimSerialNumber(context));
            // map.put("电话号码", getn(context));
            // map.put("运营商", getProvidersName(context));
        }
        map.put("IMEI", getIMEI(context));
        map.put("voiceNumber", getVoiceMailNumber(context));

        //map.put("手机信息", getPhoneInfo(context));
        return map;
    }


    public void printLog(Context context) {
        Map<String, String> map = getInfo(context);
        LogUtil.d("SIM卡信息");
        Set<String> set = map.keySet();
        for (String s : set) {
            LogUtil.d(s + ":" + map.get(s));
        }
    }


    private boolean getSIMState(Context context) {
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int simState = telephonyManager.getSimState();
            return simState == TelephonyManager.SIM_STATE_ABSENT;
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    private String getIMSI(Context context) {
        String ret = null;
        try {
            if (!PermissonUtil.READ_PHONE_STATE(context)) {
                LogUtil.e(this, "没有授予READ_PHONE_STATE权限");
                return "";
            }
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            ret = telephonyManager.getSubscriberId();
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        if (!TextUtils.isEmpty(ret)) {
            return ret;
        } else {
            return "";
        }
    }

    @SuppressLint("MissingPermission")
    private String getIMEI(Context context) {
        try {
            if (!PermissonUtil.READ_PHONE_STATE(context)) {
                LogUtil.e(this, "没有授予READ_PHONE_STATE权限");
                return "";
            }
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                return tm.getDeviceId();
            }
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return "";
    }

    /**
     * 返回注册的网络运营商的国家代码
     *
     * @param context
     * @return
     */
    protected String getNetworkCountryIso(Context context) {
        try {
            TelephonyManager manager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String networkCountryIso = manager.getNetworkCountryIso();
            if (null != networkCountryIso && !networkCountryIso.equals("")) {
                return networkCountryIso;
            }
        } catch (Throwable e) {
            LogUtil.w(this, "NetworkCountryIso collect error");
        }
        return "";
    }


    /**
     * 返回的MCC +跨国公司的注册网络运营商
     *
     * @param context
     * @return
     */
    protected String getNetworkOperator(Context context) {
        try {
            TelephonyManager manager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String networkOperator = manager.getNetworkOperator();
            if (null != networkOperator && !networkOperator.equals("")) {
                return networkOperator;
            }
        } catch (Throwable e) {
            LogUtil.w(this, "NetworkOperator collect error");
        }
        return "";
    }

    /**
     * 获取SIM卡提供的移动国家码和移动网络码
     * 5或6位的十进制数字.
     *
     * @param context
     * @return
     */
    protected String getPhoneType(Context context) {
        try {
            TelephonyManager manager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (manager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                String phoneType = manager.getPhoneType() + "";
                if (!phoneType.equals("")) {
                    return phoneType;
                }
            }
        } catch (Throwable e) {
            LogUtil.w(this, "PhoneType collect error");
        }
        return "";
    }

    /**
     * 返回SIM卡运营商的国家代码
     *
     * @param context
     * @return
     */
    protected String getSimCountryIso(Context context) {
        try {
            TelephonyManager manager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String simCountryIso = manager.getSimCountryIso();
            if (null != simCountryIso && !simCountryIso.equals("")) {
                return simCountryIso;
            }
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return "";
    }

    /**
     * 返回SIM卡的序列号
     *
     * @param context
     * @return
     */
    protected String getSimSerialNumber(Context context) {
        try {
            if (!PermissonUtil.READ_PHONE_STATE(context)) {
                LogUtil.e(this, "没有授予READ_PHONE_STATE权限");
                return "";
            }
            TelephonyManager manager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String simSerialNumber = manager.getSimSerialNumber();
            if (null != simSerialNumber && !simSerialNumber.equals("")) {
                return simSerialNumber;
            }
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return "";
    }

    /**
     * 获取语音邮件号码
     *
     * @param context
     * @return
     */
    protected String getVoiceMailNumber(Context context) {
        try {
            if (!PermissonUtil.READ_PHONE_STATE(context)) {
                LogUtil.e(this, "没有授予READ_PHONE_STATE权限");
                return "";
            }
            TelephonyManager manager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String voiceMailNumber = manager.getVoiceMailNumber();
            if (null != voiceMailNumber && !voiceMailNumber.equals("")) {
                return voiceMailNumber;
            }
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return "";
    }
}
