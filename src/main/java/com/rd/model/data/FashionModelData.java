package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class FashionModelData {

    private byte id;

    private DropData cost;

    private int time;

    private int[] attr;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public DropData getCost() {
        return cost;
    }

    public void setCost(DropData cost) {
        this.cost = cost;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }
}
