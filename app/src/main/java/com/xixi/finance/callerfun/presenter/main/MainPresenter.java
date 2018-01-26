package com.xixi.finance.callerfun.presenter.main;

import com.xixi.finance.callerfun.model.main.IVersionModel;
import com.xixi.finance.callerfun.model.main.VersionModel;
import com.xixi.finance.callerfun.presenter.BasePresenter;
import com.xixi.finance.callerfun.ui.view.main.IRecordView;

import java.util.Map;

import cn.chutong.sdk.conn.OkHttpRequest;

/**
 * Created by TanJiaJun on 2016/4/20.
 */
public class MainPresenter extends BasePresenter<IRecordView> {

    private IVersionModel versionModel;
    private UpdateCallBack updateCallBack;

    public MainPresenter() {
        versionModel = new VersionModel();
    }

    /**
     * 定义请求接口 监听回调
     */
    public interface UpdateCallBack {
        /**
         * 更新回调
         */
        void updateCallBack(Map<String, Object> dataMap);
    }

    public void checkVersionUpdate(UpdateCallBack updateCallBack) {
        OkHttpRequest request = versionModel.checkVersionUpdate();
        this.updateCallBack = updateCallBack;
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
