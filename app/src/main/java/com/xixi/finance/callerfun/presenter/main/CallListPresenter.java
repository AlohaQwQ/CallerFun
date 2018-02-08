package com.xixi.finance.callerfun.presenter.main;

import com.xixi.finance.callerfun.constant.ServiceAPIConstant;
import com.xixi.finance.callerfun.model.record.IRecordModel;
import com.xixi.finance.callerfun.model.record.RecordModel;
import com.xixi.finance.callerfun.presenter.BasePresenter;
import com.xixi.finance.callerfun.ui.view.main.ICallListView;

import cn.chutong.sdk.conn.OkHttpRequest;

/**
 * Created by TanJiaJun on 2016/4/20.
 */
public class CallListPresenter extends BasePresenter<ICallListView> {

    private IRecordModel recordModel;

    public CallListPresenter() {
        recordModel = new RecordModel();
    }

    public void fetchRecordDetailMonth(String year, String month) {
        OkHttpRequest request = recordModel.fetchRecordDetailMonth(year,month);
        addRequestAsyncTaskForJson(request);
    }

    @Override
    protected void onResponseAsyncTaskRender(final String status, final String message,
                                             final String response, final String requestID) {
        super.onResponseAsyncTaskRender(status, message, response, requestID);
        if (ServiceAPIConstant.REQUEST_API_RECORD_FETCH_RECORD_DETAIL_MONTH.equals(requestID)) {
            if (null != getView())
                CallListPresenter.this.getView().onshowRecordDetailMonth(response,status,message);
        }
    }
}
