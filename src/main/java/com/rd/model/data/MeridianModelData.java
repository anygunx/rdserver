package com.rd.model.data;

import com.rd.bean.drop.DropData;

/**
 * 经脉数据
 *
 * @author Created by U-Demon on 2016年11月3日 下午7:43:11
 * @version 1.0.0
 */
public class MeridianModelData {

    private final int lv;

    private final DropData cost;

    private final int[] attr;

    /**
     * 增加战力 计算值
     **/
    private final int fighting;


    public MeridianModelData(int lv, DropData cost, int[] attr) {
        this.lv = lv;
        this.cost = cost;
        this.attr = attr;

        this.fighting = 0;//GameCommon.calculationFighting(attr);
    }

    public int getLv() {
        return lv;
    }

    public DropData getCost() {
        return cost;
    }

    public int[] getAttr() {
        return attr;
    }

    public int getFighting() {
        return fighting;
    }
}
