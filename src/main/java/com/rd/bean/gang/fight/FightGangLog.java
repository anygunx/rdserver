package com.rd.bean.gang.fight;

import com.rd.net.message.Message;

public class FightGangLog {

    private String selfName;

    private String targetName;

    private byte beStar;

    private byte star;

    private int score;

    public FightGangLog(String selfName, String targetName, byte beStar, byte star, int score) {
        this.selfName = selfName;
        this.targetName = targetName;
        this.beStar = beStar;
        this.star = star;
        this.score = score;
    }

    public String getSelfName() {
        return selfName;
    }

    public void setSelfName(String selfName) {
        this.selfName = selfName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public byte getBeStar() {
        return beStar;
    }

    public void setBeStar(byte beStar) {
        this.beStar = beStar;
    }

    public byte getStar() {
        return star;
    }

    public void setStar(byte star) {
        this.star = star;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void getMessage(Message message) {
        message.setString(selfName);
        message.setString(targetName);
        message.setByte(beStar);
        message.setByte(star);
        message.setInt(score);
    }
}
