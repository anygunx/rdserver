package com.rd.define;

import java.util.HashMap;
import java.util.Map;

public enum NSkinType {
    ZUOQI(1),
    CHIBANG(2),
    TIANNV(3),
    TIANXIAN(4),
    SHIZHUANG(5),
    CHENGHAO(6),
    CHENGHAO_NEW(7);

    private int type;

    NSkinType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    private static Map<Byte, NSkinType> valueMap;

    static {
        valueMap = new HashMap<>();
        for (NSkinType etit : NSkinType.values()) {
            valueMap.put((byte) etit.type, etit);
        }
    }

    public static NSkinType getNPiFuType(byte type) {
        return valueMap.get(type);
    }
}
