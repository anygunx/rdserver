package com.rd.bean.fighter;

public class HeroData {

    private byte id;

    private short[] attr;

    private byte[] arrSkillId;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public short[] getAttr() {
        return attr;
    }

    public void setAttr(short[] attr) {
        this.attr = attr;
    }

    public byte[] getArrSkillId() {
        return arrSkillId;
    }

    public void setArrSkillId(byte[] arrSkillId) {
        this.arrSkillId = arrSkillId;
    }
}
