package com.xixi.finance.callerfun.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.xixi.finance.callerfun.MyApplication;
import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.config.ShowPref;
import com.xixi.finance.callerfun.constant.APIKey;
import com.xixi.finance.callerfun.constant.ServiceAPIConstant;
import com.xixi.finance.callerfun.presenter.BasePresenter;
import com.xixi.finance.callerfun.presenter.main.MainPresenter;
import com.xixi.finance.callerfun.service.CallRecordingService;
import com.xixi.finance.callerfun.ui.adapter.TabsViewpagerAdapter;
import com.xixi.finance.callerfun.ui.fragment.CallDetailListFragment;
import com.xixi.finance.callerfun.ui.fragment.RecordFragment;
import com.xixi.finance.callerfun.ui.fragment.UserFragment;
import com.xixi.finance.callerfun.util.AudioFileUtils;
import com.xixi.finance.callerfun.util.LogUtil;
import com.xixi.finance.callerfun.version.PersistentDataCacheEntity;
import com.xixi.finance.callerfun.widget.overlay.OverlayView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.chutong.sdk.common.util.TypeUtil;

/**
 * Created by Aloha <br>
 * -explain MainActivity
 *
 * @Date 2018/1/18 11:13
 */
public class MainActivity extends BaseFragmentActivity {

    @BindView(R.id.tabs_main)
    TabLayout tabsLayout;
    @BindView(R.id.viewpager_tabs_main)
    ViewPager viewpagerTabs;

    /**
     * 是否获取过配置
     */
    private boolean obtain = true;

    /**
     * 配置信息
     */
    private ShowPref pref = null;

    /**
     * 来电秀显示的形式
     */
    private int mShowType = ShowPref.TYPE_HALF_DIALOG_DEFAULT;

    /**
     * PERMISSION
     */
    private static final int PERMISSION_CHECK = 105;

    private List<Fragment> fragments = new ArrayList<>();

    /**
     * Action
     */
    public static final String ACTION_REFRESH = "ACTION_REFRESH";
    public static final String ACTION_LOGOUT = "ACTION_LOGOUT";
    public static final String ACTION_RECEIVE_CALL_RING = "ACTION_CALL_RING";
    public static final String ACTION_RECEIVE_RECORD_CALL_UP = "ACTION_CALL_UP";
    public static final String ACTION_RECEIVE_RECORD_CALL_DOWN = "ACTION_CALL_DOWN";
    public static final String ACTION_RECEIVE_OUTGOING_CALL = "ACTION_OUTGOING_CALL";

    /**
     * Key
     */
    public static final String KEY_CALL_OUT_NUMBER = "KEY_CALL_OUT_NUMBER";
    public static final String KEY_CALL_ON_NUMBER = "KEY_CALL_ON_NUMBER";

    private int currentTabIndex;
    private long preBackTime;
    private MainPresenter mainPresenter;
    private PagerAdapter mAdapter;
    private MainReceiver mainReceiver;

    private boolean mBound = false;
    private CallRecordingService callRecordingService;
    private CallRecordingService.RecordBinder recordBinder;
    private CallRecordingService.OnRecordOverCallBack onRecordOverCallBack = new CallRecordingService.OnRecordOverCallBack() {
        @Override
        public void onRecordOver(File recordFile) {
            if (recordFile != null && recordFile.exists()) {
                /**
                 * 刷新首页录音列表
                 */
                if (null != fragments.get(0))
                    ((RecordFragment) fragments.get(0)).refreshLocalCallRecordsForMain();
               /*mainPresenter.uploadRecordFile(recordFile,
                        String.valueOf(AudioFileUtils.getRecordDuration(recordFile.getAbsolutePath())),
                        PersistentDataCacheEntity.getInstance().getCallNumber(), new MainPresenter.ResponseCallBack() {
                            @Override
                            public void responseCallBack(Map<String, Object> dataMap, String status, String message) {
                                if (BasePresenter.RESPONSE_STATUS_SUCCESS.equals(status)) {
                                    showToast("录音上传成功");
                                } else {
                                    showToast("录音上传失败");
                                }
                            }
                        });*/
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), recordFile);
                RequestBody requestBody = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)
                        .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"" + APIKey.RECORD_IPT_DURATION + "\""),
                                RequestBody.create(null, String.valueOf(AudioFileUtils.getRecordDurationSecond(recordFile.getAbsolutePath()))))
                        .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"" + APIKey.RECORD_IPT_PHONE + "\""),
                                RequestBody.create(null, PersistentDataCacheEntity.getInstance().getCallNumber()))
                        .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"attach[]\"; filename=\"" + recordFile.getName() + "\""), fileBody)
                        .build();

                //创建okHttpClient对象
                OkHttpClient mOkHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(ServiceAPIConstant.API_BASE_URL +
                                ServiceAPIConstant.REQUEST_API_RECORD_UPLOAD +
                                "&token=" + PersistentDataCacheEntity.getInstance().getToken())
                        .post(requestBody)
                        .build();

                mOkHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        LogUtil.biubiubiu("ServerResponse-uploadRecordFile-onFailure-" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        String callBackResponse = response.body().string();
                        LogUtil.biubiubiu("ServerResponse-uploadRecordFile = " + callBackResponse);
                        Message message = new Message();
                        message.obj = callBackResponse;
                        uploadHandler.sendMessage(message);
                    }
                });

                /**
                 * 清除本次通话信息
                 */
                PersistentDataCacheEntity.getInstance().setCallNumber("");
                PersistentDataCacheEntity.getInstance().setCallCustomerName("");
                PersistentDataCacheEntity.getInstance().setCallState(0);
            }
        }
    };

    private Handler uploadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String response = (String) msg.obj;
            LogUtil.biubiubiu("ServerResponse-uploadRecordFile = " + response);
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    String status = jsonObject.getString(APIKey.COMMON_RESPONSE_CODE);
                    String message = jsonObject.getString(APIKey.COMMON_MESSAGE2);

                    if (BasePresenter.RESPONSE_STATUS_SUCCESS.equals(status)) {
                        MainActivity.this.showToast("录音上传成功");
                    } else {
                        MainActivity.this.showToast("录音上传失败");
                    }
                } catch (Exception e) {
                    MainActivity.this.showToast("无数据");
                    LogUtil.biubiubiu("BasePresenter-Exception-" + e);
                }
            } else {
                MainActivity.this.showToast(MyApplication.getInstance().getResources().getString(R.string.error_network));
            }
        }
    };

    private ServiceConnection recordConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            recordBinder = (CallRecordingService.RecordBinder) binder;
            MainActivity.this.callRecordingService = (CallRecordingService) recordBinder.getService();
            recordBinder.setRecordOverCallBack(onRecordOverCallBack);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void init() {
        initView();

        pref = ShowPref.getInstance(this);
        /*mShowType = pref.loadInt(ShowPref.SHOW_TYPE);*/
        pref.putInt(ShowPref.SHOW_TYPE, mShowType);

        /**
         * 注册通话广播

         PhoneStateReceiver phoneStateReceiver = new PhoneStateReceiver();
         IntentFilter intentFilter = new IntentFilter();
         intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
         intentFilter.addAction("android.intent.action.PHONE_STATE");
         intentFilter.setPriority(Integer.MAX_VALUE);
         registerReceiver(phoneStateReceiver, intentFilter);
         */
        /**
         * 以半屏Dialog形式显示
         */
        // pref.putInt(ShowPref.SHOW_TYPE, ShowPref.TYPE_HALF_DIALOG);

       /* *//**
         * 以activity形式显示
         *//*
        pref.putInt(ShowPref.SHOW_TYPE, ShowPref.TYPE_ACTIVITY);
        *//**
         * 以全屏Dialog形式显示
         *//*
        pref.putInt(ShowPref.SHOW_TYPE, ShowPref.TYPE_FULL_DIALOG);
        *//**
         * 以自定义长度显示
         *//*
        pref.putInt(ShowPref.TYPE_HALF_VALUE, 100);*/

        LogUtil.biubiubiu("mShowType:" + mShowType);

        checkPermission();
        registerMainReceiver();
        if (obtain) {
            /**
             * Created by Aloha <br>
             * -explain 延迟请求
             * @Date 2017/1/3 14:34
             */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    obtainConfiguration();
                    obtain = false;
                }
            }, 1000);
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main_tabs;
    }

    /**
     * 初始化界面
     */
    private void initView() {
        mainPresenter = new MainPresenter();
        //设置tab模式
        tabsLayout.setTabMode(TabLayout.MODE_FIXED);
        tabsLayout.setTabGravity(TabLayout.GRAVITY_FILL);//默认是GRAVITY_CENTER
        List<String> list = new ArrayList<>();
        list.add("录音");
        list.add("通话统计");
        list.add("我的");
        fragments.add(RecordFragment.newInstance(1));
        fragments.add(CallDetailListFragment.newInstance(2));
        fragments.add(UserFragment.newInstance(3));
        //viewpager 设置 fragment
        mAdapter = new TabsViewpagerAdapter(getSupportFragmentManager(), this, 3, list, fragments);
        viewpagerTabs.setAdapter(mAdapter);
        //绑定viewPager的适配器到 tabLayout
        tabsLayout.setupWithViewPager(viewpagerTabs);
        tabsLayout.setVisibility(View.VISIBLE);
        viewpagerTabs.setOffscreenPageLimit(3);
        showContentView();
    }

    /**
     * Created by Aloha <br>
     * -explain 请求权限
     *
     * @Date 2018/1/25 19:55
     */
    private void checkPermission() {
        try {
            /*
            //默认关闭的权限
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                    showToast("Get Me!");
                } else {
                    String[] permissions = {Manifest.permission.SYSTEM_ALERT_WINDOW};
                    ActivityCompat.requestPermissions(this, permissions, PERMISSION_CHECK);
                }
            }*/
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.READ_PHONE_STATE};
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_CHECK);
            }
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.RECORD_AUDIO};
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_CHECK);
            }
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_CHECK);
            }

            /**
             * Created by Aloha <br>
             * -explain 悬浮窗权限
             * @Date 2018/1/25 19:55
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, PERMISSION_CHECK);
                }
            }
        } catch (java.lang.RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Created by Aloha <br>
     * -explain 获取App 配置
     *
     * @Date 2018/1/25 20:01
     */
    private void obtainConfiguration() {
        /**
         * Created by Aloha <br>
         * -explain 获取友盟token
         * @Date 2017/9/18 10:57
         *
        PushAgent mPushAgent = PushAgent.getInstance(getActivity());
        editTest.setVisibility(View.VISIBLE);
        editTest.setText(mPushAgent.getRegistrationId()+"");
         */
        String versionName = "0";
        int versionCode = 0;
        String channel = "0";
        try {
            versionName = this.getContextView().getPackageManager().getPackageInfo(
                    this.getContextView().getPackageName(), 0).versionName;
            versionCode = this.getContextView().getPackageManager().getPackageInfo(
                    this.getContextView().getPackageName(), 0).versionCode;
            /*channel = this.getContextView().getPackageManager().getApplicationInfo(
                    this.getContextView().getPackageName(), PackageManager.GET_META_DATA).metaData.getString("UMENG_CHANNEL");*/

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        /**
         * Created by Aloha <br>
         * -explain 保存版本号、渠道号
         * @Date 2016/10/12 21:08
         */
        PersistentDataCacheEntity.getInstance().setVersionNameApp(versionName);
        PersistentDataCacheEntity.getInstance().setVersionCode(versionCode);
        PersistentDataCacheEntity.getInstance().setChannelApp(channel);

        String deviceId = "";
        try {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.READ_PHONE_STATE};
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_CHECK);
            } else {
                deviceId = checkAndGetDeviceId();
            }
        } catch (Exception exception) {
            //打印捕获的异常的堆栈信息，从堆栈信息中可以发现异常发生的位置和原因
            LogUtil.biubiubiu("exception ActivityCompat.checkSelfPermission():" + exception.getMessage());
            exception.printStackTrace();
        }
        PersistentDataCacheEntity.getInstance().setDeviceId(deviceId);
    }

    /**
     * 检查并获取设备唯一号（DeviceId）
     *
     * @return 设备唯一号
     */
    private String checkAndGetDeviceId() {
        String deviceId = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            String terminalInfo = tm.getDeviceId();
            if (null != terminalInfo) {
                byte[] terminalInfoBytes = terminalInfo.getBytes();
                if (null != terminalInfoBytes) {
                    for (byte terminalInfoByte : terminalInfoBytes) {
                        if (terminalInfoByte != '0') {
                            deviceId = terminalInfo;
                            LogUtil.biubiubiu("deviceId:" + deviceId);
                            break;
                        }
                    }
                }
            }
        }
        return deviceId;
    }

    /**
     * 注册广播
     */
    private void registerMainReceiver() {
        mainReceiver = new MainReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH);
        intentFilter.addAction(ACTION_LOGOUT);
        intentFilter.addAction(ACTION_RECEIVE_RECORD_CALL_DOWN);
        intentFilter.addAction(ACTION_RECEIVE_RECORD_CALL_UP);
        intentFilter.addAction(ACTION_RECEIVE_CALL_RING);
        intentFilter.addAction(ACTION_RECEIVE_OUTGOING_CALL);
        registerReceiver(mainReceiver, intentFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_CHECK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    showToast("请允许悬浮窗授权");
                } else {
                    checkPermission();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CHECK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    showToast("请允许授权");
                }
                break;
            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            long curBackTime = System.currentTimeMillis();
            if (curBackTime - preBackTime >= 2000) {
                preBackTime = curBackTime;
                showToast(getString(R.string.press_again_to_close_application));
            } else {
                MyApplication.getInstance().finishAllActivity();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mainReceiver) {
            unregisterReceiver(mainReceiver);
            if (mBound) {
                LogUtil.biubiubiu("onDestroy-mBound:" + mBound);
                unbindService(recordConnection);
                stopService(new Intent(MainActivity.this, CallRecordingService.class));
                mBound = false;
            }
        }
    }

    private class MainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                if (null != action) {
                    if (ACTION_REFRESH.equals(action)) {
                        if (null != fragments.get(0))
                            ((RecordFragment) fragments.get(0)).initData();
                        if (null != fragments.get(1))
                            ((CallDetailListFragment) fragments.get(1)).initData();
                        if (null != fragments.get(2))
                            ((UserFragment) fragments.get(2)).initData();
                    } else if (ACTION_LOGOUT.equals(action)) {
                        if (null != fragments.get(0))
                            ((RecordFragment) fragments.get(0)).initData();
                        if (null != fragments.get(1))
                            ((CallDetailListFragment) fragments.get(1)).initData();
                        if (null != fragments.get(2))
                            ((UserFragment) fragments.get(2)).initData();
                    } else if (ACTION_RECEIVE_CALL_RING.equals(action)) {
                        /**
                         * 来电响铃
                         */
                        LogUtil.biubiubiu("ACTION_RECEIVE_CALL_RING");

                        PersistentDataCacheEntity.getInstance().setCallNumber(intent.getStringExtra("KEY_CALL_OUT_NUMBER"));
                        LogUtil.biubiubiu("call_number:" + intent.getStringExtra("KEY_CALL_OUT_NUMBER"));

                        PersistentDataCacheEntity.getInstance().setCallState(intent.getIntExtra("KEY_CALL_STATE", 1));
                        LogUtil.biubiubiu("call_state:" + intent.getIntExtra("KEY_CALL_STATE", 1));

                        //showToast("Call Ring");
                        MainActivity.this.bindService(new Intent(context, CallRecordingService.class),
                                recordConnection, Context.BIND_AUTO_CREATE);
                        /**
                         * 获取通话客户姓名
                         */
                        mainPresenter.fetchCallInCustomerInformation(PersistentDataCacheEntity.getInstance().getCallNumber(),
                                new MainPresenter.ResponseCallBack() {
                                    @Override
                                    public void responseCallBack(Map<String, Object> dataMap, String status, String message) {
                                        if (BasePresenter.RESPONSE_STATUS_SUCCESS.equals(status)) {
                                            if (dataMap != null) {
                                                PersistentDataCacheEntity.getInstance().setCallCustomerName(
                                                        TypeUtil.getString(dataMap.get(APIKey.USER_NAME), ""));
                                            }
                                        } else {
                                            showToast(message);
                                        }
                                    }
                                });
                        /**
                         * 获取通话插屏页面url
                         */
                        mainPresenter.fetchCallInPage(PersistentDataCacheEntity.getInstance().getCallNumber(),
                                new MainPresenter.FetchCallInCallBack() {
                                    @Override
                                    public void fetchCallInCallBack(String response) {
                                        if (response.length() > 20) {
                                            int value = pref.loadInt(ShowPref.TYPE_HALF_VALUE,
                                                    ShowPref.TYPE_HALF_DIALOG_DEFAULT);
                                            final int percent = value >= 25 ? (value <= 75 ? value : 75) : 25;
                                            /**
                                             * 来电通话插屏
                                             */
                                            OverlayView.show(MainActivity.this, response, PersistentDataCacheEntity.getInstance().getCallNumber(), percent);
                                        } else {
                                            showToast(response);
                                        }
                                    }
                                });
                    } else if (ACTION_RECEIVE_RECORD_CALL_UP.equals(action)) {
                        /**
                         * 来电接听
                         */
                        /**
                         * 开启Service 并开始录音
                         */
                        LogUtil.biubiubiu("ACTION_RECEIVE_RECORD_CALL_UP");

                        PersistentDataCacheEntity.getInstance().setCallNumber(intent.getStringExtra("KEY_CALL_OUT_NUMBER"));
                        LogUtil.biubiubiu("call_number:" + intent.getStringExtra("KEY_CALL_OUT_NUMBER"));

                        PersistentDataCacheEntity.getInstance().setCallState(intent.getIntExtra("KEY_CALL_STATE", 1));
                        LogUtil.biubiubiu("call_state:" + intent.getIntExtra("KEY_CALL_STATE", 1));

                        //showToast("Outgoing call is started");
                        MainActivity.this.startService(new Intent(context, CallRecordingService.class));
                    } else if (ACTION_RECEIVE_OUTGOING_CALL.equals(action)) {
                        /**
                         * 去电
                         */
                        LogUtil.biubiubiu("ACTION_RECEIVE_OUTGOING_CALL_RING");

                        PersistentDataCacheEntity.getInstance().setCallNumber(intent.getStringExtra("KEY_CALL_OUT_NUMBER"));
                        LogUtil.biubiubiu("call_number:" + intent.getStringExtra("KEY_CALL_OUT_NUMBER"));

                        PersistentDataCacheEntity.getInstance().setCallState(intent.getIntExtra("KEY_CALL_STATE", 1));
                        LogUtil.biubiubiu("call_state:" + intent.getIntExtra("KEY_CALL_STATE", 1));

                        //showToast("Call Ring");
                        MainActivity.this.bindService(new Intent(context, CallRecordingService.class),
                                recordConnection, Context.BIND_AUTO_CREATE);
                        /**
                         * 获取通话客户姓名
                         */
                        mainPresenter.fetchCallInCustomerInformation(PersistentDataCacheEntity.getInstance().getCallNumber(),
                                new MainPresenter.ResponseCallBack() {
                                    @Override
                                    public void responseCallBack(Map<String, Object> dataMap, String status, String message) {
                                        if (BasePresenter.RESPONSE_STATUS_SUCCESS.equals(status)) {
                                            if (dataMap != null) {
                                                PersistentDataCacheEntity.getInstance().setCallCustomerName(
                                                        TypeUtil.getString(dataMap.get(APIKey.USER_NAME), ""));
                                            }
                                        } else {
                                            showToast(message);
                                        }
                                    }
                                });
                        /**
                         * 获取通话插屏页面url
                         */
                        mainPresenter.fetchCallInPage(PersistentDataCacheEntity.getInstance().getCallNumber(),
                                new MainPresenter.FetchCallInCallBack() {
                                    @Override
                                    public void fetchCallInCallBack(String response) {
                                        if (response.length() > 20) {
                                            int value = pref.loadInt(ShowPref.TYPE_HALF_VALUE,
                                                    ShowPref.TYPE_HALF_DIALOG_DEFAULT);
                                            final int percent = value >= 25 ? (value <= 75 ? value : 75) : 25;
                                            /**
                                             * 来电通话插屏
                                             */
                                            OverlayView.show(MainActivity.this, response, PersistentDataCacheEntity.getInstance().getCallNumber(), percent);
                                        } else {
                                            showToast(response);
                                        }
                                    }
                                });

                        /**
                         * 延迟4秒录音
                         */
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                MainActivity.this.startService(new Intent(MainActivity.this, CallRecordingService.class));
                            }
                        }, 4000);
                    } else if (ACTION_RECEIVE_RECORD_CALL_DOWN.equals(action)) {
                        LogUtil.biubiubiu("ACTION_RECEIVE_RECORD_CALL_DOWN");
                        recordBinder.stopCallRecord();
                    }
                }
            }
        }
    }
}
