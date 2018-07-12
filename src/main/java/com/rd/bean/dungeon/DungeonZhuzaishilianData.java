package com.rd.bean.dungeon;

import com.google.common.collect.ImmutableList;
import com.rd.bean.drop.DropData;

import java.util.List;


public class DungeonZhuzaishilianData {
    private short id;

    private short fightId;

    private List<DropData> battleReward;

    public DungeonZhuzaishilianData(short id, short fightId, List<DropData> battleReward) {
        this.id = id;
        this.fightId = fightId;
        this.battleReward = ImmutableList.copyOf(battleReward);
    }

    public short getId() {
        return id;
    }

    public short getFightId() {
        return fightId;
    }

    public List<DropData> getBattleReward() {
        return battleReward;
    }

}
