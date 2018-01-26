package com.xixi.finance.callerfun.bean;

/**
 * 定时器配置
 */
public class TimerConfig {

    /**
     * 定时器名称
     */
    private String name;

    /**
     * 定时器时长，以毫秒（/millisecond）为单位
     */
    private long duration;

    /**
     * 是否已存在该定时器
     */
    private boolean isExitedTimer;

    /**
     * 回调接口
     */
    private TimerCallback callback;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isExitedTimer() {
        return isExitedTimer;
    }

    public void setExitedTimer(boolean exitedTimer) {
        isExitedTimer = exitedTimer;
    }

    public TimerCallback getCallback() {
        return callback;
    }

    public void setCallback(TimerCallback callback) {
        this.callback = callback;
    }

    public interface TimerCallback {

        /**
         * 回调方法
         * @param restDuration 剩余时间，以毫秒为单位
         */
        void result(long restDuration);
    }

}
