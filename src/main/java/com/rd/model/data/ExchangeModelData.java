package com.rd.model.data;

import com.google.common.collect.ImmutableList;
import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * 兑换数据
 * Created by XingYun on 2017/1/18.
 */
public class ExchangeModelData {
    private int id;
    private List<DropData> consume;
    private List<DropData> reward;

    public ExchangeModelData(int id, List<DropData> consume, List<DropData> reward) {
        this.id = id;
        this.consume = ImmutableList.copyOf(consume);
        this.reward = ImmutableList.copyOf(reward);
    }

    public int getId() {
        return id;
    }

    public List<DropData> getConsume() {
        return consume;
    }

    public List<DropData> getReward() {
        return reward;
    }

    public static ExchangeModelData create(Element root) {
        int id = Integer.valueOf(XmlUtils.getAttribute(root, "id"));
        List<DropData> consume = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "consume"));
        List<DropData> reward = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "reward"));
        return new ExchangeModelData(id, consume, reward);
    }
}
