package com.lucky.danagerinfoplugin.utils;

public class StringUtil {

    public static int getNumber(String value) {
        return getNumber1(value,'1');
    }

    private static int getNumber1(String value, char a) {
        int number = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == a) {
                number++;
            }
        }
        return number;
    }
}
