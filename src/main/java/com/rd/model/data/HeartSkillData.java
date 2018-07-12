package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class HeartSkillData {

    private byte id;

    private byte level;

    private byte type;

    private short rate;

    private int recover;

    private float amp;

    private float dr;

    private float recover_multi;

    private float hitback;

    private float hp_hurt;

    private float decatt;

    private float revive;

    private float pram;

    private float pram1;

    private float pram2;

    private DropData upCost;

    private DropData rmCost;

    private int[] attr;

    private int power;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public short getRate() {
        return rate;
    }

    public void setRate(short rate) {
        this.rate = rate;
    }

    public int getRecover() {
        return recover;
    }

    public void setRecover(int recover) {
        this.recover = recover;
    }

    public float getAmp() {
        return amp;
    }

    public void setAmp(float amp) {
        this.amp = amp;
    }

    public float getDr() {
        return dr;
    }

    public void setDr(float dr) {
        this.dr = dr;
    }

    public float getRecover_multi() {
        return recover_multi;
    }

    public void setRecover_multi(float recover_multi) {
        this.recover_multi = recover_multi;
    }

    public float getHitback() {
        return hitback;
    }

    public void setHitback(float hitback) {
        this.hitback = hitback;
    }

    public float getHp_hurt() {
        return hp_hurt;
    }

    public void setHp_hurt(float hp_hurt) {
        this.hp_hurt = hp_hurt;
    }

    public float getDecatt() {
        return decatt;
    }

    public void setDecatt(float decatt) {
        this.decatt = decatt;
    }

    public float getRevive() {
        return revive;
    }

    public void setRevive(float revive) {
        this.revive = revive;
    }

    public float getPram() {
        return pram;
    }

    public void setPram(float pram) {
        this.pram = pram;
    }

    public float getPram1() {
        return pram1;
    }

    public void setPram1(float pram1) {
        this.pram1 = pram1;
    }

    public float getPram2() {
        return pram2;
    }

    public void setPram2(float pram2) {
        this.pram2 = pram2;
    }

    public DropData getUpCost() {
        return upCost;
    }

    public void setUpCost(DropData upCost) {
        this.upCost = upCost;
    }

    public DropData getRmCost() {
        return rmCost;
    }

    public void setRmCost(DropData rmCost) {
        this.rmCost = rmCost;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
