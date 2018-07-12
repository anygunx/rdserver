package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.ArrayList;
import java.util.List;

public class FengCeModelData {

    private byte min;

    private byte max;

    private String title;

    private String content;

    private List<DropData> rewards = new ArrayList<>();

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

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
    }

}
