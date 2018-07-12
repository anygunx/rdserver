package com.rd.bean.grow;

import com.rd.model.data.GrowSeedData;
import com.rd.net.message.Message;

/**
 * @author ---
 * @version 1.0
 * @date 2018年5月3日下午8:37:28
 */
public class GrowSeed {

    private short id;

    private String name = "";

    private short level = 1;

    private short exp;

    private short expLimit;

    private byte flyUp;

    private short flyUpExp;

    private short aptitude;

    private short[][] skillPassive = {};

    private short[] washSkill = {};

    //洗炼星级
    private byte washStar = 10;

    private byte starUp = 1;

    public GrowSeed() {

    }

    public GrowSeed(short id, GrowSeedData data) {
        this.id = id;
        if (data.getPassiveSkill() != null) {
            int length = data.getPassiveSkill().length;
            skillPassive = new short[length][2];
            washSkill = new short[length];
            for (int i = 0; i < data.getPassiveSkill().length; ++i) {
                skillPassive[i][1] = data.getPassiveSkill()[i];
            }
        }
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public short getExp() {
        return exp;
    }

    public void setExp(short expLevel) {
        this.exp = expLevel;
    }

    public short getExpLimit() {
        return expLimit;
    }

    public void setExpLimit(short expLimit) {
        this.expLimit = expLimit;
    }

    public byte getFlyUp() {
        return flyUp;
    }

    public void setFlyUp(byte flyUp) {
        this.flyUp = flyUp;
    }

    public short getFlyUpExpLevel() {
        return flyUpExp;
    }

    public void setFlyUpExpLevel(short flyUpExp) {
        this.flyUpExp = flyUpExp;
    }

    public short getAptitude() {
        return aptitude;
    }

    public void setAptitude(short aptitude) {
        this.aptitude = aptitude;
    }

    public short[][] getSkillPassive() {
        return skillPassive;
    }

    public void setSkillPassive(short[][] skillPassive) {
        this.skillPassive = skillPassive;
    }

    public short[] getWashSkill() {
        return washSkill;
    }

    public void setWashSkill(short[] washSkill) {
        this.washSkill = washSkill;
    }

    public void getMessage(Message message) {
        message.setShort(id);
        message.setString(name);
        message.setShort(level);
        message.setShort(exp);
        message.setShort(expLimit);
        message.setByte(flyUp);
        message.setShort(flyUpExp);
        message.setShort(aptitude);
        message.setByte(starUp);
        message.setByte(skillPassive.length);
        for (short[] sp : skillPassive) {
            message.setShort(sp[0]);
            message.setShort(sp[1]);
        }
        for (short wash : washSkill) {
            message.setShort(wash);
        }
        message.setByte(washStar);
    }

    public void addLevel() {
        ++this.level;
    }

    public void addAptitude() {
        ++this.aptitude;
    }

    public void addExp(byte exp) {
        this.exp += exp;
    }

    public void addExpLimit(byte exp) {
        this.expLimit += exp;
    }

    public void addFlyUpExp(byte exp) {
        this.flyUpExp += exp;
    }

    public void addFlyUp() {
        ++this.flyUp;
    }

    public byte getWashStar() {
        return washStar;
    }

    public void setWashStar(byte washStar) {
        this.washStar = washStar;
    }

    public byte getStarUp() {
        return starUp;
    }

    public void setStarUp(byte starUp) {
        this.starUp = starUp;
    }


}
