package com.rd.model.data;

import com.rd.bean.drop.DropData;

/**
 * 镇魂宝库奖励数据
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月10日下午6:49:20
 */
public class TownSoulTreasureRewardData {

    private short time;

    private DropData reward;

    public TownSoulTreasureRewardData(short time, DropData reward) {
        this.time = time;
        this.reward = reward;
    }

    public short getTime() {
        return time;
    }

    public DropData getReward() {
        return reward;
    }
}
