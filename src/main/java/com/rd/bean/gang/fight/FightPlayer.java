package com.rd.bean.gang.fight;

import com.rd.bean.player.Player;

import java.util.List;

public class FightPlayer {

    private byte starNum;

    private int score;

    private byte fightCount;

    private Player player;

    private List<FightTarget> fightTargetList;

    private byte attackStar;

    public byte getAttackStar() {
        return attackStar;
    }

    public void setAttackStar(byte attackStar) {
        this.attackStar = attackStar;
    }

    public List<FightTarget> getFightTargetList() {
        return fightTargetList;
    }

    public void setFightTargetList(List<FightTarget> fightTargetList) {
        this.fightTargetList = fightTargetList;
    }

    public Player getPlayer() {
        return player;
    }

    public byte getStarNum() {
        return starNum;
    }

    public void setStarNum(byte starNum) {
        this.starNum = starNum;
    }

    public void addStarNum(byte star) {
        this.starNum += star;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public byte getFightCount() {
        return fightCount;
    }

    public void setFightCount(byte fightCount) {
        this.fightCount = fightCount;
    }

    public void addFightCount() {
        ++this.fightCount;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public FightPlayer(Player player) {
        this.player = player;
        this.reset();
    }

    public void reset() {
        this.fightCount = 0;
        this.fightTargetList = null;
    }
}
