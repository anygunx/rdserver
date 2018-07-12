package com.rd.model.data;

public class RedModelData {

    private short id;

    private short lv;

    private int cost;

    private byte type;

    private int[] attr;

    private int[] addAttr;

    private String name;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getLv() {
        return lv;
    }

    public void setLv(short lv) {
        this.lv = lv;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

    public int[] getAddAttr() {
        return addAttr;
    }

    public void setAddAttr(int[] addAttr) {
        this.addAttr = addAttr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
