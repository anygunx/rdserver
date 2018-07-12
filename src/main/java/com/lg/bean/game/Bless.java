package com.lg.bean.game;

import com.lg.bean.PlayerLog;

public class Bless extends PlayerLog {

    private byte type;

    private short level;

    public Bless() {
    }

    public Bless(byte type, short level) {
        this.type = type;
        this.level = level;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }
}
