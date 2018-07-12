package com.rd.bean.gang;

import com.alibaba.fastjson.annotation.JSONField;
import com.rd.net.message.Message;

public class GangTurntableLog {

    /**
     * 玩家名字
     **/
    private String n;
    /**
     * 物品类型
     **/
    private byte t;
    /**
     * 物品id
     **/
    private short g;

    public GangTurntableLog() {

    }

    public GangTurntableLog(String name, byte type, short goodsId) {
        this.n = name;
        this.t = type;
        this.g = goodsId;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public byte getT() {
        return t;
    }

    public void setT(byte t) {
        this.t = t;
    }

    public short getG() {
        return g;
    }

    public void setG(short g) {
        this.g = g;
    }

    @JSONField(serialize = false)
    public void getMessage(Message message) {
        message.setString(n);
        message.setByte(t);
        message.setShort(g);
    }
}
