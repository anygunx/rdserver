package com.rd.model.data;

import com.google.common.collect.ImmutableMap;
import com.rd.bean.drop.DropData;
import com.rd.define.EAuction;

import java.util.Map;

/**
 * 拍卖数据模板
 * Created by XingYun on 2017/10/24.
 */
public class AuctionItemModelData {
    private final short id;
    /**
     * 获得物品
     **/
    private final DropData goods;
    /**
     * 起拍价
     **/
    private final int basePrice;
    /**
     * 加价
     **/
    private final int addPrice;
    /**
     * 一口价
     **/
    private final int fixedPrice;
    /**
     * 拍卖时间
     **/
    private final Map<EAuction, Long> keepTime;
    /**
     * 订阅标识
     **/
    private final byte subscribe;


    public AuctionItemModelData(short id, DropData goods, int basePrice, int addPrice, int fixedPrice, Map<EAuction, Long> keepTime, byte subscribe) {
        this.id = id;
        this.goods = goods;
        this.basePrice = basePrice;
        this.addPrice = addPrice;
        this.fixedPrice = fixedPrice;
        this.keepTime = ImmutableMap.copyOf(keepTime);
        this.subscribe = subscribe;
    }

    public short getId() {
        return id;
    }

    public DropData getGoods() {
        return goods;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public int getAddPrice() {
        return addPrice;
    }

    public int getFixedPrice() {
        return fixedPrice;
    }

    public long getKeepTime(EAuction auction) {
        return keepTime.get(auction);
    }

    public byte getSubscribe() {
        return subscribe;
    }

}


