package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * @author wh
 */
public class KamPoLogicData3 extends BaseActivityLogicData {
    private int id;

    private int day;//服务器开服天数

    private int diaoluo;//权值

    private DropData reward;//奖励

    private DropData prices;//元宝

    private DropData prices10; //元宝10

    private int type;

    private int cost;

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public void loadData(Element root) {
        id = Integer.valueOf(XmlUtils.getAttribute(root, "id"));
        day = Integer.valueOf(XmlUtils.getAttribute(root, "round"));
        diaoluo = Integer.valueOf(XmlUtils.getAttribute(root, "diaoluo"));
        type = Integer.valueOf(XmlUtils.getAttribute(root, "type"));
        cost = Integer.valueOf(XmlUtils.getAttribute(root, "cost"));
        reward = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "reward"));
        prices = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "prices"));
        prices10 = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "prices10"));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDiaoluo() {
        return diaoluo;
    }

    public void setDiaoluo(int diaoluo) {
        this.diaoluo = diaoluo;
    }

    public DropData getReward() {
        return reward;
    }

    public void setReward(DropData reward) {
        this.reward = reward;
    }

    public DropData getPrices() {
        return prices;
    }

    public void setPrices(DropData prices) {
        this.prices = prices;
    }

    public DropData getPrices10() {
        return prices10;
    }

    public void setPrices10(DropData prices10) {
        this.prices10 = prices10;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
