/**
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 * <p>
 * <p>
 * Version Control
 * <p>
 * | version | date        | author         | description
 * 0.0.1     2015.11.30    shiliang.zou     初始化
 */

/**
 * Version Control
 *
 * | version | date        | author         | description
 *   0.0.1     2015.11.30    shiliang.zou     初始化
 *
 */

package cn.chutong.sdk.conn;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.chutong.sdk.conn.interfaces.IOkHttpClient;
import cn.chutong.sdk.conn.interfaces.IResultCallback;

/**
 * 网络请求客户端
 *
 * @author shiliang.zou
 * @version 0.0.1
 */
public class OkHttpClient implements IOkHttpClient
{

    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_FAIL = -1;

    private com.squareup.okhttp.OkHttpClient mOkHttpClient;
    private Handler mHandler;

    public OkHttpClient()
    {
        mHandler = new Handler(Looper.getMainLooper());
        mOkHttpClient = new com.squareup.okhttp.OkHttpClient();
    }

    /**
     * 初始化
     *
     * @param configuration 网络客户端配置
     * @see OkHttpClientConfiguration
     * @since 0.0.1
     */
    @Override
    public void prepare(OkHttpClientConfiguration configuration)
    {
        if (null != configuration)
        {
            // 设置客户端和服务器建立连接的超时时间
            if (configuration.connectTimeoutSeconds > 0)
            {
                mOkHttpClient.setConnectTimeout(configuration.connectTimeoutSeconds, TimeUnit.SECONDS);
            }

            // 设置客户端上传数据到服务器的超时时间
            if (configuration.readTimeoutSeconds > 0)
            {
                mOkHttpClient.setReadTimeout(configuration.readTimeoutSeconds, TimeUnit.SECONDS);
            }

            // 设置客户端从服务器下载响应数据的超时时间
            if (configuration.writeTimeoutSeconds > 0)
            {
                mOkHttpClient.setWriteTimeout(configuration.writeTimeoutSeconds, TimeUnit.SECONDS);
            }

            //            if (configuration.isPersistentCookieSupported) {
            //                mOkHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(configuration.context), CookiePolicy.ACCEPT_ALL));
            //            }

            if (configuration.isLocalCacheSupported)
            {
                mOkHttpClient.networkInterceptors().add(configuration.interceptor);
                mOkHttpClient.setCache(configuration.localCache);
            }
        }
    }

    /**
     * 提交网络请求
     *
     * @param okRequest 网络请求参数
     * @param options 网络请求选项
     * @param mResultCallback 结果回调接口
     * @since 0.0.1
     * @see #getSyncTask(OkHttpRequest, OkHttpRequestOptions, IResultCallback)
     * @see #getAsyncTask(OkHttpRequest, OkHttpRequestOptions, IResultCallback)
     * @see #postSyncTask(OkHttpRequest, OkHttpRequestOptions, IResultCallback)
     * @see #postAsyncTask(OkHttpRequest, OkHttpRequestOptions, IResultCallback)
     * @since 0.0.1
     */
    @Override
    public void commitRequestTask(OkHttpRequest okRequest, OkHttpRequestOptions options, IResultCallback mResultCallback)
    {
        if (null == okRequest)
        {
            sendResultCallback(RESULT_FAIL, "empty request", mResultCallback);
            return;
        }

        if (null == options)
        {
            options = OkHttpRequestOptions.createSimple();
        }

        if (options.async)
        {
            if (options.method.equals(OkHttpRequestOptions.GET))
            {
                getAsyncTask(okRequest, options, mResultCallback);
            } else if (options.method.equals(OkHttpRequestOptions.POST))
            {
                postAsyncTask(okRequest, options, mResultCallback);
            }
        } else
        {
            if (options.method.equals(OkHttpRequestOptions.GET))
            {
                getSyncTask(okRequest, options, mResultCallback);
            } else if (options.method.equals(OkHttpRequestOptions.POST))
            {
                postSyncTask(okRequest, options, mResultCallback);
            }
        }
    }

    /**
     * 同步GET请求
     *
     * @param okRequest 网络请求参数
     * @param options 网络请求选项
     * @param mResultCallback 结果回调接口
     * @see #sendResultCallback(int, String, IResultCallback)
     * @see #deliverSyncResult(Request, OkHttpRequest, IResultCallback)
     * @since 0.0.1
     */
    @Override
    public void getSyncTask(OkHttpRequest okRequest, OkHttpRequestOptions options, IResultCallback mResultCallback)
    {
        String requestUrl = okRequest.getBaseUrl() + okRequest.getAPIPath();
        if (TextUtils.isEmpty(requestUrl))
        {
            sendResultCallback(RESULT_FAIL, "empty request url", mResultCallback);
        } else
        {
            Request syncRequest = new Request.Builder().url(requestUrl).build();
            deliverSyncResult(syncRequest, okRequest, mResultCallback);
        }
    }

    /**
     * 异步GET请求
     *
     * @param okRequest 网络请求参数
     * @param options 网络请求选项
     * @param mResultCallback 结果回调接口
     * @see #sendResultCallback(int, String, IResultCallback)
     * @see #deliverAsyncResult(Request, OkHttpRequest, IResultCallback)
     * @since 0.0.1
     */
    @Override
    public void getAsyncTask(OkHttpRequest okRequest, OkHttpRequestOptions options, final IResultCallback mResultCallback)
    {
        String requestUrl = okRequest.getBaseUrl() + okRequest.getAPIPath();
        if (TextUtils.isEmpty(requestUrl))
        {
            sendResultCallback(RESULT_FAIL, "empty request url", mResultCallback);
        } else
        {
            Request asyncRequest = new Request.Builder().url(requestUrl).build();
            deliverAsyncResult(asyncRequest, okRequest, mResultCallback);
        }

    }

    /**
     * 同步POST请求
     *
     * @param okRequest 网络请求参数
     * @param options 网络请求选项
     * @param mResultCallback 结果回调接口
     * @see #deliverSyncResult(Request, OkHttpRequest, IResultCallback)
     * @since 0.0.1
     */
    @Override
    public void postSyncTask(OkHttpRequest okRequest, OkHttpRequestOptions options, IResultCallback mResultCallback)
    {
        if (null != okRequest)
        {
            if (null == okRequest.getRequestFileTypeParamMap())
            {
                deliverSyncResult(buildSingleFormRequest(okRequest, options), okRequest, mResultCallback);
            } else
            {
                deliverSyncResult(buildMultiPartFormRequest(okRequest, options), okRequest, mResultCallback);
            }
        }
    }

    /**
     * 异步POST请求
     *
     * @param okRequest 网络请求参数
     * @param options 网络请求选项
     * @param mResultCallback 结果回调接口
     * @see #deliverSyncResult(Request, OkHttpRequest, IResultCallback)
     * @since 0.0.1
     */
    @Override
    public void postAsyncTask(OkHttpRequest okRequest, OkHttpRequestOptions options, IResultCallback mResultCallback)
    {
        if (null != okRequest)
        {
            if (null == okRequest.getRequestFileTypeParamMap())
            {
                deliverAsyncResult(buildSingleFormRequest(okRequest, options), okRequest, mResultCallback);
            } else
            {
                deliverAsyncResult(buildMultiPartFormRequest(okRequest, options), okRequest, mResultCallback);
            }
        }
    }

    /**
     * 创建单表单（不含上传文件等）请求
     *
     * @param okRequest 网络请求参数
     * @param options 网络请求选项
     * @return 表单请求
     * @see com.squareup.okhttp.FormEncodingBuilder
     * @see com.squareup.okhttp.Request
     * @since 0.0.1
     */
    public Request buildSingleFormRequest(OkHttpRequest okRequest, OkHttpRequestOptions options)
    {
        Request request = null;

        if (null != okRequest)
        {
            String url = okRequest.getBaseUrl() + okRequest.getAPIPath();
            FormEncodingBuilder formBuilder = new FormEncodingBuilder();
            Map<String, List<String>> requestFormParamMap = okRequest.getRequestFormParamMap();

            if (null != requestFormParamMap)
            {
                for (Map.Entry<String, List<String>> paramEntry : requestFormParamMap.entrySet())
                {
                    if (null != paramEntry)
                    {
                        String paramKey = paramEntry.getKey();
                        List<String> paramValues = paramEntry.getValue();

                        if (null != paramValues)
                        {
                            for (String value : paramValues)
                            {
                                /**
                                 * Created by Aloha <br>
                                 * -explain value 不能为null
                                 * @Date 2017/12/1 10:13
                                 */
                                formBuilder.add(paramKey, value);
                            }
                        } else
                        {
                            formBuilder.add(paramKey, "");
                        }
                    }
                }
            }

            request = new Request.Builder().url(url).method(options.method, formBuilder.build()).build();
        }

        return request;
    }

    /**
     * 创建多表单（含上传文件等）请求
     *
     * @param okRequest 网络请求参数
     * @param options 网络请求选项
     * @return 表单请求
     * @see com.squareup.okhttp.MultipartBuilder
     * @see com.squareup.okhttp.Request
     * @since 0.0.1
     */
    public Request buildMultiPartFormRequest(OkHttpRequest okRequest, OkHttpRequestOptions options)
    {
        Request request = null;

        if (null != okRequest)
        {
            MultipartBuilder multipartBuilder = new MultipartBuilder();

            String url = okRequest.getBaseUrl() + okRequest.getAPIPath();
            Map<String, List<String>> requestFormParamMap = okRequest.getRequestFormParamMap();
            Map<String, List<File>> requestFileTypeMap = okRequest.getRequestFileTypeParamMap();

            if (null != requestFormParamMap)
            {
                for (Map.Entry<String, List<String>> paramEntry : requestFormParamMap.entrySet())
                {
                    if (null != paramEntry)
                    {
                        String paramKey = paramEntry.getKey();
                        List<String> paramValues = paramEntry.getValue();

                        if (null != paramValues)
                        {
                            for (String value : paramValues)
                            {
                                Headers headers = Headers.of("Content-Disposition", "form-data; name=\"" + paramKey + "\"");
                                RequestBody requestBody = RequestBody.create(null, value);
                                multipartBuilder.addPart(headers, requestBody);
                            }
                        } else
                        {
                            Headers headers = Headers.of("Content-Disposition", "form-data; name=\"" + paramKey + "\"");
                            RequestBody requestBody = RequestBody.create(null, "");
                            multipartBuilder.addPart(headers, requestBody);
                        }
                    }
                }
            }

            if (null != requestFileTypeMap)
            {
                for (Map.Entry<String, List<File>> paramEntry : requestFileTypeMap.entrySet())
                {
                    if (null != paramEntry)
                    {
                        String paramKey = paramEntry.getKey();
                        List<File> paramValue = paramEntry.getValue();

                        if (null != paramValue)
                        {
                            for (File targetFile : paramValue)
                            {
                                if (null != targetFile)
                                {
                                    String fileName = targetFile.getName();
                                    Headers headers = Headers.of("Content-Disposition", "form-data; name=\"" + paramKey + "\"; filename=\"" + fileName + "\"");
                                    RequestBody requestBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), targetFile);
                                    multipartBuilder.addPart(headers, requestBody);
                                }
                            }
                        }
                    }
                }
            }

            request = new Request.Builder().url(url).method(options.method, multipartBuilder.build()).build();
        }

        return request;
    }

    /**
     * 创建JSON请求
     *
     */
    public Request buildGetJsonFormRequest(OkHttpRequest okRequest, OkHttpRequestOptions options) {
        Request request = null;
        if (null != okRequest) {
            MultipartBuilder multipartBuilder = new MultipartBuilder();
            String url = okRequest.getBaseUrl() + okRequest.getAPIPath();
            Map<String, List<String>> requestFormParamMap = okRequest.getRequestFormParamMap();
            Map<String, List<File>> requestFileTypeMap = okRequest.getRequestFileTypeParamMap();

            if (null != requestFormParamMap) {
                for (Map.Entry<String, List<String>> paramEntry : requestFormParamMap.entrySet()) {
                    if (null != paramEntry) {
                        String paramKey = paramEntry.getKey();
                        List<String> paramValues = paramEntry.getValue();

                        if (null != paramValues) {
                            for (String value : paramValues)
                            {
                                Headers headers = Headers.of("Content-Disposition", "form-data; name=\"" + paramKey + "\"");
                                RequestBody requestBody = RequestBody.create(null, value);
                                multipartBuilder.addPart(headers, requestBody);
                            }
                        } else {
                            Headers headers = Headers.of("Content-Disposition", "form-data; name=\"" + paramKey + "\"");
                            RequestBody requestBody = RequestBody.create(null, "");
                            multipartBuilder.addPart(headers, requestBody);
                        }
                    }
                }
            }
            request = new Request.Builder().url(url).method(options.method, multipartBuilder.build()).build();
        }
        return request;
    }

    /**
     * 传递同步请求
     *
     * @param request 生成请求
     * @param okRequest 网络请求参数
     * @param mResultCallback 结果回调接口
     * @see OkHttpClientManager#saveResponseAsLocalData(Context, String, String)
     * @see com.squareup.okhttp.OkHttpClient#newCall(Request)
     * @see com.squareup.okhttp.Call#execute()
     * @since 0.0.1
     */
    private void deliverSyncResult(final Request request, OkHttpRequest okRequest, final IResultCallback mResultCallback)
    {
        if (null == request)
        {
            sendResultCallback(RESULT_FAIL, "empty request", mResultCallback);
        } else
        {
            try
            {
                Response response = mOkHttpClient.newCall(request).execute();

                // 保存离线数据至数据库
                Context context = OkHttpClientManager.getInstance().getContext();
                String requestUrl = okRequest.getLocalRequestUrl();
                String responseStr = response.body().string();
                OkHttpClientManager.getInstance().saveResponseAsLocalData(context, requestUrl, responseStr);

                sendResultCallback(RESULT_SUCCESS, responseStr, mResultCallback);
            } catch (IOException e)
            {
                e.printStackTrace();
                sendResultCallback(RESULT_FAIL, e.getMessage(), mResultCallback);
            }
        }
    }

    /**
     * 传递异步请求
     *
     * @param request 生成请求
     * @param okRequest 网络请求参数
     * @param mResultCallback 结果回调接口
     * @see OkHttpClientManager#saveResponseAsLocalData(Context, String, String)
     * @see com.squareup.okhttp.OkHttpClient#newCall(Request)
     * @see com.squareup.okhttp.Call#enqueue(Callback)
     * @since 0.0.1
     */
    public void deliverAsyncResult(final Request request, final OkHttpRequest okRequest, final IResultCallback mResultCallback)
    {
        if (null == request)
        {
            sendResultCallback(RESULT_FAIL, "empty request", mResultCallback);
        } else
        {
            mOkHttpClient.newCall(request).enqueue(new Callback()
            {
                @Override
                public void onFailure(Request request, IOException e)
                {
                    sendResultCallback(RESULT_FAIL, "", mResultCallback);
                }

                @Override
                public void onResponse(Response response) throws IOException
                {
                    // 保存离线数据至数据库
                    Context context = OkHttpClientManager.getInstance().getContext();
                    String requestUrl = okRequest.getLocalRequestUrl();
                    String responseStr = response.body().string();
                    OkHttpClientManager.getInstance().saveResponseAsLocalData(context, requestUrl, responseStr);

                    sendResultCallback(RESULT_SUCCESS, responseStr, mResultCallback);
                }
            });
        }
    }

    /**
     * 获取文件的MimeType
     *
     * @param path 文件路径
     * @return 文件的MimeType
     * @since 0.0.1
     */
    private String guessMimeType(String path)
    {
        FileNameMap mFileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = mFileNameMap.getContentTypeFor(path);

        if (contentTypeFor == null)
        {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 发送返回结果
     *
     * @param resultCode 返回结果状态
     * @param result 返回结果
     * @param mResultCallback 结果回调参数
     * @since 0.0.1
     */
    @Override
    public void sendResultCallback(final int resultCode, final String result, final IResultCallback mResultCallback)
    {
        if (null != mResultCallback)
        {
            if (RESULT_SUCCESS == resultCode)
            {
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mResultCallback.onResponse(result);
                    }
                });
            } else if (RESULT_FAIL == resultCode)
            {
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mResultCallback.onFailure(result);
                    }
                });
            }
        }
    }

}
