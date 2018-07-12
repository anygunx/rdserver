package com.rd.bean.fanbao;

public class NDanYao {
    /**
     * 位置
     */
    private byte p;
    /**
     * 使用物品数量
     */
    private short gn;

    public short getGn() {
        return gn;
    }

    public void setGn(short gn) {
        this.gn = gn;
    }

    public void addGn(short n) {
        this.gn += n;
    }

    public byte getP() {
        return p;
    }

    public void setP(byte p) {
        this.p = p;
    }

    private byte lv;

    public byte getLv() {
        return lv;
    }

    public void setLv(byte lv) {
        this.lv = lv;
    }


}
