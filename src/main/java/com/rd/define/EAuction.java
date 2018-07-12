package com.rd.define;

import com.rd.bean.auction.GangAuction;
import com.rd.bean.auction.IAuctionBuilder;
import com.rd.bean.auction.WorldAuction;

import java.util.HashMap;
import java.util.Map;

/**
 * 拍卖行类型
 * Created by XingYun on 2017/10/24.
 */
public enum EAuction {
    /**
     * 帮派拍卖
     **/
    GangAuction(1, () -> new GangAuction()),
    /**
     * 世界拍卖
     **/
    WorldAuction(2, () -> new WorldAuction());

    public final byte id;
    public final IAuctionBuilder builder;

    EAuction(int id, IAuctionBuilder builder) {
        this.id = (byte) id;
        this.builder = builder;
    }

    private static final Map<Byte, EAuction> types = new HashMap() {
        {
            for (EAuction type : EAuction.values()) {
                put(type.id, type);
            }
        }
    };

    public static EAuction get(byte id) {
        return types.get(id);
    }
}
