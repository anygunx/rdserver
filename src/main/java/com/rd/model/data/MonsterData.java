package com.rd.model.data;

/**
 * @author ---
 * @version 1.0
 * @date 2018年4月14日下午5:12:05
 */
public class MonsterData {

    private int id;

    private byte atkNum;

    private short skillId;

    private int[] attr;

    public MonsterData(int id, byte atkNum, short skillId, int[] attr) {
        this.id = id;
        this.atkNum = atkNum;
        this.skillId = skillId;
        this.attr = attr;
    }

    public int getId() {
        return id;
    }

    public byte getAtkNum() {
        return atkNum;
    }

    public short getSkillId() {
        return skillId;
    }

    public int[] getAttr() {
        return attr;
    }
}
