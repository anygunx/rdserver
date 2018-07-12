package com.rd.activity.data;

import com.google.common.collect.ImmutableList;
import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by XingYun on 2017/1/19.
 */
public class SignLogicData extends BaseActivityLogicData {
    private int id;
    private List<DropData> rewardList;

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public void loadData(Element root) {
        id = Integer.valueOf(XmlUtils.getAttribute(root, "id"));
        rewardList = ImmutableList.copyOf(StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "rewards")));
    }

    public int getId() {
        return id;
    }

    public List<DropData> getRewardList() {
        return rewardList;
    }
}
