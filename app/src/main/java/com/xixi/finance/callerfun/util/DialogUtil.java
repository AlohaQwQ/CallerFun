package com.xixi.finance.callerfun.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

/**
 * Created by Aloha <br>
 * -explain BaseDialog
 * @Date 2016/12/20 13:42
 */
public class DialogUtil {

    /**
     * 定义DialogButton 监听回调
     */
    public interface OnDialogButtonClickListener {
        /**
         * 取消按钮 监听回调
         */
        void onPositiveButtonClick(DialogInterface dialog);
        /**
         * 确定按钮 监听回调
         */
        void onNegativeButtonClick(DialogInterface dialog);
    }

    /**
     * 定义DialogButton 监听回调
     */
    public interface OnDialogOneButtonClickListener {
        /**
         * 确定按钮 监听回调
         */
        void onNegativeButtonClick(DialogInterface dialog);
    }

    /**
     * Created by Aloha <br>
     * -explain showDialog Two Button
     * @Date 2017/2/21 14:42
     */
    public static AlertDialog showDialog(Context context,String title,String message,final OnDialogButtonClickListener mOnDialogButtonClickListener){
        AlertDialog.Builder alertDialogBuilder  = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(true);//默认为true，可以被Back键取消
        if(!TextUtils.isEmpty(title))
            alertDialogBuilder.setTitle(title);
        //alertDialogBuilder.setTitle(Html.fromHtml("<font color='#01ACE6'>"+title+"</font>"));
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("确认",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                if(mOnDialogButtonClickListener!=null)
                    mOnDialogButtonClickListener.onPositiveButtonClick(dialog);
            }
        });
        alertDialogBuilder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                if(mOnDialogButtonClickListener!=null)
                    mOnDialogButtonClickListener.onNegativeButtonClick(dialog);
            }
        });
        //alertDialogBuilder.setNeutralButton()
        AlertDialog alertDialog = alertDialogBuilder.create();
        //ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG); //系统中关机对话框就是这个属性
        //alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.setCanceledOnTouchOutside(true);//设置是否可以点击外部消失
        alertDialog.show();
        return alertDialog;
    }

    /**
     * Created by Aloha <br>
     * -explain showDialog Two Button,可定义标题或按钮文本
     * @Date 2017/2/21 14:43
     */
    public static AlertDialog showDialog(Context context,String title,String message,String setPositiveButtonText,String setNegativeButtonText,
                           final OnDialogButtonClickListener mOnDialogButtonClickListener){
        AlertDialog.Builder alertDialogBuilder  = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(true);//默认为true，可以被Back键取消
        if(!TextUtils.isEmpty(title))
            alertDialogBuilder.setTitle(title);
        //alertDialogBuilder.setTitle(Html.fromHtml("<font color='#01ACE6'>"+title+"</font>"));
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(setPositiveButtonText,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                if(mOnDialogButtonClickListener!=null)
                    mOnDialogButtonClickListener.onPositiveButtonClick(dialog);
            }
        });
        alertDialogBuilder.setNegativeButton(setNegativeButtonText,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                if(mOnDialogButtonClickListener!=null)
                    mOnDialogButtonClickListener.onNegativeButtonClick(dialog);
            }
        });
        //alertDialogBuilder.setNeutralButton()
        AlertDialog alertDialog = alertDialogBuilder.create();
        //ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG); //系统中关机对话框就是这个属性
        //alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.setCanceledOnTouchOutside(true);//设置是否可以点击外部消失
        alertDialog.show();
        return alertDialog;
    }

    /**
     * Created by Aloha <br>
     * -explain showDialog Two Button,可定义是否可被外部点击取消
     * @Date 2017/2/21 14:43
     */
    public static AlertDialog showDialog(Context context,String title,String message,boolean cancleOut, final OnDialogButtonClickListener mOnDialogButtonClickListener){
        AlertDialog.Builder alertDialogBuilder  = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(true);//默认为true，可以被Back键取消
        if(!TextUtils.isEmpty(title))
            alertDialogBuilder.setTitle(title);
        //alertDialogBuilder.setTitle(Html.fromHtml("<font color='#01ACE6'>"+title+"</font>"));
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("确认",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                if(mOnDialogButtonClickListener!=null)
                    mOnDialogButtonClickListener.onPositiveButtonClick(dialog);
            }
        });
        alertDialogBuilder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                if(mOnDialogButtonClickListener!=null)
                    mOnDialogButtonClickListener.onNegativeButtonClick(dialog);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(cancleOut);//设置是否可以点击外部消失
        alertDialog.show();
        return alertDialog;
    }

    /**
     * Created by Aloha <br>
     * -explain showDialog One Button
     * @Date 2017/2/21 14:43
     */
    public static AlertDialog showOneButtonDialog(Context context,String title,String message, final OnDialogOneButtonClickListener mOnOneDialogButtonClickListener){
        AlertDialog.Builder alertDialogBuilder  = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(true);//默认为true，可以被Back键取消
        if(!TextUtils.isEmpty(title))
            alertDialogBuilder.setTitle(title);
        //alertDialogBuilder.setTitle(Html.fromHtml("<font color='#01ACE6'>"+title+"</font>"));
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                if(mOnOneDialogButtonClickListener!=null)
                    mOnOneDialogButtonClickListener.onNegativeButtonClick(dialog);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);//设置是否可以点击外部消失
        alertDialog.show();
        return alertDialog;
    }
}
