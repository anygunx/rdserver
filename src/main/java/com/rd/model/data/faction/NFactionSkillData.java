package com.rd.model.data.faction;

import com.rd.bean.drop.DropData;

import java.util.List;

public class NFactionSkillData {


    private int id;
    private byte pos;
    private short level;
    private List<DropData> cost_item;
    private int[] attr;

    public NFactionSkillData(int id, byte pos, List<DropData> cost_item, int[] attr) {
        this.id = id;
        this.pos = pos;
        this.cost_item = cost_item;
        this.attr = attr;

    }

    public int getId() {
        return id;
    }

    public byte getPos() {
        return pos;
    }

    public short getLevel() {
        return level;
    }

    public List<DropData> getCost_item() {
        return cost_item;
    }

    public int[] getAttr() {
        return attr;
    }

}
