package com.xixi.finance.callerfun.presenter;

import android.app.Dialog;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.xixi.finance.callerfun.MyApplication;
import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.ui.view.IBaseView;
import com.xixi.finance.callerfun.version.PersistentDataCacheEntity;

import java.lang.ref.WeakReference;
import java.util.Map;

import aloha.shiningstarbase.constant.APIKey;
import aloha.shiningstarbase.constant.CommonConstant;
import aloha.shiningstarbase.constant.ServiceAPIConstant;
import aloha.shiningstarbase.logger.LogUtil;
import aloha.shiningstarbase.util.CommonJSONParser;
import cn.chutong.sdk.common.util.TypeUtil;
import cn.chutong.sdk.conn.OkHttpClientManager;
import cn.chutong.sdk.conn.OkHttpRequest;
import cn.chutong.sdk.conn.interfaces.IResultCallback;

/**
 * Created by Aloha <br>
 * -explain 父类Presenter
 * @Date 2016/9/26 17:10
 * @version 1.0.0
 */
public abstract class BasePresenter<V extends IBaseView> implements IBasePresenter {

    /**
     * 传递view
     */
    private WeakReference<V> mView;

    /**
     * 响应码
     */
    public static final String RESPONSE_STATUS_SUCCESS = "0";
    private Dialog dialog;

    /**
     * Created by Aloha <br>
     * @Date 2016/9/29 14:15
     * @explain 绑定view
     */
    @Override
    public void attachView(IBaseView view) {
        this.mView = new WeakReference<V>((V) view);
        LogUtil.biu(getClass().toString());
    }

    /**
     * Created by Aloha <br>
     * @Date 2016/9/29 14:16
     * @explain 解绑view
     */
    @Override
    public void detachView() {
        if (mView!=null){
            mView.clear();
            mView = null;
        }
    }

    /**
     * Created by Aloha <br>
     * -explain 获取 presenter绑定的view
     * @Date 2016/10/10 10:42
     */
    @Override
    public IBaseView getAttachView() {
        if (mView!=null){
            return mView.get();
        }
        return null;
    }

    /**
     * Created by Aloha <br>
     * -explain Json-CallBack 请求
     * @Date 2017/11/27 14:41
     */
    protected void addRequestAsyncTaskForJson(final OkHttpRequest request) {
        this.addRequestAsyncTaskForJson(request, false);
    }

    protected void addRequestAsyncTaskForJson(final OkHttpRequest request, boolean showDialog) {
        if (null != request) {
            String userId = PersistentDataCacheEntity.getInstance().getUserId();
            String token = PersistentDataCacheEntity.getInstance().getToken();
            //String token = "22516402824076";
            if (null != userId && token != null) {
                request.addRequestFormParam(APIKey.ACCOUNT_USER_ID, userId);
                request.addRequestFormParam(APIKey.USER_TOKEN_USER_ID, userId);
                request.addRequestFormParam(APIKey.USER_TOKEN, token);
                request.addRequestFormParam(APIKey.STATISTICS_OSNAME, "android");

                if (!TextUtils.isEmpty(PersistentDataCacheEntity.getInstance().getUserMobile())
                        && request.getRequestFormParam(APIKey.USER_MOBILE) == null) {
                    request.addRequestFormParam(APIKey.USER_MOBILE, PersistentDataCacheEntity.getInstance().getUserMobile());
                    request.addRequestFormParam(APIKey.USER_REQ_MOBILE, PersistentDataCacheEntity.getInstance().getUserMobile());
                }
                request.addRequestFormParam(APIKey.COMMON_APPLICATION_TYPE, CommonConstant.APP_APPLICATION_TYPE);
                runRequestAsyncTaskForJson(request, showDialog);
                if (showDialog) {
                    if (getAttachView() != null) {
                        getAttachView().showProgressView();
                    }
                }
            }
        }
    }

    /**
     * Created by Aloha <br>
     * -explain HashMap-CallBack 请求
     * @Date 2018/1/18 19:46
     */
    protected void addRequestAsyncTask(final OkHttpRequest request) {
        if (null != request) {
            String userId = PersistentDataCacheEntity.getInstance().getUserId();
            String token = PersistentDataCacheEntity.getInstance().getToken();

            if (null != userId && token != null) {
                request.addRequestFormParam(APIKey.ACCOUNT_USER_ID, userId);
                request.addRequestFormParam(APIKey.USER_TOKEN_USER_ID, userId);
                request.addRequestFormParam(APIKey.USER_TOKEN, token);
                request.addRequestFormParam(APIKey.STATISTICS_OSNAME, "android");
                if (!TextUtils.isEmpty(PersistentDataCacheEntity.getInstance().getUserMobile())
                        && request.getRequestFormParam(APIKey.USER_MOBILE) == null) {
                    request.addRequestFormParam(APIKey.USER_MOBILE, PersistentDataCacheEntity.getInstance().getUserMobile());
                    request.addRequestFormParam(APIKey.USER_REQ_MOBILE, PersistentDataCacheEntity.getInstance().getUserMobile());
                }
                request.addRequestFormParam(APIKey.COMMON_APPLICATION_TYPE, CommonConstant.APP_APPLICATION_TYPE);
                runRequestAsyncTask(request);
            }
        }
    }

    private void runRequestAsyncTaskForJson(final OkHttpRequest request) {
        this.runRequestAsyncTaskForJson(request, true);
    }

    private void runRequestAsyncTaskForJson(final OkHttpRequest request, final boolean showDialog) {
        final String requestID = request.getRequestID();

        request.setBaseUrl(ServiceAPIConstant.API_BASE_URL);
        request.showHttpRequestLog();

        OkHttpClientManager.getInstance().commitRequestTask(request, new IResultCallback() {
            @Override
            public void onResponse(String response) {
                //Log.i("ServerResponse", "response = " + response);
                LogUtil.biu("ServerResponse = " + response);
                if (getAttachView() != null && showDialog) {
                    getAttachView().hideProgressView();
                }
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        String status = jsonObject.getString(APIKey.COMMON_STATUS);
                        String code = jsonObject.getString(APIKey.COMMON_RESPONSE_CODE);
                        String message = jsonObject.getString(APIKey.COMMON_MESSAGE2);
                        String data = jsonObject.getString(APIKey.COMMON_DATA);

                        onResponseAsyncTaskRender(status, message, data, requestID);

                        if (status.equals(RESPONSE_STATUS_SUCCESS)) {
                            if (!TextUtils.isEmpty(message)) {
                                if (getAttachView() != null)
                                    getAttachView().showToast(message);
                            }
                            if (null != dialog)
                                dialog.dismiss();
                        } else {
                            if (!TextUtils.isEmpty(message)) {
                                if (getAttachView() != null)
                                    getAttachView().showToast(message);
                            }
                            if (null != dialog)
                                dialog.dismiss();
                        }
                    } catch (Exception e) {
                        if (getAttachView() != null) {
                            getAttachView().showToast("无数据");
                            LogUtil.biu("BasePresenter-Exception-"+e);
                        }
                        e.printStackTrace();
                    }
                } else {
                    getAttachView().showToast(MyApplication.getInstance().getResources().getString(R.string.network_error));
                }
            }

            @Override
            public void onFailure(String error) {
                if (getAttachView() != null) {
                    getAttachView().hideProgressView();
                    getAttachView().showToast(MyApplication.getInstance().getResources().getString(R.string.network_error));
                }
            }
        });
    }

    private void runRequestAsyncTask(final OkHttpRequest request) {
        final String requestID = request.getRequestID();

        request.setBaseUrl(ServiceAPIConstant.API_BASE_URL);
        request.showHttpRequestLog();

        OkHttpClientManager.getInstance().commitRequestTask(request, new IResultCallback() {
            @Override
            public void onResponse(String response) {
                LogUtil.biu("response = " + response);

                Map<String, Object> responseMap = convertStringToMap(response);
                //onResponseAsyncTaskRender(responseMap, requestID);
                //onResponseAsyncTaskRender(responseMap, requestID, request.getAdditionalArgsMap());

                if (null != responseMap) {
                    String status = TypeUtil.getString(responseMap.get(APIKey.COMMON_STATUS), "");
                    String message = TypeUtil.getString(responseMap.get(APIKey.COMMON_MESSAGE2), "");
                    String code = TypeUtil.getString(responseMap.get(APIKey.COMMON_RESPONSE_CODE), "");
                    Map<String, Object> dataMap = TypeUtil.getMap(responseMap.get(APIKey.COMMON_DATA));

                    onResponseAsyncTaskRender(status, message, dataMap, requestID);
                    //onResponseAsyncTaskRender(status, message, identifyingCode, dataMap, requestID);
                    //onResponseAsyncTaskRender(status, message, dataMap, requestID, request.getAdditionalArgsMap());

                    if (status.equals(RESPONSE_STATUS_SUCCESS)) {
                        if (!TextUtils.isEmpty(message)) {
                            if (getAttachView() != null)
                                getAttachView().showToast(message);
                        }
                        if (null != dialog)
                            dialog.dismiss();
                    } else {
                        if (!TextUtils.isEmpty(message)) {
                            if (getAttachView() != null)
                                getAttachView().showToast(message);
                        }
                        if (null != dialog)
                            dialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                final boolean isFirstUse = PersistentDataCacheEntity.getInstance().isFirstUse();

                if (null != getAttachView()) {
                    if (null != dialog) {
                        dialog.dismiss();
                    }
                    /*if (getAttachView().getParentContext() instanceof StartActivity) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((StartActivity) getAttachView().getParentContext()).finish();

                                if (isFirstUse) {
                                    Intent splashIntent = new Intent(getAttachView().getParentContext(), SplashActivity.class);
                                    getAttachView().getParentContext().startActivity(splashIntent);
                                } else {
                                    Intent homeWecashIntent = new Intent(getAttachView().getParentContext(), HomeActivityWecash.class);
                                    getAttachView().getParentContext().startActivity(homeWecashIntent);
                                }
                            }
                        }, 1000);
                    }*/
                    if (getAttachView() != null) {
                        getAttachView().hideProgressView();
                        getAttachView().showToast(MyApplication.getInstance().getResources().getString(R.string.network_error));
                    }
                }
            }
        });
    }

    private Map<String, Object> convertStringToMap(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        } else {
            return new CommonJSONParser().parse(str);
        }
    }

    /**
     * Created by Aloha <br>
     * -explain 数据解析CallBack
     * @Date 2017/11/27 20:20
     */
    protected void onResponseAsyncTaskRender(final String status, final String message,
                                                       final Map<String, Object> dataMap, final String requestIDe) {}

    protected void onResponseAsyncTaskRender(final String status, final String message,
                                                       final String dataResult, final String requestID){}
}
