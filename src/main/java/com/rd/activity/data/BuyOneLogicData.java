package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * 一元抢购
 *
 * @author Created by U-Demon on 2017年2月16日 下午2:26:27
 * @version 1.0.0
 */
public class BuyOneLogicData extends BaseActivityLogicData {

    private int id;

    private int chongzhi;

    private DropData cost;

    private List<DropData> reward;

    private int max;

    @Override
    public String getKey() {
        return id + "";
    }

    @Override
    public void loadData(Element root) {
        id = Integer.valueOf(XmlUtils.getAttribute(root, "id"));
        chongzhi = Integer.valueOf(XmlUtils.getAttribute(root, "chongzhi"));
        cost = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "cost"));
        reward = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "reward"));
        max = Integer.valueOf(XmlUtils.getAttribute(root, "max"));
    }

    public int getId() {
        return id;
    }

    public int getChongzhi() {
        return chongzhi;
    }

    public DropData getCost() {
        return cost;
    }

    public List<DropData> getReward() {
        return reward;
    }

    public int getMax() {
        return max;
    }

}
