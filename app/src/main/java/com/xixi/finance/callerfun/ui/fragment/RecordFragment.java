package com.xixi.finance.callerfun.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.View;

import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.presenter.main.RecordPresenter;
import com.xixi.finance.callerfun.ui.activity.MainActivity;
import com.xixi.finance.callerfun.ui.view.main.IRecordView;
import com.xixi.finance.callerfun.util.LogUtil;
import com.xixi.finance.callerfun.version.PersistentDataCacheEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aloha.shiningstarbase.util.recycleviewadapter.CommonAdapter;
import butterknife.OnClick;

import static aloha.shiningstarbase.widget.EshineToast.showToast;

/**
 * Created by Aloha <br>
 * -explain
 * @Date 2018/1/18 17:52
 */
public class RecordFragment extends BaseFragment<IRecordView, RecordPresenter> implements IRecordView {

    private MainActivity mainActivity;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;
    private AlertDialog.Builder mBuilder1;

    private String text;
    private String begin;
    private String end;

    private static final int JUMP_LUCK_H5_HOME_PAGE = 201;
    private Boolean refreshBanner = true;
    private ArrayList<Integer> bannerImages = new ArrayList<>();
    private float progressRingPercent;
    /*private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.mipmap.ic_launcher)
            .showImageOnFail(R.mipmap.ic_launcher)
            .delayBeforeLoading(1)
            .cacheInMemory()
            .cacheOnDisc()
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new SimpleBitmapDisplayer())
            .build();*/

    private static final int READ_CONTACTS_REQUEST_CODE = 0;
    public HashMap<String, String> statisticsDataBasic = new HashMap<>();
    private String statisticsData;
    private String ocrUserStatisticsData;
    private Boolean updateState = true;

    private String noticeTitle;
    private String noticeContent;
    private int noticeIsDialog;
    private String noticeClose;
    private String noticeRemark;

    private CommonAdapter mCommonAdapterWhite;

    @Override
    protected RecordPresenter CreatePresenter() {
        return new RecordPresenter();
    }

    @Override
    protected int getLayoutID() {
        return 0;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initUI();
       /* SharedPreferences sp = getActivity().getSharedPreferences("TEMP", Context.MODE_PRIVATE);
        text = sp.getString("text", "");
        begin = sp.getString("begin", "");
        end = sp.getString("end", "");
        Date now = new Date();

        if (!text.equals("") && now.getTime() >= Long.valueOf(begin) && now.getTime() <= Long.valueOf(end)) {
            //弹出窗口
            new AlertDialog.Builder(getActivity()).setTitle(null).setMessage(text).show().setCanceledOnTouchOutside(false);
        } else {

        }*/
        initData();
    }

    private void initUI() {
        // 初始化toolbar
        //tv_title.setText("");
        //layout_toolbar.setTitle("");
        mainActivity = (MainActivity) getActivity();
        //homeAct.setSupportActionBar(layout_toolbar);
    }

    public void initData() {
        /**
         * Created by Aloha <br>
         * -explain 获取友盟token
         * @Date 2017/9/18 10:57
         *
        PushAgent mPushAgent = PushAgent.getInstance(getActivity());
        editTest.setVisibility(View.VISIBLE);
        editTest.setText(mPushAgent.getRegistrationId()+"");
         */

        /**
         * Created by Aloha <br>
         * -explain 上传用户OCR验证 前后时间状态, 添加applyOrder()方法 version参数
         * @Date 2016/10/13 10:00
         */
        String versionName = "0";
        int versionCode = 0;
        String channel = "0";
        try {
            versionName = this.getContextView().getPackageManager().getPackageInfo(
                    this.getContextView().getPackageName(), 0).versionName;
            versionCode = this.getContextView().getPackageManager().getPackageInfo(
                    this.getContextView().getPackageName(), 0).versionCode;
            channel = this.getContextView().getPackageManager().getApplicationInfo(
                    this.getContextView().getPackageName(), PackageManager.GET_META_DATA).metaData.getString("UMENG_CHANNEL");

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
        /**
         * 获取首页资金方列表
         */
        mPresenter.fetchCallList("");
    }

    /**
     * Created by Aloha <br>
     * -explain 基本埋点信息
     * @Date 2016/11/18 16:15
     */
    private void updateStatisticsInfo() {
        String deviceId = "";
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.READ_PHONE_STATE};
                requestPermissions(permissions, READ_CONTACTS_REQUEST_CODE);
            } else {
                deviceId = checkAndGetDeviceId();
            }
        } catch (Exception exception){
            //打印捕获的异常的堆栈信息，从堆栈信息中可以发现异常发生的位置和原因
            LogUtil.biubiubiu("exception ActivityCompat.checkSelfPermission():"+exception.getMessage());
            exception.printStackTrace();
        }
        PersistentDataCacheEntity.getInstance().setDeviceId(deviceId);

        /**
         * Created by Aloha <br>
         * -explain 获取用户权限
         * @Date 2016/11/15 20:48
         */
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, READ_CONTACTS_REQUEST_CODE);
        }
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, READ_CONTACTS_REQUEST_CODE);
        }
    }


    @Override
    public void showWhiteBlackProductsTitle(List<Map<String, String>> dataList) {

    }

    @Override
    public void showWhiteBlackUserType(Map<String, Object> dataMap) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
           // initData();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (0 == requestCode && null != data) {
            //mPresenter.wecashRegister(data.getStringExtra("result"));
        }
    }

    /**
     * 检查并获取设备唯一号（DeviceId）
     *
     * @return 设备唯一号
     */
    private String checkAndGetDeviceId() {
        String deviceId = null;
        if (null != getActivity()) {
            TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            if (null != tm) {
                String terminalInfo = tm.getDeviceId();
                if (null != terminalInfo) {
                    byte[] terminalInfoBytes = terminalInfo.getBytes();
                    if (null != terminalInfoBytes) {
                        for (byte terminalInfoByte : terminalInfoBytes) {
                            if (terminalInfoByte != '0') {
                                deviceId = terminalInfo;
                                LogUtil.biubiubiu("deviceId:"+deviceId);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return deviceId;
    }

    /**
     * Created by Aloha <br>
     * -explain // to handle the case where the user grants the permission. See the documentation
     * // for ActivityCompat#requestPermissions for more details.
     *
     * @Date 2016/11/16 10:24
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (READ_CONTACTS_REQUEST_CODE == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //mPresenter.uploadPhoneContacts();
            } else {
                showToast("请允许授权");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @OnClick({R.id.test,R.id.btn_get_loan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.test:
                test();
                break;
            case R.id.btn_get_loan:

                if (PersistentDataCacheEntity.getInstance().isLogin()) {
                    mPresenter.fetchSuperBorrowProductCanloan();
                } else {
                    Intent LoginIntent = new Intent(getContext(), StartActivity.class);
                    LoginIntent.putExtra("isRegisterAndLogin", true);
                    startActivity(LoginIntent);
                }
                break;
        }
    }

   private void test() {
       /*  OkHttpRequest request = new OkHttpRequest();

        request.setAPIPath("regeocoding");
        request.addRequestFormParam("l", "22.571298,113.946763");
        request.addRequestFormParam("type", "010");

        request.setBaseUrl("http://gc.ditu.aliyun.com/");

        request.showHttpRequestLog();
        OkHttpClientManager.getInstance().commitRequestTask(request, new IResultCallback() {
            @Override
            public void onResponse(String response) {
                LogUtil.biubiubiu("response:"+response);
            }

            @Override
            public void onFailure(String error) {
                LogUtil.biubiubiu("error:"+error);
            }
        });*/
       /* String result = "{" +
                "    \"SIGN_TYP\":\"\"," +
                "    \"RETURNCODE\":\"MCA00000\"," +
                "    \"RETURNCON\":\"交易成功\"," +
                "    \"TXN_CD\":\"M124\"," +
                "    \"SIGN_DAT\":\"\"," +
                "    \"JSON_DAT\":\"[{\"provId\":991000,\"provName\":\"北京\"},{\"provId\":991100,\"provName\":\"天津\"},{\"provId\":1200,\"provName\":\"河北省\"},{\"provId\":1600,\"provName\":\"山西省\"},{\"provId\":1900,\"provName\":\"内蒙古自治区\"},{\"provId\":2200,\"provName\":\"辽宁省\"},{\"provId\":2400,\"provName\":\"吉林省\"},{\"provId\":2600,\"provName\":\"黑龙江省\"},{\"provId\":3000,\"provName\":\"江苏省\"},{\"provId\":3300,\"provName\":\"浙江省\"},{\"provId\":3600,\"provName\":\"安徽省\"},{\"provId\":3900,\"provName\":\"福建省\"},{\"provId\":4200,\"provName\":\"江西省\"},{\"provId\":4500,\"provName\":\"山东省\"},{\"provId\":4900,\"provName\":\"河南省\"},{\"provId\":5200,\"provName\":\"湖北省\"},{\"provId\":5500,\"provName\":\"湖南省\"},{\"provId\":5800,\"provName\":\"广东省\"},{\"provId\":6100,\"provName\":\"广西自治区\"},{\"provId\":6400,\"provName\":\"海南省\"},{\"provId\":6500,\"provName\":\"四川省\"},{\"provId\":996900,\"provName\":\"重庆\"},{\"provId\":7000,\"provName\":\"贵州省\"},{\"provId\":7300,\"provName\":\"云南省\"},{\"provId\":7700,\"provName\":\"西藏自治区\"},{\"provId\":7900,\"provName\":\"陕西省\"},{\"provId\":8200,\"provName\":\"甘肃省\"},{\"provId\":8500,\"provName\":\"青海省\"},{\"provId\":8700,\"provName\":\"宁夏自治区\"},{\"provId\":8800,\"provName\":\"新疆维吾尔自治区\"},{\"provId\":992900,\"provName\":\"上海\"}]\"" +
                "}";
        result = result.replace("\"[","[");
        result = result.replace("]\"","]");
        Map<String, Object> responseMap = new CommonJSONParser().parse(result);
        String qq  = (String) responseMap.get("TXN_CD");*/

        //startActivity(new Intent(getActivity(), UserInfoIdentActivity.class));
    }

    /**
     * Created by Aloha <br>
     * -explain 上传用户统计埋点信息
     *
     * @Date 2016/12/15 12:43
     */
    @Override
    public void onResume() {
        super.onResume();
        if (updateState) {
            /**
             * Created by Aloha <br>
             * -explain 延迟请求
             * @Date 2017/1/3 14:34
             */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateStatisticsInfo();
                    updateState = false;
                    /*statisticsData = JsonParseUtil.parseJsonToString(StatisticsDatabaseHelper.getDatabaseHelper(getActivity()).queryDataToTable());
                    LogUtil.biubiubiu("jsonData: " + statisticsData);
                    if (statisticsData.length() > 4)
                        mPresenter.updateStatisticsInfoExpand(statisticsData);*/
                }
            }, 1000);
        }
        //开始自动翻页
        //bannerHomeFragment.startTurning(2500);
    }

    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        //bannerHomeFragment.stopTurning();
    }
}
