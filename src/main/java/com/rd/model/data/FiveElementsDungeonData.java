package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class FiveElementsDungeonData {

    private byte id;

    private short cost;

    private List<DropData> reward;

    private byte rate;//倍率

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public short getCost() {
        return cost;
    }

    public void setCost(short cost) {
        this.cost = cost;
    }

    public List<DropData> getReward() {
        return reward;
    }

    public void setReward(List<DropData> reward) {
        this.reward = reward;
    }

    public byte getRate() {
        return rate;
    }

    public void setRate(byte rate) {
        this.rate = rate;
    }

}
