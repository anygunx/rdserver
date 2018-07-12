package com.rd.model.data;

import com.rd.bean.drop.DropData;

/**
 * 勋章数据模板
 * Created by XingYun on 2017/10/31.
 */
public class MedalModelData {
    private final byte level;
    private final DropData consume;
    private final short[] attr;
    private final short levelLimit;
    private final int achievementLimit;

    public MedalModelData(byte level, DropData consume, short[] attr, short levelLimit, int achievementLimit) {
        this.level = level;
        this.consume = consume;
        this.attr = attr;
        this.levelLimit = levelLimit;
        this.achievementLimit = achievementLimit;
    }

    public byte getLevel() {
        return level;
    }

    public short[] getAttr() {
        return attr;
    }

    public DropData getConsume() {
        return consume;
    }

    public short getLevelLimit() {
        return levelLimit;
    }

    public int getAchievementLimit() {
        return achievementLimit;
    }
}
