package com.rd.bean.goods;

import com.rd.define.NEquipUpGradeType;

public class NEquipSlot {
    //强化等级
    private int str = 0;

    private int jl;


    /**
     * 锻炼
     */
    private int dl;
    /**
     * 宝石
     */
    private int bs;

    public int getDl() {
        return dl;
    }

    public void setDl(int dl) {
        this.dl = dl;
    }

    public int getBs() {
        return bs;
    }

    public void setBs(int bs) {
        this.bs = bs;
    }

    public int getJl() {
        return jl;
    }

    public void setJl(int jl) {
        this.jl = jl;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public void addJl(int jl) {
        this.jl += jl;
    }

    public void addStr(int lv) {
        this.str += lv;
    }

    public void addUpGradeByType(int lv, NEquipUpGradeType type) {
        switch (type) {
            case EQUIP_QH:
                this.str += lv;
                break;
            case EQUIQ__JL:
                this.jl += lv;
                break;
            case EQUIQ__DL:
                this.dl += lv;
                break;
            case EQUIQ__BS:
                this.bs += lv;
                break;
            default:
                break;
        }

    }

}
