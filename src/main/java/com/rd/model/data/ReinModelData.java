package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class ReinModelData {

    private short id;

    private DropData cost;

    private int[] attr;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
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
