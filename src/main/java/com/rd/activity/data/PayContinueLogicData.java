package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

public class PayContinueLogicData extends BaseActivityLogicData {
    private byte id;

    private int rmb;

    private String title;

    private String content;

    private List<DropData> rewards;

    @Override
    public String getKey() {
        return id + "";
    }

    @Override
    public void loadData(Element root) {
        this.id = Byte.valueOf(XmlUtils.getAttribute(root, "id"));
        this.rmb = Integer.valueOf(XmlUtils.getAttribute(root, "rmb"));
        this.title = XmlUtils.getAttribute(root, "title");
        this.content = XmlUtils.getAttribute(root, "content");
        this.rewards = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "reward"));
    }

    public byte getId() {
        return id;
    }

    public int getRmb() {
        return rmb;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<DropData> getRewards() {
        return rewards;
    }
}
