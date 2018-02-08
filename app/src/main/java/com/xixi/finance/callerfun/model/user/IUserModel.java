package com.xixi.finance.callerfun.model.user;

import aloha.shiningstarbase.base.IBaseModel;
import cn.chutong.sdk.conn.OkHttpRequest;

/**
 * Created by AlohaQoQ on 2018/1/18.
 */

public interface IUserModel extends IBaseModel {

    /**
     * Created by Aloha <br>
     * -explain 用户登录
     * @Date 2018/1/18 17:11
     */
    OkHttpRequest userLogin(String account,String password);

    /**
     * Created by Aloha <br>
     * -explain 用户登出
     * @Date 2018/1/18 17:11
     */
    OkHttpRequest userLogout(String account);

}
