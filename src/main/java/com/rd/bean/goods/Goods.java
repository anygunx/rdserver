package com.rd.bean.goods;

import com.rd.net.message.Message;

public class Goods {

    //ID
    private short d;

    //数量
    private int n;

    public short getD() {
        return d;
    }

    public void setD(short d) {
        this.d = d;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public Goods() {

    }

    public Goods(short id, int num) {
        this.d = id;
        this.n = num;
    }

    public void getMessage(Message message) {
        message.setShort(d);
        message.setInt(n);
    }

    public void addNum(int num) {
        this.n += num;
    }

    public void subNum(int num) {
        this.n -= num;
    }
}
