package com.rd.model.data.jingji;

public class NJingJiAoundData {

    private int id;
    private int from_min;
    private int to_max;
    private int pos;

    private int random_min;
    private int random_max;

    public NJingJiAoundData(int pos, int from_min, int tom_max, int random_min, int random_max) {
        this.pos = pos;
        this.from_min = from_min;
        this.to_max = tom_max;
        this.random_min = random_min;
        this.random_max = random_max;
    }

    public int getId() {
        return id;
    }

    public int getFrom_min() {
        return from_min;
    }

    public int getTo_max() {
        return to_max;
    }

    public int getPos() {
        return pos;
    }

    public int getRandom_min() {
        return random_min;
    }

    public int getRandom_max() {
        return random_max;
    }

}
