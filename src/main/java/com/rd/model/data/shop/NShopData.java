package com.rd.model.data.shop;

import com.rd.bean.drop.DropData;

public class NShopData {
    public int getId() {
        return id;
    }

    public byte getShop_type() {
        return shop_type;
    }

    public byte getType() {
        return type;
    }

    public short getLevel() {
        return level;
    }

    public DropData getReward() {
        return reward;
    }

    public byte getNum() {
        return num;
    }

    public DropData getCost() {
        return cost;
    }

    public byte getCondition() {
        return condition;
    }

    private int id;
    private byte shop_type;
    private byte type;
    private short level;
    private DropData reward;
    private byte num;
    private DropData cost;
    private byte condition;


    public NShopData(int id, byte shop_type, short level, DropData reward, byte num, DropData cost, byte condition) {
        this.id = id;
        this.level = level;
        this.reward = reward;
        this.num = num;
        this.cost = cost;
        this.condition = condition;
    }

}
