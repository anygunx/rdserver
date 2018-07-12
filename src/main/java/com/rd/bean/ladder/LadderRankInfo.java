package com.rd.bean.ladder;

import com.rd.bean.player.Player;
import com.rd.bean.player.SimplePlayer;
import com.rd.net.message.Message;

/**
 * 天梯排行榜信息
 *
 * @author Created by U-Demon on 2016年10月21日 下午6:26:03
 * @version 1.0.0
 */
public class LadderRankInfo extends SimplePlayer implements Comparable<LadderRankInfo> {

    private int star;

    private int rank;

    private int total;

    private int win;

    private int score;

    public void initTop(PlayerLadder ladder) {
        this.id = ladder.getPlayerId();
        this.star = ladder.getStar();
        this.total = ladder.getTotal();
        this.win = ladder.getGoal();
        this.score = ladder.getScore();
    }

    public void initHis(PlayerLadder ladder) {
        this.id = ladder.getPlayerId();
        this.star = ladder.getLastStar();
        this.total = ladder.getLastTotal();
        this.win = ladder.getLastWin() * 2 - ladder.getLastTotal();
    }

    public void init(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.head = player.getHead();
        this.rein = player.getRein();
        this.level = player.getLevel();
        this.vip = player.getVip();
        this.fighting = player.getFighting();
    }

    public void getMessage(Message message) {
        super.getSimpleMessage(message);
        message.setInt(rank);
        message.setInt(star);
        message.setInt(total);
        message.setInt(win);
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(LadderRankInfo o) {
        if (this.getId() == o.getId())
            return 0;
        if (this.getStar() < o.getStar())
            return 1;
        if (this.getStar() > o.getStar())
            return -1;
        if (this.getWin() * 2 - o.getTotal() <= o.getWin() * 2 - o.getTotal())
            return 1;
        else
            return -1;
    }

}
