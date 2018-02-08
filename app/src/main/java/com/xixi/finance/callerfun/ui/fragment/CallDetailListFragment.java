package com.xixi.finance.callerfun.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.bean.record.CallDetail;
import com.xixi.finance.callerfun.bean.record.CallDetailList;
import com.xixi.finance.callerfun.constant.APIKey;
import com.xixi.finance.callerfun.presenter.BasePresenter;
import com.xixi.finance.callerfun.presenter.main.CallListPresenter;
import com.xixi.finance.callerfun.ui.activity.LoginActivity;
import com.xixi.finance.callerfun.ui.activity.MainActivity;
import com.xixi.finance.callerfun.ui.view.main.ICallListView;
import com.xixi.finance.callerfun.version.PersistentDataCacheEntity;
import com.xixi.finance.callerfun.widget.DatePickerkerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import aloha.shiningstarbase.util.recycleviewadapter.CommonAdapter;
import aloha.shiningstarbase.util.recycleviewadapter.SuperRecycleViewHolder;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Aloha <br>
 * -explain 录音月份详情列表
 *
 * @Date 2018/1/18 17:52
 */
public class CallDetailListFragment extends BaseSwipeRefreshFragment<ICallListView, CallListPresenter> implements ICallListView {

    @BindView(R.id.lay_empty_status)
    LinearLayout layEmptyStatus;
    @BindView(R.id.lay_status_nologin)
    LinearLayout layStatusNologin;
    @BindView(R.id.tv_customer_fragment_record)
    TextView tvCustomerFragmentRecord;
    @BindView(R.id.tv_playtime_fragment_record)
    TextView tvPlaytimeFragmentRecord;
    @BindView(R.id.lay_column_fragment_calllist)
    LinearLayout layColumnFragmentCalllist;
    @BindView(R.id.tv_call_amount)
    TextView tvCallAmount;
    @BindView(R.id.tv_call_time_amount)
    TextView tvCallTimeAmount;
    @BindView(R.id.tv_call_type1_amount)
    TextView tvCallType1Amount;
    @BindView(R.id.tv_call_type2_amount)
    TextView tvCallType2Amount;
    @BindView(R.id.tv_call_type3_amount)
    TextView tvCallType3Amount;
    @BindView(R.id.tv_call_type4_amount)
    TextView tvCallType4Amount;
    @BindView(R.id.tv_all_call_amount)
    TextView tvAllCallAmount;
    @BindView(R.id.tv_all_call_time_amount)
    TextView tvAllCallTimeAmount;
    @BindView(R.id.btn_select_detail_month)
    RelativeLayout btnSelectDetailMonth;
    @BindView(R.id.lay_cotent_fragment_calllist)
    RelativeLayout layCotentFragmentCalllist;

    private MainActivity mainActivity;

    private static final String ARG_SECTION_PAGE = "section_number";
    private List<CallDetail> callDetailList = new ArrayList<>();
    private CommonAdapter mCommonAdapter;
    /**
     * Bundle 缓存value
     */
    private int mSectionPage;

    /**
     * 选择器
     */
    private String mSelectYear;
    private String mSelectMonth;

    public CallDetailListFragment() {
    }

    //创建Fragment 实例
    public static CallDetailListFragment newInstance(int sectionNumber) {
        CallDetailListFragment fragment = new CallDetailListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_PAGE, sectionNumber);
        //Bundle 保存传递Fragment参数
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected CallListPresenter CreatePresenter() {
        return new CallListPresenter();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_calllist;
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
        Calendar c = Calendar.getInstance();
        mSelectYear = String.valueOf(c.get(Calendar.YEAR));
        mSelectMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
        tvCustomerFragmentRecord.setText(mSelectYear + "年");
        tvPlaytimeFragmentRecord.setText(mSelectMonth + "月");
        if (PersistentDataCacheEntity.getInstance().isLogin()) {
            refreshRecordDetailMonth();
            layStatusNologin.setVisibility(View.GONE);
            recycler_view.setVisibility(View.VISIBLE);
            layColumnFragmentCalllist.setVisibility(View.VISIBLE);
            layCotentFragmentCalllist.setVisibility(View.VISIBLE);
        } else {
            layStatusNologin.setVisibility(View.VISIBLE);
            recycler_view.setVisibility(View.GONE);
            layColumnFragmentCalllist.setVisibility(View.GONE);
            layCotentFragmentCalllist.setVisibility(View.GONE);
            showContentView();
        }
    }

    /**
     * Created by Aloha <br>
     * -explain 获取/刷新一个月份的录音详情
     *
     * @Date 2018/2/5 16:50
     */
    private void refreshRecordDetailMonth() {
        /**
         * 获取一个月份的录音详情
         */
        mPresenter.fetchRecordDetailMonth(mSelectYear, mSelectMonth);
    }

    @Override
    public void onshowRecordDetailMonth(String response, String status, String message) {
        if (BasePresenter.RESPONSE_STATUS_SUCCESS.equals(status)) {
            if (!TextUtils.isEmpty(response)) {
                layEmptyStatus.setVisibility(View.GONE);
                JSONObject jsonObject = JSONObject.parseObject(response);
                if (jsonObject != null) {
                    if (!TextUtils.isEmpty(jsonObject.getString(APIKey.RECORD_DETAIL_LIST))) {
                        String qq = "{\"list\":" + jsonObject.getString(APIKey.RECORD_DETAIL_LIST) + "}";
                        CallDetailList cc = JSONObject.parseObject(qq, CallDetailList.class);
                        if (cc != null && cc.getList() != null && cc.getList().size() > 0) {
                            callDetailList.addAll(cc.getList());
                        }
                    }
                    tvAllCallAmount.setText(jsonObject.getString(APIKey.RECORD_ALL_COUNT));
                    tvAllCallTimeAmount.setText(jsonObject.getString(APIKey.RECORD_ALL_DURATION));
                    tvCallAmount.setText(jsonObject.getString(APIKey.RECORD_THIS_COUNT));
                    tvCallTimeAmount.setText(jsonObject.getString(APIKey.RECORD_THIS_DURATION));
                    tvCallType1Amount.setText(jsonObject.getString(APIKey.RECORD_THIS_S0));
                    tvCallType2Amount.setText(jsonObject.getString(APIKey.RECORD_THIS_S60));
                    tvCallType3Amount.setText(jsonObject.getString(APIKey.RECORD_THIS_S180));
                    tvCallType4Amount.setText(jsonObject.getString(APIKey.RECORD_THIS_S360));

                    if (mCommonAdapter == null) {
                        mCommonAdapter = new CommonAdapter<CallDetail>(getContext(), R.layout.item_call_detail_list, callDetailList) {
                            @Override
                            public void convert(final SuperRecycleViewHolder holder, final int position, final CallDetail callDetail) {
                                holder.setText(R.id.tv_date_item_call_detail, callDetail.getDate() + "日");
                                holder.setText(R.id.tv_call_amount_item_call_detail, callDetail.getCount());
                                holder.setText(R.id.tv_call_amount_duration_item_call_detail, callDetail.getDuration());

                                holder.setText(R.id.tv_call_type1_item_call_detail, callDetail.getT60());
                                holder.setText(R.id.tv_call_type2_item_call_detail, callDetail.getT60_180());
                                holder.setText(R.id.tv_call_type3_item_call_detail, callDetail.getT180_360());
                                holder.setText(R.id.tv_call_type4_item_call_detail, callDetail.getT360());
                            }
                        };
                        recycler_view.setAdapter(mCommonAdapter);
                        //recycleview 设置缓存大小
                        recycler_view.getRecycledViewPool().setMaxRecycledViews(mCommonAdapter.getItemViewType(0), 0);
                    } else {
                        mCommonAdapter.notifyDataSetChanged();
                    }
                } else {
                    showToast("网络错误");
                }
            } else {
                layEmptyStatus.setVisibility(View.VISIBLE);
            }
        } else {
            showToast(message);
        }
        setRefresh(false);
        setLoadMore(false);
        showContentView();
    }

    /**
     * 时间选择器
     */
    private void showExitGameAlert() {
        Calendar c = Calendar.getInstance();
        new DatePickerkerDialog(getContext(), 0, new DatePickerkerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                  int startDayOfMonth) {
                String textString = String.format("选择月份：%d-%02d", startYear,
                        startMonthOfYear + 1);
                showToast(textString);
                mSelectYear = String.valueOf(startYear);
                mSelectMonth = String.valueOf(startMonthOfYear + 1);
                tvCustomerFragmentRecord.setText(mSelectYear + "年");
                tvPlaytimeFragmentRecord.setText(mSelectMonth + "月");
                if (PersistentDataCacheEntity.getInstance().isLogin()) {
                    callDetailList.clear();
                    refreshRecordDetailMonth();
                }
            }
        }, Integer.valueOf(mSelectYear), Integer.valueOf(mSelectMonth), c.get(Calendar.DATE)).show();
    }

    @Override
    protected void onLoadMore() {
    }

    @Override
    protected void onSwipeRefresh() {
        if (!isRefreshing()) {
            setLoadMore(true);
            callDetailList.clear();
            refreshRecordDetailMonth();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            // initData();
        }
    }

    @Override
    public boolean isLoadingMore() {
        return false;
    }

    @OnClick({R.id.lay_status_nologin, R.id.btn_select_detail_month})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lay_status_nologin:
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.btn_select_detail_month:
                if (PersistentDataCacheEntity.getInstance().isLogin())
                    showExitGameAlert();
                break;
        }
    }
}
