package com.xixi.finance.callerfun.ui.view.main;

import com.xixi.finance.callerfun.ui.view.ISwipeRefreshView;

/**
 * Created by Aloha on 2016/8/22.
 */
public interface IRecordView extends ISwipeRefreshView {

    /**
     * 刷新本地录音文件
     */
    void refreshLocalCallRecords();

    /**
     * 显示客户资料page
     * @param responsePage
     */
    void showCustomerPage(String responsePage);

}
