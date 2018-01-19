package com.xixi.finance.callerfun.ui.view.main;

import com.xixi.finance.callerfun.ui.view.IBaseView;
import java.util.List;
import java.util.Map;

/**
 * Created by Aloha on 2016/8/22.
 */
public interface IRecordView extends IBaseView {

    /**
     * Created by Aloha <br>
     * -explain 获取首页黑白灰标题查询
     * @Date 2017/7/13 10:01
     * @param dataList
     */
    void showWhiteBlackProductsTitle(List<Map<String, String>> dataList);

    /**
     * Created by Aloha <br>
     * -explain 查询用户黑白灰类型
     * @Date 2017/12/13 10:01
     */
    void showWhiteBlackUserType(Map<String, Object> dataMap);
}
