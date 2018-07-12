package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * 一折神通
 *
 * @author Created by U-Demon on 2017年2月17日 上午11:48:42
 * @version 1.0.0
 */
public class ShenTongLogicData extends BaseActivityLogicData {

    private int id;

    private DropData item;

    private DropData cost;

    private DropData reward;

    private int rewardTime;

    private int timeMax;

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public void loadData(Element root) {
        id = Integer.valueOf(XmlUtils.getAttribute(root, "id"));
        item = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "item"));
        cost = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "cost"));
        reward = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "reward"));
        rewardTime = Integer.valueOf(XmlUtils.getAttribute(root, "rewardTime"));
        timeMax = Integer.valueOf(XmlUtils.getAttribute(root, "timeMax"));
    }

    public int getId() {
        return id;
    }

    public DropData getItem() {
        return item;
    }

    public DropData getCost() {
        return cost;
    }

    public DropData getReward() {
        return reward;
    }

    public int getRewardTime() {
        return rewardTime;
    }

    public int getTimeMax() {
        return timeMax;
    }

}
