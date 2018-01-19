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

import cn.chutong.sdk.conn.OkHttpClientConfiguration;
import cn.chutong.sdk.conn.OkHttpRequest;
import cn.chutong.sdk.conn.OkHttpRequestOptions;

/**
 * 网络客户端接口
 *
 * @author shiliang.zou
 * @version 0.0.1
 */
public interface IOkHttpClient {

    void prepare(OkHttpClientConfiguration configuration);
    void sendResultCallback(int resultCode, String result, IResultCallback mResultCallback);
    void commitRequestTask(OkHttpRequest okRequest, OkHttpRequestOptions options, IResultCallback mResultCallback);

    void getSyncTask(OkHttpRequest okRequest, OkHttpRequestOptions options, IResultCallback mResultCallback);
    void getAsyncTask(OkHttpRequest okRequest, OkHttpRequestOptions options, IResultCallback mResultCallback);

    void postSyncTask(OkHttpRequest okRequest, OkHttpRequestOptions options, IResultCallback mResultCallback);
    void postAsyncTask(OkHttpRequest okRequest, OkHttpRequestOptions options, IResultCallback mResultCallback);

}
