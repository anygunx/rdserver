package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

public class LeiChongLogicData extends BaseActivityLogicData {

    private byte day;

    private List<DropData> reward;

    private String title;

    private String content;

    @Override
    public String getKey() {
        return day + "";
    }

    @Override
    public void loadData(Element root) {
        day = Byte.valueOf(XmlUtils.getAttribute(root, "day"));
        reward = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "reward"));
        title = XmlUtils.getAttribute(root, "title");
        content = XmlUtils.getAttribute(root, "content");
    }

    public byte getDay() {
        return day;
    }

    public void setDay(byte day) {
        this.day = day;
    }

    public List<DropData> getReward() {
        return reward;
    }

    public void setReward(List<DropData> reward) {
        this.reward = reward;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
