package com.rd.model.data.faction;

public class NFactionTaskData {
    private int id;
    private int num;
    private int rewards;


    public NFactionTaskData(int id, int num, int rewards) {
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


    public int getRewards() {
        return rewards;
    }

}
