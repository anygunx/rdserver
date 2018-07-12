package com.rd.define;

import java.util.HashMap;
import java.util.Map;

/**
 * 拍品类型
 * Created by XingYun on 2017/10/25.
 */
public enum EAuctionItemType {
    Gang(1),
    Personal(2),;

    public final byte id;

    EAuctionItemType(int id) {
        this.id = (byte) id;
    }

    private static final Map<Byte, EAuctionItemType> types = new HashMap() {
        {
            for (EAuctionItemType type : EAuctionItemType.values()) {
                put(type.id, type);
            }
        }
    };

    public static EAuctionItemType get(byte id) {
        return types.get(id);
    }
}
