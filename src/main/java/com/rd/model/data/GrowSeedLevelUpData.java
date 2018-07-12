package com.rd.model.data;

import com.rd.bean.drop.DropData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年5月4日下午2:06:28
 */
public class GrowSeedLevelUpData {

    private short level;

    private DropData cost;

    private DropData costLimit;

    private DropData costGold;

    private byte exp;

    private short expMax;

    private int[] attr;

    public GrowSeedLevelUpData(short level, DropData cost, DropData costLimit, DropData costGold, byte exp, short expMax, int[] attr) {
        this.level = level;
        this.cost = cost;
        this.costLimit = costLimit;
        this.costGold = costGold;
        this.exp = exp;
        this.expMax = expMax;
        this.attr = attr;
    }

    public short getLevel() {
        return level;
    }

    public DropData getCost() {
        return cost;
    }

    public DropData getCostLimit() {
        return costLimit;
    }

    public DropData getCostGold() {
        return costGold;
    }

    public byte getExp() {
        return exp;
    }

    public short getExpMax() {
        return expMax;
    }

    public int[] getAttr() {
        return attr;
    }

}
