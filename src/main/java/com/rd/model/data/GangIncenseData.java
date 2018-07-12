package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class GangIncenseData {

    private byte id;

    private short gangExp;

    private short donate;

    private DropData cost;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public short getGangExp() {
        return gangExp;
    }

    public void setGangExp(short gangExp) {
        this.gangExp = gangExp;
    }

    public short getDonate() {
        return donate;
    }

    public void setDonate(short donate) {
        this.donate = donate;
    }

    public DropData getCost() {
        return cost;
    }

    public void setCost(DropData cost) {
        this.cost = cost;
    }
}
