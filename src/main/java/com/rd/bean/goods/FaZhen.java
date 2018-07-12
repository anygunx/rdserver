package com.rd.bean.goods;

import com.rd.net.message.Message;

public class FaZhen {

    private byte t;
    private short lev;

    public byte getT() {
        return t;
    }

    public void setT(byte t) {
        this.t = t;
    }

    public short getLev() {
        return lev;
    }

    public void setLev(short lev) {
        this.lev = lev;
    }

    public void getMessage(Message msg) {
        msg.setInt(t);
        msg.setShort(lev);

    }
}
