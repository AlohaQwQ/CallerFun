package com.xixi.finance.callerfun.util;

import java.io.UnsupportedEncodingException;

/**
 * Created by TanJiaJun on 2016/6/6.
 */
public class PasswordUtil {

    // 判断字符是否是中文
    public static boolean isChinese(String str) {
        try {
            byte[] bytes = str.getBytes("UTF-8");
            if (bytes.length == str.length()) {
                return false;
            } else {
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }
}
