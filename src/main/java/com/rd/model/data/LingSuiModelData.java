package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class LingSuiModelData {
    private byte id;
    private byte level;
    private List<DropData> cost;
    private short needlv;
    private byte num;
    private int[] attr;
    private String name;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

    public short getNeedlv() {
        return needlv;
    }

    public void setNeedlv(short needlv) {
        this.needlv = needlv;
    }

    public byte getNum() {
        return num;
    }

    public void setNum(byte num) {
        this.num = num;
    }

    public List<DropData> getCost() {
        return cost;
    }

    public void setCost(List<DropData> cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LingSuiModelData(byte id, byte level, short needlv, byte num, List<DropData> cost, int[] attr, String name) {
        this.id = id;
        this.level = level;
        this.cost = cost;
        this.needlv = needlv;
        this.num = num;
        this.attr = attr;
        this.name = name;
    }

}
