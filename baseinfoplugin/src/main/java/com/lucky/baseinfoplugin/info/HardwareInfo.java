package com.lucky.baseinfoplugin.info;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.util.DisplayMetrics;

import com.lucky.baseinfoplugin.utils.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.Context.ACTIVITY_SERVICE;

public class HardwareInfo {
    private static final HardwareInfo ourInstance = new HardwareInfo();

    public static HardwareInfo getInstance() {
        return ourInstance;
    }

    private HardwareInfo() {
    }

    public Map<String, String> getInfo(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("androidId", androidId(context));
        map.put("蓝牙地址", bluetoothAddress(context));
        map.put("无线网卡地址", macAddress(context));
        map.put("支持的cpu架构", CPUABI());
        map.put("cpu名字", cpuName());
        map.put("cpu温度", cpuTemp());
        map.put("cpu最大频率", maxCpuFreq());
        map.put("cpu当前频率", curCpuFreq());
        map.put("cpu最小频率", minCpuFreq());
        map.put("系统总内存", totalMemory() + "");
        map.put("系统可用内存", availableMemory(context) + "");
        map.put("sd卡总容量", totalSD() + "");
        map.put("sd卡可用容量", availableSDCard() + "");
        map.put("系统卡总容量", totalSys() + "");
        map.put("系统卡可用容量", availableSys() + "");
        map.put("屏幕亮度", screenBrightness(context) + "");
        map.put("屏幕分辨率", resolution(context));
        return map;
    }

    public Map<String, String> getData(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("androidId", androidId(context));
        map.put("blueAddress", bluetoothAddress(context));
        map.put("macAddress", macAddress(context));
        map.put("cpuAbi", CPUABI());
        map.put("cpuName", cpuName());
        map.put("cpuTemp", cpuTemp());
        map.put("maxCpuFreq", maxCpuFreq());
        map.put("curCpuFreq", curCpuFreq());
        map.put("minCpuFreq", minCpuFreq());
        map.put("totalMemory", totalMemory() + "");
        map.put("availableMemory", availableMemory(context) + "");
        map.put("totalSD", totalSD() + "");
        map.put("availableSDCard", availableSDCard() + "");
        map.put("totalSys", totalSys() + "");
        map.put("availableSys", availableSys() + "");
        map.put("screenBrightness", screenBrightness(context) + "");
        map.put("resolution", resolution(context));
        return map;
    }


    public void printLog(Context context) {
        Map<String, String> map = getInfo(context);
        LogUtil.d("硬件信息");
        Set<String> set = map.keySet();
        for (String s : set) {
            LogUtil.d(s + ":" + map.get(s));
        }
    }

    //androidID获取
    public String androidId(Context context) {
        try {
            String androidId = Settings.System.getString(context.getContentResolver(),
                    Settings.System.ANDROID_ID);
            if ("9774d56d682e549c".equals(androidId)) {
                return "";
            }
            return androidId;
        } catch (Throwable e) {
            //return "";
        }
        return "";
    }

    //蓝牙地址获取
    public String bluetoothAddress(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= 18) {
                String blue = Settings.Secure.getString(context.getContentResolver(),
                        "bluetooth_address");
                if (blue != null && !blue.isEmpty()) {
                    return blue.toLowerCase();
                }
            } else {
                BluetoothAdapter bAdapt = BluetoothAdapter.getDefaultAdapter();
                if (bAdapt != null && bAdapt.isEnabled()) {
                    String address = bAdapt.getAddress();
                    if (address != null && !address.isEmpty()) {
                        return address.toLowerCase();
                    }
                }
            }
        } catch (Throwable e) {
            LogUtil.w(this, "Blue Collect Error");
        }
        return "";
    }

    //mac地址获取
    @SuppressLint("HardwareIds")
    public String macAddress(Context context) {
        if (Build.VERSION.SDK_INT == 23) {
            String str = null;
            Process pp = null;
            InputStreamReader ir = null;
            LineNumberReader input = null;
            try {
                pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
                ir = new InputStreamReader(pp.getInputStream(), StandardCharsets.UTF_8);
                input = new LineNumberReader(ir);
                str = input.readLine();
                if (str != null && str.contains(":") && str.length() == 17) {
                    input.close();
                    ir.close();
                    pp.destroy();
                    return str;
                }
            } catch (Throwable ex) {
                LogUtil.w(this, ex.getMessage());
            }
        } else if (Build.VERSION.SDK_INT < 23) {
            try {
                if (context != null) {
                    String result;
                    WifiManager wifiManager =
                            (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    assert wifiManager != null;
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    result = wifiInfo.getMacAddress();
                    return result.replace("=", "").replace("&", "").toLowerCase();
                }
            } catch (Throwable e) {
                LogUtil.w(this, e.getMessage());
            }
        } else {
            try {
                List<NetworkInterface> all =
                        Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface nif : all) {
                    if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }
                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }
                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString().toLowerCase();
                }
            } catch (Throwable ex) {
                LogUtil.w(this, ex.getMessage());
            }
        }
        return "";
    }


    /**
     * cpu信息
     */
    public String CPUABI() {
        StringBuffer sb = new StringBuffer("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] supportedAbis = Build.SUPPORTED_ABIS;
            for (String abi : supportedAbis) {
                sb.append(abi);
            }
        } else {
            sb.append(Build.CPU_ABI).append(Build.CPU_ABI2);
        }
        return sb.toString();
    }

    // 获取CPU最大频率（单位KHZ）
    // "/system/bin/cat" 命令行
    // "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 存储最大频率的文件的路径
    public String maxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq" +
                    "/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (Throwable ex) {
            LogUtil.w(this, ex.getMessage());
        }
        return result.trim();
    }

    // 获取CPU最小频率（单位KHZ）
    public String minCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq" +
                    "/cpuinfo_min_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (Throwable ex) {
            LogUtil.w(this, ex.getMessage());
            result = "";
        }
        return result.trim();
    }

    // 实时获取CPU当前频率（单位KHZ）
    public String curCpuFreq() {
        String result = "";
        try {
            FileReader fr = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            result = text.trim();
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return result;
    }

    // 获取CPU名字
    public String cpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return "";
    }

    //cpu温度
    public String cpuTemp() {
        String text4 = "";
        try {
            FileReader fr4 = new FileReader("/sys/class/thermal/thermal_zone9/subsystem/thermal_zone9/temp");
            BufferedReader br4 = new BufferedReader(fr4);
            text4 = br4.readLine();
            br4.close();
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return "";
    }

    //系统内存
    public long availableMemory(Context context) {
        try {
            ActivityManager activityManager =
                    (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(info);
            return info.availMem;

        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return 0;
    }

    //可用sd卡容量
    @SuppressWarnings(value = {"deprecated"})
    public long availableSDCard() {
        try {

            String extSdcardPath = System.getenv("SECONDARY_STORAGE");
            if (extSdcardPath != null) {
                File base = new File(extSdcardPath);
                StatFs stat = new StatFs(base.getPath());
                long nAvailableCount = stat.getBlockSize() * ((long) stat.getAvailableBlocks());
                return nAvailableCount;

            }
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return 0;
    }

    //可用系统容量
    @SuppressWarnings(value = {"deprecated"})
    public long availableSys() {
        try {
            File file = Environment.getDataDirectory();
            StatFs statFs = new StatFs(file.getPath());
            long blockSize = statFs.getBlockSize();
            long availableBlocks = statFs.getAvailableBlocks();
            return availableBlocks * blockSize;
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return 0;
    }

    //总内存
    public long totalMemory() {
        String str1 = "/proc/meminfo";
        long initial_memory;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            String str2 = localBufferedReader.readLine();
            String[] arrayOfString = str2.split("\\s+");
            initial_memory = Long.valueOf(arrayOfString[1]).intValue();
            localBufferedReader.close();
            return initial_memory * 1024;
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return 0;
    }

    //总SD卡容量
    public long totalSD() {
        try {
            if (Build.VERSION.SDK_INT <= 23) {
                String extSdcardPath = System.getenv("SECONDARY_STORAGE");
                if (extSdcardPath != null) {
                    File base = new File(extSdcardPath);
                    StatFs stat = new StatFs(base.getPath());
                    long nAvailableCount = stat.getBlockSize() * ((long) stat.getBlockCount());
                    return nAvailableCount;
                }
            }
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return 0;
    }

    //总系统容量
    public long totalSys() {
        try {
            File file = Environment.getDataDirectory();
            StatFs statFs = new StatFs(file.getPath());
            long blockSize = statFs.getBlockSize();
            long totalBlocks = statFs.getBlockCount();
            return totalBlocks * blockSize;
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return 0;
    }

    //屏幕亮度
    private int screenBrightness(Context context) {
        try {
            if (context != null) {
                ContentResolver resolver = context.getContentResolver();
                return Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
            }
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return 0;
    }

    //屏幕分辨率
    private String resolution(Context context) {
        try {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            return ("[" + dm.density + "," + dm.widthPixels + "," + dm.heightPixels + "," + dm.scaledDensity + "," + dm.xdpi + "," + dm.ydpi + "]");

        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return "";
    }
}
