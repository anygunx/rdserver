package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * 宝石抽奖逻辑数据
 *
 * @author Created by U-Demon on 2017年2月13日 下午6:00:27
 * @version 1.0.0
 */
public class JewelLogicData extends BaseActivityLogicData {

    private int id;

    //十连抽
    private int oneKeyCount;

//	private DropData cost;

//	private DropData costOneKey;

    private DropData money;

    private DropData moneyOneKey;

//	private int points;

//	private int pointsOneKey;

    private short dropId;

    private short freeId;

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public void loadData(Element root) {
        id = Integer.valueOf(XmlUtils.getAttribute(root, "id"));
        oneKeyCount = Integer.valueOf(XmlUtils.getAttribute(root, "oneKeyCount"));
//		cost = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "cost"));
//		costOneKey = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "costOneKey"));
        money = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "money"));
        moneyOneKey = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "moneyOneKey"));
//		points = Integer.valueOf(XmlUtils.getAttribute(root, "points"));
//		pointsOneKey = Integer.valueOf(XmlUtils.getAttribute(root, "pointsOneKey"));
        dropId = Short.valueOf(XmlUtils.getAttribute(root, "gainId"));
        freeId = Short.valueOf(XmlUtils.getAttribute(root, "firstGain"));
    }

    public int getId() {
        return id;
    }

    public int getOneKeyCount() {
        return oneKeyCount;
    }

    public DropData getMoney() {
        return money;
    }

    public DropData getMoneyOneKey() {
        return moneyOneKey;
    }

    public short getDropId() {
        return dropId;
    }

    public short getFreeId() {
        return freeId;
    }

}
