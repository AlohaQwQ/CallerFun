package com.xixi.finance.callerfun.util;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by AlohaQoQ on 2017/4/24.
 */

public class DpToPxUtil {

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    public static int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }
}
