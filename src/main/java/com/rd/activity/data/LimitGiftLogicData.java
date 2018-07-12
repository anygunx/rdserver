package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

public class LimitGiftLogicData extends BaseActivityLogicData {

    private byte id;

    private DropData price;

    private List<DropData> rewards;

    private int limit;

    private int max = 1;

    private float discount;

    @Override
    public String getKey() {
        return id + "";
    }

    @Override
    public void loadData(Element root) {
        id = Byte.valueOf(XmlUtils.getAttribute(root, "id"));
        String priceStr = XmlUtils.getAttribute(root, "price");
        if (!StringUtil.isEmpty(priceStr))
            price = StringUtil.getRewardDropData(priceStr);
        rewards = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "rewards"));
        String value = XmlUtils.getAttribute(root, "vip");
        if (value == null || value.length() == 0)
            value = XmlUtils.getAttribute(root, "level");
        limit = Integer.valueOf(value);
        String maxStr = XmlUtils.getAttribute(root, "max");
        if (!StringUtil.isEmpty(maxStr))
            max = Integer.valueOf(maxStr);
        String discountStr = XmlUtils.getAttribute(root, "zhekou");
        if (!StringUtil.isEmpty(discountStr))
            discount = Float.valueOf(discountStr);
    }

    public byte getId() {
        return id;
    }

    public DropData getPrice() {
        return price;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public int getLimit() {
        return limit;
    }

    public int getMax() {
        return max;
    }

    public float getDiscount() {
        return discount;
    }

}
