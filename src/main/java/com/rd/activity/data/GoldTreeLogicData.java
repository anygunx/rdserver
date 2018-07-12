package com.rd.activity.data;


import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * 摇钱树数据
 *
 * @author Created by U-Demon on 2016年12月27日 下午2:31:54
 * @version 1.0.0
 */
public class GoldTreeLogicData extends BaseActivityLogicData {

    private short id;

    private int gold;

    private DropData cost;

    private int doubleRate;

    private int addRate;

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public void loadData(Element root) {
        id = Short.valueOf(XmlUtils.getAttribute(root, "id"));
        gold = Integer.valueOf(XmlUtils.getAttribute(root, "gold"));
        cost = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "cost"));
        doubleRate = Integer.valueOf(XmlUtils.getAttribute(root, "baoji"));
        addRate = Integer.valueOf(XmlUtils.getAttribute(root, "baifenbi"));
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public DropData getCost() {
        return cost;
    }

    public void setCost(DropData cost) {
        this.cost = cost;
    }

    public int getDoubleRate() {
        return doubleRate;
    }

    public void setDoubleRate(int doubleRate) {
        this.doubleRate = doubleRate;
    }

    public int getAddRate() {
        return addRate;
    }

    public void setAddRate(int addRate) {
        this.addRate = addRate;
    }

}
