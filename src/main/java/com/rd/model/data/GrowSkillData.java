package com.rd.model.data;

import com.rd.bean.drop.DropData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年5月8日上午11:07:17
 */
public class GrowSkillData {

    private byte level;

    private byte pos;

    private byte needLevel;

    private DropData cost;

    private int[] attr;

    public GrowSkillData(byte level, byte pos, byte needLevel, DropData cost, int[] attr) {
        this.level = level;
        this.pos = pos;
        this.needLevel = needLevel;
        this.cost = cost;
        this.attr = attr;
    }

    public byte getLevel() {
        return level;
    }

    public byte getPos() {
        return pos;
    }

    public byte getNeedLevel() {
        return needLevel;
    }

    public DropData getCost() {
        return cost;
    }

    public int[] getAttr() {
        return attr;
    }

}
