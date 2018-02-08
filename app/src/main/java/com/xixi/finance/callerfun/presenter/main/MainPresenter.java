package com.xixi.finance.callerfun.presenter.main;

import com.xixi.finance.callerfun.constant.ServiceAPIConstant;
import com.xixi.finance.callerfun.model.main.IVersionModel;
import com.xixi.finance.callerfun.model.main.VersionModel;
import com.xixi.finance.callerfun.model.record.IRecordModel;
import com.xixi.finance.callerfun.model.record.RecordModel;
import com.xixi.finance.callerfun.presenter.BasePresenter;
import com.xixi.finance.callerfun.ui.view.IBaseView;

import java.io.File;
import java.util.Map;

import cn.chutong.sdk.conn.OkHttpRequest;

/**
 * Created by TanJiaJun on 2016/4/20.
 */
public class MainPresenter extends BasePresenter<IBaseView> {

    private IVersionModel versionModel;
    private IRecordModel recordModel;
    private ResponseCallBack responseCallBack1;
    private ResponseCallBack responseCallBack2;
    private FetchCallInCallBack fetchCallInCallBack;

    public MainPresenter() {
        versionModel = new VersionModel();
        recordModel = new RecordModel();
    }

    /**
     * 请求接口 监听回调
     */
    public interface ResponseCallBack {
        /**
         * 回调
         */
        void responseCallBack(Map<String, Object> dataMap, String status, String message);
    }

    /**
     * 定义请求接口 监听回调
     */
    public interface FetchCallInCallBack {
        /**
         * 通话插屏回调
         */
        void fetchCallInCallBack(String response);
    }

    /**
     * 获取通话插屏页面url
     */
    public void fetchCallInPage(String callPhone,FetchCallInCallBack fetchCallInCallBack) {
        OkHttpRequest request = recordModel.fetchCallInPage(callPhone);
        this.fetchCallInCallBack = fetchCallInCallBack;
        addRequestAsyncTaskWithoutResolve(request);
    }

    /**
     * 获取客户信息
     */
    public void fetchCallInCustomerInformation(String callPhone,ResponseCallBack responseCallBack) {
        OkHttpRequest request = recordModel.fetchCallInCustomerInformation(callPhone);
        responseCallBack1 = responseCallBack;
        addRequestAsyncTask(request);
    }

    /**
     * 上传录音文件
     */
    public void uploadRecordFile(File recordFile, String recordDuration, String phone,ResponseCallBack responseCallBack) {
        OkHttpRequest request = recordModel.uploadRecordFile(recordFile, recordDuration, phone);
        responseCallBack2 = responseCallBack;
        addRequestAsyncTaskForJson(request);
    }

    @Override
    protected void onResponseAsyncTaskRender(final String response,final String requestID) {
        super.onResponseAsyncTaskRender(response, requestID);
        /**
         * 插屏页面Html
         */
        if (ServiceAPIConstant.REQUEST_API_CALL_IN_PAGE.equals(requestID))
            this.fetchCallInCallBack.fetchCallInCallBack(response);
    }

    @Override
    protected void onResponseAsyncTaskRender(final String status, final String message,
                                             final Map<String, Object> dataMap, final String requestID) {
        super.onResponseAsyncTaskRender(status,message,dataMap, requestID);
        /**
         * 获取客户信息
         */
        if (ServiceAPIConstant.REQUEST_API_FETCH_CALL_DETAIL.equals(requestID)) {
            responseCallBack1.responseCallBack(dataMap,status,message);
        }
    }

    @Override
    protected void onResponseAsyncTaskRender(final String status, final String message,
                                             final String response, final String requestID) {
        super.onResponseAsyncTaskRender(status, message, response, requestID);
        /**
         * 上传录音文件
         */
        if (ServiceAPIConstant.REQUEST_API_RECORD_UPLOAD.equals(requestID)) {
            responseCallBack2.responseCallBack(null,status,message);
        }
    }
}
