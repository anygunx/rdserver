package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

public class RankLogicData extends BaseActivityLogicData {

    private int type;

    private int lowRank;

    private int highRank;

    private List<DropData> rewards;

    @Override
    public String getKey() {
        return type + "_" + lowRank;
    }

    @Override
    public void loadData(Element root) {
        type = Integer.valueOf(XmlUtils.getAttribute(root, "type"));
        lowRank = Integer.valueOf(XmlUtils.getAttribute(root, "lowRank"));
        highRank = Integer.valueOf(XmlUtils.getAttribute(root, "highRank"));
        rewards = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "rate"));
    }

    public int getType() {
        return type;
    }

    public int getLowRank() {
        return lowRank;
    }

    public int getHighRank() {
        return highRank;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

}
