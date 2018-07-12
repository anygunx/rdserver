package com.rd.model.data.taskadvanced;

import com.rd.bean.drop.DropData;

import java.util.List;

public class NTaskAdvancedData {
    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getParam() {
        return param;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    private int id;
    private int type;
    private int param;
    private List<DropData> rewards;

    public NTaskAdvancedData(int id, int type, int param, List<DropData> rewards) {
        this.id = id;
        this.type = type;
        this.param = param;
        this.rewards = rewards;

    }

}
