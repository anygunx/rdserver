package com.rd.model.data.richang;

import com.rd.bean.drop.DropData;

import java.util.ArrayList;
import java.util.List;

public class NRiChangData {
    private byte id;
    private int type;
    private int target;
    private List<DropData> reward = new ArrayList<>();


    public NRiChangData(byte id, int type, int target, List<DropData> reward) {
        this.id = id;
        this.type = type;
        this.target = target;
        this.reward = reward;
    }


    public byte getId() {
        return id;
    }


    public int getType() {
        return type;
    }


    public int getTarget() {
        return target;
    }


    public List<DropData> getReward() {
        return reward;
    }
}
