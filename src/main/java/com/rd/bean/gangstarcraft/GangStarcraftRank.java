package com.rd.bean.gangstarcraft;

/**
 * 公会战 传世争霸公会排行
 *
 * @author ---
 * @version 1.0
 * @date 2017年12月28日下午7:15:43
 */
public class GangStarcraftRank {

    private int id;

    private String name;

    private int score;

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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int score) {
        this.score += score;
    }
}
