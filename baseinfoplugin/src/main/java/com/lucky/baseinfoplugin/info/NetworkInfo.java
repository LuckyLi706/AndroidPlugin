package com.lucky.baseinfoplugin.info;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lucky.baseinfoplugin.utils.LogUtil;
import com.lucky.baseinfoplugin.utils.PermissonUtil;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NetworkInfo {
    private static final NetworkInfo ourInstance = new NetworkInfo();

    public static NetworkInfo getInstance() {
        return ourInstance;
    }

    private NetworkInfo() {
    }

    public Map<String, String> getInfo(Context context) {
        Map<String, String> map = new HashMap<>();
        if (isNetworkConnected(context)) {
            String networkType = getNetworkType(context);
            map.put("网络类型", networkType);
            if (networkType.endsWith("WIFI")) {
                map.put("WIFIip", getWifiIp(context));
                map.put("当前连接的WIFI信息", currentWiFi(context));
                map.put("周围wifi列表", wifiList(context));
            } else {
                map.put("蜂窝ip", getCellarIp());
            }
        } else {
            map.put("网络状态", "网络不可用");
        }
        return map;
    }

    public Map<String, String> getData(Context context) {
        Map<String, String> map = new HashMap<>();
        if (isNetworkConnected(context)) {
            String networkType = getNetworkType(context);
            map.put("networkType", networkType);
            if (networkType.endsWith("WIFI")) {
                map.put("ip", getWifiIp(context));
                map.put("currentWifi", currentWiFi(context));
                map.put("wifiList", wifiList(context));
            } else {
                map.put("ip", getCellarIp());
            }
        } else {
            map.put("networkType", "网络不可用");
        }
        return map;
    }

    public void printLog(Context context) {
        Map<String, String> map = getInfo(context);
        LogUtil.d("网络信息");
        Set<String> set = map.keySet();
        for (String s : set) {
            LogUtil.d(s + ":" + map.get(s));
        }
    }

    //判断当前网络是否可用
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (PermissonUtil.ACCESS_NETWORK_STATE(context)) {
                android.net.NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isConnected();
                }
            } else {
                LogUtil.w(this, "没有授予ACCESS_NETWORK_STATE权限");
            }
        }
        return false;
    }

    /**
     * 检查当前网络类型
     * 参考:https://www.cnblogs.com/meteoric_cry/p/4627075.html
     */
    public String getNetworkType(Context context) {
        String strNetworkType = "";
        try {
            if (!PermissonUtil.ACCESS_NETWORK_STATE(context)) {
                LogUtil.w(this, "没有授予ACCESS_NETWORK_STATE权限");
                return "";
            }
            android.net.NetworkInfo networkInfo =
                    ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    strNetworkType = "WIFI";
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    String _strSubTypeName = networkInfo.getSubtypeName();

                    Log.e("cocos2d-x", "Network getSubtypeName : " + _strSubTypeName);

                    // TD-SCDMA   networkType is 17
                    int networkType = networkInfo.getSubtype();
                    switch (networkType) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                            strNetworkType = "2G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                        case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                        case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                            strNetworkType = "3G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                            strNetworkType = "4G";
                            break;
                        default:
                            // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                            if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                strNetworkType = "3G";
                            } else {
                                strNetworkType = _strSubTypeName;
                            }

                            break;
                    }
                }
            }
        } catch (Throwable e) {
            LogUtil.w(this, e.getMessage());
        }
        return strNetworkType;
    }

//    //http代理信息
//    private String HttpProxy(Context context) {
//        try {
//            String proxyHost;
//            int proxyPort;
//            proxyHost = System.getProperty("http.proxyHost");
//            String port = System.getProperty("http.proxyPort");
//            proxyPort = Integer.parseInt(port != null ? port : "-1");
//            //return proxyHost != null && proxyPort != -1 ? IS_PROXY : NO_PROXY;
//            return proxyHost != null && proxyPort != -1 ? (proxyHost + ":" + proxyPort) : "";
//        } catch (Throwable e) {
//            LogUtil.w(this, e.getMessage());
//        }
//        return "";
//    }


    //wifi的ip
    public String getWifiIp(Context context) {
        try {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            assert wifiManager != null;
            if (!wifiManager.isWifiEnabled()) {
                return "";
            }
            if (!PermissonUtil.ACCESS_WIFI_STATE(context)) {
                LogUtil.e(this, "没有授予ACCESS_NETWORK_STATE权限");
                return "";
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = intToIp(ipAddress);
            return ip;
        } catch (Throwable ex) {
            LogUtil.w("getWifiip", ex.getMessage());
        }
        return "";
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    public String getCellarIp() {
        try {
            String ipv4;
            ArrayList<NetworkInterface> nilist =
                    Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : nilist) {
                ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address : ialist) {
                    if (!address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
                        ipv4 = address.getHostAddress();
                        return ipv4;
                    }
                }
            }
        } catch (SocketException ex) {
            LogUtil.w("getLocalIpV4Address", ex.getMessage());
        }
        return "";
    }

    //Current WiFi
    public String currentWiFi(Context context) {
        try {
            if (context != null) {
                WifiManager wifiManager =
                        (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    if (!PermissonUtil.ACCESS_WIFI_STATE(context)) {
                        LogUtil.e(this, "没有授予ACCESS_NETWORK_STATE权限");
                        return "";
                    }
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    Map<String, String> map = new HashMap<>();
                    String BSSID = wifiInfo.getBSSID();
                    map.put("bssid", BSSID);
                    //.replace("\"", "");
                    String SSID = wifiInfo.getSSID().replace("\"", "");
                    map.put("ssid", SSID);
                    //return ("[" + SSID + "," + BSSID + "]").replace("=", "").replace("&", "");
                    return map.toString();
                }
            }
        } catch (Throwable e) {
            LogUtil.w("currentWiFi", e.getMessage());
        }
        return "";
    }

    public String wifiList(Context context) {
        try {
            if (context != null) {
                WifiManager manager =
                        (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                assert manager != null;
                if (!PermissonUtil.CHANGE_WIFI_STATE(context)) {
                    LogUtil.e(this, "没有授予ACCESS_NETWORK_STATE权限");
                    return "";
                }
                manager.startScan();
                StringBuffer str = new StringBuffer("");
                if (!PermissonUtil.ACCESS_WIFI_STATE(context)) {
                    LogUtil.w(this, "没有授予ACCESS_NETWORK_STATE权限");
                    return "";
                }
                List<ScanResult> result = manager.getScanResults();
                if (result != null && result.size() > 0) {
                    for (ScanResult scanResult : result) {
                        str.append(scanResult.SSID);
                        str.append(",");
                        str.append(scanResult.BSSID);
                        str.append(",");
                        str.append(scanResult.capabilities.replace("[", "").replace("]", ""));
                        str.append(",");
                    }
                    return (str.substring(0, str.length() - 1) + "]").replace("=", "").replace(
                            "&", "");
                }
            }
        } catch (Throwable e) {
            LogUtil.w("wifiList", e.getMessage());
        }
        return "";
    }
}
