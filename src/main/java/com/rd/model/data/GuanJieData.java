package com.rd.model.data;

/**
 * @author lwq
 */
public class GuanJieData {

    private byte id;
    private byte level;

    private int[] attr;

    private int bossinch;
    private int need;
    private int income;


    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public int getBossinch() {
        return bossinch;
    }

    public void setBossinch(int bossinch) {
        this.bossinch = bossinch;
    }

    public int getNeed() {
        return need;
    }

    public void setNeed(int need) {
        this.need = need;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }


}
