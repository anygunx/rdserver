package com.rd.define;

/**
 * 物品品质
 *
 * @author Created by U-Demon on 2016年11月21日 下午2:57:43
 * @version 1.0.0
 */
public enum EGoodsQuality {

    WHITE(0, "白"),
    GREEN(1, "绿"),
    BLUE(2, "紫"),
    PURPLE(3, "橙"),
    ORANGE(4, "红"),
    RED(5, "盛世"),;

    //品质
    private byte value;

    //描述
    private String desc;

    EGoodsQuality(int value, String desc) {
        this.value = (byte) value;
        this.desc = desc;
    }

    public byte getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
