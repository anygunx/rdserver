package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class DomLvData {

    private byte pos;

    private short lv;

    private DropData cost;

    private int rate;

    private int[] attr;

    public byte getPos() {
        return pos;
    }

    public void setPos(byte pos) {
        this.pos = pos;
    }

    public short getLv() {
        return lv;
    }

    public void setLv(short lv) {
        this.lv = lv;
    }

    public DropData getCost() {
        return cost;
    }

    public void setCost(DropData cost) {
        this.cost = cost;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

}
