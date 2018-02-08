package com.xixi.finance.callerfun.ui.view.user;

import com.xixi.finance.callerfun.ui.view.IBaseView;

/**
 * Created by Aloha <br>
 * -explain
 * @Date 2018/1/29 10:42
 */
public interface ILoginView extends IBaseView {

    void onLoginSuccess(String response, String status, String message);

}
