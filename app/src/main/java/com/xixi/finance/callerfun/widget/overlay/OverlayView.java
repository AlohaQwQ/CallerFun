
package com.xixi.finance.callerfun.widget.overlay;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.constant.ServiceAPIConstant;
import com.xixi.finance.callerfun.util.DpToPxUtil;
import com.xixi.finance.callerfun.util.LogUtil;
import com.xixi.finance.callerfun.util.Utils;
import com.xixi.finance.callerfun.version.PersistentDataCacheEntity;
import com.xixi.finance.callerfun.widget.Title;

/**
 * 半屏显示
 *
 * @author likebamboo
 */
public class OverlayView extends Overlay {

    /**
     * 网络操作结果
     */
    public static final int MSG_OK = 0x1000;

    /**
     * 网络操作结果
     */
    public static final int MSG_FAILED = 0x1001;

    /**
     * 来电号码extra
     */
    public static final String EXTRA_PHONE_NUM = "phoneNum";

    private static Context mContext = null;

    /**
     * 标题栏
     */
    private static Title mTitle = null;

    private static WebView webView;
    private static ProgressBar progressBar;

    /**
     * 正在加载布局
     */
    private static LinearLayout mLoadingLayout = null;

    /**
     * 正在加载文字显示
     */
    private static TextView mLoadingTv = null;

    /**
     * 网络操作结果界面。
     */
    private static LinearLayout mRetLayout = null;

    /**
     * 挂电话按钮
     */
    private static Button mEndCallBt = null;

    /**
     * 接听电话按钮
     */
    private static Button mAnswerCallBt = null;

    /**
     * 通话插屏页Html
     */
    private static String mWebPageHtml;

    public OverlayView(Context context) {
        super(context);
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 显示
     *
     * @param context 上下文对象
     * @param number
     */
    public static void show(final Context context, final String webPageHtml,final String number, final int percentScreen) {
        synchronized (monitor) {
            mContext = context;
            mWebPageHtml = webPageHtml;
            init(context, number, R.layout.content_main, percentScreen);
            /*InputMethodManager imm = (InputMethodManager)context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);*/
        }
    }

    /**
     * 隐藏
     *
     * @param context
     */
    public static void hide(Context context) {
        synchronized (monitor) {
            if (mOverlay != null) {
                try {
                    WindowManager wm = (WindowManager)context
                            .getSystemService(Context.WINDOW_SERVICE);
                    // Remove view from WindowManager
                    wm.removeView(mOverlay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mOverlay = null;
            }
        }
    }

    /**
     * 初始化布局
     *
     * @param context 上下文对象
     * @param number 电话号码
     * @param layout 布局文件
     * @return 布局
     */
    private static ViewGroup init(Context context, String number, int layout, int percentScreen) {
        WindowManager.LayoutParams params = getShowingParams();
        //int height = getHeight(context, percentScreen);
        int height = DpToPxUtil.dp2px(450);
        params.height = height;
        LogUtil.biubiubiu("params.height: " + height);
        ViewGroup overlay = init(context, layout, params);

        initView(overlay, number, percentScreen);

        return overlay;
    }

    /**
     * 初始化界面
     */
    private static void initView(View v, String phoneNum, int percentScreen) {
        // 标题栏
       /* mTitle = (Title)v.findViewById(R.id.overlay_title);
        mTitle.setTitle(R.string.call_ringing);

        // 显示来电电话
        ((TextView)v.findViewById(R.id.overlay_result_msg))
                .setText(Utils.formatHtml(mContext.getString(R.string.call_ringing_msg,
                        "<font color='red'>" + phoneNum + "</font>")));

        // 初始化各个控件
        mLoadingLayout = (LinearLayout)v.findViewById(R.id.overlay_loading_layout);
        mLoadingTv = (TextView)v.findViewById(R.id.overlay_loading_tv);
        mRetLayout = (LinearLayout)v.findViewById(R.id.overlay_result_layout);

        // 显示正在加载数据。
        mLoadingLayout.setVisibility(View.VISIBLE);
        v.findViewById(R.id.overlay_loading_pb).setVisibility(View.VISIBLE);
        mRetLayout.setVisibility(View.GONE);

        if (percentScreen == 100) {
            // 接听电话与挂断电话
            mEndCallBt = (Button)v.findViewById(R.id.overlay_end_call_bt);
            mAnswerCallBt = (Button)v.findViewById(R.id.overlay_answer_call_bt);
            v.findViewById(R.id.overlay_dealwith_layout).setVisibility(View.VISIBLE);
            addListener();
        }
        mLoadingLayout.setVisibility(View.GONE);
        mRetLayout.setVisibility(View.VISIBLE);
        mLoadingTv.setText("QQQ");*/
        webView = (WebView) v.findViewById(R.id.web_view);
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar_browser);

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
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    if (progressBar != null) {
                        if (progressBar.getVisibility() == View.GONE) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        progressBar.setProgress(newProgress);
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
        webView.setBackgroundColor(0);
        if (!TextUtils.isEmpty(mWebPageHtml)) {
            webView.loadDataWithBaseURL(ServiceAPIConstant.API_BASE_PAGE_URL, mWebPageHtml , "text/html","utf-8", null);
        }
    }

    /**
     * 添加监听器
     */
    private static void addListener() {
        mEndCallBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Utils.endCall(mContext);
            }
        });
        mAnswerCallBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (Utils.hasGingerbread()) {
                    Utils.answerRingingCall(mContext);
                } else {
                    Utils.answerRingingCall(mContext);
                }
            }
        });
    }

    /**
     * 获取显示参数
     *
     * @return
     */
    private static WindowManager.LayoutParams getShowingParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        // TYPE_TOAST TYPE_SYSTEM_OVERLAY 在其他应用上层 在通知栏下层 位置不能动鸟
        // TYPE_PHONE 在其他应用上层 在通知栏下层
        // TYPE_PRIORITY_PHONE TYPE_SYSTEM_ALERT 在其他应用上层 在通知栏上层 没试出来区别是啥
        // TYPE_SYSTEM_ERROR 最顶层(通过对比360和天天动听歌词得出)
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.x = 0;
        params.y = 0;
        params.format = PixelFormat.RGBA_8888;// value = 1
        params.gravity = Gravity.TOP;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        return params;
    }

    /**
     * 获取界面显示的高度 ，默认为手机高度的2/3
     *
     * @param context 上下文对象
     * @return
     */
    private static int getHeight(Context context, int percentScreen) {
        return getLarger(context) * percentScreen / 100;
    }

    @SuppressWarnings("deprecation")
    private static int getLarger(Context context) {
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int height = 0;
        if (Utils.hasHoneycombMR2()) {
            height = getLarger(display);
        } else {
            height = display.getHeight() > display.getWidth() ? display.getHeight() : display
                    .getWidth();
        }
        LogUtil.biubiubiu("getLarger: " + height);
        return height;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private static int getLarger(Display display) {
        Point size = new Point();
        display.getSize(size);
        return size.y > size.x ? size.y : size.x;
    }

    /**
     * Created by Aloha <br>
     * -explain H5调用本地分享方法
     *
     * @Date 2016/10/28 10:57
     */
    static class JavaScriptFormH5Interface {

        JavaScriptFormH5Interface() {}

        @JavascriptInterface
        public void toastSomethingTips(String tips) {
            Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void saveCallCustomerName(String name) {

        }

        @JavascriptInterface
        public void loadUrlToH5(final String url) {
            String qq = url + "?token=" + PersistentDataCacheEntity.getInstance().getToken();
            webView.loadUrl(qq);
        }
    }

}
