package com.rd.model.data.skill;

public class NSkillData {
    private short id;
    private short level;

    private byte pos;

    private byte typeCost;

    public byte getTypeCost() {
        return typeCost;
    }

    public int getGoodId() {
        return goodId;
    }

    public int getNum() {
        return num;
    }

    private int goodId;
    private int num;

    public short getId() {
        return id;
    }

    public short getLevel() {
        return level;
    }


    public byte getPos() {
        return pos;
    }

    public NSkillData(short id, short level, byte pos, byte typeCost, int goodId, int num) {
        this.id = id;
        this.level = level;
        this.pos = pos;
        this.typeCost = typeCost;
        this.goodId = goodId;
        this.num = num;

    }


}
