package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class GangFightRankData {

    private byte id;

    private String title;

    private String content;

    private List<DropData> reward;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
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

    public List<DropData> getReward() {
        return reward;
    }

    public void setReward(List<DropData> reward) {
        this.reward = reward;
    }
}
