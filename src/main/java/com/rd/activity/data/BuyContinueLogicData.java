package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * 持续购买
 *
 * @author Created by U-Demon on 2017年2月16日 上午11:11:28
 * @version 1.0.0
 */
public class BuyContinueLogicData extends BaseActivityLogicData {

    private int id;

    private DropData goods;

    private DropData price;

    private int max;

    private int pre;

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public void loadData(Element root) {
        id = Integer.valueOf(XmlUtils.getAttribute(root, "id"));
        goods = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "goods"));
        price = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "price"));
        max = Integer.valueOf(XmlUtils.getAttribute(root, "max"));
        pre = Integer.valueOf(XmlUtils.getAttribute(root, "pre"));
    }

    public int getId() {
        return id;
    }

    public DropData getGoods() {
        return goods;
    }

    public DropData getPrice() {
        return price;
    }

    public int getMax() {
        return max;
    }

    public int getPre() {
        return pre;
    }

}
