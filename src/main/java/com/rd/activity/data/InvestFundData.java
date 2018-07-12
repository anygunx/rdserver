package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

public class InvestFundData extends BaseActivityLogicData {

    private byte id;

    private short level;

    private long power;

    private List<DropData> reward;

    @Override
    public String getKey() {
        return Byte.toString(id);
    }

    @Override
    public void loadData(Element root) {
        id = Byte.parseByte(XmlUtils.getAttribute(root, "id"));
        level = Short.parseShort(XmlUtils.getAttribute(root, "level"));
        power = Long.parseLong(XmlUtils.getAttribute(root, "power"));
        reward = GameCommon.parseDropDataList(XmlUtils.getAttribute(root, "rewards"));
    }

    public byte getId() {
        return id;
    }

    public short getLevel() {
        return level;
    }

    public long getPower() {
        return power;
    }

    public List<DropData> getReward() {
        return reward;
    }
}
