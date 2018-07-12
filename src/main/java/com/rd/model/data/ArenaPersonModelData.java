package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class ArenaPersonModelData {

    private byte id;

    private byte min;

    private byte max;

    private List<DropData> rewards;

    private String title;

    private String content;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getMin() {
        return min;
    }

    public void setMin(byte min) {
        this.min = min;
    }

    public byte getMax() {
        return max;
    }

    public void setMax(byte max) {
        this.max = max;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
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
