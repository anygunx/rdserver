package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class BossCitData {

    private short id;

    private int limitLv;

    private int fuhuoTime;

    private short modelId;

    private List<DropData> rewards;

    private short redLev;

    private short ownredpro;

    private short joinredpro;

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

    public short getRedLev() {
        return redLev;
    }

    public void setRedLev(short redLev) {
        this.redLev = redLev;
    }

    public short getOwnredpro() {
        return ownredpro;
    }

    public void setOwnredpro(short ownredpro) {
        this.ownredpro = ownredpro;
    }

    public short getJoinredpro() {
        return joinredpro;
    }

    public void setJoinredpro(short joinredpro) {
        this.joinredpro = joinredpro;
    }
}
