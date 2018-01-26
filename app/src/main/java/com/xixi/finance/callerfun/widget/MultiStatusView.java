package com.xixi.finance.callerfun.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.xixi.finance.callerfun.R;


/**
 * 多状态更新视图（加载中、加载失败）
 *
 * @author shiliang.zou
 * @version 0.0.1
 */
public class MultiStatusView extends FrameLayout {

    public static final int STATUS_NORMAL = 0;      // 正常状态
    public static final int STATUS_LOADING = 1;     // 加载状态，对应LoadingView
    public static final int STATUS_ERROR = 2;       // 错误状态，对应ErrorView
    public static final int STATUS_EMPTY = 3;       // 空状态, 对应EmptyView

    private View mLoadingView;
    private View mErrorView;
    private View mEmptyView;
    private int status;
    private TypedArray mTypeArray;

    private LayoutInflater inflater;

    public MultiStatusView(Context context) {
        this(context, null);
    }

    public MultiStatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflater = LayoutInflater.from(context);

        mTypeArray = context.obtainStyledAttributes(attrs, R.styleable.MultiStatusView, defStyleAttr, 0);

        this.status = mTypeArray.getInt(R.styleable.MultiStatusView_msv_status, STATUS_NORMAL);

        //int mLoadingViewResId = mTypeArray.getResourceId(R.styleable.MultiStatusView_msv_loading_view, R.layout.status_view_loading);
        int mLoadingViewResId = mTypeArray.getResourceId(R.styleable.MultiStatusView_msv_loading_view, R.layout.progress_loading_tantantan);
        this.mLoadingView = inflater.inflate(mLoadingViewResId, this, false);
        //android 6.0替换clip的加载动画
        if (android.os.Build.VERSION.SDK_INT > 22) {
            final Drawable drawable =  context.getApplicationContext().getResources().getDrawable(R.drawable.anim_loading_tantan_v23);
            ((ProgressBar)mLoadingView.findViewById(R.id.progress_loading_tantantan)).setIndeterminateDrawable(drawable);
        }
//        addView(this.mLoadingView, this.mLoadingView.getLayoutParams());

        int mErrorViewResId = mTypeArray.getResourceId(R.styleable.MultiStatusView_msv_error_view, R.layout.status_view_error);
        this.mErrorView = inflater.inflate(mErrorViewResId, this, false);
//        addView(this.mErrorView, this.mErrorView.getLayoutParams());

        int mEmptyViewResId = mTypeArray.getResourceId(R.styleable.MultiStatusView_msv_empty_view, R.layout.status_view_empty);
        this.mEmptyView = inflater.inflate(mEmptyViewResId, this, false);
//        addView(this.mEmptyView, this.mEmptyView.getLayoutParams());

        mTypeArray.recycle();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        setLoadingView(this.mLoadingView);
        setErrorView(this.mErrorView);
        setEmptyView(this.mEmptyView);
        switchStatusView(this.status);
    }

    /**
     * 切换状态对应视图
     * @param status 目标状态
     */
    public void switchStatusView(int status) {
        //        if (this.status == status) return;
        switch (status) {
            case STATUS_NORMAL:
                mLoadingView.setVisibility(View.GONE);
                mErrorView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
                break;
            case STATUS_LOADING:
                mLoadingView.setVisibility(View.VISIBLE);
                mErrorView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
                break;
            case STATUS_ERROR:
                mLoadingView.setVisibility(View.GONE);
                mErrorView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
                break;
            case STATUS_EMPTY:
                mLoadingView.setVisibility(View.GONE);
                mErrorView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
        this.status = status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setLoadingView(int resId) {
        setLoadingView(inflater.inflate(resId, this, false));
    }

    public void setLoadingView(View view) {
        if (null != view) {
            if (null != this.mLoadingView) {
                this.removeView(this.mLoadingView);
            }
            addView(view);
            this.mLoadingView = view;
        }
    }

    public View getLoadingView() {
        return this.mLoadingView;
    }

    public void setErrorView(int resId) {
        setErrorView(inflater.inflate(resId, this, false));
    }

    public void setErrorView(View view) {
        if (null != view) {
            if (null != this.mErrorView) {
                this.removeView(this.mErrorView);
            }
            addView(view);
            this.mErrorView = view;
        }
    }

    public View getErrorView() {
        return this.mErrorView;
    }

    public void setEmptyView(int resId) {
        setEmptyView(inflater.inflate(resId, this, false));
    }

    public void setEmptyView(View view) {
        if (null != view) {
            if (null != this.mEmptyView) {
                this.removeView(this.mEmptyView);
            }
            addView(view);
            this.mEmptyView = view;
        }
    }

    public View getEmptyView() {
        return this.mEmptyView;
    }

    public void clearTypeArray(){
        if(mTypeArray!=null)
            mTypeArray = null;
    }
}
