package com.lucky.danagerinfoplugin.utils;

public class CommandUtils {
    private static final CommandUtils ourInstance = new CommandUtils();

    public static CommandUtils getInstance() {
        return ourInstance;
    }

    private CommandUtils() {
    }

    /**
     * 通过反射来获取/system/build.prop下的文件
     * @param propName
     * @return
     */

    public String getProperty(String propName) {
        String value = null;
        Object roSecureObj;
        try {
            roSecureObj = Class.forName("android.os.SystemProperties").getMethod("get",
                    String.class).invoke(null, propName);
            if (roSecureObj != null) value = (String) roSecureObj;
        } catch (Throwable e) {
        }
        return value;
    }
}
