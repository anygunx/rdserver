package com.lg.bean;

import com.lg.util.ReflectionUtil;

/**
 * Created by XingYun on 2016/6/15.
 */
public abstract class PlayerLog extends BaseLog {
    protected int playerId;
    protected String account;
    protected short channel;
    /**
     * 20160815添加
     * 渠道内的子渠道标志
     **/
    protected short subChannel;
    /**
     * 20170417添加
     * 平台 0:不分 1:android 2:ios
     */
    protected byte platform;
    /**
     * 服务器id
     **/
    protected short serverId;
    protected String name;

    public void init(int playerId, String account, short channel, short subChannel, short serverId, String name, byte platform) {
        this.playerId = playerId;
        this.account = account;
        this.channel = channel;
        this.subChannel = subChannel;
        this.platform = platform;
        this.serverId = serverId;
        this.name = name;
    }

    public short getServerId() {
        return serverId;
    }

    public void setServerId(short serverId) {
        this.serverId = serverId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public short getChannel() {
        return channel;
    }

    public void setChannel(short channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getSubChannel() {
        return subChannel;
    }

    public void setSubChannel(short subChannel) {
        this.subChannel = subChannel;
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
                .append("serverId=").append(serverId).append(",")
                .append("playerId=").append(playerId).append(",")
                .append("account=").append(account).append(",")
                .append("channel=").append(channel).append(",")
                .append("subChannel=").append(subChannel).append(",")
                .append("name=").append(name).append(",")
                .append("platform=").append(platform).append(",")
                .append(ReflectionUtil.getFieldsString(this));
        return builder.toString();
    }

}
