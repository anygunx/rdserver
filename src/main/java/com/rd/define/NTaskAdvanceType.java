package com.rd.define;

import java.util.HashMap;
import java.util.Map;

/****
 *
 *
 * 1=坐骑；2=翅膀；3=神兵；4=天仙；5=仙阵；6=仙位；7=通灵；8=兽魂
 9=天女；10=仙器；11=花辇；12=灵气
 *
 * **/
public enum NTaskAdvanceType {


    ZUOQI(1),
    WING(2),
    SHENGBING(3),
    TIANXIAN(4),
    XIANZHEN(5),
    XIANWEI(6),
    TONGLING(7),
    SHOUHUN(8),
    TIANNV(9),
    XIANQI(10),
    HUAPAN(11),
    LINGQI(12);
    private int type;

    NTaskAdvanceType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    private static Map<Integer, NTaskAdvanceType> valueMap;

    static {
        valueMap = new HashMap<>();
        for (NTaskAdvanceType etit : NTaskAdvanceType.values()) {
            valueMap.put(etit.type, etit);
        }
    }

    public static NTaskAdvanceType getNTaskAdvanceType(int type) {
        return valueMap.get(type);
    }

}
