package com.xixi.finance.callerfun.bean.record;

import java.io.Serializable;

/**
 * Created by AlohaQoQ on 2017/4/14.
 */
public class CallDetail implements Serializable {

    /**
     * 时间
     */
    private String date;

    /**
     * 通话数量
     */
    private String count;

    /**
     * 通话累计时长
     */
    private String duration;

    private String t60;

    private String t60_180;

    private String t180_360;

    private String t360;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getT60() {
        return t60;
    }

    public void setT60(String t60) {
        this.t60 = t60;
    }

    public String getT60_180() {
        return t60_180;
    }

    public void setT60_180(String t60_180) {
        this.t60_180 = t60_180;
    }

    public String getT180_360() {
        return t180_360;
    }

    public void setT180_360(String t180_360) {
        this.t180_360 = t180_360;
    }

    public String getT360() {
        return t360;
    }

    public void setT360(String t360) {
        this.t360 = t360;
    }
}
