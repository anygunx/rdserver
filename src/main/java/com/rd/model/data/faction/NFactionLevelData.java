package com.rd.model.data.faction;

public class NFactionLevelData {


    private short level;
    private int exp;
    private int num;

    public short getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getNum() {
        return num;
    }

    public NFactionLevelData(short level, int exp, int num) {

        this.level = level;
        this.exp = exp;
        this.num = num;

    }


}
