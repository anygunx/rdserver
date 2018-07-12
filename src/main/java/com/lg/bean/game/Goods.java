package com.lg.bean.game;

import com.lg.bean.PlayerLog;

public class Goods extends PlayerLog {

    private byte type;
    private int gid;
    private int value;
    private int changeType;

    public Goods() {
    }

    public Goods(byte type, int gid, int value, int changeType) {
        this.type = type;
        this.gid = gid;
        this.value = value;
        this.changeType = changeType;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getChangeType() {
        return changeType;
    }

    public void setChangeType(int changeType) {
        this.changeType = changeType;
    }
}
