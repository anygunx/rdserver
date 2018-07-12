package com.rd.bean.gangstarcraft;

/**
 * 公会战 传世争霸记录
 *
 * @author ---
 * @version 1.0
 * @date 2017年12月28日下午1:00:51
 */
public class GangStarcraft {

    private int gangId;

    private int presidentId;

    private String fightDay;

    private String gangRank;

    private String memberRank;

    public int getGangId() {
        return gangId;
    }

    public void setGangId(int gangId) {
        this.gangId = gangId;
    }

    public int getPresidentId() {
        return presidentId;
    }

    public void setPresidentId(int presidentId) {
        this.presidentId = presidentId;
    }

    public String getFightDay() {
        return fightDay;
    }

    public void setFightDay(String fightDay) {
        this.fightDay = fightDay;
    }

    public String getGangRank() {
        return gangRank;
    }

    public void setGangRank(String gangRank) {
        this.gangRank = gangRank;
    }

    public String getMemberRank() {
        return memberRank;
    }

    public void setMemberRank(String memberRank) {
        this.memberRank = memberRank;
    }
}
