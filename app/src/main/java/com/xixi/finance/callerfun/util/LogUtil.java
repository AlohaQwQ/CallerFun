package com.xixi.finance.callerfun.util;

import android.util.Log;

/**
 * author biubiubiu
 * date 2016/9/8 11:54
 * describe log输出
 */
public class LogUtil {

    //private static final Boolean DEBUG = BuildConfig.DEBUG;
    private static final Boolean DEBUG = true;

    public static void biubiubiu(String msg) {
        if (DEBUG) {
            Log.i("QwQ", msg);
        }
    }
}
