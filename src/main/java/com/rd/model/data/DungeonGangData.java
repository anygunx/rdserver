package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class DungeonGangData {

    private short id;

    private short fightId;

    private List<DropData> rewards;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getFightId() {
        return fightId;
    }

    public void setFightId(short fightId) {
        this.fightId = fightId;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
    }

}
