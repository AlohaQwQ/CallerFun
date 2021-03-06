package com.xixi.finance.callerfun.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.presenter.BasePresenter;
import com.xixi.finance.callerfun.ui.view.IBaseView;
import com.xixi.finance.callerfun.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ly on 2016/4/7.
 */
public abstract class BaseFragmentActivity<V extends IBaseView, T extends BasePresenter<V>> extends BaseActivity implements IBaseView {

    private static final String TAG = "BaseFragmentActivity";
    public static final String CONTENT_FRAGMEMT = "content_fragment";
    public static final String CONTENT_BUNDLE = "content_bundle";

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    public Map<String, Fragment> mFragmentMap = new HashMap<String, Fragment>();
    private OnBackPressListener onBackPressListener;
    private Fragment mCurFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.biubiubiu(getLocalClassName());

        if (savedInstanceState == null) {
            OpenContentFragment();
        } else {
            if (getSupportFragmentManager().getFragments() != null) {
                Log.d(TAG, "fragment size = " + getSupportFragmentManager().getFragments().size());
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
                    if (getSupportFragmentManager().getFragments().get(i) != null) {
                        mFragmentList.add(getSupportFragmentManager().getFragments().get(i));
                    }
                }
                for (int i = 0; i < mFragmentList.size(); i++) {
                    if (i == mFragmentList.size() - 1) {
                        ft.show(mFragmentList.get(mFragmentList.size() - 1));
                    } else {
                        ft.hide(mFragmentList.get(i)); // 隐藏上一个Fragment
                    }
                }
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected BasePresenter CreatePresenter() {
        return null;
    }

    @Override
    protected int getLayoutID() {
        return 0;
    }

    /**
     * 打开内容fragment
     */
    protected void OpenContentFragment() {
        String target = getIntent().getStringExtra(CONTENT_FRAGMEMT);
        if (null != target) {
            Bundle b = getIntent().getBundleExtra(CONTENT_BUNDLE);
            Bundle data = null;
            if (b != null) {
                data = new Bundle();
                data.putBundle(CONTENT_BUNDLE, b);
            }
            openFragment(target, true, data);
        }
    }

    // 通过这种方式打开的

    /**
     * @String target 目标Fragment
     * @boolean addToStack 是否保存在隐藏栈里
     * @Bundle info
     */
    public void openFragment(String target, boolean addToStack, Bundle info) {
        //initToolbar(target);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = Fragment.instantiate(this, target);
        if (mFragmentList.size() > 0) {
            // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.hide(mFragmentList.get(mFragmentList.size() - 1)); // 隐藏上一个Fragment
            Log.d(TAG, "fragment hide");
        }
        // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // //fragment的开启动画
        ft.add(R.id.ll_content, f);
        if (null != info) {
            // 设置参数
            f.setArguments(info);
        }
        if (addToStack) {
            // 放进栈里
            Log.i(TAG, "准备放进栈里");
            mFragmentList.add(f);
            Log.d(TAG, "fragment size = " + mFragmentList.size());
        }
        //ft.commit();
        ft.commitAllowingStateLoss();
    }

    //切换fragment
    public void openFragment(String target, Bundle info) {
        //initToolbar(target);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = Fragment.instantiate(this, target);

        if (mFragmentMap.containsKey(target)) {
            ft.hide(mCurFragment);
            ft.show(mFragmentMap.get(target));
            mCurFragment = mFragmentMap.get(target);
        } else {
            if (mCurFragment != null) {
                ft.hide(mCurFragment);
            }

            if (null != info) {
                // 设置参数
                f.setArguments(info);
            }
            mFragmentMap.put(target, f);
            ft.add(R.id.ll_content, f);
            mCurFragment = f;
        }

        // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // //fragment的开启动画
        //ft.commit();
        ft.commitAllowingStateLoss();
    }

    public Fragment getCurrentFragment() {
        return mCurFragment;
    }

    public void setBackPressListener(OnBackPressListener onBackPressListener) {
        this.onBackPressListener = onBackPressListener;
    }

    public interface OnBackPressListener {
        public void doMyPressBack();
    }

    /**
     * 显示上一个栈里的Fragment
     */
    @Override
    public void onBackPressed() {
        // 隐藏键盘
        if (getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (onBackPressListener != null) {
            onBackPressListener.doMyPressBack();
            onBackPressListener = null;
            return;
        }
        if (mFragmentList.size() < 2) {
            super.onBackPressed();
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.remove(mFragmentList.get(mFragmentList.size() - 1));
        mFragmentList.remove(mFragmentList.size() - 1);
        ft.show(mFragmentList.get(mFragmentList.size() - 1));
        ft.commit();
        Log.d(TAG, "fragment size = " + getSupportFragmentManager().getFragments().size());
        Log.d(TAG, "mFragmentList.size()" + mFragmentList.size());
    }
}
