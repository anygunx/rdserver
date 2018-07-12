package com.rd.model.data.fabao;

import com.rd.bean.drop.DropData;

public class NDanYaoData {
    private byte pos;
    private byte level;

    public byte getLevel() {
        return level;
    }

    private int[] attr;
    private DropData cost;

    public DropData getCost() {
        return cost;
    }

    public int[] getAttr() {
        return attr;
    }

    public NDanYaoData(byte pos, DropData cost, byte level, int[] attr) {
        this.pos = pos;
        this.attr = attr;
        this.cost = cost;
        this.level = level;
    }

    public byte getPos() {
        return pos;
    }
}
