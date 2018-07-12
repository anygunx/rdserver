package com.rd.bean.shop;

import com.rd.model.NShopModel;
import com.rd.model.data.shop.NShopData;

import java.util.HashMap;
import java.util.Map;

public class NTypeShop {

    private byte t;

    private Map<Integer, Byte> m = new HashMap<>();

    public byte getT() {
        return t;
    }

    public void setT(byte t) {
        this.t = t;
    }

    public Map<Integer, Byte> getM() {
        return m;
    }

    public void setM(Map<Integer, Byte> m) {
        this.m = m;
    }

    public void retset(byte subType) {
        for (Map.Entry<Integer, Byte> map : m.entrySet()) {
            NShopData data = NShopModel.getShopMap(subType, map.getKey());
            if (data == null) {
                continue;
            }
            byte type = data.getType();
            if (type != 1) {
                continue;
            }
            m.remove(map.getKey());
        }
    }


}
