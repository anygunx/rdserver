package com.rd.model.data;

/**
 * 五行数据
 *
 * @author ---
 * @version 1.0
 * @date 2018年1月26日下午7:08:57
 */
public class FiveElementsData {

    private byte id;

    private byte vip;

    private byte day;

    private int[] attribute;

    public FiveElementsData(byte id, byte vip, byte day, int[] attribute) {
        this.id = id;
        this.vip = vip;
        this.day = day;
        this.attribute = attribute;
    }

    public byte getId() {
        return id;
    }

    public byte getVip() {
        return vip;
    }

    public byte getDay() {
        return day;
    }

    public int[] getAttribute() {
        return attribute;
    }
}
