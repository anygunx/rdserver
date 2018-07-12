package com.rd.model.data;

/**
 * @author ---
 * @version 1.0
 * @date 2018年5月12日上午11:54:17
 */
public class WashingData {

    public byte level;

    public short[] rate;

    public byte[] num;

    public WashingData(byte level, short[] rate, byte[] num) {
        this.level = level;
        this.rate = rate;
        this.num = num;
    }

    public byte getLevel() {
        return level;
    }

    public short[] getRate() {
        return rate;
    }

    public byte[] getNum() {
        return num;
    }

}
