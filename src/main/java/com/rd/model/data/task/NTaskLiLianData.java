package com.rd.model.data.task;

public class NTaskLiLianData {

    private int id;
    private int eventtype;
    private int target;
    private int lilianExp;

    public int getId() {
        return id;
    }

    public int getEventtype() {
        return eventtype;
    }

    public int getTarget() {
        return target;
    }

    public int getLilianExp() {
        return lilianExp;
    }

    public NTaskLiLianData(int id, int eventtype, int target, int lilianExp) {
        this.id = id;
        this.eventtype = eventtype;
        this.target = target;
        this.lilianExp = lilianExp;
    }

}
