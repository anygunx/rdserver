package com.rd.model.data;

import com.rd.bean.drop.DropData;


/**
 * 神兵模板数据
 * Created by XingYun on 2017/11/30.
 */
public class ShenBingModelData {
    private final short id;
    /**
     * 神兵类型
     **/
    private final byte type;
    /**
     * 阶
     **/
    private final byte stage;
    /**
     * 星级
     **/
    private final short star;
    /**
     * 经验消耗
     **/
    private final DropData consume;
    /**
     * 升星经验
     **/
    private final int exp;
    /**
     * 属性加成
     **/
    private final float bonus;
    /**
     * 属性
     **/
    private final int[] attr;

    public ShenBingModelData(short id, byte type, byte stage, short star, DropData consume, int exp, float bonus, int[] attr) {
        this.id = id;
        this.type = type;
        this.stage = stage;
        this.star = star;
        this.consume = consume;
        this.exp = exp;
        this.bonus = bonus;
        this.attr = attr;
    }

    public short getId() {
        return id;
    }

    public byte getType() {
        return type;
    }

    public int[] getAttr() {
        return attr;
    }

    public byte getStage() {
        return stage;
    }

    public short getStar() {
        return star;
    }

    public DropData getConsume() {
        return consume;
    }

    public int getExp() {
        return exp;
    }

    public float getBonus() {
        return bonus;
    }
}
