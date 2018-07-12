package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class WelfareModelData {

    private byte id;

    private int price;

    private List<DropData> rewards;

    private byte loop;

    private String title;

    private String content;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
    }

    public byte getLoop() {
        return loop;
    }

    public void setLoop(byte loop) {
        this.loop = loop;
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
