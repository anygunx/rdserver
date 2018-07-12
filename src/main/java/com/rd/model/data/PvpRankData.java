package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class PvpRankData {

    private short rankMin;

    private short rankMax;

    private List<DropData> rewardList;

    private String title;

    private String content;

    public short getRankMin() {
        return rankMin;
    }

    public void setRankMin(short rankMin) {
        this.rankMin = rankMin;
    }

    public short getRankMax() {
        return rankMax;
    }

    public void setRankMax(short rankMax) {
        this.rankMax = rankMax;
    }

    public List<DropData> getRewardList() {
        return rewardList;
    }

    public void setRewardList(List<DropData> rewardList) {
        this.rewardList = rewardList;
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
