package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class FaZhenModelData {
    private short id;
    private byte type;
    private short lev;
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

    public short getLev() {
        return lev;
    }

    public void setLev(short lev) {
        this.lev = lev;
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

    public FaZhenModelData(short id, byte type, short lev, DropData cost, int[] attr) {
        this.id = id;
        this.type = type;
        this.lev = lev;
        this.cost = cost;
        this.attr = attr;
    }

}
