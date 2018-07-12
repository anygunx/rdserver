package com.rd.model.data;

/**
 * @author ---
 * @version 1.0
 * @date 2018年5月8日下午3:15:29
 */
public class GrowEquipData {

    private short id;

    private byte pos;

    private byte needLevel;

    private byte quality;

    private int[] attr;

    public GrowEquipData(short id, byte pos, byte needLevel, byte quality, int[] attr) {
        this.id = id;
        this.pos = pos;
        this.needLevel = needLevel;
        this.quality = quality;
        this.attr = attr;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public byte getPos() {
        return pos;
    }

    public void setPos(byte pos) {
        this.pos = pos;
    }

    public byte getNeedLevel() {
        return needLevel;
    }

    public void setNeedLevel(byte needLevel) {
        this.needLevel = needLevel;
    }

    public byte getQuality() {
        return quality;
    }

    public void setQuality(byte quality) {
        this.quality = quality;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }


}
