package com.rd.model.data;

public class TitleModelData {

    private short id;

    //正数表示消耗宝箱的ID  复数表示相应的来源
    private short cost;

    private int time;

    private int[] attr;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getCost() {
        return cost;
    }

    public void setCost(short cost) {
        this.cost = cost;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

}
