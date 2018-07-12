package com.rd.bean.gang.fight;

public class FightGangRankData {

    private int id;

    private String name;

    private short starNum;

    private int score;

    public FightGangRankData() {

    }

    public FightGangRankData(int id, String name, short starNum, int score) {
        this.id = id;
        this.name = name;
        this.starNum = starNum;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getStarNum() {
        return starNum;
    }

    public void setStarNum(short starNum) {
        this.starNum = starNum;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}