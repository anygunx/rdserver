package com.lg.bean.game;


import com.lg.bean.PlayerLog;

/**
 * ��ֵ��¼Log
 * Created by XingYun on 2016/6/15.
 */
public class Pay extends PlayerLog {
    /**
     * 20160815���
     * ��ֵ������־
     **/
    private String channelPay;
    private String orderno;
    /**
     * ��ֵ���(Ԫ)
     **/
    private int amount;
    private short level;
    private long createTime;

    public Pay() {
    }

    public Pay(String channelPay, String orderno, int amount, short level, long createTime) {
        this.channelPay = channelPay;
        this.orderno = orderno;
        this.amount = amount;
        this.level = level;
        this.createTime = createTime;
    }

    public String getChannelPay() {
        return channelPay;
    }

    public void setChannelPay(String channelPay) {
        this.channelPay = channelPay;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

}
