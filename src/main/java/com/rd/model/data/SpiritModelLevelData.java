package com.rd.model.data;

/**
 * 元魂等级数据
 *
 * @author Created by U-Demon on 2016年11月10日 下午5:21:32
 * @version 1.0.0
 */
public class SpiritModelLevelData {

    private short lv;

    private int expBase;

    private int[] attr;

    private float[] expPerc;

    private float[] attrPerc;

    public short getLv() {
        return lv;
    }

    public void setLv(short lv) {
        this.lv = lv;
    }

    public int getExpBase() {
        return expBase;
    }

    public void setExpBase(int expBase) {
        this.expBase = expBase;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

    public float[] getExpPerc() {
        return expPerc;
    }

    public void setExpPerc(float[] expPerc) {
        this.expPerc = expPerc;
    }

    public float[] getAttrPerc() {
        return attrPerc;
    }

    public void setAttrPerc(float[] attrPerc) {
        this.attrPerc = attrPerc;
    }

}
