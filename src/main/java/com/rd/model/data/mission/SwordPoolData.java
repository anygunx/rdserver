package com.rd.model.data.mission;

import com.rd.bean.drop.DropData;

import java.util.List;

public class SwordPoolData {

    private short level;

    private int exp;

    private int[] attr;

    private List<DropData> reward;

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

    public List<DropData> getReward() {
        return reward;
    }

    public void setReward(List<DropData> reward) {
        this.reward = reward;
    }
}
