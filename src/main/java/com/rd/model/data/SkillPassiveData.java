package com.rd.model.data;

/**
 * @author ---
 * @version 1.0
 * @date 2018年5月9日下午12:53:43
 */
public class SkillPassiveData {

    private short id;

    private byte level;

    private byte quality;

    //0:被动 1：固定
    private byte largeType;

    private byte smallType;

    private int[] attr;

    private short buff;

    public SkillPassiveData(short id, byte level, byte quality, byte largeType, byte smallType, int[] attr, short buff) {
        this.id = id;
        this.level = level;
        this.quality = quality;
        this.largeType = largeType;
        this.smallType = smallType;
        this.attr = attr;
        this.buff = buff;
    }

    public short getId() {
        return id;
    }

    public byte getLevel() {
        return level;
    }

    public byte getQuality() {
        return quality;
    }

    public byte getLargeType() {
        return largeType;
    }

    public byte getSmallType() {
        return smallType;
    }

    public int[] getAttr() {
        return attr;
    }

    public short getBuff() {
        return buff;
    }
}
