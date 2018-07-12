package com.rd.combat;

import com.rd.enumeration.EAttr;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月6日下午4:27:07
 */
public class CombatEffect {

    /**
     * 伤害倍率
     **/
    private int hurtRate;

    /**
     * 伤害反弹
     **/
    private int reboundHurtRate;

    /**
     * 属性提升倍率
     **/
    private int[] attrRate = new int[EAttr.SIZE];

    /**
     * 属性整体倍率影响
     **/
    private int allAttrRate;

    /**
     * 连续攻击
     **/
    private byte serialAtk;

    /**
     * 效果状态
     **/
    private byte state;

    /**
     * 吸血
     **/
    private int blood;

    /**
     * 提升技能%伤害
     **/
    private double skillHurtUp;

    /**
     * 提升技能%暴击
     **/
    private double skillCritUp;

    /**
     * 反击技能
     **/
    private short counterSkill;

    //private

    public CombatEffect() {

    }

    public int getHurtRate() {
        return hurtRate;
    }

    public void addHurtRate(int hurtRate) {
        this.hurtRate += hurtRate;
    }

    public int getReboundHurtRate() {
        return reboundHurtRate;
    }

    public void addReboundHurtRate(int reboundHurtRate) {
        this.reboundHurtRate += reboundHurtRate;
    }

    public int[] getAttrRate() {
        return attrRate;
    }

    public void addAttrRate(EAttr attr, int value) {
        this.attrRate[attr.ordinal()] += value;
    }

    public int getAllAttrRate() {
        return allAttrRate;
    }

    public void addAllAttrRate(int allAttrRate) {
        this.allAttrRate += allAttrRate;
    }

    public byte getSerialAtk() {
        return serialAtk;
    }

    public void setSerialAtk(byte serialAtk) {
        this.serialAtk = serialAtk;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public int getBlood() {
        return blood;
    }

    public void addBlood(int blood) {
        this.blood += blood;
    }

    public double getSkillHurtUp() {
        return skillHurtUp;
    }

    public void addSkillHurtUp(double skillHurtUp) {
        this.skillHurtUp += skillHurtUp;
    }

    public double getSkillCritUp() {
        return skillCritUp;
    }

    public void addSkillCritUp(double skillCritUp) {
        this.skillCritUp += skillCritUp;
    }

    public short getCounterSkill() {
        return counterSkill;
    }

    public void setCounterSkill(short counterSkill) {
        this.counterSkill = counterSkill;
    }

    public void reset() {
        hurtRate = 0;
        reboundHurtRate = 0;
        attrRate = new int[EAttr.SIZE];
        allAttrRate = 0;
        serialAtk = 0;
        state = 0;
        blood = 0;
        skillHurtUp = 0;
        skillCritUp = 0;
        counterSkill = 0;
    }
}
