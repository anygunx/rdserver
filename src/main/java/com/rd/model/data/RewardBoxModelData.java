package com.rd.model.data;

import com.google.common.collect.ImmutableList;
import com.rd.bean.drop.DropData;

import java.util.List;

/**
 * 通用奖励宝箱模板
 */
public class RewardBoxModelData {
    private final byte id;
    private final int score;
    private final List<DropData> rewardList;

    public RewardBoxModelData(byte id, int score, List<DropData> rewardList) {
        this.id = id;
        this.score = score;
        this.rewardList = ImmutableList.copyOf(rewardList);
    }

    public byte getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public List<DropData> getRewardList() {
        return rewardList;
    }
}
