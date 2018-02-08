package com.xixi.finance.callerfun.bean.record;

import java.io.Serializable;

/**
 * Created by AlohaQoQ on 2017/4/14.
 */
public class CallRecordLocal implements Serializable {

    /**
     * 排序
     */
    private int position;

    /**
     * 录音长度
     */
    private int duration;

    /**
     * 录音长度(分秒)
     */
    private String minuteSecond1;

    /**
     * 录音长度(分秒)
     */
    private String minuteSecond2;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 呼叫客户手机号
     */
    private String callPhoneNumber;

    /**
     * 呼叫客户名
     */
    private String callCustomerName;

    /**
     * 上传状态，是否上传成功
     */
    private String uploadStatus;

    /**
     * 是否正在播放
     */
    private boolean isPlaying;

    /**
     * 呼叫状态，1-呼出, 2-呼入
     */
    private int callStatus;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCallPhoneNumber() {
        return callPhoneNumber;
    }

    public void setCallPhoneNumber(String callPhoneNumber) {
        this.callPhoneNumber = callPhoneNumber;
    }

    public String getCallCustomerName() {
        return callCustomerName;
    }

    public void setCallCustomerName(String callCustomerName) {
        this.callCustomerName = callCustomerName;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getMinuteSecond1() {
        return minuteSecond1;
    }

    public void setMinuteSecond1(String minuteSecond1) {
        this.minuteSecond1 = minuteSecond1;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getMinuteSecond2() {
        return minuteSecond2;
    }

    public void setMinuteSecond2(String minuteSecond2) {
        this.minuteSecond2 = minuteSecond2;
    }

    public int getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(int callStatus) {
        this.callStatus = callStatus;
    }
}
