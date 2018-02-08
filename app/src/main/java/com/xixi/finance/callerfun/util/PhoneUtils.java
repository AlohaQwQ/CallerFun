package com.xixi.finance.callerfun.util;

/**
 * Created by TanJiaJun on 2016/4/23.
 */
public class PhoneUtils {

    /**
     * 验证手机格式
     */
    public static boolean isPhone(String phone) {
        String telRegex = "[1][3456789]\\d{9}";
        if (phone.length() == 11) {
            return phone.matches(telRegex);
        } else {
            return false;
        }
    }
}
