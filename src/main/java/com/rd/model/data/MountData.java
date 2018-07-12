package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class MountData {

    private short stage;

    private byte star;

    private int exp;

    private int goldCost;

    private DropData itemCost;

    private short goldExp;

    private short itemExp;

    private int[] attr;

    public short getStage() {
        return stage;
    }

    public void setStage(short stage) {
        this.stage = stage;
    }

    public byte getStar() {
        return star;
    }

    public void setStar(byte star) {
        this.star = star;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getGoldCost() {
        return goldCost;
    }

    public void setGoldCost(int goldCost) {
        this.goldCost = goldCost;
    }

    public DropData getItemCost() {
        return itemCost;
    }

    public void setItemCost(DropData itemCost) {
        this.itemCost = itemCost;
    }

    public short getGoldExp() {
        return goldExp;
    }

    public void setGoldExp(short goldExp) {
        this.goldExp = goldExp;
    }

    public short getItemExp() {
        return itemExp;
    }

    public void setItemExp(short itemExp) {
        this.itemExp = itemExp;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }
}
