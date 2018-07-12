package com.lg.bean;

import com.lg.define.Define;

/**
 * 所有log的基类
 * Created by XingYun on 2016/6/15.
 */
public abstract class BaseLog implements ILog {
    /**
     * 系统时间
     **/
    protected String timestamp;
    /**
     * 日志级别
     **/
    protected byte logLevel;

    @Override
    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public byte getLogLevel() {
        return logLevel;
    }

    @Override
    public void setLogLevel(byte level) {
        this.logLevel = level;
    }

    @Override
    public String getPrefix() {
        StringBuilder builder = new StringBuilder(Define.TARGET_PREFIX).append(this.getClass().getName()).append(":");
        return builder.toString();
    }

}
