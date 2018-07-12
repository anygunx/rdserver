package com.lg.bean;

/**
 * Created by XingYun on 2016/8/29.
 */
public interface IUser {
    String getAccount();

    String getChannel();

    String getChannelInner();

    /**
     * 20170417添加
     * 平台 0:不分 1:android 2:ios
     */
    byte getPlatform();
}
