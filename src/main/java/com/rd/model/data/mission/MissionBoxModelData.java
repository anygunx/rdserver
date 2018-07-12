package com.rd.model.data.mission;

import com.google.common.collect.ImmutableList;
import com.rd.bean.drop.DropData;

import java.util.List;

/**
 * Created by XingYun on 2017/11/2.
 */
public class MissionBoxModelData {
    private byte id;
    private int score;
    private List<DropData> rewardList;

    public MissionBoxModelData(byte id, int score, List<DropData> rewardList) {
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
