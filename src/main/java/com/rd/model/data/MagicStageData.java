package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class MagicStageData {

    private short stage;

    private byte star;

    private int exp;

    private DropData itemCost;

    private byte itemExp;

    private int[] attr;

    private int specialPower;


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

    public DropData getItemCost() {
        return itemCost;
    }

    public void setItemCost(DropData itemCost) {
        this.itemCost = itemCost;
    }

    public byte getItemExp() {
        return itemExp;
    }

    public void setItemExp(byte itemExp) {
        this.itemExp = itemExp;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

    public int getSpecialPower() {
        return specialPower;
    }

    public void setSpecialPower(int specialPower) {
        this.specialPower = specialPower;
    }
}
