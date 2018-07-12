package com.rd.bean.skill;

public class NSkill {
    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public short getLv() {
        return lv;
    }

    public void setLv(short lv) {
        this.lv = lv;
    }

    /**
     * 技能id
     */
    private int sid;
    /**
     * 技能等级
     */
    private short lv = 0;


}
