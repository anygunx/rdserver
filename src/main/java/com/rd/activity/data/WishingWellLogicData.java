package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

public class WishingWellLogicData extends BaseActivityLogicData {

    private List<DropData> rewards;

    private List<Integer> prices;

    private List<Integer> first;

    private List<Integer> second;

    private List<Integer> third;

    @Override
    public String getKey() {
        return round + "";
    }

    @Override
    public void loadData(Element root) {
        rewards = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "rewards"));
        prices = StringUtil.getIntList(XmlUtils.getAttribute(root, "prices"), ",");
        first = StringUtil.getIntList(XmlUtils.getAttribute(root, "first"), ",");
        second = StringUtil.getIntList(XmlUtils.getAttribute(root, "second"), ",");
        third = StringUtil.getIntList(XmlUtils.getAttribute(root, "third"), ",");
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public List<Integer> getPrices() {
        return prices;
    }

    public List<Integer> getFirst() {
        return first;
    }

    public List<Integer> getSecond() {
        return second;
    }

    public List<Integer> getThird() {
        return third;
    }

}
