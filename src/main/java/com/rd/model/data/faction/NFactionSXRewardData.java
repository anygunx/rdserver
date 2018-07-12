package com.rd.model.data.faction;

import com.rd.bean.drop.DropData;

import java.util.List;

public class NFactionSXRewardData {


    private int id;
    private int needFrag;
    private List<DropData> rewards;

    public int getId() {
        return id;
    }

    public int getNeedFrag() {
        return needFrag;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public NFactionSXRewardData(int id, List<DropData> rewards, int needFrag) {
        this.id = id;
        this.needFrag = needFrag;
        this.rewards = rewards;

    }


}
