package com.xixi.finance.callerfun.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.xixi.finance.callerfun.MyApplication;
import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.config.ShowPref;
import com.xixi.finance.callerfun.presenter.main.MainPresenter;
import com.xixi.finance.callerfun.ui.adapter.TabsViewpagerAdapter;
import com.xixi.finance.callerfun.ui.fragment.CallListFragment;
import com.xixi.finance.callerfun.ui.fragment.RecordFragment;
import com.xixi.finance.callerfun.ui.fragment.UserFragment;
import com.xixi.finance.callerfun.util.LogUtil;
import com.xixi.finance.callerfun.version.PersistentDataCacheEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by Aloha <br>
 * -explain MainActivity
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

    /**
     * Action
     */
    public static final String ACTION_REFRESH = "ACTION_REFRESH";
    public static final String ACTION_LOGOUT = "ACTION_LOGOUT";

    private int currentTabIndex;
    private long preBackTime;
    private MainPresenter mainPresenter;
    private PagerAdapter mAdapter;
    private MainReceiver mainReceiver;

    @Override
    protected void init() {
        initView();

        /*pref = ShowPref.getInstance(this);
        mShowType = pref.loadInt(ShowPref.SHOW_TYPE);*/
        pref.putInt(ShowPref.SHOW_TYPE, mShowType);

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

        LogUtil.biubiubiu("mShowType:"+mShowType);

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
        mainPresenter.checkVersionUpdate(new MainPresenter.UpdateCallBack() {
            @Override
            public void updateCallBack(Map<String, Object> dataMap) {

            }
        });
        //设置tab模式
        tabsLayout.setTabMode(TabLayout.MODE_FIXED);
        tabsLayout.setTabGravity(TabLayout.GRAVITY_FILL);//默认是GRAVITY_CENTER
        List<String> list = new ArrayList<>();
        List<Fragment> fragments = new ArrayList<>();
        list.add("录音");
        list.add("通话统计");
        list.add("我的");
        fragments.add(RecordFragment.newInstance(1));
        fragments.add(CallListFragment.newInstance(2));
        fragments.add(UserFragment.newInstance(3));
        //viewpager 设置 fragment
        mAdapter = new TabsViewpagerAdapter(getSupportFragmentManager(), this, 3, list, fragments);
        viewpagerTabs.setAdapter(mAdapter);
        //绑定viewPager的适配器到 tabLayout
        tabsLayout.setupWithViewPager(viewpagerTabs);
        tabsLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Created by Aloha <br>
     * -explain 请求权限
     * @Date 2018/1/25 19:55
     */
    private void checkPermission(){
        /**
         * Created by Aloha <br>
         * -explain 悬浮窗权限
         * @Date 2018/1/25 19:55
         */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.SYSTEM_ALERT_WINDOW};
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CHECK);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PERMISSION_CHECK);
            }
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this,permissions, PERMISSION_CHECK);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this,permissions, PERMISSION_CHECK);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.READ_PHONE_STATE};
            ActivityCompat.requestPermissions(this,permissions, PERMISSION_CHECK);
        }
    }

    /**
     * Created by Aloha <br>
     * -explain 获取App 配置
     * @Date 2018/1/25 20:01
     */
    private void obtainConfiguration(){
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
                ActivityCompat.requestPermissions(this,permissions, PERMISSION_CHECK);
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
        registerReceiver(mainReceiver, intentFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_CHECK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    Toast.makeText(MainActivity.this,"not granted",Toast.LENGTH_SHORT);
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
        }
    }

    private class MainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                if (null != action) {
                    if (ACTION_REFRESH.equals(action)) {

                    } else if(ACTION_LOGOUT.equals(action)){

                    }
                }
            }
        }
    }
}
