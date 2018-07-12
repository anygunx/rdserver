package com.rd.model.data;

public class HolyLinesMasterData {

    private final byte stage;
    private final double addPercent;    //float存在精度问题 240*1.05f等于251.99998

    public HolyLinesMasterData(byte stage, double addPercent) {
        this.stage = stage;
        this.addPercent = addPercent;
    }

    public byte getStage() {
        return stage;
    }

    public double getAddPercent() {
        return addPercent;
    }
}
