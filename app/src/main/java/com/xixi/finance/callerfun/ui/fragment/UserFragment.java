package com.xixi.finance.callerfun.ui.fragment;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.presenter.main.RecordPresenter;
import com.xixi.finance.callerfun.ui.activity.MainActivity;
import com.xixi.finance.callerfun.ui.view.main.IRecordView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by Aloha <br>
 * -explain
 *
 * @Date 2018/1/18 17:52
 */
public class UserFragment extends BaseFragment<IRecordView, RecordPresenter> implements IRecordView {

    @BindView(R.id.img_user_head)
    ImageView imgUserHead;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_user_depart)
    TextView tvUserDepart;
    @BindView(R.id.btn_record_clear)
    LinearLayout btnRecordClear;
    @BindView(R.id.btn_advise_feedback)
    LinearLayout btnAdviseFeedback;
    @BindView(R.id.btn_user_setup)
    LinearLayout btnUserSetup;
    @BindView(R.id.btn_user_loginout)
    LinearLayout btnUserLoginout;

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

    private static final String ARG_SECTION_PAGE = "section_number";
    /**
     * Bundle 缓存value
     */
    private int mSectionPage;

    public UserFragment() {}

    //创建Fragment 实例
    public static UserFragment newInstance(int sectionNumber) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_PAGE, sectionNumber);
        //Bundle 保存传递Fragment参数
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected RecordPresenter CreatePresenter() {
        return new RecordPresenter();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_user;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initUI();
        initData();
    }

    private void initUI() {
        mainActivity = (MainActivity) getActivity();
    }

    public void initData() {
        /**
         * 获取首页资金方列表
         */
        mPresenter.fetchCallList("");
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

   /* @OnClick({R.id.test,R.id.btn_get_loan})
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
    }*/
}
