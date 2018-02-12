package com.xixi.finance.callerfun;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.xixi.finance.callerfun.ui.activity.BaseActivity;
import com.xixi.finance.callerfun.util.ImageLoaderManager;

import java.util.Stack;

import aloha.shiningstarbase.logger.LogUtil;
import cn.chutong.sdk.conn.OkHttpClientConfiguration;
import cn.chutong.sdk.conn.OkHttpClientManager;

/**
 * Created by Aloha <br>
 * -explain
 * @Date 2018/1/18 16:43
 */
public class MyApplication extends Application {

    private static MyApplication myApplication;
    private boolean isDebug = true;

    private static final boolean IS_OPEN_NETWORK_DETECTION = false;

    private boolean isUpdateDownloading = false;

    /**
     * Activity 管理栈
     */
    private Stack<BaseActivity> activityStack;

    public static MyApplication getInstance() {
        return myApplication;
    }

    public static MyApplication getInstance(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /*if(isDebug){
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
           // LeakCanary.install(this);
        }*/

        // 初始化OkHttpClientManager
        OkHttpClientConfiguration configuration = OkHttpClientConfiguration.createDefault(this);
        OkHttpClientManager.getInstance().init(configuration);

        // 初始化ImageLoader
        ImageLoaderManager imageLoader = new ImageLoaderManager();
        imageLoader.init(this);

        //数据库初始化
        //StatisticsDatabaseHelper.init(this);

        // 初始化ImageLoader
        //ImageLoaderManager imageLoader = new ImageLoaderManager();
        //imageLoader.init(this);

        // 初始化闪银
        //WecashManager.init(this);

        /*//初始化友盟分享
        UMShareAPI.get(this);
        //Config.DEBUG = true;
        //禁用友盟默认页面统计
        MobclickAgent.openActivityDurationTrack(false);
        //初始化友盟推送
        mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                LogUtil.biu("友盟推送注册成功-deviceToken:"+deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                LogUtil.biu("友盟推送注册失败:"+s+"--"+s1);
            }
        });
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            //友盟推送自定义行为
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                //Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        //调试模式下开启异常捕获模式
        if (CommonConstant.IS_DEBUG_MODE) {
            LogUtil.biu("开启异常捕获");
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(this);
        }

        if (IS_OPEN_NETWORK_DETECTION) {
            Intent service = new Intent();
            service.setClass(this, NetworkDetectionService.class);
            startService(service);
        } else {
            NetworkTrafficDetector.getInstance().detect();
        }


        //微信 appid appsecret
        PlatformConfig.setWeixin("wx70c840a3ba23be2e", "7e6fa955fad7d6e9db1f77742b4b7d4f");
        //新浪微博 appkey appsecret
        PlatformConfig.setSinaWeibo("732879687","d9065b070eff26cc7a73374d0525c38d","http://sns.whalecloud.com");
        //QQ appkey appsecret
        PlatformConfig.setQQZone("1105364491", "FJ1I1byZmggbP3H4");
        setUmengPushAlias();

        *//**
         * 设置环境变量
         *//*
        if (PersistentDataCacheEntity.getInstance().getDebugOpen()) {
            if (!TextUtils.isEmpty(PersistentDataCacheEntity.getInstance().getServiceBaseAPI())){
                ServiceAPIConstant.API_BASE_URL = PersistentDataCacheEntity.getInstance().getServiceBaseAPI();
                ServiceAPIConstant.API_BASE_URL_NEW = PersistentDataCacheEntity.getInstance().getServiceBaseAPINewRequest();
            }
        }*/
        myApplication = this;
    }

    /**
     * Created by Aloha <br>
     * @Date 2016/9/30 14:10
     * @explain Activity push to Stack
     **/
    public void pushOneActivity(BaseActivity actvity) {
        if (activityStack == null) {
            activityStack = new Stack<BaseActivity>();
        }
        activityStack.push(actvity);
        LogUtil.biu("activityStack size = " + activityStack.size());
    }

    /**
     * Created by Aloha <br>
     * -explain 获取栈顶的activity，先进后出原则
     * @Date 2016/10/10 9:41
     */
    public BaseActivity getLastActivity() {
        return activityStack.lastElement();
    }

    /**
     * Created by Aloha <br>
     * -explain 获取管理栈activity个数
     * @Date 2016/10/10 9:41
     */
    public int getActivityStackSize() {
        if(activityStack!=null){
            LogUtil.biu("activityStack size = " + activityStack.size());
            return activityStack.size();
        }
        return 0;
    }

    /**
     * Created by Aloha <br>
     * -explain 移除一个activity
     * @Date 2016/10/10 9:41
     */
    public void popMultipleActivity(int number) {
        if (activityStack != null) {
            LogUtil.biu("activityStack size = " + activityStack.size());
            for (int i = number; i > 0; i--) {
                if(activityStack.size() > i){
                    Activity activity = getLastActivity();
                    if (activity == null)
                        break;
                    popOneActivity(activity);
                }
            }
        }
    }

    /**
     * Created by Aloha <br>
     * -explain 移除当前activity
     * @Date 2017/10/10 9:41
     */
    public void popActivity() {
        LogUtil.biu("activity = " + this.getClass().getSimpleName());
        activityStack.remove(this);
    }

    /**
     * Created by Aloha <br>
     * -explain 移除一个activity
     * @Date 2016/10/10 9:41
     */
    public void popOneActivity(Activity activity) {
        if (activityStack != null && activityStack.size() > 0) {
            if (activity != null) {
                activity.finish();
                LogUtil.biu("activity = " + activity.getLocalClassName());
                activityStack.remove(activity);
                activity = null;
            }
        }
    }

    /**
     * Created by Aloha <br>
     * -explain 退出所有activity
     * @Date 2016/10/10 9:41
     */
    public void finishAllActivity() {
        if (activityStack != null) {
            LogUtil.biu("activityStack size = " + activityStack.size());
            while (activityStack.size() > 0) {
                Activity activity = getLastActivity();
                if (activity == null)
                    break;
                popOneActivity(activity);
            }
        }
    }
}