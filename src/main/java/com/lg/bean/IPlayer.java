package com.lg.bean;

/**
 * Created by XingYun on 2016/6/15.
 */
public interface IPlayer {
    short getServerId();

    int getId();

    String getAccount();

    short getChannel();

    short getSubChannel();

    String getName();

    /**
     * 20170417添加
     * 平台 0:不分 1:android 2:ios
     */
    byte getPlatform();
}
