package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

public class LogonLogicData extends BaseActivityLogicData {

    private byte days;

    private List<DropData> rewards;

    @Override
    public String getKey() {
        return days + "";
    }

    @Override
    public void loadData(Element root) {
        this.days = Byte.valueOf(XmlUtils.getAttribute(root, "days"));
        this.rewards = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "reward"));
    }

    public byte getDays() {
        return days;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

}
