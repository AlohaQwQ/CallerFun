package com.xixi.finance.callerfun.ui.view.main;

import java.util.List;
import java.util.Map;

import aloha.shiningstarbase.base.IBaseView;

/**
 * Created by Aloha on 2016/8/22.
 */
public interface IUserView extends IBaseView {

    /**
     * Created by Aloha <br>
     * -explain 拓展埋点统计数据上传完成
     * @Date 2016/11/18 14:32
     */
    void finishUpdateStatisticsDataExpand();

    /**
     * Created by Aloha <br>
     * -explain Base埋点统计数据上传完成
     * @Date 2016/11/18 14:32
     */
    void finishUpdateStatisticsDataBase();

    /**
     * Created by Aloha <br>
     * -explain 获取 盐值sign
     * @Date 2016/11/28 20:25
     */
    void fetchStatisticsEncodeSign();

    /**
     * Created by Aloha <br>
     * -explain 盐值sign 失效,重新上传数据
     * @Date 2016/11/18 14:32
     */
    void updateStatisticsDataAgain(String encodeSign);

    /**
     * Created by Aloha <br>
     * -explain 获取首页提示语/公告弹框
     * @Date 2016/11/18 14:32
     */
    void showHomePageNocicePop(String close, String title, String content, int whetherDialog, String remark);

    /**
     * Created by Aloha <br>
     * -explain 存储首页黑白灰点击记录
     * @Date 2017/7/13 10:01
     */
    void showSaveProductClickRecord(String status, String message);

    /**
     * Created by Aloha <br>
     * -explain 获取首页黑白灰资方分类查询
     * @Date 2017/7/13 10:01
     * @param dataList
     */
    void showWhiteBlackProductCategory(List dataList);

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
