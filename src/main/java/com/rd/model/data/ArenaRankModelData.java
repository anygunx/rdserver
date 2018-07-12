package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class ArenaRankModelData {

    private final byte level;
    /**
     * 奖励倍率
     **/
    private final List<DropData> rewards;
    /**
     * 排名范围:最低
     **/
    private final int lowRank;
    /**
     * 排名范围:最高
     **/
    private final int highRank;
    //标题
    private String title;
    //内容
    private String content;

    public ArenaRankModelData(byte level, List<DropData> rewards, int lowRank, int highRank) {
        this.level = level;
        this.rewards = rewards;
        this.lowRank = lowRank;
        this.highRank = highRank;
    }

    public byte getLevel() {
        return level;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public int getLowRank() {
        return lowRank;
    }

    public int getHighRank() {
        return highRank;
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
