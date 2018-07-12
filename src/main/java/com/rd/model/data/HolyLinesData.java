package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class HolyLinesData {

    private final byte id;
    private final byte pos;
    private final byte level;
    private final byte stageLimit;
    private final DropData combineCost;
    private final DropData transformCost;
    private final int[] attr;

    public HolyLinesData(byte id, byte pos, byte level, byte stageLimit, DropData combineCost, DropData transformCost, int[] attr) {
        this.id = id;
        this.pos = pos;
        this.level = level;
        this.stageLimit = stageLimit;
        this.combineCost = combineCost;
        this.transformCost = transformCost;
        this.attr = attr;
    }

    public byte getId() {
        return id;
    }

    public byte getPos() {
        return pos;
    }

    public byte getLevel() {
        return level;
    }

    public byte getStageLimit() {
        return stageLimit;
    }

    public DropData getCombineCost() {
        return combineCost;
    }

    public DropData getTransformCost() {
        return transformCost;
    }

    public int[] getAttr() {
        return attr;
    }

}
