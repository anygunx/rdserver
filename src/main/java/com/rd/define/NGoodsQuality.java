package com.rd.define;

public enum NGoodsQuality {

    WHITE(0, "白"),
    GREEN(1, "绿"),
    BLUE(2, "紫"),
    PURPLE(3, "橙"),
    ORANGE(4, "红"),;

    //品质
    private byte value;

    //描述
    private String desc;

    NGoodsQuality(int value, String desc) {
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
