package com.rd.bean.goods;

import com.rd.net.message.Message;

/**
 * 元神
 *
 * @author Created by U-Demon on 2016年11月9日 下午2:13:31
 * @version 1.0.0
 */
public class Spirit {

    //元神id
    private int d;

    //物品原型
    private short g;

    public void getMessage(Message message) {
        message.setInt(d);
        message.setShort(g);
    }

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

}
