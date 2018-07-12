package com.rd.bean.relationship;

import com.rd.bean.player.SimplePlayer;

public class NRelatedPlayer extends SimplePlayer {

    private int relationCost;
    private long updateTime;
    private long loginOutTime;

    public long getLoginOutTime() {
        return loginOutTime;
    }

    public void setLoginOutTime(long loginOutTime) {
        this.loginOutTime = loginOutTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getRelationCost() {
        return relationCost;
    }

    public void setRelationCost(int relationCost) {
        this.relationCost = relationCost;
    }

    public void addRelationCost(int num) {
        this.relationCost += relationCost;
    }

    public void init(int id, String name, byte head, short level, long fighting) {
        this.id = id;
        this.name = name;
        this.head = head;
        this.level = level;
        this.fighting = fighting;

    }


    public void init(int id, int relationCost, long updateTime) {
        this.id = id;
        this.relationCost = relationCost;
        this.updateTime = updateTime;
    }

}
