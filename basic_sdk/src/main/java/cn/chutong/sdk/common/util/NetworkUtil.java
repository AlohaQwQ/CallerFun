/**
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 * <p>
 * <p>
 * Version Control
 * <p>
 * | version | date        | author         | description
 * 0.0.1     2015.11.30    shiliang.zou     整理代码
 */

/**
 * Version Control
 *
 * | version | date        | author         | description
 *   0.0.1     2015.11.30    shiliang.zou     整理代码
 *
 */

package cn.chutong.sdk.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 网络工具类
 *
 * @author shiliang.zou
 * @version 0.0.1
 */
public class NetworkUtil {

    public static final int TYPE_NULL = 0;
    public static final int TYPE_MOBILE = 1;
    public static final int TYPE_WIFI = 2;

    /** 没有网络 */
    public static final String NETWORN_NONE = "0";
    /** wifi网络 */
    public static final String NETWORN_WIFI = "wifi";
    /**手机网络数据连接类型 */
    public static final String NETWORN_2G = "2G";
    public static final String NETWORN_3G = "3G";
    public static final String NETWORN_4G = "4G";
    public static final String NETWORN_MOBILE = "mobile";

    /**
     * 判断当前网络是否可用
     *
     * @param context 上下文环境
     * @return true表示网络可用
     * @since 0.0.1
     */
    public static boolean isNetworkAvaliable(Context context) {
        boolean isNetworkAvaliable = false;
        if (null != context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected())
            {
                isNetworkAvaliable = true;
            } else
            {
                isNetworkAvaliable = false;
            }
        }
        return isNetworkAvaliable;
    }

    /**
     * 获得当前网络状态
     *
     * @param context 上下文环境
     * @return 网络状态
     * @since 0.0.1
     */
    public static int getNetworkState(Context context) {
        int networkState = 0;
        if (null != context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                switch (networkInfo.getType()) {
                    case ConnectivityManager.TYPE_MOBILE:
                        networkState = TYPE_MOBILE;
                        break;

                    case ConnectivityManager.TYPE_WIFI:
                        networkState = TYPE_WIFI;
                        break;

                    default:
                        networkState = TYPE_NULL;
                        break;
                }
            } else {
                networkState = TYPE_NULL;
            }
        }
        return networkState;
    }

    /**
     * 获得当前网络状态详细
     *
     * @param context 上下文环境
     * @return 网络状态
     * @since 0.0.1
     */
    public static String getNetworkStateDetail(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager==null)
            return NETWORN_NONE;
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable())
            return NETWORN_NONE;
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORN_WIFI;
                }
        }
        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        //如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORN_2G;
                        //如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORN_3G;
                        //如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORN_4G;
                        default:
                            //中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return NETWORN_3G;
                            } else {
                                return NETWORN_MOBILE;
                            }
                    }
                }
        }
        return NETWORN_NONE;
    }

}
