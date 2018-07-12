package com.rd.model.data.faction;

import com.rd.bean.drop.DropData;

import java.util.List;

public class NFactionActiveData {

    private short lv;
    private int exp;
    private int[] attr;
    List<DropData> rewards;


    public NFactionActiveData(short lv, int exp, List<DropData> rewards, int[] attr) {
        this.lv = lv;
        this.exp = exp;
        this.rewards = rewards;
        this.attr = attr;
    }


    public short getLv() {
        return lv;
    }


    public int getExp() {
        return exp;
    }


    public int[] getAttr() {
        return attr;
    }


    public List<DropData> getRewards() {
        return rewards;
    }


}
