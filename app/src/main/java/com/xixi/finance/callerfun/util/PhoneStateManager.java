package com.xixi.finance.callerfun.util;

import android.telephony.TelephonyManager;

/**
 * Created by AlohaQoQ on 2018/2/8.
 */

public class PhoneStateManager {

    public static String calloutPhoneNumber = "";

    /**
     * 呼叫状态，1-呼出, 2-呼入
     */
    public static int callState = 0;

    public static  final Object monitor = new Object();

    public static int lastCallState = TelephonyManager.CALL_STATE_IDLE;

    public static boolean isIncoming = false;

    /**
     * 呼出状态
     */
    public static boolean isCallOut = false;

    public static int stateChange = 0;
}
