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
 * 网络返回结果缓存接口
 *
 * @author shiliang.zou
 * @version 0.0.1
 */
public interface IConnectorDatabank {

    String getData(String request);

    void saveData(String request, String response);

    void deleteData();

    void closeDatabase();
}
