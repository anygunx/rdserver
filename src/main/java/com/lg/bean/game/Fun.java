package com.lg.bean.game;

import com.lg.bean.PlayerLog;

public class Fun extends PlayerLog {

    private byte type;

    private int count;

    public Fun() {

    }

    public Fun(byte type, int count) {
        this.type = type;
        this.count = count;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
