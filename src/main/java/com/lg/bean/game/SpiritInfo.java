package com.lg.bean.game;

public class SpiritInfo {

    private int sid;

    private int lv;

    private int exp;

    private int totalExp;

    public SpiritInfo() {

    }

    public SpiritInfo(int sid, int lv, int exp, int totalExp) {
        this.sid = sid;
        this.lv = lv;
        this.exp = exp;
        this.totalExp = totalExp;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getTotalExp() {
        return totalExp;
    }

    public void setTotalExp(int totalExp) {
        this.totalExp = totalExp;
    }

    @Override
    public String toString() {
        return "SpiritInfo [sid=" + sid + ", lv=" + lv + ", exp=" + exp + ", totalExp=" + totalExp + "]";
    }

}
