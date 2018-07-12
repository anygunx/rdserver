package com.lg.bean;

/**
 * Created by XingYun on 2016/6/15.
 */
public interface ILog extends IDBSource {
    String getPrefix();

    String getFormatLog() throws Exception;

    void setTimestamp(String timestamp);

    void setLogLevel(byte logLevel);

    String getTimestamp();

    byte getLogLevel();
}
