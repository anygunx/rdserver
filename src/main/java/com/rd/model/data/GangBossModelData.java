package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class GangBossModelData {

    private byte id;

    private List<DropData> rewards;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
    }

}
