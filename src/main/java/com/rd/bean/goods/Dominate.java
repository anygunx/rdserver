package com.rd.bean.goods;

import com.rd.net.message.Message;

/**
 * 主宰
 *
 * @author U-Demon
 */
public class Dominate {

    //等级
    private short l = 0;

    //阶级
    private short r = 0;

    public void getMsg(Message msg) {
        msg.setShort(l);
        msg.setShort(r);
    }

    public short getL() {
        return l;
    }

    public void setL(short l) {
        this.l = l;
    }

    public void addL() {
        this.l++;
    }

    public short getR() {
        return r;
    }

    public void setR(short r) {
        this.r = r;
    }

    public void addR() {
        this.r++;
    }

}
