package com.rd.bean.data;

import com.rd.bean.drop.DropData;

/**
 * 商城物品数据
 *
 * @author U-Demon Created on 2017年3月6日 下午3:43:54
 * @version 1.0.0
 */
public class ShopItem {

    //物品
    private DropData g;

    //购买了多少个
    private int n = 0;

    //折扣
    private byte d = 100;

    //价格类型
    private byte pt = 5;

    //价格
    private int pn = 0;

    public DropData getG() {
        return g;
    }

    public void setG(DropData g) {
        this.g = g;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public void addN(int num) {
        this.n += num;
    }

    public byte getPt() {
        return pt;
    }

    public void setPt(byte pt) {
        this.pt = pt;
    }

    public int getPn() {
        return pn;
    }

    public void setPn(int pn) {
        this.pn = pn;
    }

    public byte getD() {
        return d;
    }

    public void setD(int d) {
        this.d = (byte) d;
    }

}
