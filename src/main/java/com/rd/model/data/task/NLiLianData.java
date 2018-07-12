package com.rd.model.data.task;

import com.rd.bean.drop.DropData;

import java.util.List;

public class NLiLianData {

    private int id;
    private int needExp;

    public int getId() {
        return id;
    }

    public int getNeedExp() {
        return needExp;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public int[] getAttr() {
        return attr;
    }

    private List<DropData> rewards;
    private int[] attr;


    public NLiLianData(int id, int needExp, List<DropData> rewards, int[] attr) {
        this.id = id;
        this.needExp = needExp;
        this.rewards = rewards;
        this.attr = attr;
    }


}
