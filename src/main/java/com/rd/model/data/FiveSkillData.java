package com.rd.model.data;

public class FiveSkillData {

    private byte id;

    private short lv;

    private int[] attr;

    public FiveSkillData(byte id, short lv, int[] attr) {
        this.id = id;
        this.lv = lv;
        this.attr = attr;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public short getLv() {
        return lv;
    }

    public void setLv(short lv) {
        this.lv = lv;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

}
