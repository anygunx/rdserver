package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class ShareModelData {

    private byte id;

    private byte times;

    private List<DropData> rewardList;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getTimes() {
        return times;
    }

    public void setTimes(byte times) {
        this.times = times;
    }

    public List<DropData> getRewardList() {
        return rewardList;
    }

    public void setRewardList(List<DropData> rewardList) {
        this.rewardList = rewardList;
    }
}
