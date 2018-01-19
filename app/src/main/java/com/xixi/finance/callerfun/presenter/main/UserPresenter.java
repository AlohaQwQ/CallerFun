package com.xixi.finance.callerfun.presenter.main;

import com.xixi.finance.callerfun.model.record.IRecordModel;
import com.xixi.finance.callerfun.model.record.RecordModel;
import com.xixi.finance.callerfun.presenter.BasePresenter;
import com.xixi.finance.callerfun.ui.view.main.IRecordView;

import java.util.Map;

import cn.chutong.sdk.conn.OkHttpRequest;

/**
 * Created by TanJiaJun on 2016/4/20.
 */
public class UserPresenter extends BasePresenter<IRecordView> {

    private IRecordModel recordModel;

    public UserPresenter() {
        recordModel = new RecordModel();
    }

    public void fetchConfigDetailRequest(String configCategoryCode) {
        OkHttpRequest request = recordModel.fetchCallList();
        addRequestAsyncTask(request);
    }

    @Override
    protected void onResponseAsyncTaskRender(final String status, final String message,
                                             final Map<String, Object> dataMap, final String requestID) {
        super.onResponseAsyncTaskRender(status, message, dataMap, requestID);
        /*if (ServiceAPIConstant.REQUEST_API_NAME_CONFIGDETAIL_FETCH_M.equals(requestID)) {
            if (0 == status) {
                List<Map<String, Object>> configDetailList = TypeUtil.getList(resultMap.get(APIKey.CONFIG_DETAIL_LIST), null);

                if (null != configDetailList) {
                    String value = TypeUtil.getString(configDetailList.get(0).get(APIKey.CONFIG_VALUE), "");

                    PersistentDataCacheEntity.getInstance().setValue(value);

                    if (null != getView()) {
                        RecordPresenter.this.getView().fetchConfigDetailSuccess(value);
                    }
                }
            } else {
                if (null != getView()) {
                    RecordPresenter.this.getView().fetchConfigDetailFailure();
                }
            }
        }*/
    }
}
