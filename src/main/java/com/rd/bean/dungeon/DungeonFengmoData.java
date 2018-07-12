package com.rd.bean.dungeon;

import com.google.common.collect.ImmutableList;
import com.rd.bean.drop.DropData;

import java.util.List;

/**
 * 封魔塔数据
 */
public class DungeonFengmoData {
    private final short id;

    private final short fightId;

    private final List<DropData> battleReward;

    private final List<DropData> dailyReward;

    public DungeonFengmoData(short id, short fightId, List<DropData> battleReward, List<DropData> dailyReward) {
        this.id = id;
        this.fightId = fightId;
        this.battleReward = ImmutableList.copyOf(battleReward);
        this.dailyReward = ImmutableList.copyOf(dailyReward);
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

    public List<DropData> getDailyReward() {
        return dailyReward;
    }
}
