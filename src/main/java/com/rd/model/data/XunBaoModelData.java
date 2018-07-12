package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class XunBaoModelData {

    private short id;

    private int oneKeyCount;

    private DropData cost;

    private DropData costOneKey;

    private DropData reward;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public int getOneKeyCount() {
        return oneKeyCount;
    }

    public void setOneKeyCount(int oneKeyCount) {
        this.oneKeyCount = oneKeyCount;
    }

    public DropData getCost() {
        return cost;
    }

    public void setCost(DropData cost) {
        this.cost = cost;
    }

    public DropData getCostOneKey() {
        return costOneKey;
    }

    public void setCostOneKey(DropData costOneKey) {
        this.costOneKey = costOneKey;
    }

    public DropData getReward() {
        return reward;
    }

    public void setReward(DropData reward) {
        this.reward = reward;
    }

}
