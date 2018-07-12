package com.rd.model.data;

import com.rd.bean.drop.DropData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年5月7日下午4:32:38
 */
public class GrowCostData {

    private byte level;

    private DropData cost;

    private int[] attr;

    public GrowCostData(byte level, DropData cost, int[] attr) {
        this.level = level;
        this.cost = cost;
        this.attr = attr;
    }

    public byte getLevel() {
        return level;
    }

    public DropData getCost() {
        return cost;
    }

    public int[] getAttr() {
        return attr;
    }
}
