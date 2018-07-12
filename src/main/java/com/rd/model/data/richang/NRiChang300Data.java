package com.rd.model.data.richang;

import com.rd.bean.drop.DropData;

import java.util.List;

public class NRiChang300Data {


    private int id;
    private short lv;
    private int[] targets;
    private int[] exps;
    private List<List<DropData>> rewardList;


    public NRiChang300Data(int id, int[] target, int[] exps, List<List<DropData>> rewardList) {
        this.id = id;
        this.targets = target;
        this.exps = exps;
        this.rewardList = rewardList;

    }

    public int getId() {
        return id;
    }

    public short getLv() {
        return lv;
    }

    public int[] getTarget() {
        return targets;
    }

    public int[] getExps() {
        return exps;
    }

    public List<List<DropData>> getRewardList() {
        return rewardList;
    }


}
