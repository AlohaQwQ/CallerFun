package com.xixi.finance.callerfun.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.xixi.finance.callerfun.R;


/**
 * Created by DuoNuo on 2016/4/1.
 */
public class LeRecyclerView extends RecyclerView {

    private LayoutManager defaultLayoutManager;
    private ItemAnimator defaultItemAnimator;
    private ItemDecoration defaultItemDecoration;
    private boolean recycleIsHorizontal;
    private boolean recycleIsGrid;

    public LeRecyclerView(Context context) {
        this(context, null);
    }

    public LeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LeRecyclerView, 0, 0);
        recycleIsHorizontal = typedArray.getBoolean(R.styleable.LeRecyclerView_recycle_horizontal,false);
        recycleIsGrid = typedArray.getBoolean(R.styleable.LeRecyclerView_recycle_grid, false);
        typedArray.recycle();

        if (recycleIsGrid){
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context,3);
            gridLayoutManager.setAutoMeasureEnabled(true);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            this.defaultLayoutManager = gridLayoutManager;
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setAutoMeasureEnabled(true);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            this.defaultLayoutManager = linearLayoutManager;
        }

        this.defaultItemAnimator = new DefaultItemAnimator();

        this.setLayoutManager(this.defaultLayoutManager);
        this.setItemAnimator(this.defaultItemAnimator);
        this.setHasFixedSize(true);

        //recycleview 设置缓存大小
        //recyclerView.getRecycledViewPool().setMaxRecycledViews(mContactAdapter.getItemViewType(0), 0);
    }

}
