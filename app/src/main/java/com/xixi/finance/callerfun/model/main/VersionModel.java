package com.xixi.finance.callerfun.model.main;

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
        request.setRequestID(ServiceAPIConstant.REQUEST_API_CHECK);
        request.setAPIPath(ServiceAPIConstant.REQUEST_API_CHECK);
        return request;
    }

}
