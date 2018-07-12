package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

public class SetWordsLogicData extends BaseActivityLogicData {

    private byte id;

    private List<DropData> cost;

    private DropData rewards;

    private int cishu;

    @Override
    public String getKey() {
        return id + "";
    }

    @Override
    public void loadData(Element root) {
        id = Byte.valueOf(XmlUtils.getAttribute(root, "id"));
        cost = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "cost"));
        rewards = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "reward"));
        cishu = Integer.valueOf(XmlUtils.getAttribute(root, "cishu"));
    }


    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public DropData getRewards() {
        return rewards;
    }

    public void setRewards(DropData rewards) {
        this.rewards = rewards;
    }

    public List<DropData> getCost() {
        return cost;
    }

    public void setCost(List<DropData> cost) {
        this.cost = cost;
    }

    public int getCishu() {
        return cishu;
    }

    public void setCishu(int cishu) {
        this.cishu = cishu;
    }
}
