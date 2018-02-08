package com.xixi.finance.callerfun.presenter.main;

import com.xixi.finance.callerfun.constant.ServiceAPIConstant;
import com.xixi.finance.callerfun.model.record.IRecordModel;
import com.xixi.finance.callerfun.model.record.RecordModel;
import com.xixi.finance.callerfun.presenter.BasePresenter;
import com.xixi.finance.callerfun.ui.view.main.IRecordView;

import cn.chutong.sdk.conn.OkHttpRequest;

/**
 * Created by TanJiaJun on 2016/4/20.
 */
public class RecordPresenter extends BasePresenter<IRecordView> {

    private IRecordModel recordModel;

    public RecordPresenter() {
        recordModel = new RecordModel();
    }

    /**
     * 客户资料page
     */
    public void fetchCustomerPage(String callPhone) {
        OkHttpRequest request = recordModel.fetchCustomerPage(callPhone);
        addRequestAsyncTaskWithoutResolve(request);
    }

    @Override
    protected void onResponseAsyncTaskRender(final String response,final String requestID) {
        super.onResponseAsyncTaskRender(response, requestID);
        /**
         * 客户资料page
         */
        if (ServiceAPIConstant.REQUEST_API_CUSTOMER_PAGE.equals(requestID))
            if(getView()!=null)
                RecordPresenter.this.getView().showCustomerPage(response);
        }

}
