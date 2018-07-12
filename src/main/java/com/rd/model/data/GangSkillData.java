package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class GangSkillData {

    private byte id;

    private short level;

    private List<DropData> costList;

    private int[] attr;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public List<DropData> getCostList() {
        return costList;
    }

    public void setCostList(List<DropData> costList) {
        this.costList = costList;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }
}
