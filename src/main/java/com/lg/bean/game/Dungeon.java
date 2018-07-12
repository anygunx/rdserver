package com.lg.bean.game;

import com.lg.bean.PlayerLog;

public class Dungeon extends PlayerLog {

    private byte type;

    private byte attackTime;

    private short pass;

    public Dungeon() {

    }

    public Dungeon(byte type, byte attackTime, short pass) {
        this.type = type;
        this.attackTime = attackTime;
        this.pass = pass;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getAttackTime() {
        return attackTime;
    }

    public void setAttackTime(byte attackTime) {
        this.attackTime = attackTime;
    }

    public short getPass() {
        return pass;
    }

    public void setPass(short pass) {
        this.pass = pass;
    }
}
