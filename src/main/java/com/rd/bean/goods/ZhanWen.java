package com.rd.bean.goods;

import com.rd.net.message.Message;

/**
 * 战纹
 *
 * @author lwq
 */
public class ZhanWen {

    //战纹id
    private int d;
    //对应战纹模型数据id
    private short g;


    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public short getG() {
        return g;
    }

    public void setG(short g) {
        this.g = g;
    }

    public void getMessage(Message msg) {
        msg.setInt(d);
        msg.setShort(g);

    }
}
