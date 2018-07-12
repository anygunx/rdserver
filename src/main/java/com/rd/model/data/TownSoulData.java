package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class TownSoulData {

    private byte level;

    private DropData cost;

    private DropData compose;

    private DropData decompose;

    private int[] attribute;

    private int holyPower;

    public TownSoulData(byte level, DropData cost, DropData compose, DropData decompose, int[] attribute, int holyPower) {
        this.level = level;
        this.cost = cost;
        this.compose = compose;
        this.decompose = decompose;
        this.attribute = attribute;
        this.holyPower = holyPower;
    }

    public byte getLevel() {
        return level;
    }

    public DropData getCost() {
        return cost;
    }

    public DropData getCompose() {
        return compose;
    }

    public DropData getDecompose() {
        return decompose;
    }

    public int[] getAttribute() {
        return attribute;
    }

    public int getHolyPower() {
        return holyPower;
    }
}
