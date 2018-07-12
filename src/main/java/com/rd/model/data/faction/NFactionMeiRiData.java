package com.rd.model.data.faction;

import com.rd.bean.drop.DropData;

import java.util.List;

public class NFactionMeiRiData {

    private int id;
    private int num;
    private List<DropData> rewards;

    public NFactionMeiRiData(int id, int num, List<DropData> rewards) {
        this.id = id;
        this.num = num;
        this.rewards = rewards;


    }

    public int getId() {
        return id;
    }

    public int getNum() {
        return num;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

}
