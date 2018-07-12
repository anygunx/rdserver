package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class EscortModelData {

    private byte id;

    private int keeptime;

    private int rate;

    private List<DropData> reward;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public int getKeeptime() {
        return keeptime;
    }

    public void setKeeptime(int keeptime) {
        this.keeptime = keeptime;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public List<DropData> getReward() {
        return reward;
    }

    public void setReward(List<DropData> reward) {
        this.reward = reward;
    }

}
