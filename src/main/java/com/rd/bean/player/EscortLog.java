package com.rd.bean.player;

import com.rd.net.message.Message;

public class EscortLog {

    //0--运镖结束 1--开始护送 2--被劫 3--劫杀
    private byte t;

    //1--成功 0--失败
    private byte r;

    //玩家ID
    private int id;

    //头像
    private byte h = 0;

    //玩家名字
    private String m;

    //品质
    private byte q;

    //时间
    private long s;

    //战斗力
    private long f = 0;

    //复仇奖励
    private byte rv = 0;

    public void getLogMsg(Message msg) {
        msg.setByte(t);
        msg.setByte(r);
        msg.setInt(id);
        msg.setString(m);
        msg.setByte(q);
        msg.setLong(s);
        msg.setByte(h);
        msg.setLong(f);
        msg.setByte(rv);
    }

    public byte getT() {
        return t;
    }

    public void setT(int t) {
        this.t = (byte) t;
    }

    public byte getR() {
        return r;
    }

    public void setR(int r) {
        this.r = (byte) r;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public byte getQ() {
        return q;
    }

    public void setQ(byte q) {
        this.q = q;
    }

    public long getS() {
        return s;
    }

    public void setS(long s) {
        this.s = s;
    }

    public byte getH() {
        return h;
    }

    public void setH(byte h) {
        this.h = h;
    }

    public long getF() {
        return f;
    }

    public void setF(long f) {
        this.f = f;
    }

    public byte getRv() {
        return rv;
    }

    public void setRv(byte rv) {
        this.rv = rv;
    }

}
