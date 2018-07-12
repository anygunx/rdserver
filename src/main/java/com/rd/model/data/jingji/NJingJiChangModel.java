package com.rd.model.data.jingji;

import com.rd.bean.drop.DropData;

import java.util.List;

public class NJingJiChangModel {
    private int id;
    private int rank_min;
    private int rank_max;
    private List<DropData> rewards;

    public NJingJiChangModel(int id, int rank_min, int rank_max, List<DropData> rewards) {
        this.id = id;
        this.rank_min = rank_min;
        this.rank_max = rank_max;
    }

    public int getId() {
        return id;
    }

    public int getRank_min() {
        return rank_min;
    }

    public int getRank_max() {
        return rank_max;
    }

    public List<DropData> getRewards() {
        return rewards;
    }


}
