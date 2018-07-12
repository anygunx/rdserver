package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class GangShopModelData {

    private short id;

    private List<DropData> costs;

    private List<DropData> rewards;

    private short limitNum;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public List<DropData> getCosts() {
        return costs;
    }

    public void setCosts(List<DropData> costs) {
        this.costs = costs;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
    }

    public short getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(short limitNum) {
        this.limitNum = limitNum;
    }

    @Override
    public String toString() {
        return "GangShopModelData [id=" + id + ", costs=" + costs + ", rewards=" + rewards + ", limitNum=" + limitNum
                + "]";
    }
}
