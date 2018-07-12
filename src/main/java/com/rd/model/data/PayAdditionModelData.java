package com.rd.model.data;

import com.google.common.collect.ImmutableList;
import com.rd.bean.drop.DropData;
import com.rd.model.PayModel;

import java.util.List;

public class PayAdditionModelData {
    private final int id;
    private final int times;
    private final String title;
    private final String content;
    private final List<DropData> rewardList;
    private final PayModel.ECounterType countType;


    public PayAdditionModelData(int id, int times, PayModel.ECounterType countType, String title, String content, List<DropData> rewardList) {
        this.id = id;
        this.times = times;
        this.countType = countType;
        this.title = title;
        this.content = content;
        this.rewardList = ImmutableList.copyOf(rewardList);
    }

    public int getId() {
        return id;
    }

    public int getTimes() {
        return times;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<DropData> getRewardList() {
        return rewardList;
    }

    public PayModel.ECounterType getCountType() {
        return countType;
    }
}
