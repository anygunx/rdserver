package com.rd.model.data;

public class CombineRuneData {

    private byte id;

    private short level;

    private int[] attr;

    //分解
    private short decompose;

    //合成
    private short compose;

    private byte into;

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

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

    public short getDecompose() {
        return decompose;
    }

    public void setDecompose(short decompose) {
        this.decompose = decompose;
    }

    public short getCompose() {
        return compose;
    }

    public void setCompose(short compose) {
        this.compose = compose;
    }

    public byte getInto() {
        return into;
    }

    public void setInto(byte into) {
        this.into = into;
    }
}
