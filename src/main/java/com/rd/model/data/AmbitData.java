package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class AmbitData {

    private byte stage;

    private byte star;

    private DropData cost;

    private short exp;

    private int[] attr;

    public AmbitData(byte stage, byte star, DropData cost, short exp, int[] attr) {
        this.stage = stage;
        this.star = star;
        this.cost = cost;
        this.exp = exp;
        this.attr = attr;
    }

    public byte getStage() {
        return stage;
    }

    public byte getStar() {
        return star;
    }

    public DropData getCost() {
        return cost;
    }

    public short getExp() {
        return exp;
    }

    public int[] getAttr() {
        return attr;
    }
}
