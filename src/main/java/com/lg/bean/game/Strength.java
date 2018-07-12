package com.lg.bean.game;

import com.lg.bean.PlayerLog;

public class Strength extends PlayerLog {

    private byte type;

    private int level;

    public Strength() {
    }

    public Strength(byte type, int level) {
        this.type = type;
        this.level = level;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
