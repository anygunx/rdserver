package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.Map;

/**
 * 镇魂宝库数据
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月10日下午3:11:43
 */
public class TownSoulTreasureData {

    private Map<Short, DropData> reward;

    private Map<Short, TownSoulTreasureRewardData> timeRewardMap;

    public TownSoulTreasureData(Map<Short, DropData> reward, Map<Short, TownSoulTreasureRewardData> timeRewardMap) {
        this.reward = reward;
        this.timeRewardMap = timeRewardMap;
    }

    public Map<Short, DropData> getReward() {
        return reward;
    }

    public Map<Short, TownSoulTreasureRewardData> getTimeRewardMap() {
        return timeRewardMap;
    }
}