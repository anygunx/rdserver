package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class HolyGoodsData {

    private byte stage;

    private byte star;

    private short exp;

    private DropData cost;

    private int[] attr;

    public HolyGoodsData(byte stage, byte star, short exp, DropData cost, int[] attr) {
        this.stage = stage;
        this.star = star;
        this.exp = exp;
        this.cost = cost;
        this.attr = attr;
    }

    public byte getStage() {
        return stage;
    }

    public byte getStar() {
        return star;
    }

    public short getExp() {
        return exp;
    }

    public DropData getCost() {
        return cost;
    }

    public int[] getAttr() {
        return attr;
    }
}
