package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * 限时有礼
 *
 * @author Created by U-Demon on 2016年12月27日 下午2:33:12
 * @version 1.0.0
 */
public class TLGiftLogicData extends BaseActivityLogicData {

    private byte id;

    private byte type;

    private int mubiao;

    private List<DropData> reward;

    @Override
    public String getKey() {
        return id + "";
    }

    @Override
    public void loadData(Element root) {
        id = Byte.valueOf(XmlUtils.getAttribute(root, "id"));
        type = Byte.valueOf(XmlUtils.getAttribute(root, "type"));
        mubiao = Integer.valueOf(XmlUtils.getAttribute(root, "mubiao"));
        reward = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "reward"));
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getMubiao() {
        return mubiao;
    }

    public void setMubiao(int mubiao) {
        this.mubiao = mubiao;
    }

    public List<DropData> getReward() {
        return reward;
    }

    public void setReward(List<DropData> reward) {
        this.reward = reward;
    }

}
