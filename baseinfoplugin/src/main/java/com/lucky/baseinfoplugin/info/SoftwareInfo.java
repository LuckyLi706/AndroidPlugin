package com.lucky.baseinfoplugin.info;

/**
 * 作者：jacky on 2019/8/14 08:23
 * 邮箱：jackyli706@gmail.com
 */
public class SoftwareInfo {
    private static final SoftwareInfo ourInstance = new SoftwareInfo();

    public static SoftwareInfo getInstance() {
        return ourInstance;
    }

    private SoftwareInfo() {
    }


}
