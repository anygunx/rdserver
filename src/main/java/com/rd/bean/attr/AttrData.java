package com.rd.bean.attr;

import com.rd.net.message.Message;

public class AttrData {

    private byte i;

    private int v;

    public byte getI() {
        return i;
    }

    public void setI(byte i) {
        this.i = i;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public AttrData() {

    }

    public AttrData(byte i, int v) {
        this.i = i;
        this.v = v;
    }

    public void getMessage(Message message) {
        message.setByte(i);
        message.setInt(v);
    }
}
