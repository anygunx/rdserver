package com.rd.model.data;

/**
 * 熔炼数据
 *
 * @author Created by U-Demon on 2016年10月31日 下午6:36:16
 * @version 1.0.0
 */
public class EquipRlModelData {

    private int lv;

    private int exp;

    private int[] attr;

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }
}
