package com.xixi.finance.callerfun.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.presenter.BasePresenter;
import com.xixi.finance.callerfun.ui.view.IBaseView;
import com.xixi.finance.callerfun.ui.view.ISwipeRefreshView;
import com.xixi.finance.callerfun.widget.LeRecyclerView;

import butterknife.BindView;

/**
 * Fragment基类，支持列表的下拉刷新和上拉加载
 *
 * @author shiliang.zou
 * @version 0.0.1
 */
public abstract class BaseSwipeRefreshFragment<V extends IBaseView, T extends BasePresenter<V>> extends BaseFragment<V, T> implements SwipeRefreshLayout.OnRefreshListener, ISwipeRefreshView {

    @Nullable
    @BindView(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout swipe_refresh_layout;
    @Nullable
    @BindView(R.id.recycler_view)
    protected LeRecyclerView recycler_view;

    /**
     * 是否正在刷新
     */
    private boolean isRefreshing;

    /**
     * 是否正常加载
     */
    private boolean isLoadingMore;

    @Override
    protected void init(Bundle savedInstanceState) {
        initSwipeRefreshLayout();
        initRecyclerView();
    }

    private void initSwipeRefreshLayout() {
        if (null != swipe_refresh_layout) {
            swipe_refresh_layout.setColorSchemeResources(R.color.colorPrimaryDark,R.color.colorAccent);
            swipe_refresh_layout.setOnRefreshListener(this);
        }
    }

    private void initRecyclerView() {
        if (null != recycler_view) {
            recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    int totalItemCount;
                    int lastVisibleItemPosition;
                    RecyclerView.LayoutManager mLayoutManager = recyclerView.getLayoutManager();

                    if (mLayoutManager instanceof LinearLayoutManager) { // 线性布局
                        LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) mLayoutManager;
                        lastVisibleItemPosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                        totalItemCount = mLayoutManager.getItemCount();

                        if (!isLoadingMore && lastVisibleItemPosition == (totalItemCount - 1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                            isLoadingMore = true;
                            onLoadMore();
                        }
                    } else if (mLayoutManager instanceof StaggeredGridLayoutManager) { // 瀑布流布局
                        StaggeredGridLayoutManager mStaggeredGridLayoutManager = (StaggeredGridLayoutManager) mLayoutManager;
                        int spanCount = mStaggeredGridLayoutManager.getSpanCount();
                        int[] lastVisibleItemPositionArray = mStaggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(new int[spanCount]);
                        lastVisibleItemPosition = lastVisibleItemPositionArray[spanCount - 1];
                        totalItemCount = mStaggeredGridLayoutManager.getItemCount();

                        if (!isLoadingMore && lastVisibleItemPosition == (totalItemCount - 1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                            isLoadingMore = true;
                            onLoadMore();
                        }
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }
    }

    protected abstract void onLoadMore();

    protected abstract void onSwipeRefresh();

    @Override
    public void onRefresh() {
        if (!this.isRefreshing) {
            this.isRefreshing = true;
            onSwipeRefresh();
        }
    }

    @Override
    public boolean isRefreshing() {
        return this.isRefreshing;
    }

    @Override
    public void setRefresh(boolean refresh) {
        if (this.isRefreshing != refresh) {
            this.isRefreshing = refresh;
            swipe_refresh_layout.setRefreshing(refresh);
        }
    }

    @Override
    public void setLoadMore(boolean loadMore) {
        this.isLoadingMore = loadMore;
    }
}
