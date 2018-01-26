package com.xixi.finance.callerfun.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.xixi.finance.callerfun.MyApplication;
import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.presenter.BasePresenter;
import com.xixi.finance.callerfun.ui.fragment.BaseFragment;
import com.xixi.finance.callerfun.ui.view.IBaseView;

import java.util.Stack;

import aloha.shiningstarbase.logger.LogUtil;
import com.xixi.finance.callerfun.widget.MultiStatusView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Aloha <br>
 * -explain 父类Activity
 *
 * @version 1.0.0
 * @Date 2016/9/26 16:47
 **/
public abstract class BaseActivity<V extends IBaseView, P extends BasePresenter<V>> extends AppCompatActivity implements IBaseView {

    protected P mPresenter;
    private Unbinder unbinder;
    @Nullable
    @BindView(R.id.swipe_refresh_status_layout)
    MultiStatusView mSwipeRefreshStatusLayout;
    @Nullable
    @BindView(R.id.layout_toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.tv_title)
    TextView titleTv;

    private Stack<String> fragmentNameStack;
    protected ProgressDialog baseProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getLayoutID());

        unbinder = ButterKnife.bind(this);

        MyApplication.getInstance().pushOneActivity(this);

        LogUtil.biu(getLocalClassName());

        mPresenter = CreatePresenter();
        if (mPresenter != null)
            //Presenter 绑定 View
            mPresenter.attachView(this);

        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new TestFragment("param")).commit();
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        if (toolbar != null) {
            toolbar.setBackgroundResource(R.color.xixi_blue);
            //toolbar.setNavigationIcon(R.mipmap.ic_arrow_left);
            toolbar.setTitle("");
            titleTv.setTextColor(getResources().getColor(R.color.xixi_title_color));
        }
        this.init();
    }

    /**
     * Created by Aloha <br>
     *
     * @Date 2016/9/26 17:42
     * @explain 初始化数据等
     */
    protected abstract void init();

    /**
     * Created by Aloha <br>
     *
     * @Date 2016/9/26 17:42
     * @explain 创建Presenter
     */
    protected abstract P CreatePresenter();

    /**
     * Created by Aloha <br>
     *
     * @Date 2016/9/26 16:48
     * @explain 获取布局文件ID
     */
    protected abstract int getLayoutID();

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        unbinder.unbind();
        System.gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        boolean isLastFragment = true;

        if (null != fragmentNameStack && fragmentNameStack.size() >= 2) {
            fragmentNameStack.pop();
            BaseFragment backFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(fragmentNameStack.peek());
            getSupportFragmentManager().popBackStack();

            isLastFragment = false;
        } // if (null != fragmentNameStack && fragmentNameStack.size() >= 2)

        if (isLastFragment) {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
        Toast.makeText(getContextView(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToastLong(String msg) {
        Toast.makeText(getContextView(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showErrorView() {}

    @Override
    public void showProgressView() {
        if (null == baseProgressDialog)
            baseProgressDialog = new ProgressDialog(this);
        if (null != baseProgressDialog && !baseProgressDialog.isShowing()) {
            baseProgressDialog.setMessage("加载中");
            baseProgressDialog.show();
        }
    }

    @Override
    public void showProgressView(String msg) {
        if (null == baseProgressDialog)
            baseProgressDialog = new ProgressDialog(this);

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

    /**
     * Created by Aloha <br>
     * -explain 重载finish 方法，解决内存泄露
     * @Date 2017/10/28 14:48
     */
    @Override
    public void finish() {
        MyApplication.getInstance().popActivity();
        super.finish();
    }

    @Override
    public void hideErrorView() {}


    @Override
    public Context getContextView() {
        return this;
    }

    protected void setStatusBarFloatable(boolean isStatusBarFloat) {
        /*this.isStatusBarFloat = isStatusBarFloat;
        if (isStatusBarFloat) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //设置为悬浮透明
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            }
        }*/
    }

    protected void setStatusBarColor(int color) {
        /*if (isStatusBarFloat) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //设置背景色
                if (systemBarTintManager == null) {
                    systemBarTintManager = new SystemBarTintManager(this);
                }
                systemBarTintManager.setStatusBarTintEnabled(true);
                systemBarTintManager.setStatusBarTintResource(color);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(color));
            }
        }*/
    }

    /**
     * 默认的页面切换效果
     * @param intent
     * @param options
     *//*
    @Override
    public void startActivity(Intent intent, Bundle options) {
        super.startActivity(intent, options);
        onSwitchActivityAnimation();
    }


    private void startActivityNoAnim(Intent intent, Bundle options) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            super.startActivity(intent, options);
        } else {
            super.startActivity(intent);
        }
    }

    *//**
     * 自定义切换特效的Activity
     * @param intent
     *//*
    public void startActivity(Intent intent, int enterAnim, int outAnim) {
        startActivityNoAnim(intent, null);
        overridePendingTransition(enterAnim, outAnim);
    }

    *//**
     * 默认的页面切换效果
     *//*
    //// TODO: 16-8-1, 写定集中切换方式
    protected void onSwitchActivityAnimation() {
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    protected void onFinishAnimation() {
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onFinishAnimation();
    }
*/
}