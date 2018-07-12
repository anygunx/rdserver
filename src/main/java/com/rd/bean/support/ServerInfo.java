package com.rd.bean.support;

/**
 * 玩吧拉取服务器信息
 * * TODO 提到common层
 * Created by XingYun on 2017/6/5.
 */
public class ServerInfo {
    private short serverId;
    private short channelId;
    private String spid;
    private int playerCount;
    private String adId;

    public ServerInfo() {
        this.playerCount = 0;
    }

    public short getChannelId() {
        return channelId;
    }

    public void setChannelId(short channelId) {
        this.channelId = channelId;
    }

    public String getSpid() {
        return spid;
    }

    public void setSpid(String spid) {
        this.spid = spid;
    }

    public short getServerId() {
        return serverId;
    }

    public void setServerId(short serverId) {
        this.serverId = serverId;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }
}
