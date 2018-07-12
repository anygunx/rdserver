package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class GongFaModelData {

    private short id;

    private byte type;

    private short lv;

    private short rank;

    private DropData cost;

    private int[] attr;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public short getLv() {
        return lv;
    }

    public void setLv(short lv) {
        this.lv = lv;
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

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

}
