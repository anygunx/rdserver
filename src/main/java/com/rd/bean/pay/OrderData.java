package com.rd.bean.pay;

/**
 * 订单数据
 * TODO 提到common层
 * 注：目前修改此文件需要与GameServer中的OrderData，这样不好
 * Created by XingYun on 2016/12/1.
 */
public class OrderData {
    private String orderId;
    private short channelId;
    private short serverId;
    private String account;
    private byte platform;
    private int playerId;
    private int amount;

    // 以下数据由GameServer返回
    private short subChannel;
    private int diamond;
    private short goodsId;
    private int goodsNum;
    private long createTime;
    private int level;

    public String getUID() {
        return getUID(channelId, orderId);
    }

    public static String getUID(short channelId, String id) {
        return channelId + id;
    }

    public short getChannelId() {
        return channelId;
    }

    public void setChannelId(short channelId) {
        this.channelId = channelId;
    }

    public short getSubChannel() {
        return subChannel;
    }

    public void setSubChannel(short subChannel) {
        this.subChannel = subChannel;
    }

    public short getServerId() {
        return serverId;
    }

    public void setServerId(short serverId) {
        this.serverId = serverId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public short getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(short goodsId) {
        this.goodsId = goodsId;
    }

    public int getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(int goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public byte getPlatform() {
        return platform;
    }

    public void setPlatform(byte platform) {
        this.platform = platform;
    }

    /**
     * 是否累充计数
     * 月卡啥啥的不算
     */
    public boolean isCountable() {
        return diamond != 0;
    }

    public void init(short channel, short serverId, String uid, byte platform, int playerId, String trans_id, int payment) {
        this.channelId = channel;
        this.serverId = serverId;
        this.account = uid;
        this.platform = platform;
        this.playerId = playerId;
        this.orderId = trans_id;
        this.amount = payment;
    }

    @Override
    public String toString() {
        return "OrderData [orderId=" + orderId
                + ", channelId=" + channelId
                + ", serverId=" + serverId
                + ", account=" + account
                + ", platform=" + platform
                + ", playerId=" + playerId
                + ", amount=" + amount
                + ", subChannel=" + subChannel
                + ", diamond=" + diamond
                + ", goodsId=" + goodsId
                + ", goodsNum=" + goodsNum
                + ", createTime=" + createTime
                + ", level=" + level + "]";
    }

}
