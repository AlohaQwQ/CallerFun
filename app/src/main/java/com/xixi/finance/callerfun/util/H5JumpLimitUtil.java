package com.xixi.finance.callerfun.util;

import android.content.Context;
import android.content.Intent;

import com.xixi.finance.callerfun.constant.ServiceAPIConstant;
import com.xixi.finance.callerfun.version.PersistentDataCacheEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AlohaQoQ on 2017/11/21.
 * H5 跳转链接post传递基本参
 */
public class H5JumpLimitUtil {

    /**
     HashMap<String,String> parameterMap = new HashMap();
     parameterMap.put("productCode", productCode);
     parameterMap.put("orderNO", borrowOrderDetail.getCode());

     HashMap<String,String> extraMap = new HashMap();
     extraMap.put(LuckDrawGameActivity.KEY_LUCK_H5_JUMP_URL_TO_POST,
        ServiceAPIConstant.API_BASE_URL+ServiceAPIConstant.REQUEST_BORROW_MONEY_BORROW_ORDER_CONTRACT);
     extraMap.put(LuckDrawGameActivity.KEY_LUCK_H5_JUMP_URL_TO_POST_PARA,
        H5JumpLimitUtil.buildPostUrlParameter(parameterMap));

     H5JumpLimitUtil.jumpH5Limit(BorrowMoneyDetailPeriodsActivity.this,LuckDrawGameActivity.class, extraMap);
     */

    public static void jumpH5Limit(Context context, Class<?> jumpCls, HashMap<String,String> extraMap){
        Intent intent = new Intent(context,jumpCls);
        for (Map.Entry<String, String> entry : extraMap.entrySet()) {
            intent.putExtra(entry.getKey(),entry.getValue());
        }
        context.startActivity(intent);
    }

    /**
     * App类型 reqApplicationType
     * App版本 reqApplicationVersion
     * 操作系统名称 osName
     * 操作系统版本 osVersion
     * 设备类型 mobileModel
     * 访问的手机号码 reqMobile
     * 登录令牌 token
     * */
    public static String buildPostUrlParameter(HashMap<String,String> parameterMap){
        String parameter = "";
        if(parameterMap!=null){
            for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
                parameter = parameter + entry.getKey()+"="+entry.getValue()+"&";
            }
        }
        parameter = parameter +
                "&reqApplicationVersion=" + String.valueOf(PersistentDataCacheEntity.getInstance().getVersionCode())+
                "&reqMobile=" + PersistentDataCacheEntity.getInstance().getUserMobile()+
                "&mobile=" + PersistentDataCacheEntity.getInstance().getUserMobile();
        return parameter;
    }

    public static String buildUrl(String jumpUrl){
        if(!jumpUrl.contains("?")){
            jumpUrl=jumpUrl+"?";
        }
        return ServiceAPIConstant.API_BASE_URL + jumpUrl+
                "&reqApplicationVersion=" + String.valueOf(PersistentDataCacheEntity.getInstance().getVersionCode())+
                "&reqMobile=" + PersistentDataCacheEntity.getInstance().getUserMobile()+
                "&mobile=" + PersistentDataCacheEntity.getInstance().getUserMobile();
    }

    public static String buildUrlNoBaseUrl(String jumpUrl){
        if(!jumpUrl.contains("?")){
            jumpUrl=jumpUrl+"?";
        }
        return jumpUrl+
                "&reqApplicationVersion=" + String.valueOf(PersistentDataCacheEntity.getInstance().getVersionCode())+
                "&reqMobile=" + PersistentDataCacheEntity.getInstance().getUserMobile()+
                "&mobile=" + PersistentDataCacheEntity.getInstance().getUserMobile();
    }
}
