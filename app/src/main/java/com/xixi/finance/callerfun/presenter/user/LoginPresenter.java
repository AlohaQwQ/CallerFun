package com.xixi.finance.callerfun.presenter.user;

import com.xixi.finance.callerfun.constant.ServiceAPIConstant;
import com.xixi.finance.callerfun.model.user.IUserModel;
import com.xixi.finance.callerfun.model.user.UserModel;
import com.xixi.finance.callerfun.presenter.BasePresenter;
import com.xixi.finance.callerfun.ui.view.user.ILoginView;

import cn.chutong.sdk.conn.OkHttpRequest;

/**
 * Created by TanJiaJun on 2016/4/20.
 */
public class LoginPresenter extends BasePresenter<ILoginView> {

    private IUserModel userModel;

    public LoginPresenter() {
        userModel = new UserModel();
    }

    public void userLogin(String account, String password) {
        OkHttpRequest request = userModel.userLogin(account,password);
        addRequestAsyncTaskForJson(request);
    }

    @Override
    protected void onResponseAsyncTaskRender(final String status, final String message,
                                             final String response, final String requestID) {
        super.onResponseAsyncTaskRender(status, message, response, requestID);
        if (ServiceAPIConstant.REQUEST_API_USER_LOGIN.equals(requestID)) {
            if (null != getView())
                LoginPresenter.this.getView().onLoginSuccess(response,status,message);
        }
    }
}
