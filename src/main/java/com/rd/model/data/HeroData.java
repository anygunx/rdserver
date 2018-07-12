package com.rd.model.data;

/**
 * @author ---
 * @version 1.0
 * @date 2018年4月18日下午8:49:50
 */
public class HeroData {

    private short lv;

    private int[] attr;

    public HeroData(short lv, int[] attr) {
        this.lv = lv;
        this.attr = attr;
    }

    public short getLv() {
        return lv;
    }

    public int[] getAttr() {
        return attr;
    }
}
