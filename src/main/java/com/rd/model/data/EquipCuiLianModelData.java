package com.rd.model.data;

import com.rd.bean.drop.DropData;

/**
 * 装备淬炼
 * Created by XingYun on 2017/5/6.
 */
public class EquipCuiLianModelData {
    private final byte level;
    private final int exp;
    private final int addPercent;
    private final DropData cost;

    public EquipCuiLianModelData(byte level, int exp, int addPercent, DropData cost) {
        this.level = level;
        this.exp = exp;
        this.addPercent = addPercent;
        this.cost = cost;
    }

    public byte getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getAddPercent() {
        return addPercent;
    }

    public DropData getCost() {
        return cost;
    }
}
