package com.rd.activity.data;

import com.google.common.collect.ImmutableList;
import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

public class PayFeastLogicData extends BaseActivityLogicData {

    private int cost;

    private List<DropData> rewards;

    @Override
    public String getKey() {
        return "0";
    }

    @Override
    public void loadData(Element root) {
        cost = Integer.valueOf(XmlUtils.getAttribute(root, "cost"));
        rewards = ImmutableList.copyOf(StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "reward")));
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
    }

}
