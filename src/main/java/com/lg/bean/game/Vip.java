package com.lg.bean.game;

import com.lg.bean.PlayerLog;

/**
 * vip����log
 * Created by XingYun on 2016/6/15.
 */
public class Vip extends PlayerLog {
    private byte vip;
    private int cost;

    public Vip() {
    }

    public Vip(byte vip, int cost) {
        this.vip = vip;
        this.cost = cost;
    }

    public byte getVip() {
        return vip;
    }

    public void setVip(byte vip) {
        this.vip = vip;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public static void main(String[] args) throws Exception {
        Vip data = new Vip((byte) 5, 10);
        System.out.println(data.getFormatLog());
    }
}
