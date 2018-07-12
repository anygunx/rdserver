package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

/**
 * 天梯竞技场每段信息
 *
 * @author Created by U-Demon on 2016年10月18日 上午11:10:13
 * @version 1.0.0
 */
public class LadderModelData {

    //段位
    private int rank;

    //阶段
    private int grade;

    //最大星级
    private int maxStar;

    //胜利奖励
    private List<DropData> winReward;

    //失败奖励
    private List<DropData> lostReward;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getMaxStar() {
        return maxStar;
    }

    public void setMaxStar(int maxStar) {
        this.maxStar = maxStar;
    }

    public List<DropData> getWinReward() {
        return winReward;
    }

    public void setWinReward(List<DropData> winReward) {
        this.winReward = winReward;
    }

    public List<DropData> getLostReward() {
        return lostReward;
    }

    public void setLostReward(List<DropData> lostReward) {
        this.lostReward = lostReward;
    }

}
