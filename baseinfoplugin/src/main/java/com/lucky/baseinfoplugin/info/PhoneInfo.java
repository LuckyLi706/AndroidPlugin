package com.lucky.baseinfoplugin.info;

import android.annotation.SuppressLint;
import android.os.Build;

import com.lucky.baseinfoplugin.utils.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PhoneInfo {
    private static final PhoneInfo ourInstance = new PhoneInfo();

    public static PhoneInfo getInstance() {
        return ourInstance;
    }

    private PhoneInfo() {
    }

    public Map<String, String> getAllInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("设备主板", BOARD());
        map.put("设备品牌", BRAND());
        map.put("主板引导程序", BOOTLOADER());
        map.put("设备驱动名称", DEVICE());
        map.put("设备显示的版本包", DISPLAY());
        map.put("指纹", FINGERPRINT());
        map.put("RadioVersion", getRadioVersion());
        map.put("分区信息", getFingerprintedPartitions());
        map.put("硬件名称", HARDWARE());
        map.put("串口序列号", Serial());
        map.put("设备主机地址", HOST());
        map.put("设备版本号", ID());
        map.put("制造商", MANUFACTURER());
        map.put("设备型号", MODEL());
        map.put("产品的名称", PRODUCT());
        map.put("设备标签", TAGS());
        map.put("设备版本类型", TYPE());
        map.put("编译时间", TIME());
        map.put("设备用户名", USER());
        map.put("baseos", BASE_OS());
        map.put("当前开发代号", CODENAME());
        map.put("源码控制版本号", INCREMENTAL());
        map.put("系统版本字符串", RELEASE());
        map.put("SECURITY_PATCH", SECURITY_PATCH());
        map.put("系统版本值", SDK());
        map.put("系统API级别", SDK_INT());
        map.put("PREVIEW_SDK_INT", PREVIEW_SDK_INT());
        return map;
    }

    public Map<String, String> getData() {
        Map<String, String> map = new HashMap<>();
        map.put("board", BOARD());
        map.put("brand", BRAND());
        map.put("bootloader", BOOTLOADER());
        map.put("device", DEVICE());
        map.put("display", DISPLAY());
        map.put("fingerprint", FINGERPRINT());
        map.put("radioversion", getRadioVersion());
        map.put("partitions", getFingerprintedPartitions());
        map.put("hardware", HARDWARE());
        map.put("serial", Serial());
        map.put("host", HOST());
        map.put("id", ID());
        map.put("manufacturer", MANUFACTURER());
        map.put("model", MODEL());
        map.put("product", PRODUCT());
        map.put("tags", TAGS());
        map.put("type", TYPE());
        map.put("time", TIME());
        map.put("user", USER());
        map.put("base_os", BASE_OS());
        map.put("codename", CODENAME());
        map.put("incremental", INCREMENTAL());
        map.put("release", RELEASE());
        map.put("security_patch", SECURITY_PATCH());
        map.put("sdk", SDK());
        map.put("sdk_int", SDK_INT());
        map.put("preview_sdk_int", PREVIEW_SDK_INT());
        return map;
    }

    public void printLog() {
        Map<String, String> map = getAllInfo();
        LogUtil.d("手机信息");
        Set<String> set = map.keySet();
        for (String s : set) {
            LogUtil.d(s + ":" + map.get(s));
        }
    }

    public String BOARD() {
        return Build.BOARD;
    }

    public String BRAND() {
        return Build.BRAND;
    }

    public String BOOTLOADER() {
        return Build.BOOTLOADER;
    }

    public String DEVICE() {
        return Build.DEVICE;
    }

    public String DISPLAY() {
        return Build.DISPLAY;
    }

    public String FINGERPRINT() {
        return Build.FINGERPRINT;
    }

    public String getRadioVersion() {
        return Build.getRadioVersion();
    }

    @SuppressLint("MissingPermission")
    public String Serial() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Build.getSerial();
        } else {
            return Build.SERIAL;
        }
    }

    public String getFingerprintedPartitions() {
        try {
            StringBuffer sb = new StringBuffer("");
            List<Build.Partition> partitions = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                partitions = Build.getFingerprintedPartitions();
                for (Build.Partition partition : partitions) {
                    sb.append(partition.getName()).append(partition.getBuildTimeMillis()).append(partition.getFingerprint()).append(",");
                }
            } else {
                return "";
            }
            return sb.toString();
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return "";
    }

    public String HARDWARE() {
        return Build.HARDWARE;
    }

    public String HOST() {
        return Build.HOST;
    }

    public String ID() {
        return Build.ID;
    }

    public String MANUFACTURER() {
        return Build.MANUFACTURER;
    }

    public String MODEL() {
        return Build.MODEL;
    }

    public String PRODUCT() {
        return Build.PRODUCT;
    }

    public String TAGS() {
        return Build.TAGS;
    }

    public String TYPE() {
        return Build.TYPE;
    }

    public String TIME() {
        return Build.TIME + "";
    }

    public String USER() {
        return Build.USER;
    }

    public String BASE_OS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Build.VERSION.BASE_OS;
        }
        return "";
    }

    public String CODENAME() {
        return Build.VERSION.CODENAME;
    }

    public String INCREMENTAL() {
        return Build.VERSION.INCREMENTAL;
    }

    public String RELEASE() {
        return Build.VERSION.RELEASE;
    }

    public String SECURITY_PATCH() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Build.VERSION.SECURITY_PATCH;
        }
        return "";
    }

    public String SDK() {
        return Build.VERSION.SDK;
    }

    public String PREVIEW_SDK_INT() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Build.VERSION.PREVIEW_SDK_INT + "";
        }
        return "";
    }

    public String SDK_INT() {
        return Build.VERSION.SDK_INT + "";
    }

    /**
     *   Build.VERSION_CODES下对应了所有版本的版本号
     */


}
