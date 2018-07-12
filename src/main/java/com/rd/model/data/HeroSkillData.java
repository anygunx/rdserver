package com.rd.model.data;

/**
 * @author ---
 * @version 1.0
 * @date 2018年4月19日上午10:34:54
 */
public class HeroSkillData {

    private short id;

    private short needLevel;

    private byte atkNum;

    private byte atkType;

    private float hurtRate;

    private short hurtFix;

    private short buff;

    public HeroSkillData(short id, short needLevel, byte atkNum, byte atkType, float hurtRate, short hurtFix) {
        this.id = id;
        this.needLevel = needLevel;
        this.atkNum = atkNum;
        this.atkType = atkType;
        this.hurtRate = hurtRate;
        this.hurtFix = hurtFix;
    }

    public short getId() {
        return id;
    }

    public short getNeedLevel() {
        return needLevel;
    }

    public byte getAtkNum() {
        return atkNum;
    }

    public byte getAtkType() {
        return atkType;
    }

    public float getHurtRate() {
        return hurtRate;
    }

    public short getHurtFix() {
        return hurtFix;
    }
}
