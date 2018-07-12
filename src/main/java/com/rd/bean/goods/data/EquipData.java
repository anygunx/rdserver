package com.rd.bean.goods.data;

public class EquipData extends GoodsData {

    private short level;

    private byte occupation;

    private byte position;

    private String name;

    private int attr[];

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public byte getOccupation() {
        return occupation;
    }

    public void setOccupation(byte occupation) {
        this.occupation = occupation;
    }

    public byte getPosition() {
        return position;
    }

    public void setPosition(byte position) {
        this.position = position;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EquipData() {

    }
}
