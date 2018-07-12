package com.rd.model.data.richang;

import com.rd.bean.drop.DropData;

import java.util.List;

public class NZhongKuiData {

    private int id;
    private short level;
    private int exp;
    private List<DropData> reward;

    public NZhongKuiData(int id, short level, List<DropData> reward, int exp) {
        this.id = id;
        this.level = level;
        this.reward = reward;
        this.exp = exp;
    }

    public int getExp() {
        return exp;
    }

    public int getId() {
        return id;
    }

    public short getLevel() {
        return level;
    }

    public List<DropData> getReward() {
        return reward;
    }

}
