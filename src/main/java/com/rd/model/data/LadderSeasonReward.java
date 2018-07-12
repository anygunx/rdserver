package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

/**
 * 天梯竞技场奖励数据
 *
 * @author Created by U-Demon on 2016年10月18日 上午11:10:13
 * @version 1.0.0
 */
public class LadderSeasonReward {

    //段位
    private int rank;

    //奖励
    private List<DropData> reward;

    //标题
    private String title;

    //内容
    private String content;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public List<DropData> getReward() {
        return reward;
    }

    public void setReward(List<DropData> reward) {
        this.reward = reward;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
