package com.rd.model.data.skin;

import com.rd.bean.drop.DropData;

public class NSkinData {

    private int id;
    private DropData cost;
    private byte clas;
    private int[] attr;

    public int getId() {
        return id;
    }

    public DropData getCost() {
        return cost;
    }

    public byte getClas() {
        return clas;
    }

    public int[] getAttar() {
        return attr;
    }

    public NSkinData(int id, DropData cost, byte clas, int[] attr) {
        this.id = id;
        this.cost = cost;
        this.clas = clas;
        this.attr = attr;
    }


}
