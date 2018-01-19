package com.xixi.finance.callerfun.model.record;

import aloha.shiningstarbase.base.IBaseModel;
import cn.chutong.sdk.conn.OkHttpRequest;

/**
 * Created by AlohaQoQ on 2018/1/18.
 */

public interface IRecordModel extends IBaseModel {

    /**
     * Created by Aloha <br>
     * -explain 获取通话列表记录
     * @Date 2018/1/18 17:11
     */
    OkHttpRequest fetchCallList();

}
