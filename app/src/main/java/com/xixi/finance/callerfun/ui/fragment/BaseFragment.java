package com.xixi.finance.callerfun.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.presenter.BasePresenter;
import com.xixi.finance.callerfun.ui.view.IBaseView;
import com.xixi.finance.callerfun.util.LogUtil;

import com.xixi.finance.callerfun.widget.MultiStatusView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *  Created by Aloha <br>
 *  -explain 父类Activity
 *  @Date 2016/9/26 16:47
 *  @version 1.0.0
 **/

public abstract class BaseFragment<V extends IBaseView,P extends BasePresenter<V>> extends Fragment implements IBaseView{

    @Nullable
    @BindView(R.id.swipe_refresh_status_layout)
    MultiStatusView mSwipeRefreshStatusLayout;
    @Nullable
    @BindView(R.id.layout_toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.tv_title)
    TextView titleTv;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    /**Bundle 缓存标识 key,用于保存 Activity 传递参数并保存。当fragment 强制初始化(如横竖屏切换)时,
     getArguments() 会返回之前setArguments() 保存的 bundle参数,并作用于fragment 初始化 onCreate().
     */
    private static final String ARG_SECTION_PAGE = "section_page";
    /**Bundle 缓存value
     * */
    private int mPage = 0;

    protected Context mContext;
    protected P mPresenter;
    protected View mContentView;
    private Unbinder unbinder;
    protected ProgressDialog baseProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(getLayoutID(), container, false);
        ButterKnife.bind(this, mContentView);
        LogUtil.biubiubiu(getClass().getName());
        mPresenter = CreatePresenter();
        if (mPresenter != null)
            mPresenter.attachView(this);
        unbinder = ButterKnife.bind(mContentView);

        this.init(savedInstanceState);
        return mContentView;
    }

    /**
     * Created by Aloha <br>
     * @Date 2016/9/26 17:42
     * @explain 初始化数据等
     */
    protected abstract void init(Bundle savedInstanceState);

    /**
     * Created by Aloha <br>
     * @Date 2016/9/26 17:42
     * @explain 创建Presenter
     */
    protected abstract P CreatePresenter();

    /**
     * Created by Aloha <br>
     * @Date 2016/9/26 16:48
     * @explain 获取布局文件ID
     */
    protected abstract int getLayoutID();

    public View getContentView() {
        return mContentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        unbinder.unbind();
    }

    @Override
    public void showContentView() {
        showStatusView(MultiStatusView.STATUS_NORMAL);
    }

    private void showStatusView(int status) {
        if (null != mSwipeRefreshStatusLayout) {
            mSwipeRefreshStatusLayout.switchStatusView(status);
        }
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getContextView(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToastLong(String msg) {
        Toast.makeText(getContextView(),msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressView() {
        if (null == baseProgressDialog)
            baseProgressDialog = new ProgressDialog(getContextView());
        if (null != baseProgressDialog && !baseProgressDialog.isShowing()) {
            baseProgressDialog.setMessage("加载中");
            baseProgressDialog.show();
        }
    }

    @Override
    public void showProgressView(String msg) {
        if (null == baseProgressDialog)
            baseProgressDialog = new ProgressDialog(getContextView());

        if (null != baseProgressDialog && !baseProgressDialog.isShowing()) {
            if (!TextUtils.isEmpty(msg)) {
                baseProgressDialog.setMessage(msg);
            } else {
                baseProgressDialog.setMessage("加载中");
            }
            baseProgressDialog.show();
        }
    }

    @Override
    public void hideProgressView() {
        if (null != baseProgressDialog && baseProgressDialog.isShowing()) {
            baseProgressDialog.dismiss();
        }
    }

    @Override
    public void showErrorView() {

    }


    @Override
    public void hideErrorView() {

    }

    @Override
    public Context getContextView() {
        return this.getActivity();
    }

}
