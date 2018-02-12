package com.xixi.finance.callerfun.util;

/**
 * Created by AlohaQoQ on 2018/2/9.
 */

public class TimeUtils {

    private static String recordRootPath = "record";

    private static String tempRecordFileName = "tempRecord";

    private static String recordFileName;
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 格式化时分
     *
     * @return
     */
    public static String formatRecordSecond(String time) {
        StringBuffer sb = new StringBuffer();
        int se = 60;
        Long hour = Long.valueOf(time) / se / se;
        Long minute = (Long.valueOf(time) /se) - hour*60;

        if (hour > 0) {
            if(hour < 10) {
                sb.append("0" + hour + ":");
            } else {
                sb.append(hour + ":");
            }
        } else {
            sb.append("00:");
        }
        if (minute>0) {
            if (minute<10) {
                sb.append("0" + minute);
            } else {
                sb.append(minute);
            }
        } else {
            sb.append("01");
        }
        return sb.toString();
    }
}
