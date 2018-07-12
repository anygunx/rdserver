package com.rd.bean.gangstarcraft;

import com.rd.bean.gang.GangMember;

/**
 * 传世霸业 参战者
 *
 * @author ---
 * @version 1.0
 * @date 2017年12月28日下午7:56:44
 */
public class StarcraftFighter implements Comparable<StarcraftFighter> {

    private GangMember member;

    private int score;

    private byte area;

    private long enterTime;

    private short feat;

    private long deadTime;

    private long attackTime;

    private byte recordArea;

    private byte count;

    public StarcraftFighter(GangMember member) {
        this.member = member;
    }

    public GangMember getMember() {
        return member;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public byte getArea() {
        return area;
    }

    public void setArea(byte area) {
        this.area = area;
    }

    public long getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(long enterTime) {
        this.enterTime = enterTime;
    }

    public short getFeat() {
        return feat;
    }

    public void setFeat(short feat) {
        this.feat = feat;
    }

    public long getDeadTime() {
        return deadTime;
    }

    public void setDeadTime(long deadTime) {
        this.deadTime = deadTime;
    }

    public long getAttackTime() {
        return attackTime;
    }

    public void setAttackTime(long attackTime) {
        this.attackTime = attackTime;
    }

    public byte getRecordArea() {
        return recordArea;
    }

    public void setRecordArea(byte recordArea) {
        this.recordArea = recordArea;
    }

    public byte getCount() {
        return count;
    }

    public void setCount(byte count) {
        this.count = count;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void addFeat(int feat) {
        this.feat += feat;
    }

    @Override
    public int compareTo(StarcraftFighter fighter) {
        int result = Integer.compare(fighter.getScore(), this.getScore());
        if (result == 0) {
            result = Long.compare(this.enterTime, fighter.enterTime);
        }
        return result;
    }
}
