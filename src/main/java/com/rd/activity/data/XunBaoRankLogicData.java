package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

public class XunBaoRankLogicData extends BaseActivityLogicData {

    private byte id;

    private List<DropData> rewards;

    private String title;

    private String content;

    private int min;

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public void loadData(Element root) {
        id = Byte.valueOf(XmlUtils.getAttribute(root, "id"));
        id -= round * 5;
        rewards = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "rewards"));
        title = String.valueOf(XmlUtils.getAttribute(root, "title"));
        content = String.valueOf(XmlUtils.getAttribute(root, "content"));
        min = Integer.valueOf(XmlUtils.getAttribute(root, "jifen"));
    }

    public byte getId() {
        return id;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getMin() {
        return min;
    }

}
