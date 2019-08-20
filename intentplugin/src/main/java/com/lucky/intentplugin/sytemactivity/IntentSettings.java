package com.lucky.intentplugin.sytemactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

/**
 * 作者：jacky on 2019/8/20 09:13
 * 邮箱：jackyli706@gmail.com
 */
public class IntentSettings {
    private static final IntentSettings ourInstance = new IntentSettings();

    public static IntentSettings getInstance() {
        return ourInstance;
    }

    private IntentSettings() {
    }

    /**
     * ACTION_SETTINGS	系统设置界面
     * ACTION_APN_SETTINGS	APN设置界面
     * ACTION_LOCATION_SOURCE_SETTINGS	定位设置界面
     * ACTION_AIRPLANE_MODE_SETTINGS	更多连接方式设置界面
     * ACTION_DATA_ROAMING_SETTINGS	双卡和移动网络设置界面
     * ACTION_ACCESSIBILITY_SETTINGS	无障碍设置界面/辅助功能界面
     * ACTION_SYNC_SETTINGS	同步设置界面
     * ACTION_ADD_ACCOUNT	添加账户界面
     * ACTION_NETWORK_OPERATOR_SETTINGS	选取运营商的界面
     * ACTION_SECURITY_SETTINGS	安全设置界面
     * ACTION_PRIVACY_SETTINGS	备份重置设置界面
     * ACTION_VPN_SETTINGS	VPN设置界面,可能不存在
     * ACTION_WIFI_SETTINGS	无线网设置界面
     * ACTION_WIFI_IP_SETTINGS	WIFI的IP设置
     * ACTION_BLUETOOTH_SETTINGS	蓝牙设置
     * ACTION_CAST_SETTINGS	投射设置
     * ACTION_DATE_SETTINGS	日期时间设置
     * ACTION_SOUND_SETTINGS	声音设置
     * ACTION_DISPLAY_SETTINGS	显示设置
     * ACTION_LOCALE_SETTINGS	语言设置
     * ACTION_VOICE_INPUT_SETTINGS	辅助应用和语音输入设置
     * ACTION_INPUT_METHOD_SETTINGS	语言和输入法设置
     * ACTION_USER_DICTIONARY_SETTINGS	个人字典设置界面
     * ACTION_INTERNAL_STORAGE_SETTINGS	存储空间设置的界面
     * ACTION_SEARCH_SETTINGS	搜索设置界面
     * ACTION_APPLICATION_DEVELOPMENT_SETTINGS	开发者选项
     * ACTION_DEVICE_INFO_SETTINGS	手机状态信息的界面
     * ACTION_DREAM_SETTINGS	互动屏保设置的界面
     * ACTION_NOTIFICATION_LISTENER_SETTINGS	通知使用权设置的界面
     * ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS	勿扰权限设置的界面
     * ACTION_CAPTIONING_SETTINGS	字幕设置的界面
     * ACTION_PRINT_SETTINGS	打印设置界面
     * ACTION_BATTERY_SAVER_SETTINGS	节电助手界面
     * ACTION_HOME_SETTINGS	主屏幕设置界面
     * ACTION_APPLICATION_DETAILS_SETTINGS	根据包名跳转到系统自带的应用程序信息
     * ACTION_APPLICATION_SETTINGS	应用程序列表
     * ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS	应用程序界面【所有的】
     * ACTION_MANAGE_APPLICATIONS_SETTINGS	应用程序列表界面【已安装的】
     * ACTION_INPUT_METHOD_SUBTYPE_SETTINGS	【API 11及以上】语言选择界面 【多国语言选择】
     * ACTION_NFCSHARING_SETTINGS	显示NFC共享设置【API 14及以上】
     * ACTION_NFC_SETTINGS	显示NFC设置【API 16及以上】
     * ACTION_QUICK_LAUNCH_SETTINGS	快速启动设置界面
     *
     * @param context
     * @param name
     */
    public void startSettings(Context context, String name) {
        Intent intentSettings = new Intent(name);
        ((Activity) context).startActivity(intentSettings);
    }
}
