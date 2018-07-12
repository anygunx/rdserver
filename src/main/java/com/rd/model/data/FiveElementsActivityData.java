package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class FiveElementsActivityData {

    private byte id;

    private DropData cost;

    private List<DropData> reward;

    private byte rate;//倍率

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public DropData getCost() {
        return cost;
    }

    public void setCost(DropData cost) {
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
