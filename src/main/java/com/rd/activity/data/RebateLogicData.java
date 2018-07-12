package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

public class RebateLogicData extends BaseActivityLogicData {

    private byte id;

    private int price;

    private List<DropData> rewards;

    private byte pre = 0;

    @Override
    public String getKey() {
        return id + "";
    }

    @Override
    public void loadData(Element root) {
        id = Byte.valueOf(XmlUtils.getAttribute(root, "id"));
        price = Integer.valueOf(XmlUtils.getAttribute(root, "price"));
        rewards = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "rewards"));
        String ps = XmlUtils.getAttribute(root, "pre");
        if (!StringUtil.isEmpty(ps))
            pre = Byte.valueOf(ps);
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
    }

    public byte getPre() {
        return pre;
    }

    public void setPre(byte pre) {
        this.pre = pre;
    }

}
