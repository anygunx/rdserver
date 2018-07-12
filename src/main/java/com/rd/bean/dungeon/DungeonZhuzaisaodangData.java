package com.rd.bean.dungeon;

import com.rd.bean.drop.DropData;

import java.util.List;


public class DungeonZhuzaisaodangData {
    private short id;
    private String[] tips;
    private List<DropData> reward;
    private DropData cost;

    public DungeonZhuzaisaodangData(short id, String[] tips, List<DropData> reward, DropData cost) {
        this.id = id;
        this.tips = tips;
        this.reward = reward;
        this.cost = cost;
    }

    public short getId() {
        return id;
    }

    public String[] getTips() {
        return tips;
    }

    public void setTips(String[] tips) {
        this.tips = tips;
    }

    public List<DropData> getReward() {
        return reward;
    }

    public void setReward(List<DropData> reward) {
        this.reward = reward;
    }

    public DropData getCost() {
        return cost;
    }

    public void setCost(DropData cost) {
        this.cost = cost;
    }


}
