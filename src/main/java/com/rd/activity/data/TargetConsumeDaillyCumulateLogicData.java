package com.rd.activity.data;

import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * 每日累计消费达标
 *
 * @author wh
 */
public class TargetConsumeDaillyCumulateLogicData extends BaseActivityLogicData {

    private short id;

    private short dabiao;

    @Override
    public String getKey() {
        return round + "";
    }

    @Override
    public void loadData(Element root) {
        id = Short.valueOf(XmlUtils.getAttribute(root, "id"));
        dabiao = Short.valueOf(XmlUtils.getAttribute(root, "dabiao"));
    }

    public short getId() {
        return id;
    }

    public short getDabiao() {
        return dabiao;
    }

    public void setDabiao(short dabiao) {
        this.dabiao = dabiao;
    }
}
