package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class BossMysteryData {

    private short id;

    private int limitLv;

    private int fuhuoTime;

    private short modelId;

    private String levelScope;

    private Short dropid;

    public Short getDropid() {
        return dropid;
    }

    public void setDropid(Short dropid) {
        this.dropid = dropid;
    }

    public String getLevelScope() {
        return levelScope;
    }

    public void setLevelScope(String levelScope) {
        this.levelScope = levelScope;
    }

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

    public int getFuhuoTime() {
        return fuhuoTime;
    }

    public void setFuhuoTime(int fuhuoTime) {
        this.fuhuoTime = fuhuoTime;
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
