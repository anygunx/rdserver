package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class DomRankData {

    private byte pos;

    private short rank;

    private DropData cost;

    private int rate;

    private int[] attr;

    public byte getPos() {
        return pos;
    }

    public void setPos(byte pos) {
        this.pos = pos;
    }

    public short getRank() {
        return rank;
    }

    public void setRank(short rank) {
        this.rank = rank;
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
