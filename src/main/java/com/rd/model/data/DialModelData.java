package com.rd.model.data;

import com.google.common.collect.ImmutableList;
import com.rd.bean.drop.DropData;
import com.rd.bean.drop.DropGroupData;
import com.rd.model.DropModel;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by XingYun on 2017/1/19.
 */
public class DialModelData {
    private int id;

    private int oneKeyCount;

    private List<DropData> cost;

    private List<DropData> costOneKey;

    private List<DropData> money;

    private List<DropData> moneyOneKey;

    /**
     * 热更支持
     **/
    private short gainId;
    //private DropGroupData drop;

    private DropData points;

    private DropData pointsOneKey;

    public DialModelData(int id, int oneKeyCount,
                         List<DropData> cost, List<DropData> costOneKey,
                         List<DropData> money, List<DropData> moneyOneKey,
                         short gainId, DropData points, DropData pointsOneKey) {
        this.id = id;
        this.oneKeyCount = oneKeyCount;
        this.cost = ImmutableList.copyOf(cost);
        this.costOneKey = ImmutableList.copyOf(costOneKey);
        this.money = ImmutableList.copyOf(money);
        this.moneyOneKey = ImmutableList.copyOf(moneyOneKey);
        this.gainId = gainId;
        this.points = points;
        this.pointsOneKey = pointsOneKey;
    }

    public int getId() {
        return id;
    }

    public int getOneKeyCount() {
        return oneKeyCount;
    }

    public List<DropData> getCost() {
        return cost;
    }

    public List<DropData> getCostOneKey() {
        return costOneKey;
    }

    public List<DropData> getMoney() {
        return money;
    }

    public List<DropData> getMoneyOneKey() {
        return moneyOneKey;
    }

    public short getGainId() {
        return gainId;
    }

    public DropData getPoints() {
        return points;
    }

    public DropData getPointsOneKey() {
        return pointsOneKey;
    }

    public DropGroupData getDrop() {
        return DropModel.getDropGroupData(gainId);
    }

    public static DialModelData create(Element root) {
        int id = Integer.valueOf(XmlUtils.getAttribute(root, "id"));
        int oneKeyCount = Integer.valueOf(XmlUtils.getAttribute(root, "oneKeyCount"));
        List<DropData> cost = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "cost"));
        List<DropData> costOneKey = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "costOneKey"));
        List<DropData> money = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "money"));
        List<DropData> moneyOneKey = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "moneyOneKey"));
        short gainId = Short.valueOf(XmlUtils.getAttribute(root, "gainId"));
        DropData points = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "points"));
        DropData pointsOneKey = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "pointsOneKey"));
        return new DialModelData(id, oneKeyCount, cost, costOneKey, money, moneyOneKey, gainId, points, pointsOneKey);
    }
}
