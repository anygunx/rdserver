package com.rd.model.data;

import com.rd.bean.drop.DropData;

/**
 * 装备强化数据
 *
 * @author Created by U-Demon on 2016年10月28日 下午4:23:14
 * @version 1.0.0
 */
public class EquipStrModelData {

    private int lv;

    private int pos;

    private int[] attr;

    private DropData cost;

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

    public DropData getCost() {
        return cost;
    }

    public void setCost(DropData cost) {
        this.cost = cost;
    }

}
