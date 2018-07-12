package com.rd.model.data;

import com.rd.bean.drop.DropData;

/**
 * 元魂数据
 *
 * @author Created by U-Demon on 2016年11月10日 下午5:21:32
 * @version 1.0.0
 */
public class SpiritModelData {

    private short id;

    private short lv;

    private DropData cost;

    private byte type;

    private DropData fenjie;

    private byte pinzhi;

    private int[] attr;

    public String getTQL() {
        return this.type + "_" + this.pinzhi + "_" + this.lv;
    }

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

    public DropData getCost() {
        return cost;
    }

    public void setCost(DropData cost) {
        this.cost = cost;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public DropData getFenjie() {
        return fenjie;
    }

    public void setFenjie(DropData fenjie) {
        this.fenjie = fenjie;
    }

    public byte getPinzhi() {
        return pinzhi;
    }

    public void setPinzhi(byte pinzhi) {
        this.pinzhi = pinzhi;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

}
