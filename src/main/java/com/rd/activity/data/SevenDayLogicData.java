package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.define.EReach;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * 七日开服活动
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月24日下午2:43:31
 */
public class SevenDayLogicData extends BaseActivityLogicData {

    private byte round;

    private byte type;

    private byte id;

    private int target;

    private List<DropData> rewards;

    @Override
    public String getKey() {
        return round + "_" + type + "_" + id;
    }

    @Override
    public void loadData(Element root) {
        round = Byte.parseByte(XmlUtils.getAttribute(root, "round"));
        type = Byte.parseByte(XmlUtils.getAttribute(root, "type"));
        id = Byte.parseByte(XmlUtils.getAttribute(root, "id"));
        if (type == EReach.SUPER_SALES.getType()) {
            target = Integer.parseInt(XmlUtils.getAttribute(root, "target").split("#")[1]);
        } else {
            target = Integer.parseInt(XmlUtils.getAttribute(root, "target"));
        }
        rewards = GameCommon.parseDropDataList(XmlUtils.getAttribute(root, "rewards"));
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public int getTarget() {
        return target;
    }

    public List<DropData> getRewards() {
        return rewards;
    }
}
