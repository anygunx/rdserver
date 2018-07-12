package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

/**
 * 商城商品数据
 *
 * @author Created by U-Demon on 2016年11月5日 上午11:58:17
 * @version 1.0.0
 */
public class ShopItemData {

    private byte shopType;

    private int id;

    //商品
    private DropData item;

    //价格
    private List<DropData> price;

    public byte getShopType() {
        return shopType;
    }

    public void setShopType(byte shopType) {
        this.shopType = shopType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DropData getItem() {
        return item;
    }

    public void setItem(DropData item) {
        this.item = item;
    }

    public List<DropData> getPrice() {
        return price;
    }

    public void setPrice(List<DropData> price) {
        this.price = price;
    }

}
