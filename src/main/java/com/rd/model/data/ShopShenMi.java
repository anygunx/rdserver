package com.rd.model.data;

public class ShopShenMi {

    private short id;

    private byte itemType;

    private short itemId;

    private int itemNum;

    private int gold;

    private int diamond;

    private int rate;

    private byte discount;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public byte getItemType() {
        return itemType;
    }

    public void setItemType(byte itemType) {
        this.itemType = itemType;
    }

    public short getItemId() {
        return itemId;
    }

    public void setItemId(short itemId) {
        this.itemId = itemId;
    }

    public int getItemNum() {
        return itemNum;
    }

    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public byte getDiscount() {
        return discount;
    }

    public void setDiscount(byte discount) {
        this.discount = discount;
    }

}
