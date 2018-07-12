package com.rd.model.data;

/**
 * @author ---
 * @version 1.0
 * @date 2018年5月9日下午12:52:44
 */
public class SkillActiveData {

    private short id;

    //技能类型 1：标准伤害技能 2:带buff伤害技能
    private byte type;

    //攻击类型 0：近身 1:远程
    private byte atkType;

    //百分比伤害加成
    private double percentHurt;

    //固定伤害加成
    private int fixedHurt;

    //buff
    private short buff;

    private byte atkNum = 1;

    public SkillActiveData(short id, byte type, byte atkType, double percentHurt, int fixedHurt, short buff) {
        this.id = id;
        this.type = type;
        this.atkType = atkType;
        this.percentHurt = percentHurt;
        this.fixedHurt = fixedHurt;
        this.buff = buff;
    }

    public SkillActiveData(short id, byte type, byte atkType, double percentHurt, int fixedHurt, short buff, byte atkNum) {
        this.id = id;
        this.type = type;
        this.atkType = atkType;
        this.percentHurt = percentHurt;
        this.fixedHurt = fixedHurt;
        this.buff = buff;
        this.atkNum = atkNum;
    }

    public short getId() {
        return id;
    }

    public byte getType() {
        return type;
    }

    public byte getAtkType() {
        return atkType;
    }

    public double getPercentHurt() {
        return percentHurt;
    }

    public int getFixedHurt() {
        return fixedHurt;
    }

    public short getBuff() {
        return buff;
    }

    public byte getAtkNum() {
        return atkNum;
    }
}
