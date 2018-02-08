package com.xixi.finance.callerfun.ui.fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.bean.record.CallRecordLocal;
import com.xixi.finance.callerfun.presenter.main.RecordPresenter;
import com.xixi.finance.callerfun.ui.activity.CustomerDataDetailActivity;
import com.xixi.finance.callerfun.ui.activity.LoginActivity;
import com.xixi.finance.callerfun.ui.activity.MainActivity;
import com.xixi.finance.callerfun.ui.view.main.IRecordView;
import com.xixi.finance.callerfun.util.AudioFileUtils;
import com.xixi.finance.callerfun.util.LogUtil;
import com.xixi.finance.callerfun.version.PersistentDataCacheEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import aloha.shiningstarbase.util.recycleviewadapter.CommonAdapter;
import aloha.shiningstarbase.util.recycleviewadapter.SuperRecycleViewHolder;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Aloha <br>
 * -explain 录音列表
 * @Date 2018/1/18 17:52
 */
public class RecordFragment extends BaseSwipeRefreshFragment<IRecordView, RecordPresenter> implements IRecordView {

    @BindView(R.id.seekbar_fragment_record)
    SeekBar seekbarFragmentRecord;
    @BindView(R.id.tv_customer_fragment_record)
    TextView tvCustomerFragmentRecord;
    @BindView(R.id.tv_playtime_fragment_record)
    TextView tvPlaytimeFragmentRecord;
    @BindView(R.id.btn_play_record_fragment_record)
    ImageView btnPlayRecordFragmentRecord;
    @BindView(R.id.btn_play_next_fragment_record)
    ImageView btnPlayNextFragmentRecord;
    @BindView(R.id.lay_play_record)
    LinearLayout layPlayRecord;
    @BindView(R.id.lay_empty_status)
    LinearLayout layEmptyStatus;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;
    @BindView(R.id.lay_status_nologin)
    LinearLayout layStatusNologin;

    private MainActivity mainActivity;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;
    private AlertDialog.Builder mBuilder1;

    private static final String ARG_SECTION_PAGE = "section_number";
    private CommonAdapter mCommonAdapter;

    /**
     * Bundle 缓存value
     */
    private int mSectionPage;

    /**
     * 录音文件
     */
    private List<File> recordFiles = new ArrayList<>();
    private List<CallRecordLocal> callRecordLocals = new ArrayList<>();

    /**
     * 正在播放录音文件排序
     */
    private int selectRecordPlaying = -1;

    /**
     * 音频播放
     */
    private MediaPlayer mediaPlayer;

    /**
     * 播放状态
     */
    private boolean isPlaying = false;

    /**
     * 记录播放位置
     */
    private int playTime;

    /**
     * 记录播放时长(秒)
     */
    private int playTimeMax;

    /**
     * 记录选择用户录音文件，用户资料页播放
     */
    private String recordFilePath;

    /**
     * Message
     */
    private int STATE_PLAY = 2;
    private int STATE_PAUSE = 3;

    private Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshLocalCallRecords();
        }
    };

    private Handler recordPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    // 将SeekBar位置设置到当前播放位置
                    LogUtil.biubiubiu("mediaPlayer.getCurrentPosition():" + mediaPlayer.getCurrentPosition());
                    LogUtil.biubiubiu("AudioFileUtils.getCurrentPosition():" +
                            AudioFileUtils.getRecordDurationSecond(mediaPlayer.getCurrentPosition()));
                    seekbarFragmentRecord.setProgress(
                            AudioFileUtils.getRecordDurationSecond(mediaPlayer.getCurrentPosition()));
                    break;
                case 3:

                    break;
            }

        }
    };

    private Runnable recordPlayRunnable = new Runnable() {
        @Override
        public void run() {
            while (isPlaying && mediaPlayer.isPlaying()) {
                Message message = new Message();
                message.what = STATE_PLAY;
                recordPlayHandler.sendMessage(message);
                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(500);
                    //播放进度
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    public RecordFragment() {
    }

    //创建Fragment 实例
    public static RecordFragment newInstance(int sectionNumber) {
        RecordFragment fragment = new RecordFragment();
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
        return R.layout.fragment_record;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null)
            mSectionPage = bundle.getInt(ARG_SECTION_PAGE);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlaying = false;
                callRecordLocals.get(selectRecordPlaying).setPlaying(isPlaying);
                mCommonAdapter.notifyDataSetChanged();
                playTime = 0;
                btnPlayRecordFragmentRecord.setBackgroundResource(R.mipmap.ic_record_play_normal);
                seekbarFragmentRecord.setProgress(seekbarFragmentRecord.getMax());
                tvPlaytimeFragmentRecord.setText("00:00");
            }
        });
        initUI();
        initData();
    }

    private void initUI() {
        mainActivity = (MainActivity) getActivity();
        seekbarFragmentRecord.setEnabled(true);
    }

    public void initData() {
        if(PersistentDataCacheEntity.getInstance().isLogin()){
            layStatusNologin.setVisibility(View.GONE);
            /**
             * 获取录音文件夹
             */
            if (AudioFileUtils.hasRecordFiles(getContext())) {
                layEmptyStatus.setVisibility(View.GONE);
                seekbarFragmentRecord.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //防止在拖动进度条进行进度设置时与Thread更新播放进度条冲突
                        isPlaying = true;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (!TextUtils.isEmpty(showRecordTime(seekBar.getProgress())))
                            tvPlaytimeFragmentRecord.setText(showRecordTime(seekBar.getProgress()));
                        //将media进度设置为当前seekbar的进度
                        mediaPlayer.seekTo(seekBar.getProgress() * 1000);
                        isPlaying = false;
                        startListenMediaPlay();
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        LogUtil.biubiubiu("onProgressChanged+" + progress);
                        if (!TextUtils.isEmpty(showRecordTime(seekBar.getProgress())))
                            tvPlaytimeFragmentRecord.setText(showRecordTime(progress));
                    }
                });
                refreshLocalCallRecords();
            } else {
                layEmptyStatus.setVisibility(View.VISIBLE);
            }
        } else {
            layStatusNologin.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 播放下一录音
     * @param callRecordLocal
     */
    private void playNextRecord(CallRecordLocal callRecordLocal){
        mediaPlayer.pause();
        playTime = 0;
        isPlaying = false;
        callRecordLocal.setPlaying(isPlaying);
        mediaPlayer.reset();
    }

    /**
     * 录音状态初始化
     *
     * @param recordNumber    播放录音文件序号
     * @param callRecordLocal
     */
    private void prepareRecord(int recordNumber, CallRecordLocal callRecordLocal) {
        try {
            mediaPlayer.setDataSource(recordFiles.get(recordNumber).getAbsolutePath());
            mediaPlayer.prepare();
            selectRecordPlaying = recordNumber;
            if (TextUtils.isEmpty(callRecordLocal.getCallCustomerName()) |
                    callRecordLocal.getCallCustomerName().equals("null")) {
                tvCustomerFragmentRecord.setText("用户");
            } else {
                tvCustomerFragmentRecord.setText(callRecordLocal.getCallCustomerName());
            }
            tvPlaytimeFragmentRecord.setText(callRecordLocal.getMinuteSecond2());
            playTimeMax = callRecordLocal.getDuration();
            seekbarFragmentRecord.setMax(callRecordLocal.getDuration());
            seekbarFragmentRecord.setProgress(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放录音
     *
     * @param recordNumber 播放录音文件序号
     */
    private void playRecord(int recordNumber, CallRecordLocal callRecordLocal) {
        /**
         * 暂停——继续播放
         */
        if (!isPlaying) {
            // if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            mediaPlayer.seekTo(playTime);
            isPlaying = true;
            callRecordLocal.setPlaying(isPlaying);
            btnPlayRecordFragmentRecord.setBackgroundResource(R.mipmap.ic_record_pause_normal);
            //}
        } else {
            //if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playTime = mediaPlayer.getCurrentPosition();
            isPlaying = false;
            callRecordLocal.setPlaying(isPlaying);
            btnPlayRecordFragmentRecord.setBackgroundResource(R.mipmap.ic_record_play_normal);
            // }
        }
        startListenMediaPlay();
        selectRecordPlaying = recordNumber;
        mCommonAdapter.notifyDataSetChanged();
    }

    /**
     * MainActivity Refresh
     */
    public void refreshLocalCallRecordsForMain() {
        refreshHandler.sendEmptyMessage(new Message().what);
    }

    @Override
    public void refreshLocalCallRecords() {
        layEmptyStatus.setVisibility(View.GONE);
        recordFiles = AudioFileUtils.getWavRecordFiles(getContext());
        callRecordLocals = transformationRecord(recordFiles);
        if (callRecordLocals.size() > 0)
            prepareRecord(0, callRecordLocals.get(0));
        if (mCommonAdapter == null) {
            mCommonAdapter = new CommonAdapter<CallRecordLocal>(getContext(), R.layout.item_record_list, callRecordLocals) {
                @Override
                public void convert(final SuperRecycleViewHolder holder, final int position, final CallRecordLocal callRecord) {
                    if(callRecord.getCallStatus()==1){
                        holder.setBackgroundRes(R.id.img_call_staut, R.mipmap.ic_record_callout);
                    } else {
                        holder.setBackgroundRes(R.id.img_call_staut, R.mipmap.ic_record_calldown);
                    }
                    if (!TextUtils.isEmpty(callRecord.getCallCustomerName()) && !callRecord.getCallCustomerName().equals("null"))
                        holder.setText(R.id.tv_call_customer_name, callRecord.getCallCustomerName());
                    if (!TextUtils.isEmpty(callRecord.getCallPhoneNumber()) && !callRecord.getCallPhoneNumber().equals("null"))
                        holder.setText(R.id.tv_call_customer_phone, callRecord.getCallPhoneNumber());
                    holder.setText(R.id.tv_call_duration, callRecord.getMinuteSecond1());
                    holder.setText(R.id.tv_call_time, callRecord.getCreateTime());
                    if (callRecord.isPlaying()) {
                        holder.setBackgroundRes(R.id.btn_record_play, R.mipmap.ic_record_pause_normal_round);
                    } else {
                        holder.setBackgroundRes(R.id.btn_record_play, R.mipmap.ic_record_play_normal_round);
                    }
                    holder.setOnClickListener(R.id.tv_call_customer_name, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /**
                             * 存储用户录音文件路径
                             */
                            recordFilePath = recordFiles.get(position).getAbsolutePath();
                            /**
                             * 获取客户资料page
                             */
                            mPresenter.fetchCustomerPage(callRecord.getCallPhoneNumber());
                        }
                    });
                    holder.setOnClickListener(R.id.tv_call_customer_phone, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /**
                             * 存储用户录音文件路径
                             */
                            recordFilePath = recordFiles.get(position).getAbsolutePath();
                            /**
                             * 获取客户资料page
                             */
                            mPresenter.fetchCustomerPage(callRecord.getCallPhoneNumber());
                        }
                    });
                    holder.setOnClickListener(R.id.btn_record_play, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (selectRecordPlaying != position){
                                playNextRecord(callRecordLocals.get(selectRecordPlaying));
                                prepareRecord(position, callRecord);
                            }
                            playRecord(position, callRecord);
                        }
                    });
                }
            };
            recycler_view.setAdapter(mCommonAdapter);
            //recycleview 设置缓存大小
            recycler_view.getRecycledViewPool().setMaxRecycledViews(mCommonAdapter.getItemViewType(0), 0);
        } else {
            mCommonAdapter.notifyDataSetChanged();
        }
        setRefresh(false);
        setLoadMore(false);
        showContentView();
    }

    /**
     * 显示用户资料Page
     * @param responsePage
     */
    @Override
    public void showCustomerPage(String responsePage) {
        if(responsePage.length()>20){
            getContext().startActivity(new Intent(getActivity(), CustomerDataDetailActivity.class).
                    putExtra(CustomerDataDetailActivity.KEY_CUSTOMER_PAGE, responsePage).
                    putExtra(CustomerDataDetailActivity.KEY_RECORD_FILE_PATH, recordFilePath));
        } else {
            showToast(responsePage);
        }
    }

    /**
     * 开始监听播放进度并更新progressbar
     */
    private void startListenMediaPlay() {
       /* Message message = new Message();
        if (isPlaying) {
            message.what = STATE_PLAY;
        } else {
            message.what = STATE_PAUSE;
        }
        recordPlayHandler.sendMessage(message);*/
        new Thread(recordPlayRunnable).start();
    }

    /**
     * 录音文件转换
     *
     * @param recordFiles
     * @return
     */
    private List<CallRecordLocal> transformationRecord(List<File> recordFiles) {
        List<CallRecordLocal> recordList = new ArrayList<>();
        for (int i = 0; i < recordFiles.size(); i++) {
            CallRecordLocal callRecordLocal = new CallRecordLocal();
            String[] temp = recordFiles.get(i).getName().split("_");
            callRecordLocal.setCreateTime(AudioFileUtils.getRecordCreateTime(recordFiles.get(i).getAbsolutePath()));
            callRecordLocal.setPosition(i);
            callRecordLocal.setCallCustomerName(temp[0]);
            callRecordLocal.setCallPhoneNumber(temp[1]);
            callRecordLocal.setCallStatus(Integer.valueOf(temp[2]));
            callRecordLocal.setDuration(
                    AudioFileUtils.getRecordDurationSecond(
                            AudioFileUtils.getRecordDuration(recordFiles.get(i).getAbsolutePath())));
            callRecordLocal.setMinuteSecond1(AudioFileUtils.getRecordMinute(recordFiles.get(i).getAbsolutePath()));
            callRecordLocal.setMinuteSecond2(AudioFileUtils.getRecordMinute2(recordFiles.get(i).getAbsolutePath()));
            recordList.add(callRecordLocal);
        }
        return recordList;
    }

    /**
     * 时间显示函数,获得的是以秒为单位的，转换成00:00格式
     *
     * @param time
     * @return
     */
    public String showRecordTime(int time) {
        if (time == 0)
            return "";
        time = playTimeMax - time;
        int second = time % 60;
        int minute = time / 60;
        int hour = minute / 60;
        //return String.format("%02d:%02d:%02d", hour, minute, second);
        return String.format("%02d:%02d", minute, second);
    }

    @Override
    protected void onLoadMore() {}

    /**
     * Created by Aloha <br>
     * -explain 下拉刷新
     *
     * @Date 2017/9/26 15:22
     */
    @Override
    protected void onSwipeRefresh() {
        if (!isRefreshing()) {
            setLoadMore(true);
            refreshLocalCallRecords();
        }
    }

    @Override
    public boolean isLoadingMore() {
        return false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            // initData();
        }
    }

    @OnClick({R.id.btn_play_record_fragment_record, R.id.btn_play_next_fragment_record ,R.id.lay_status_nologin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play_record_fragment_record:
                if (callRecordLocals.size() > 0 && selectRecordPlaying <= callRecordLocals.size())
                    playRecord(selectRecordPlaying, callRecordLocals.get(selectRecordPlaying));
                break;
            case R.id.btn_play_next_fragment_record:
                if (callRecordLocals.size() > 0 && selectRecordPlaying<callRecordLocals.size()-1) {
                   // if (mediaPlayer.isPlaying()) {
                    playNextRecord(callRecordLocals.get(selectRecordPlaying));
                    selectRecordPlaying += 1;
                    prepareRecord(selectRecordPlaying, callRecordLocals.get(selectRecordPlaying));
                    playRecord(selectRecordPlaying, callRecordLocals.get(selectRecordPlaying));
                   // }
                } else {
                    showToast("没有更多了");
                }
                break;
            case R.id.lay_status_nologin:
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.biubiubiu("RecordFragment-onDestroy");
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.biubiubiu("RecordFragment-onResume");
        if (isPlaying) {
            if (mediaPlayer != null) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.biubiubiu("RecordFragment-onPause");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }
}
