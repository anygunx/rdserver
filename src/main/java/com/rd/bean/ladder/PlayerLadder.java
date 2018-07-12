package com.rd.bean.ladder;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色天梯数据
 *
 * @author Created by U-Demon on 2016年10月18日 下午2:52:46
 * @version 1.0.0
 */
public class PlayerLadder {

    //玩家ID
    private int playerId;

    //总星级数量
    private int star;

    //总战斗场次
    private int total;

    //胜场数量
    private int win;

    //失败场数
    private int lose;

    //连胜场次
    private int conwin;

    //战斗次数
    private int count;

    //战斗次数恢复开始时间
    private long recoverTime;

    //购买次数
    private int buyCount;

    //战斗列表
    private List<Integer> fightIds = new ArrayList<>();

    //积分
    private int score;

    //上赛季星级
    private int lastStar;

    //上赛季总次数
    private int lastTotal;

    //上赛季总胜利
    private int lastWin;

    //上赛季排名
    private int lastRank;

    //比赛ID，不入库，校验一下
    private int matchId;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getWin() {
        return win;
    }

    /**
     * 净胜
     *
     * @return
     */
    public int getGoal() {
        //return getWin() * 2 - getTotal();
        return this.win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getConwin() {
        return conwin;
    }

    public void setConwin(int conwin) {
        this.conwin = conwin;
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

    public int getLastRank() {
        return lastRank;
    }

    public void setLastRank(int lastRank) {
        this.lastRank = lastRank;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void reduceCount() {
        this.count--;
    }

    public void reduceTotal() {
        this.total--;
    }

    public void addCount() {
        this.count++;
    }

    public void addTotal() {
        this.total++;
    }

    public long getRecoverTime() {
        return recoverTime;
    }

    public void setRecoverTime(long recoverTime) {
        this.recoverTime = recoverTime;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public List<Integer> getFightIds() {
        return fightIds;
    }

    public void setFightIds(List<Integer> fightIds) {
        this.fightIds = fightIds;
    }

    public int getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(int buyCount) {
        this.buyCount = buyCount;
    }

    public void addBuyCount() {
        this.buyCount++;
    }

    public void addWin() {
        this.win++;
    }

    //去掉天梯连胜的机制
    public void addConWin() {
        //this.conwin++;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public void addLose() {
        this.lose++;
    }

    public int getLastStar() {
        return lastStar;
    }

    public void setLastStar(int lastStar) {
        this.lastStar = lastStar;
    }

    public int getLastTotal() {
        return lastTotal;
    }

    public void setLastTotal(int lastTotal) {
        this.lastTotal = lastTotal;
    }

    public int getLastWin() {
        return lastWin;
    }

    public void setLastWin(int lastWin) {
        this.lastWin = lastWin;
    }

    public String toFightIdString() {
        StringBuilder fightIdSb = new StringBuilder();
        for (int fightId : this.fightIds) {
            fightIdSb.append(fightId).append(",");
        }
        return fightIdSb.toString();
    }

}
