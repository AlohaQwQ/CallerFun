package com.xixi.finance.callerfun.model.record;

import com.xixi.finance.callerfun.constant.APIKey;
import com.xixi.finance.callerfun.constant.ServiceAPIConstant;

import java.io.File;

import cn.chutong.sdk.conn.OkHttpRequest;

/**
 * Created by AlohaQoQ on 2018/1/18.
 */

public class RecordModel implements IRecordModel {

    @Override
    public OkHttpRequest uploadRecordFile(File recordFile, String recordDuration, String phone) {
        OkHttpRequest request = new OkHttpRequest();
        request.setRequestID(ServiceAPIConstant.REQUEST_API_RECORD_UPLOAD);
        request.setAPIPath(ServiceAPIConstant.REQUEST_API_RECORD_UPLOAD);
        request.addRequestFileTypeParam("attach[]", recordFile);
        request.addRequestFormParam(APIKey.RECORD_IPT_DURATION, recordDuration);
        request.addRequestFormParam(APIKey.RECORD_IPT_PHONE, phone);
        return request;
    }

    @Override
    public OkHttpRequest fetchRecords() {
        OkHttpRequest request = new OkHttpRequest();
        request.setRequestID(ServiceAPIConstant.REQUEST_API_RECORD_FETCH_RECORDS);
        request.setAPIPath(ServiceAPIConstant.REQUEST_API_RECORD_FETCH_RECORDS);
        return request;
    }

    @Override
    public OkHttpRequest fetchRecordDetailMonth(String year, String month) {
        OkHttpRequest request = new OkHttpRequest();
        request.setRequestID(ServiceAPIConstant.REQUEST_API_RECORD_FETCH_RECORD_DETAIL_MONTH);
        request.setAPIPath(ServiceAPIConstant.REQUEST_API_RECORD_FETCH_RECORD_DETAIL_MONTH);
        request.addRequestFormParam(APIKey.RECORD_IPT_YEAR, year);
        request.addRequestFormParam(APIKey.RECORD_IPT_MONTH, month);
        return request;
    }

    @Override
    public OkHttpRequest fetchCallInPage(String callPhone) {
        OkHttpRequest request = new OkHttpRequest();
        request.setRequestID(ServiceAPIConstant.REQUEST_API_CALL_IN_PAGE);
        request.setAPIPath(ServiceAPIConstant.REQUEST_API_CALL_IN_PAGE);
        request.addRequestFormParam(APIKey.RECORD_PHONE, callPhone);
        return request;
    }

    @Override
    public OkHttpRequest fetchCallInCustomerInformation(String callPhone) {
        OkHttpRequest request = new OkHttpRequest();
        request.setRequestID(ServiceAPIConstant.REQUEST_API_FETCH_CALL_DETAIL);
        request.setAPIPath(ServiceAPIConstant.REQUEST_API_FETCH_CALL_DETAIL);
        request.addRequestFormParam(APIKey.RECORD_PHONE, callPhone);
        return request;
    }

    @Override
    public OkHttpRequest fetchCustomerPage(String callPhone) {
        OkHttpRequest request = new OkHttpRequest();
        request.setRequestID(ServiceAPIConstant.REQUEST_API_CUSTOMER_PAGE);
        request.setAPIPath(ServiceAPIConstant.REQUEST_API_CUSTOMER_PAGE);
        request.addRequestFormParam(APIKey.RECORD_PHONE, callPhone);
        return request;
    }
}
