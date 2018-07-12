package com.rd.bean.dungeon;

import java.util.ArrayList;
import java.util.List;

public class DungeonData {

    private byte id;

    private byte type;

    private short levelLimit;

    private byte vipLimit;

    private int price;

    private byte priceAdd;

    private short timeLimit;

    private List<byte[]> timesList;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public short getLevelLimit() {
        return levelLimit;
    }

    public void setLevelLimit(short levelLimit) {
        this.levelLimit = levelLimit;
    }

    public byte getVipLimit() {
        return vipLimit;
    }

    public void setVipLimit(byte vipLimit) {
        this.vipLimit = vipLimit;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public byte getPriceAdd() {
        return priceAdd;
    }

    public void setPriceAdd(byte priceAdd) {
        this.priceAdd = priceAdd;
    }

    public short getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(short timeLimit) {
        this.timeLimit = timeLimit;
    }

    public List<byte[]> getTimesList() {
        return timesList;
    }

    public void setTimesList(List<byte[]> timesList) {
        this.timesList = timesList;
    }

    public DungeonData() {
        this.timesList = new ArrayList<>();
    }

    public int getFreeTimes(byte vipLevel) {
        byte times = this.timesList.get(vipLevel)[0];
        if (times < 0) {
            return Integer.MAX_VALUE / 2;
        } else {
            return times;
        }
    }

    public int getBuyTimes(byte vipLevel) {
        byte times = this.timesList.get(vipLevel)[1];
        if (times < 0) {
            return Integer.MAX_VALUE / 2;
        } else {
            return times;
        }
    }
}
