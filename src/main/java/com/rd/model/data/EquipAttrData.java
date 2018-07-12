package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class EquipAttrData {

    private short level;

    private byte position;

    private DropData costData;

    private int[] attr;

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public byte getPosition() {
        return position;
    }

    public void setPosition(byte position) {
        this.position = position;
    }

    public DropData getCostData() {
        return costData;
    }

    public void setCostData(DropData costData) {
        this.costData = costData;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }
}
