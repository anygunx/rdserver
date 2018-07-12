package com.lg.bean;

import com.lg.util.ReflectionUtil;

/**
 * Created by XingYun on 2016/8/29.
 */
public class UserLog extends BaseLog {
    protected String account;
    protected String channel;
    /**
     * 20160815添加
     * 渠道内的子渠道标志
     **/
    protected String channelInner;
    /**
     * 20170417添加
     * 平台 0:不分 1:android 2:ios
     */
    protected byte platform;

    public void init(String account, String channel, String channelInner, byte platform) {
        this.account = account;
        this.channel = channel;
        this.channelInner = channelInner;
        this.platform = platform;
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannelInner() {
        return channelInner;
    }

    public void setChannelInner(String channelInner) {
        this.channelInner = channelInner;
    }

    public byte getPlatform() {
        return platform;
    }

    public void setPlatform(byte platform) {
        this.platform = platform;
    }

    @Override
    public String getFormatLog() throws Exception {
        StringBuilder builder = new StringBuilder(getPrefix())
                .append("account=").append(account).append(",")
                .append("channel=").append(channel).append(",")
                .append("channelInner=").append(channelInner).append(",")
                .append("platform=").append(platform).append(",")
                .append(ReflectionUtil.getFieldsString(this));
        return builder.toString();
    }
}
