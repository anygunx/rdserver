package com.rd.model.data;

public class ShopPointsData {

    private byte shopType;

    private short id;

    private byte itemType;

    private short itemId;

    private int itemNum;

    private int points;

    private int diamond;

    public byte getShopType() {
        return shopType;
    }

    public void setShopType(byte shopType) {
        this.shopType = shopType;
    }

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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

}
