package com.xixi.finance.callerfun.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.config.ShowPref;
import com.xixi.finance.callerfun.util.LogUtil;
import com.xixi.finance.callerfun.util.Utils;
import com.xixi.finance.callerfun.widget.TimeDragPicker;
import com.xixi.finance.callerfun.widget.Title;

/**
 * Created by Aloha <br>
 * -explain MainActivity
 * @Date 2018/1/18 11:13
 */
public class MainActivity extends AppCompatActivity {

    /**
     * 标题栏
     */
    private Title mTitle = null;

    /**
     * 全屏 DiaLog显示
     */
    private View mShowAsFullDialog = null;

    /**
     * 半屏显示
     */
    private View mShowAsHalfDialog = null;

    /**
     * 以Activity形式显示
     */
    private View mShowAsActivity = null;

    /**
     * 百分比选择器
     */
    private TimeDragPicker mTimePicker = null;

    /**
     * 配置信息
     */
    private ShowPref pref = null;

    /**
     * 来电秀显示的形式
     */
    private int mShowType = ShowPref.TYPE_HALF_DIALOG_DEFAULT;

    private static final int PERMISSION_CHECK_ALERT_WINDOW = 105;

    private int[] markLong = new int[] {
            30, 40, 50, 60, 70
    };

    private int[] markShort = new int[] {
            25, 35, 45, 55, 65, 75
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        pref = ShowPref.getInstance(this);
        mShowType = pref.loadInt(ShowPref.SHOW_TYPE);
        LogUtil.biubiubiu("mShowType:"+mShowType);
        initView();
        addListener();
        checkPermissionAlertWindow();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        mTitle = (Title)findViewById(R.id.main_title);
        mTitle.setTitle(R.string.app_name);

        mShowAsActivity = findViewById(R.id.main_activity_layout);
        mShowAsFullDialog = findViewById(R.id.main_full_dialog_layout);
        mShowAsHalfDialog = findViewById(R.id.main_half_dialog_layout);

        mTimePicker = (TimeDragPicker)findViewById(R.id.main_time_picker);

        mTimePicker.defineMark(markShort, markLong);
        mTimePicker.setTextView(Utils.formatHtml("<b>" + getString(R.string.percent) + "</b>"));
        // mTimePicker.enablePopupRemind(false);
        if (mShowType == ShowPref.TYPE_ACTIVITY) {// 以Activity形式显示
            showIcon(R.id.main_activity_layout);
        } else if (mShowType == ShowPref.TYPE_FULL_DIALOG) {
            showIcon(R.id.main_full_dialog_layout);
        } else {
            showIcon(R.id.main_half_dialog_layout);
        }
    }

    /**
     * 获取选中的项目
     *
     * @param longMark
     * @param shorMark
     * @param value
     */
    private int setSelect(int[] longMark, int[] shorMark, int value) {
        for (int i = 0; i < longMark.length; i++) {
            if (longMark[i] == value) {
                return i * 2 + 1;
            }
        }
        for (int i = 0; i < shorMark.length; i++) {
            if (shorMark[i] == value) {
                return i * 2;
            }
        }
        return (longMark.length + shorMark.length) / 2;
    }

    /**
     * 添加监听器
     */
    private void addListener() {
        mShowAsActivity.setOnClickListener(new ShowOnClickListener());
        mShowAsFullDialog.setOnClickListener(new ShowOnClickListener());
        mShowAsHalfDialog.setOnClickListener(new ShowOnClickListener());

        mTimePicker.setOnDigitSelectListener(new TimeDragPicker.OnDigitSelectListener() {
            @Override
            public void onDigitConfirm(TimeDragPicker v, int select) {
                // TODO Auto-generated method stub
                pref.putInt(ShowPref.TYPE_HALF_VALUE, select);
            }

            @Override
            public void onDigitChange(TimeDragPicker v, int select, int index, int[] collection) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void checkPermissionAlertWindow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (! Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,PERMISSION_CHECK_ALERT_WINDOW);
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.SYSTEM_ALERT_WINDOW};
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CHECK_ALERT_WINDOW);
        }
    }

    private class ShowOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.main_activity_layout:// 以activity形式显示
                    pref.putInt(ShowPref.SHOW_TYPE, ShowPref.TYPE_ACTIVITY);
                    break;
                case R.id.main_full_dialog_layout:// 以全屏Dialog形式显示
                    pref.putInt(ShowPref.SHOW_TYPE, ShowPref.TYPE_FULL_DIALOG);
                    break;
                case R.id.main_half_dialog_layout:// 以半屏Dialog形式显示
                    pref.putInt(ShowPref.SHOW_TYPE, ShowPref.TYPE_HALF_DIALOG);
                    break;
                default:
                    break;
            }
            showIcon(v.getId());
        }
    }

    /**
     * 显示选中项图标
     *
     * @param resId
     */
    private void showIcon(int resId) {
        switch (resId) {
            case R.id.main_activity_layout:
                findViewById(R.id.main_activity_iv).setVisibility(View.VISIBLE);
                findViewById(R.id.main_full_dialog_iv).setVisibility(View.INVISIBLE);
                findViewById(R.id.main_half_dialog_iv).setVisibility(View.INVISIBLE);
                mTimePicker.setVisibility(View.GONE);
                break;
            case R.id.main_full_dialog_layout:
                findViewById(R.id.main_activity_iv).setVisibility(View.INVISIBLE);
                findViewById(R.id.main_full_dialog_iv).setVisibility(View.VISIBLE);
                findViewById(R.id.main_half_dialog_iv).setVisibility(View.INVISIBLE);
                mTimePicker.setVisibility(View.GONE);
                break;
            case R.id.main_half_dialog_layout:
                findViewById(R.id.main_activity_iv).setVisibility(View.INVISIBLE);
                findViewById(R.id.main_full_dialog_iv).setVisibility(View.INVISIBLE);
                findViewById(R.id.main_half_dialog_iv).setVisibility(View.VISIBLE);
                mTimePicker.setVisibility(View.VISIBLE);
                mTimePicker.setSeleted(setSelect(markLong, markShort,
                        pref.loadInt(ShowPref.TYPE_HALF_VALUE, ShowPref.TYPE_HALF_DIALOG_DEFAULT)));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_CHECK_ALERT_WINDOW) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    Toast.makeText(MainActivity.this,"not granted",Toast.LENGTH_SHORT);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CHECK_ALERT_WINDOW:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                break;
            default:
                break;
        }
    }
}
