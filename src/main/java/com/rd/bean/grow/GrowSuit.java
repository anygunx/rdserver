package com.rd.bean.grow;

import com.alibaba.fastjson.annotation.JSONField;
import com.rd.net.message.Message;

/**
 * @author ---
 * @version 1.0
 * @date 2018年5月17日下午9:07:00
 */
public class GrowSuit {

    @JSONField(name = "l")
    private byte level = 1;

    @JSONField(name = "e")
    private short exp;

    @JSONField(name = "x")
    private short expLimit;

    @JSONField(name = "s")
    private byte[] skill;

    @JSONField(name = "p")
    private byte pill;

    @JSONField(name = "q")
    private short[] equip = new short[4];

    public GrowSuit() {

    }

    public GrowSuit(int skillNum) {
        this.skill = new byte[skillNum];
        this.skill[0] = 1;
    }

    public void addExp(int exp) {
        this.exp += exp;
    }

    public void addExpLimit(int exp) {
        this.expLimit += exp;
    }

    public void getMessage(Message message) {
        message.setByte(level);
        message.setShort(exp);
        message.setShort(expLimit);
        message.setByte(skill.length);
        for (byte kill : skill) {
            message.setByte(kill);
        }
        message.setByte(pill);
        for (short equip : equip) {
            message.setShort(equip);
        }
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public short getExp() {
        return exp;
    }

    public void setExp(short exp) {
        this.exp = exp;
    }

    public short getExpLimit() {
        return expLimit;
    }

    public void setExpLimit(short expLimit) {
        this.expLimit = expLimit;
    }

    public byte[] getSkill() {
        return skill;
    }

    public void setSkill(byte[] skill) {
        this.skill = skill;
    }

    public byte getPill() {
        return pill;
    }

    public void setPill(byte pill) {
        this.pill = pill;
    }

    public short[] getEquip() {
        return equip;
    }

    public void setEquip(short[] equip) {
        this.equip = equip;
    }

}
