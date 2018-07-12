package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * 限时商城
 *
 * @author Created by U-Demon on 2016年12月27日 下午2:32:32
 * @version 1.0.0
 */
public class TLShopLogicData extends BaseActivityLogicData {

    private int id;

    private byte shopType;

    private DropData goods;

    private DropData price;

    private int max;

    @Override
    public String getKey() {
        return id + "";
    }

    @Override
    public void loadData(Element root) {
        id = Integer.valueOf(XmlUtils.getAttribute(root, "id"));
        shopType = Byte.valueOf(XmlUtils.getAttribute(root, "shopType"));
        price = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "price")).get(0);
        max = Integer.valueOf(XmlUtils.getAttribute(root, "max"));
        goods = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "goods")).get(0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DropData getGoods() {
        return goods;
    }

    public void setGoods(DropData goods) {
        this.goods = goods;
    }

    public byte getShopType() {
        return shopType;
    }

    public void setShopType(byte shopType) {
        this.shopType = shopType;
    }

    public DropData getPrice() {
        return price;
    }

    public void setPrice(DropData price) {
        this.price = price;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

}
