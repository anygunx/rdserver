package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.lg.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * 限时限级限购
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月22日下午6:55:17
 */
public class LimitLimitLimitData extends BaseActivityLogicData {

    private byte id;

    private List<DropData> rewards;

    private DropData price;

    private byte vip;

    private short level;

    @Override
    public String getKey() {
        return Integer.toString(id + round * 1000);
    }

    @Override
    public void loadData(Element root) {
        id = Byte.parseByte(XmlUtils.getAttribute(root, "id"));
        rewards = GameCommon.parseDropDataList(XmlUtils.getAttribute(root, "rewards"));
        price = GameCommon.parseDropData(XmlUtils.getAttribute(root, "price"));
        vip = Byte.parseByte(XmlUtils.getAttribute(root, "vip"));
        level = Short.parseShort(XmlUtils.getAttribute(root, "needlevel"));
    }

    public byte getId() {
        return id;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public DropData getPrice() {
        return price;
    }

    public byte getVip() {
        return vip;
    }

    public short getLevel() {
        return level;
    }
}
