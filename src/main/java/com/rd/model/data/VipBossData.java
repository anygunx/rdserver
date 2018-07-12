package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

/**
 * BOSS之家BOSSdata
 *
 * @author lwq
 */
public class VipBossData {

    private short id;

    private int limitLv;

    private short modelId;

    private List<DropData> rewards;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public int getLimitLv() {
        return limitLv;
    }

    public void setLimitLv(int limitLv) {
        this.limitLv = limitLv;
    }

    public short getModelId() {
        return modelId;
    }

    public void setModelId(short modelId) {
        this.modelId = modelId;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
    }


}
