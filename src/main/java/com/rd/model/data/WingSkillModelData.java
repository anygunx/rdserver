package com.rd.model.data;

public class WingSkillModelData {
    private final short id;
    private final byte lv;
    private final short wingGodLvLimit;
    private final int amp;

    public WingSkillModelData(short id, byte lv, short wingGodLvLimit, int amp) {
        this.id = id;
        this.lv = lv;
        this.wingGodLvLimit = wingGodLvLimit;
        this.amp = amp;
    }

    public short getId() {
        return id;
    }

    public byte getLv() {
        return lv;
    }

    public short getWingGodLvLimit() {
        return wingGodLvLimit;
    }

    public int getAmp() {
        return amp;
    }
}
