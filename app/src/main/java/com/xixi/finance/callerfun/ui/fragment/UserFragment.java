package com.xixi.finance.callerfun.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.presenter.user.UserPresenter;
import com.xixi.finance.callerfun.ui.activity.LoginActivity;
import com.xixi.finance.callerfun.ui.activity.MainActivity;
import com.xixi.finance.callerfun.ui.view.main.IUserView;
import com.xixi.finance.callerfun.util.AudioFileUtils;
import com.xixi.finance.callerfun.util.DialogUtil;
import com.xixi.finance.callerfun.version.PersistentDataCacheEntity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Aloha <br>
 * -explain
 * @Date 2018/1/18 17:52
 */
public class UserFragment extends BaseFragment<IUserView, UserPresenter> implements IUserView {

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
    @BindView(R.id.tv_record_size)
    TextView tvRecordSize;

    private MainActivity mainActivity;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;
    private AlertDialog.Builder mBuilder1;

    private String text;
    private String begin;
    private String end;

    private static final String ARG_SECTION_PAGE = "section_number";
    /**
     * Bundle 缓存value
     */
    private int mSectionPage;

    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.mipmap.ic_launcher)
            .showImageOnFail(R.mipmap.ic_launcher)
            .delayBeforeLoading(1)
            .cacheInMemory()
            .cacheOnDisc()
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new SimpleBitmapDisplayer())
            .build();

    public UserFragment() {
    }

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
    protected UserPresenter CreatePresenter() {
        return new UserPresenter();
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
        // mPresenter.fetchCallList("");
        tvRecordSize.setText(AudioFileUtils.getRecordFilesStorageSize(getContext()));

        if(!TextUtils.isEmpty(PersistentDataCacheEntity.getInstance().getName()))
            tvUserName.setText(PersistentDataCacheEntity.getInstance().getName());
        if(!TextUtils.isEmpty(PersistentDataCacheEntity.getInstance().getUserHearImg())){
            ImageLoader.getInstance().displayImage(PersistentDataCacheEntity.getInstance().getUserHearImg(),
                    imgUserHead, options);
        } else {
            imgUserHead.setBackgroundResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            // initData();
        }
    }

    @OnClick({R.id.btn_record_clear, R.id.btn_advise_feedback, R.id.btn_user_setup, R.id.btn_user_loginout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_record_clear:
                DialogUtil.showDialog(getContext(), "提示", "是否清除录音缓存文件","确定", "取消", new DialogUtil.OnDialogButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(DialogInterface dialog) {
                        dialog.dismiss();
                        AudioFileUtils.deleteRecordFolderFile(getContext());
                        tvRecordSize.setText(AudioFileUtils.getRecordFilesStorageSize(getContext()));
                        showToast("清除完毕");
                    }
                    @Override
                    public void onNegativeButtonClick(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.btn_advise_feedback:

                break;
            case R.id.btn_user_setup:

                break;
            case R.id.btn_user_loginout:
                Intent intent = new Intent();
                intent.setAction(MainActivity.ACTION_LOGOUT);
                mainActivity.sendBroadcast(intent);
                PersistentDataCacheEntity.getInstance().setToken("");
                PersistentDataCacheEntity.getInstance().setUserHearImg("");
                imgUserHead.setBackgroundResource(R.mipmap.ic_launcher);
                PersistentDataCacheEntity.getInstance().setName("用户");
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
        }
    }
}