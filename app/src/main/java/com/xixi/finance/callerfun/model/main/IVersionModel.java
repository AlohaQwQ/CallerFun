package com.xixi.finance.callerfun.model.main;

import aloha.shiningstarbase.base.IBaseModel;
import cn.chutong.sdk.conn.OkHttpRequest;

/**
 * Created by AlohaQoQ on 2018/1/18.
 */

public interface IVersionModel extends IBaseModel {

    /**
     * Created by Aloha <br>
     * -explain 检测版本更新
     * @Date 2018/1/18 17:11
     */
    OkHttpRequest checkVersionUpdate();

}
