package com.rd.bean.goods.data;

import com.rd.bean.drop.DropData;

public class CallBackGoodsData {
    private short id;
    private DropData item;
    private DropData reward;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public DropData getItem() {
        return item;
    }

    public void setItem(DropData item) {
        this.item = item;
    }

    public DropData getReward() {
        return reward;
    }

    public void setReward(DropData reward) {
        this.reward = reward;
    }
}
