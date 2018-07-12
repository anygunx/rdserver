package com.rd.bean.drop;

import com.rd.common.goods.EGoodsType;
import com.rd.net.message.Message;

public class DropData {

    /**
     * type 类型
     */
    private byte t;
    /**
     * goodsId 物品ID
     */
    private short g;
    /**
     * 品质
     */
    private byte q;
    /**
     * number 物品数量
     */
    private int n;

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

    public byte getQ() {
        return q;
    }

    public void setQ(byte q) {
        this.q = q;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public DropData() {
        this.t = 0;
        this.g = 0;
        this.q = 0;
        this.n = 0;
    }

    public DropData(byte type, short goodsId, byte quality, int num) {
        this.t = type;
        this.g = goodsId;
        this.q = quality;
        this.n = num;
    }

    public DropData(EGoodsType type, int goodsId, int num) {
        this.t = type.getId();
        this.g = (short) goodsId;
        this.q = 0;
        this.n = num;
    }

    public DropData(byte type, int goodsId, int num) {
        this.t = type;
        this.g = (short) goodsId;
        this.q = 0;
        this.n = num;
    }

    public void getMessage(Message message) {
        message.setByte(t);
        message.setShort(g);
        message.setByte(q);
        message.setInt(n);
    }

    public DropData createCopy() {
        return new DropData(t, g, q, n);
    }

    @Override
    public String toString() {
        return "DropData [t=" + t + ", g=" + g + ", q=" + q + ", n=" + n + "]";
    }

}
