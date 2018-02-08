package com.xixi.finance.callerfun.model.record;

import java.io.File;

import aloha.shiningstarbase.base.IBaseModel;
import cn.chutong.sdk.conn.OkHttpRequest;

/**
 * Created by AlohaQoQ on 2018/1/18.
 */

public interface IRecordModel extends IBaseModel {

    /**
     * 上传录音文件
     * @return
     */
    OkHttpRequest uploadRecordFile(File recordFile,String recordDuration, String phone);

    /**
     * 获取录音列表
     * @return
     */
    OkHttpRequest fetchRecords();

    /**
     * 获取一个月份的录音详情
     * @return
     */
    OkHttpRequest fetchRecordDetailMonth(String year, String month);

    /**
     * 获取来电显示用户资料页
     * @return
     */
    OkHttpRequest fetchCallInPage(String callPhone);

    /**
     * 获取来电显示用户详情
     * @return
     */
    OkHttpRequest fetchCallInCustomerInformation(String callPhone);

    /**
     * 获取用户资料页
     * @return
     */
    OkHttpRequest fetchCustomerPage(String callPhone);

}
