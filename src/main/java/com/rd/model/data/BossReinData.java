package com.rd.model.data;

public class BossReinData {

    private short id;

    private int limitLv;

    private String startTime;

    private int durationTime;

    private int fightCD;

    private short modelId;

    private short rewardWin;

    private short rewardLose;

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(int durationTime) {
        this.durationTime = durationTime;
    }

    public int getFightCD() {
        return fightCD;
    }

    public void setFightCD(int fightCD) {
        this.fightCD = fightCD;
    }

    public short getModelId() {
        return modelId;
    }

    public void setModelId(short modelId) {
        this.modelId = modelId;
    }

    public short getRewardWin() {
        return rewardWin;
    }

    public void setRewardWin(short rewardWin) {
        this.rewardWin = rewardWin;
    }

    public short getRewardLose() {
        return rewardLose;
    }

    public void setRewardLose(short rewardLose) {
        this.rewardLose = rewardLose;
    }

}
