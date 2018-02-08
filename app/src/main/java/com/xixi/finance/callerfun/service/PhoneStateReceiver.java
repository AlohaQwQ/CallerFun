package com.xixi.finance.callerfun.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.xixi.finance.callerfun.config.ShowPref;
import com.xixi.finance.callerfun.ui.activity.MainActivity;
import com.xixi.finance.callerfun.ui.activity.OverLayActivity;
import com.xixi.finance.callerfun.util.LogUtil;
import com.xixi.finance.callerfun.util.PhoneStateManager;
import com.xixi.finance.callerfun.util.Utils;
import com.xixi.finance.callerfun.widget.overlay.OverlayView;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.xixi.finance.callerfun.util.PhoneStateManager.calloutPhoneNumber;
import static com.xixi.finance.callerfun.util.PhoneStateManager.monitor;

/**
 * Created by Aloha <br>
 * -explain
 * @Date 2018/1/26 15:33
 */
public class PhoneStateReceiver extends BroadcastReceiver {

    /**
     * 电话管理
     */
    private TelephonyManager telMgr = null;

    //private static final Object monitor = new Object();

    @Override
    public void onReceive(Context context, Intent intent) {
        final Context ctx = context;
        final ShowPref pref = ShowPref.getInstance(ctx);
        //lastCallState  = TelephonyManager.CALL_STATE_IDLE;
        //isCallOut = false;
        /**
         * 以什么方式显示界面
         */
        final int showType = pref.loadInt(ShowPref.SHOW_TYPE);

        /**
         * 5.1版本
         */
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
            //如果是去电
            if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
                PhoneStateManager.calloutPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                LogUtil.biubiubiu("call out:" + PhoneStateManager.calloutPhoneNumber);
                //否则最近的状态不是来电响铃的话，意味着本次通话是去电
                PhoneStateManager.isIncoming = false;
                PhoneStateManager.callState = 1;
                onOutgoingCallStarted(ctx, PhoneStateManager.calloutPhoneNumber);
            } else {
                telMgr = (TelephonyManager)ctx.getSystemService(TELEPHONY_SERVICE);
                int phoneCallState = telMgr.getCallState();
                LogUtil.biubiubiu("phoneCallState:"+phoneCallState);
                switch (phoneCallState) {
                    case TelephonyManager.CALL_STATE_RINGING:// 来电响铃
                        LogUtil.biubiubiu("....来电话了...");
                        PhoneStateManager.calloutPhoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        PhoneStateManager.callState = 2;
                        PhoneStateManager.lastCallState = TelephonyManager.CALL_STATE_RINGING;
                        LogUtil.biubiubiu("number:" + calloutPhoneNumber);
                        LogUtil.biubiubiu("showType:" + showType);

                        if (!TextUtils.isEmpty(PhoneStateManager.calloutPhoneNumber)) {
                            synchronized (PhoneStateManager.monitor) {
                                switch (showType) {
                                    case ShowPref.TYPE_ACTIVITY:
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // TODO Auto-generated method stub
                                                showActivity(ctx, calloutPhoneNumber);
                                            }
                                        }, 1000);
                                        break;
                                    case ShowPref.TYPE_FULL_DIALOG:
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // TODO Auto-generated method stub
                                                onIncomingCallRing(ctx, calloutPhoneNumber);
                                                //showWindow(ctx, calloutPhoneNumber, 100);
                                            }
                                        }, 100);
                                        break;
                                    /**
                                     * -explain 非满屏Dialog
                                     */
                                    case ShowPref.TYPE_HALF_DIALOG:
                                    default:// 默认显示半屏dialog
                                        int value = pref.loadInt(ShowPref.TYPE_HALF_VALUE,
                                                ShowPref.TYPE_HALF_DIALOG_DEFAULT);
                                        final int percent = value >= 25 ? (value <= 75 ? value : 75) : 25;
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // TODO Auto-generated method stub
                                                onIncomingCallRing(ctx, calloutPhoneNumber);
                                                //showWindow(ctx, calloutPhoneNumber, percent);
                                            }
                                        }, 0);
                                        break;
                                }
                            }
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:// 接听电话
                        //摘机状态
                        PhoneStateManager.stateChange = TelephonyManager.CALL_STATE_OFFHOOK;
                        if (PhoneStateManager.lastCallState == TelephonyManager.CALL_STATE_RINGING){
                            //如果本次通话是来电
                            PhoneStateManager.isIncoming = true;
                            PhoneStateManager.callState = 2;
                            onIncomingCallAnswered(context, PhoneStateManager.calloutPhoneNumber);
                        } else {
                            //否则最近的状态不是来电响铃的话，意味着本次通话是去电
                            PhoneStateManager.isIncoming = false;
                            PhoneStateManager.callState = 1;
                        /*if(PhoneStateManager.isCallOut){
                            PhoneStateManager.isCallOut = false;
                            onOutgoingCallStarted(context, PhoneStateManager.calloutPhoneNumber);
                        } else {
                            PhoneStateManager.isCallOut = true;
                            onIncomingCallRing(ctx, calloutPhoneNumber);
                        }*/
                            onOutgoingCallStarted(ctx, calloutPhoneNumber);
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:// 挂断电话
                        synchronized (monitor) {
                            onCallHandUp(context);
                            PhoneStateManager.lastCallState = TelephonyManager.CALL_STATE_IDLE;
                            switch (showType) {
                                case ShowPref.TYPE_ACTIVITY:
                                    Utils.sendEndCallBroadCast(ctx);
                                    break;
                                case ShowPref.TYPE_FULL_DIALOG:
                                case ShowPref.TYPE_HALF_DIALOG:
                                default:// 默认会显示半屏的dialog
                                    closeWindow(ctx);
                                    break;
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        /**
         * 6.0及以上
         */
        else {
            calloutPhoneNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            LogUtil.biubiubiu("call out:" + calloutPhoneNumber);

            telMgr = (TelephonyManager)ctx.getSystemService(TELEPHONY_SERVICE);
            int phoneCallState = telMgr.getCallState();
            LogUtil.biubiubiu("phoneCallState:"+phoneCallState);
            switch (phoneCallState) {
                case TelephonyManager.CALL_STATE_RINGING:// 来电响铃
                    LogUtil.biubiubiu("....来电话了...");
                    PhoneStateManager.calloutPhoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    PhoneStateManager.callState = 2;
                    PhoneStateManager.lastCallState = TelephonyManager.CALL_STATE_RINGING;
                    LogUtil.biubiubiu("number:" + calloutPhoneNumber);
                    LogUtil.biubiubiu("showType:" + showType);

                    if (!TextUtils.isEmpty(PhoneStateManager.calloutPhoneNumber)) {
                        synchronized (PhoneStateManager.monitor) {
                            switch (showType) {
                                case ShowPref.TYPE_ACTIVITY:
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub
                                            showActivity(ctx, calloutPhoneNumber);
                                        }
                                    }, 1000);
                                    break;
                                case ShowPref.TYPE_FULL_DIALOG:
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub
                                            onIncomingCallRing(ctx, calloutPhoneNumber);
                                            //showWindow(ctx, calloutPhoneNumber, 100);
                                        }
                                    }, 100);
                                    break;
                                /**
                                 * -explain 非满屏Dialog
                                 */
                                case ShowPref.TYPE_HALF_DIALOG:
                                default:// 默认显示半屏dialog
                                    int value = pref.loadInt(ShowPref.TYPE_HALF_VALUE,
                                            ShowPref.TYPE_HALF_DIALOG_DEFAULT);
                                    final int percent = value >= 25 ? (value <= 75 ? value : 75) : 25;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub
                                            onIncomingCallRing(ctx, calloutPhoneNumber);
                                            //showWindow(ctx, calloutPhoneNumber, percent);
                                        }
                                    }, 0);
                                    break;
                            }
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:// 接听电话
                    //摘机状态
                    PhoneStateManager.stateChange = TelephonyManager.CALL_STATE_OFFHOOK;
                    if (PhoneStateManager.lastCallState == TelephonyManager.CALL_STATE_RINGING){
                        //如果本次通话是来电
                        PhoneStateManager.isIncoming = true;
                        PhoneStateManager.callState = 2;
                        onIncomingCallAnswered(context, PhoneStateManager.calloutPhoneNumber);
                    } else {
                        //否则最近的状态不是来电响铃的话，意味着本次通话是去电
                        PhoneStateManager.isIncoming = false;
                        PhoneStateManager.callState = 1;
                        /*if(PhoneStateManager.isCallOut){
                            PhoneStateManager.isCallOut = false;
                            onOutgoingCallStarted(context, PhoneStateManager.calloutPhoneNumber);
                        } else {
                            PhoneStateManager.isCallOut = true;
                            onIncomingCallRing(ctx, calloutPhoneNumber);
                        }*/
                        onOutgoingCallStarted(ctx, calloutPhoneNumber);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:// 挂断电话
                    synchronized (monitor) {
                        onCallHandUp(context);
                        PhoneStateManager.lastCallState = TelephonyManager.CALL_STATE_IDLE;
                        switch (showType) {
                            case ShowPref.TYPE_ACTIVITY:
                                Utils.sendEndCallBroadCast(ctx);
                                break;
                            case ShowPref.TYPE_FULL_DIALOG:
                            case ShowPref.TYPE_HALF_DIALOG:
                            default:// 默认会显示半屏的dialog
                                closeWindow(ctx);
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 显示来电Activity
     *
     * @param ctx
     * @param number
     */
    private void showActivity(Context ctx, String number) {
        Intent intent = new Intent(ctx, OverLayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(OverLayActivity.EXTRA_PHONE_NUM, number);
        ctx.startActivity(intent);
    }

    /**
     * 显示来电弹窗
     *
     * @param ctx 上下文对象
     * @param number 电话号码
     */
    private void showWindow(Context ctx, String number, int percentScreen) {
        OverlayView.show(ctx,"", number, percentScreen);
    }

    /**
     * 关闭来电弹窗
     *
     * @param ctx 上下文对象
     */
    private void closeWindow(Context ctx) {
        OverlayView.hide(ctx);
    }

    /**
     * 来电响铃
     * @param context
     * @param number
     */
    protected void onIncomingCallRing(Context context, String number){
        context.sendBroadcast(new Intent().
                putExtra("KEY_CALL_OUT_NUMBER",number).
                putExtra("KEY_CALL_STATE",PhoneStateManager.callState).
                setAction(MainActivity.ACTION_RECEIVE_CALL_RING));
    }

    /**
     * 去电拨打
     * @param context
     * @param number
     */
    protected void onOutgoingCallStarted(Context context,String number){
        context.sendBroadcast(new Intent().
                putExtra("KEY_CALL_OUT_NUMBER",number).
                putExtra("KEY_CALL_STATE",PhoneStateManager.callState).
                setAction(MainActivity.ACTION_RECEIVE_OUTGOING_CALL));
    }

    /**
     * 来电接听
     * @param context
     * @param number
     */
    protected void onIncomingCallAnswered(Context context, String number) {
        context.sendBroadcast(new Intent().
                putExtra("KEY_CALL_OUT_NUMBER",number).
                putExtra("KEY_CALL_STATE", PhoneStateManager.callState).
                setAction(MainActivity.ACTION_RECEIVE_RECORD_CALL_UP));
    }

    /**
     * 挂断电话
     * @param context
     */
    protected void onCallHandUp(Context context) {
        context.sendBroadcast(new Intent().
                setAction(MainActivity.ACTION_RECEIVE_RECORD_CALL_DOWN));
    }
}
