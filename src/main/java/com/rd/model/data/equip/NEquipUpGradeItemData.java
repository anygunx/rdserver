package com.rd.model.data.equip;

import com.rd.bean.drop.DropData;

/**
 * 装备槽位的各种升级 比如强化 淬炼
 *
 * @author MyPC
 */
public class NEquipUpGradeItemData {

    private int lv;

    private int pos;

    private DropData cost;
    private int[] attr;

    private int target;//装备不同种大师用的数据

    public int getTarget() {
        return target;
    }

    public int getLv() {
        return lv;
    }

    public int getPos() {
        return pos;
    }

    public DropData getCost() {
        return cost;
    }

    public int[] getAttr() {
        return attr;
    }


    public NEquipUpGradeItemData(int lv, int pos, DropData cost, int[] attr) {
        this.lv = lv;
        this.cost = cost;
        this.pos = pos;
    }

    public NEquipUpGradeItemData(int lv, int target, int[] attr) {
        this.lv = lv;

        this.target = target;
    }
}
