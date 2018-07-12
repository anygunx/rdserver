package com.rd.activitynew.config.data;

import com.rd.bean.drop.DropData;

import java.util.List;

/**
 * 累计充值数据
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月3日下午4:03:08
 */
public class ActivityCumulatePayData {

    private byte id;

    private List<DropData> reward;

    private int cost;

    private String title;

    private String content;

    public ActivityCumulatePayData(byte id, List<DropData> reward, int cost, String title, String content) {
        this.id = id;
        this.reward = reward;
        this.cost = cost;
        this.title = title;
        this.content = content;
    }

    public byte getId() {
        return id;
    }

    public List<DropData> getReward() {
        return reward;
    }

    public int getCost() {
        return cost;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
