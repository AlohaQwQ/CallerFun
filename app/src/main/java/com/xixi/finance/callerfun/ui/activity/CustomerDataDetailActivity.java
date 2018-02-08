package com.xixi.finance.callerfun.ui.activity;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.constant.ServiceAPIConstant;
import com.xixi.finance.callerfun.presenter.BasePresenter;
import com.xixi.finance.callerfun.util.LogUtil;
import com.xixi.finance.callerfun.version.PersistentDataCacheEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Aloha <br>
 * -explain
 * @Date 2018/1/29 14:04
 */
public class CustomerDataDetailActivity extends BaseActivity {

    @BindView(R.id.btn_back)
    LinearLayout btnBack;
    @BindView(R.id.toolbar_linear)
    AppBarLayout toolbarLinear;
    @BindView(R.id.progress_bar_browser)
    ProgressBar progressBarBrowser;
    @BindView(R.id.web_view)
    WebView webView;

    private boolean isHistoryClearable = false;
    public boolean indexMark = false;
    /**
     * 首页刷新标识，是否正在刷新
     */
    private boolean isRefreshing;


    private String urlTemp;

    /**
     * 标题缓存
     */
    private String titleTemp = "";

    /**
     * 友盟记录标识
     */
    private boolean umengMark = false;
    /**
     * 回退标识
     */
    private boolean goBackMark = false;
    /**
     * 标题存储
     */
    private List<String> titleList = new ArrayList();
    /**
     * 存储记录约束
     */
    private boolean uploadMark1 = false;
    private boolean uploadMark2 = false;
    /**
     * 相同标题累加值
     */
    private int iTemp = 1;

    private String webURL;
    /**
     * post方式加载参数
     */
    private String webPostParameter;
    private String title;
    /**
     * 是否post方式加载url
     */
    private boolean postOrNot = false;

    /**
     * 客户资料Page
     */
    private String mWebPage;

    /**
     * 记录选择用户录音文件，用户资料页播放
     */
    private String recordFilePath;

    public static final String KEY_CUSTOMER_PAGE = "CUSTOMER_PAGE";
    public static final String KEY_RECORD_FILE_PATH = "RECORD_FILE_PATH";
    public static final String KEY_LUCK_H5_JUMP_URL_TO_GET = "H5_URL_TO_GET";
    public static final String KEY_LUCK_H5_JUMP_URL_TO_POST = "H5_URL_TO_POST";
    public static final String KEY_LUCK_H5_JUMP_URL_TO_POST_PARA = "H5_URL_TO_POST_PARA";
    public static final String KEY_LUCK_H5_JUMP_TITLE = "H5_TITLE";
    public static final int KEY_LUCK_H5_REFRESH = 206;

    /**
     * 音频播放
     */
    private MediaPlayer mediaPlayer;

    /**
     * 播放状态
     */
    private boolean isPlaying = false;

    /**
     * 初始化状态
     */
    private boolean isPrepare = false;

    /**
     * 记录播放位置
     */
    private int playTime;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_web_customer_data_detail;
    }

    @Override
    protected void init() {
        initData();
        initUI();
    }

    @Override
    protected BasePresenter CreatePresenter() {
        return null;
    }

    private void initUI() {
        // 初始化Toolbar
        titleTv.setText("客户资料");
        setSupportActionBar(toolbar);
    }

    public void initData() {
        if (getIntent() != null) {
           /* *//**
             * Get 方式展示web
             *//*
            if (!TextUtils.isEmpty(getIntent().getStringExtra(KEY_LUCK_H5_JUMP_URL_TO_GET))) {
                postOrNot = false;
                webURL = getIntent().getStringExtra(KEY_LUCK_H5_JUMP_URL_TO_GET);
                title = getIntent().getStringExtra(KEY_LUCK_H5_JUMP_TITLE);
            }
            *//**
             * Post 方式展示web
             *//*
            if (!TextUtils.isEmpty(getIntent().getStringExtra(KEY_LUCK_H5_JUMP_URL_TO_POST))) {
                postOrNot = true;
                webURL = getIntent().getStringExtra(KEY_LUCK_H5_JUMP_URL_TO_POST);
                webPostParameter = getIntent().getStringExtra(KEY_LUCK_H5_JUMP_URL_TO_POST_PARA);
                title = getIntent().getStringExtra(KEY_LUCK_H5_JUMP_TITLE);
            }*/
            mWebPage = getIntent().getStringExtra(KEY_CUSTOMER_PAGE);
            recordFilePath = getIntent().getStringExtra(KEY_RECORD_FILE_PATH);
        }
        /**
         * MediaPlayer
         */
        mediaPlayer = new MediaPlayer();
        isPrepare = false;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlaying = false;
                playTime = 0;
            }
        });

        /**
         * 配置WebSetting
         */
        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true); // 能够执行JavaScript脚本
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // 支持通过JS打开新窗口
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);//设置webview推荐使用的窗口
        webSettings.setLoadWithOverviewMode(true);//设置webview加载的页面的模式
        webSettings.setDisplayZoomControls(false);//隐藏webview缩放按钮
        webSettings.setDefaultTextEncodingName("utf-8");//设置默认为utf-8
        /**
         * Created by Aloha <br>
         * -explain Webview 调试
         * @Date 2016/10/27 14:58
         */
        /*if(BuildConfig.VERSION_CODE>=19){
            webView.setWebContentsDebuggingEnabled(true);
        }*/
        webView.addJavascriptInterface(new JavaScriptFormH5Interface(), "javaScriptFunction");
        webView.setWebViewClient(new WebViewClient() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                //LogUtil.biubiubiu("shouldInterceptRequest-url:"+request.getUrl());
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
                //return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtil.biubiubiu("onPageStarted-url:" + url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtil.biubiubiu("onPageFinished-url:" + url);
                super.onPageFinished(view, url);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                LogUtil.biubiubiu("webview-url-" + request.getUrl() + "-error-" + error);
                if (request.getUrl().toString().contains("favicon.ico"))
                    return;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                LogUtil.biubiubiu("webview-url-" + request.getUrl() + "-errorResponse-" + errorResponse.getStatusCode());
                if (request.getUrl().toString().contains("favicon.ico"))
                    return;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    if (progressBarBrowser != null) {
                        progressBarBrowser.setVisibility(View.GONE);
                    }
                } else {
                    if (progressBarBrowser != null) {
                        if (progressBarBrowser.getVisibility() == View.GONE) {
                            progressBarBrowser.setVisibility(View.VISIBLE);
                        }
                        progressBarBrowser.setProgress(newProgress);
                    }
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                /*try {
                    goBackMark = false;
                    String titleDeal = title;
                    if (!TextUtils.isEmpty(titleDeal) && tv_title != null) {
                        if (titleDeal.contains("lbdApp") | titleDeal.contains("com") | title.contains("http"))
                            return;
                        if (titleDeal.equals(titleTemp)) {
                            titleDeal = titleDeal + iTemp;
                            iTemp++;
                        }
                        titleTemp = title;
                        if (titleDeal.contains("-")) {
                            titleDeal = titleDeal.replaceAll("-", "");
                        }
                        titleDeal = titleDeal.trim();
                        titleTv.setText(title + "");
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }*/
                super.onReceivedTitle(view, title);
            }
        });
        if (!TextUtils.isEmpty(mWebPage)) {
            webView.loadDataWithBaseURL(ServiceAPIConstant.API_BASE_PAGE_URL, mWebPage , "text/html","utf-8", null);
        }
    }

    /**
     * 录音状态初始化
     *
     */
    private void prepareRecord() {
        try {
            mediaPlayer.setDataSource(recordFilePath);
            mediaPlayer.prepare();
            isPrepare = true;
            playRecord();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放录音
     */
    private void playRecord() {
        if(isPrepare){
            /**
             * 暂停——继续播放
             */
            if (!isPlaying) {
                mediaPlayer.start();
                mediaPlayer.seekTo(playTime);
                isPlaying = true;
            } else {
                mediaPlayer.pause();
                playTime = mediaPlayer.getCurrentPosition();
                isPlaying = false;
            }
        } else {
            prepareRecord();
        }
    }

    @OnClick({R.id.btn_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }

    /**
     * Created by Aloha <br>
     * -explain ToolBar 返回按钮监听
     *
     * @Date 2016/12/12 9:38
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Created by Aloha <br>
     * -explain 返回键处理
     * @Date 2017/9/9 14:55
     */
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.biubiubiu("Page-onDestroy");
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.biubiubiu("Page-onResume");
        if (isPlaying) {
            if (mediaPlayer != null) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.biubiubiu("Page-onPause");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }

    /**
     * Created by Aloha <br>
     * -explain H5调用本地分享方法
     *
     * @Date 2016/10/28 10:57
     */
    class JavaScriptFormH5Interface {

        JavaScriptFormH5Interface() {}

        @JavascriptInterface
        public void toastSomethingTips(String tips) {
            Toast.makeText(CustomerDataDetailActivity.this, tips, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void saveCallCustomerName(String name) {

        }

        @JavascriptInterface
        public void loadUrlToH5(final String url) {
            String qq = url + "?token=" + PersistentDataCacheEntity.getInstance().getToken();
            webView.loadUrl(qq);
        }

        @JavascriptInterface
        public void playLocalRecord() {
            playRecord();
        }
    }
}
