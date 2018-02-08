package com.xixi.finance.callerfun.bean.record;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlohaQoQ on 2017/4/14.
 */

public class CallDetailList implements Serializable {

    private List<CallDetail> list = new ArrayList<>();

    public List<CallDetail> getList() {
        return list;
    }

    public void setList(List<CallDetail> list) {
        this.list = list;
    }
}
