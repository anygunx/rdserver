package com.rd.model.data;

/**
 * 镇魂宝库转盘数据
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月10日下午2:58:16
 */
public class TownSoulTurntableProbailityData {

    private byte id;

    private byte[] probability;

    private short time;

    private short timeMax;

    private byte[] target;

    public TownSoulTurntableProbailityData(byte id, byte[] probability, short time, short timeMax, byte[] target) {
        this.id = id;
        this.probability = probability;
        this.time = time;
        this.timeMax = timeMax;
        this.target = target;
    }

    public byte getId() {
        return id;
    }

    public byte[] getProbability() {
        return probability;
    }

    public short getTime() {
        return time;
    }

    public short getTimeMax() {
        return timeMax;
    }

    public byte[] getTarget() {
        return target;
    }
}
