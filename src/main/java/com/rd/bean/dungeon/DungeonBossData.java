package com.rd.bean.dungeon;

import com.rd.bean.drop.DropData;

import java.util.List;

public class DungeonBossData {

    private byte id;

    private short bossId;

    private List<DropData> rewards;

    private short[] qualityChance;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public short getBossId() {
        return bossId;
    }

    public void setBossId(short bossId) {
        this.bossId = bossId;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
    }

    public short[] getQualityChance() {
        return qualityChance;
    }

    public void setQualityChance(short[] qualityChance) {
        this.qualityChance = qualityChance;
    }
}
