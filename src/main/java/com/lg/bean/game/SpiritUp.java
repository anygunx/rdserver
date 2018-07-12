package com.lg.bean.game;

import com.lg.bean.PlayerLog;

public class SpiritUp extends PlayerLog {

    private int sid;

    private int oldLv;

    private int oldExp;

    private int totalExp;

    private int newLv;

    private int newExp;

    //private List<SpiritInfo> infos = new ArrayList<>();

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getOldLv() {
        return oldLv;
    }

    public void setOldLv(int oldLv) {
        this.oldLv = oldLv;
    }

    public int getOldExp() {
        return oldExp;
    }

    public void setOldExp(int oldExp) {
        this.oldExp = oldExp;
    }

    public int getTotalExp() {
        return totalExp;
    }

    public void setTotalExp(int totalExp) {
        this.totalExp = totalExp;
    }

//	public List<SpiritInfo> getInfos() {
//		return infos;
//	}
//
//	public void setInfos(List<SpiritInfo> infos) {
//		this.infos = infos;
//	}

    public int getNewLv() {
        return newLv;
    }

    public void setNewLv(int newLv) {
        this.newLv = newLv;
    }

    public int getNewExp() {
        return newExp;
    }

    public void setNewExp(int newExp) {
        this.newExp = newExp;
    }

}
