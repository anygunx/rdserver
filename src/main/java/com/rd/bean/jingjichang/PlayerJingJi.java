package com.rd.bean.jingjichang;

public class PlayerJingJi {
    private int playerId;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private long fight;

    public long getFight() {
        return fight;
    }

    public void setFight(long fight) {
        this.fight = fight;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    private int type;
    private int rank;


}
