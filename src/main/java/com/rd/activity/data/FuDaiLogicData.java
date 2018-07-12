package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class FuDaiLogicData extends BaseActivityLogicData {

    //物品消耗
    private DropData costItem;

    //元宝消耗
    private DropData costDiamond;

    //掉落组
    private short dropId;

    //特殊奖励
    private int[][] spec = null;

    @Override
    public String getKey() {
        return round + "";
    }

    @Override
    public void loadData(Element root) {
        costItem = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "cost1"));
        costDiamond = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "cost2"));
        dropId = Short.valueOf(XmlUtils.getAttribute(root, "gain"));
        int index = 1;
        List<Integer> list = new ArrayList<>();
        while (true) {
            String time = XmlUtils.getAttribute(root, "time" + index);
            String reward = XmlUtils.getAttribute(root, "reward" + index);
            if (time.length() == 0 || reward.length() == 0)
                break;
            String[] times = time.split(",");
            list.add(Integer.valueOf(times[0]));
            list.add(Integer.valueOf(times[1]));
            String[] rewards = reward.split(",");
            list.add(Integer.valueOf(rewards[0]));
            list.add(Integer.valueOf(rewards[1]));
            list.add(Integer.valueOf(rewards[2]));
            index++;
        }
        spec = new int[list.size() / 5][5];
        for (int i = 0; i < list.size(); i++) {
            int value = list.get(i);
            spec[i / 5][i % 5] = value;
        }
    }

    public DropData getCostItem() {
        return costItem;
    }

    public DropData getCostDiamond() {
        return costDiamond;
    }

    public short getDropId() {
        return dropId;
    }

    public int[][] getSpec() {
        return spec;
    }

}
