package com.rd.game.data;

import com.alibaba.fastjson.annotation.JSONField;
import com.rd.bean.drop.DropData;

/**
 * 镇魂宝库转盘记录
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月12日下午4:09:08
 */
public class TownSoulTurntableRecord {

    @JSONField(name = "n")
    private String name;

    @JSONField(name = "r")
    private DropData reward;

    public TownSoulTurntableRecord() {

    }

    public TownSoulTurntableRecord(String name, DropData reward) {
        this.name = name;
        this.reward = reward;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DropData getReward() {
        return reward;
    }

    public void setReward(DropData reward) {
        this.reward = reward;
    }
}
