package com.rd.model.data.mission;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * 卡牌任务组
 */
public class CardMissionGroupModelData {
    private final byte id;
    private final Set<Short> rewards;
    private final long keepTime;
    private final long openTime;

    public CardMissionGroupModelData(byte id, Set<Short> rewards, long keepTime, long openTime) {
        this.id = id;
        this.rewards = ImmutableSet.copyOf(rewards);
        this.keepTime = keepTime;
        this.openTime = openTime;
    }

    public long getOpenTime() {
        return openTime;
    }

    public byte getId() {
        return id;
    }

    public Set<Short> getRewards() {
        return rewards;
    }

    public long getKeepTime() {
        return keepTime;
    }
}
