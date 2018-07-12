package com.rd.model.data.fabao;

import com.rd.bean.drop.DropData;

public class NPulseData {
    private short id;

    private DropData cost;
    private int[] attr;

    public int[] getAttr() {
        return attr;
    }

    public short getId() {
        return id;
    }


    public DropData getCost() {
        return cost;
    }

    public NPulseData(short id, DropData cost, int[] attr) {
        this.id = id;

        this.cost = cost;
        this.attr = attr;
    }

}
