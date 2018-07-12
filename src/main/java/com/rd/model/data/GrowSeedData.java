package com.rd.model.data;

import com.rd.bean.drop.DropData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年5月3日下午8:20:09
 */
public class GrowSeedData {

    private short id;

    private byte quality;

    private DropData cost;

    private byte levelUp;

    private byte up;

    private byte aptitude;

    private byte activeSkill;

    private byte atkNum;

    private short[] passiveSkill;

    public GrowSeedData(short id, byte quality, DropData cost, byte levelUp, byte up, byte aptitude, byte activeSkill, byte atkNum, short[] passiveSkill) {
        this.id = id;
        this.quality = quality;
        this.cost = cost;
        this.levelUp = levelUp;
        this.up = up;
        this.aptitude = aptitude;
        this.activeSkill = activeSkill;
        this.atkNum = atkNum;
        this.passiveSkill = passiveSkill;
    }

    public short getId() {
        return id;
    }

    public byte getQuality() {
        return quality;
    }

    public DropData getCost() {
        return cost;
    }

    public byte getLevelUp() {
        return levelUp;
    }

    public byte getUp() {
        return up;
    }

    public byte getAptitude() {
        return aptitude;
    }

    public byte getActiveSkill() {
        return activeSkill;
    }

    public byte getAtkNum() {
        return atkNum;
    }

    public short[] getPassiveSkill() {
        return passiveSkill;
    }

}
