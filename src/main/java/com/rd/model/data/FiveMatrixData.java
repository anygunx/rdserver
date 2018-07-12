package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class FiveMatrixData {

    private byte id;

    private byte ordelv;

    private short level;

    private DropData cost;

    private int[] attribute;

    public FiveMatrixData(byte id, byte ordelv, short level, DropData cost, int[] attribute) {
        this.id = id;
        this.ordelv = ordelv;
        this.level = level;
        this.cost = cost;
        this.attribute = attribute;
    }

    public byte getId() {
        return id;
    }

    public byte getOrdelv() {
        return ordelv;
    }

    public short getLevel() {
        return level;
    }

    public DropData getCost() {
        return cost;
    }

    public int[] getAttribute() {
        return attribute;
    }
}
