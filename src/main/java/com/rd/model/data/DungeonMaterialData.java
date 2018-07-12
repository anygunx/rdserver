package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class DungeonMaterialData {

    private short bossDrop;

    private DropData reward;

    public DungeonMaterialData(short bossDrop, DropData dropData) {
        this.bossDrop = bossDrop;
        this.reward = dropData;
    }

    public short getBossDrop() {
        return bossDrop;
    }

    public DropData getReward() {
        return reward;
    }
}
