package com.rd.model.data;

import com.rd.bean.drop.DropData;

/**
 * @author lwq
 */
public class ZhanWenModelData {

    private short id;

    private byte lv;

    private byte pinzhi;

    private DropData cost;

    private DropData fenjie;

    private int[] attr;

    private byte type;

    private int power;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public byte getLv() {
        return lv;
    }

    public void setLv(byte lv) {
        this.lv = lv;
    }

    public byte getPinzhi() {
        return pinzhi;
    }

    public void setPinzhi(byte pinzhi) {
        this.pinzhi = pinzhi;
    }

    public DropData getCost() {
        return cost;
    }

    public void setCost(DropData cost) {
        this.cost = cost;
    }

    public DropData getFenjie() {
        return fenjie;
    }

    public void setFenjie(DropData fenjie) {
        this.fenjie = fenjie;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getTQL() {
        return this.type + "_" + this.getPinzhi() + "_" + this.getLv();
    }
}
