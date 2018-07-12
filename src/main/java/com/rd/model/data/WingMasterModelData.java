package com.rd.model.data;

public class WingMasterModelData {
    private final short id;
    private final byte lv;
    private final byte wingGodLevelLimit;
    private final double addPercent;    //float存在精度问题 240*1.05f等于251.99998

    public WingMasterModelData(short id, byte lv, byte wingGodLevelLimit, double addPercent) {
        this.id = id;
        this.lv = lv;
        this.wingGodLevelLimit = wingGodLevelLimit;
        this.addPercent = addPercent;
    }

    public short getId() {
        return id;
    }

    public byte getLv() {
        return lv;
    }

    public byte getWingGodLevelLimit() {
        return wingGodLevelLimit;
    }

    public double getAddPercent() {
        return addPercent;
    }
}
