package com.lg.bean.game;

import com.lg.bean.PlayerLog;

public class BlessTime extends PlayerLog {

    private byte type;

    private byte timeFlag;

    public BlessTime() {
    }

    public BlessTime(byte type, byte timeFlag) {
        this.type = type;
        this.timeFlag = timeFlag;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getTimeFlag() {
        return timeFlag;
    }

    public void setTimeFlag(byte timeFlag) {
        this.timeFlag = timeFlag;
    }

}
