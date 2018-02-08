package com.xixi.finance.callerfun.model.user;

import com.xixi.finance.callerfun.constant.APIKey;
import com.xixi.finance.callerfun.constant.ServiceAPIConstant;

import cn.chutong.sdk.conn.OkHttpRequest;

/**
 * Created by AlohaQoQ on 2018/1/18.
 */
public class UserModel implements IUserModel {

    @Override
    public OkHttpRequest userLogin(String account, String password) {
        OkHttpRequest request = new OkHttpRequest();
        request.setRequestID(ServiceAPIConstant.REQUEST_API_USER_LOGIN);
        request.setAPIPath(ServiceAPIConstant.REQUEST_API_USER_LOGIN);
        request.addRequestFormParam(APIKey.USER_USER_NAME, account);
        request.addRequestFormParam(APIKey.USER_PASSWORD, password);
        return request;
    }

    @Override
    public OkHttpRequest userLogout(String account) {
        OkHttpRequest request = new OkHttpRequest();
        request.setRequestID(ServiceAPIConstant.REQUEST_API_USER_LOGOUT);
        request.setAPIPath(ServiceAPIConstant.REQUEST_API_USER_LOGOUT);
        request.addRequestFormParam(APIKey.USER_USER_NAME, account);
        return request;
    }
}
