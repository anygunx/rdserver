package com.rd.bean.gangstarcraft;

/**
 * 公会战 传世争霸公会成员排行
 *
 * @author ---
 * @version 1.0
 * @date 2017年12月28日下午7:18:30
 */
public class GangStarcraftMemberRank {

    private int id;

    private String name;

    private String gangName;

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

    public String getGangName() {
        return gangName;
    }

    public void setGangName(String gangName) {
        this.gangName = gangName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
