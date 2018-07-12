package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

/**
 * 通灵属性丹
 *
 * @author wh
 */
public class SpiritProData {

    private final int lv;
    private final List<DropData> costItem;
    private final int att;
    private final int def;
    private final int hp;

    public SpiritProData(int lv, List<DropData> costItem, int att, int def, int hp) {
        super();
        this.lv = lv;
        this.costItem = costItem;
        this.att = att;
        this.def = def;
        this.hp = hp;
    }

    public int getLv() {
        return lv;
    }

    public List<DropData> getCostItem() {
        return costItem;
    }

    public int getAtt() {
        return att;
    }

    public int getDef() {
        return def;
    }

    public int getHp() {
        return hp;
    }

}
