/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */

/**
 * Version Control
 *
 * | version | date        | author         | description
 *   0.0.1     2015.11.30    shiliang.zou     初始化
 *
 */

package cn.chutong.sdk.conn.interfaces;

/**
 * 返回结果回调接口
 *
 * @author shiliang.zou
 * @version 0.0.1
 */

public interface IResultCallback {

    void onResponse(String response);

    void onFailure(String error);
}
