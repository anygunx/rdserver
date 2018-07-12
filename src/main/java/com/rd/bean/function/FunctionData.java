package com.rd.bean.function;

public class FunctionData {

    private byte id;

    private short level;

    private short guanqia;

    private short days;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public void addLevel(int lv) {
        this.level += lv;
    }

    public short getGuanqia() {
        return guanqia;
    }

    public void setGuanqia(short guanqia) {
        this.guanqia = guanqia;
    }

    public short getDays() {
        return days;
    }

    public void setDays(short days) {
        this.days = days;
    }

}
