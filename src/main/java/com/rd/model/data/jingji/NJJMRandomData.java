package com.rd.model.data.jingji;

public class NJJMRandomData {

    public void setStartnum(int startnum) {
        this.startnum = startnum;
    }

    public void setEndnum(int endnum) {
        this.endnum = endnum;
    }

    public void setRandom_num_start(int random_num_start) {
        this.random_num_start = random_num_start;
    }

    public void setRandom_num_end(int random_num_end) {
        this.random_num_end = random_num_end;
    }

    private int startnum;
    private int endnum;

    public int getStartnum() {
        return startnum;
    }

    public int getEndnum() {
        return endnum;
    }

    public int getRandom_num_start() {
        return random_num_start;
    }

    public int getRandom_num_end() {
        return random_num_end;
    }

    private int random_num_start;
    private int random_num_end;

}
