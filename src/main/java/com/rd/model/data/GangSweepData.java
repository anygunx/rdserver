package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class GangSweepData {

    private short id;

    private short chance;

    private List<DropData> rewards;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getChance() {
        return chance;
    }

    public void setChance(short chance) {
        this.chance = chance;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
    }

}
