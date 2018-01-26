package com.xixi.finance.callerfun.model.main;

import com.xixi.finance.callerfun.constant.APIKey;
import com.xixi.finance.callerfun.constant.ServiceAPIConstant;

import cn.chutong.sdk.conn.OkHttpRequest;

/**
 * Created by AlohaQoQ on 2018/1/18.
 */

public class VersionModel implements IVersionModel {

    /**
     * Created by Aloha <br>
     * -explain 获取通话列表记录
     * @Date 2018/1/18 17:11
     */
    @Override
    public OkHttpRequest checkVersionUpdate() {
        OkHttpRequest request = new OkHttpRequest();
        request.setAPIPath(ServiceAPIConstant.REQUEST_API_NAME_USER_REGISTER);
        request.setRequestID(ServiceAPIConstant.REQUEST_API_NAME_USER_REGISTER);
        request.addRequestFormParam(APIKey.BORROW_PRODUCTS_FIRST_LOGIN, "");
        return request;
    }

}
