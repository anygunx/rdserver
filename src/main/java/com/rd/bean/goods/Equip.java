package com.rd.bean.goods;

import com.rd.net.message.Message;

public class Equip {

    /**
     * 装备id
     */
    private short d;
    /**
     * 物品原型id
     */
    private short g;
    /**
     * 品质
     */
    private byte q;
    /**
     * 属性浮动系数
     */
    private byte f;

    public short getD() {
        return d;
    }

    public void setD(short d) {
        this.d = d;
    }

    public short getG() {
        return g;
    }

    public void setG(short g) {
        this.g = g;
    }

    public byte getQ() {
        return q;
    }

    public void setQ(byte q) {
        this.q = q;
    }

    public byte getF() {
        return f;
    }

    public void setF(byte f) {
        this.f = f;
    }

    public Equip() {

    }

    public void getMessage(Message message) {
        //message.setShort(d);
        message.setShort(g);
        message.setByte(q);
//		message.setByte(f);
    }


}
