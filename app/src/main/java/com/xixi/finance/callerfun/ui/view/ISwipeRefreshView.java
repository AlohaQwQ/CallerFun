package com.xixi.finance.callerfun.ui.view;

/**
 * 列表刷新视图接口
 *
 * @author shiliang.zou
 * @version 0.0.1
 */
public interface ISwipeRefreshView extends IBaseView {

    /**
     * 是否正在加载
     * @return
     */
    boolean isLoadingMore();

    /**
     * 是否正在刷新
     * @return
     */
    boolean isRefreshing();

    /**
     * 设置是否正在刷新
     * @param refresh 是否刷新
     */
    void setRefresh(boolean refresh);

    /**
     * 设置是否正在加载
     */
    void setLoadMore(boolean loadMore);
}
