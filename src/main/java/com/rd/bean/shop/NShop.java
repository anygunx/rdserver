package com.rd.bean.shop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.rd.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class NShop {

    private int playerId;
    //限购 key 是商品id value 购买次数
    private Map<Byte, NTypeShop> shopMap = new HashMap<>();

    public int getPlayerId() {
        return playerId;
    }


    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }


    public Map<Byte, NTypeShop> getShopMap() {
        return shopMap;
    }


    public void setShopMap(String shopJson) {
        if (!StringUtil.isEmpty(shopJson)) {
            this.shopMap = JSON.parseObject(shopJson, new TypeReference<Map<Byte, NTypeShop>>() {
            });
        }

    }

    public String getShopMapJson() {
        return JSON.toJSONString(shopMap);
    }


}
