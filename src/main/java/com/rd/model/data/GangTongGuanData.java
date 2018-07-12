package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class GangTongGuanData {

    private short id;

    private short guanqia;

    private List<DropData> rewards;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getGuanqia() {
        return guanqia;
    }

    public void setGuanqia(short guanqia) {
        this.guanqia = guanqia;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
    }

}
