package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * 累计充值逻辑数据
 *
 * @author Created by U-Demon on 2016年12月26日 下午3:23:45
 * @version 1.0.0
 */
public class ConsumCumulateLogicData extends BaseActivityLogicData {

    private int id;

    private List<DropData> rewards;

    private String title;

    private String content;

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public void loadData(Element root) {
        id = Integer.valueOf(XmlUtils.getAttribute(root, "cost"));
        rewards = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "reward"));
        title = XmlUtils.getAttribute(root, "title");
        content = XmlUtils.getAttribute(root, "content");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
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
