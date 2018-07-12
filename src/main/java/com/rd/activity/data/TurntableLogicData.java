package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * @author wh
 */
public class TurntableLogicData extends BaseActivityLogicData {

    private int id;

    private DropData reward;

    private int round;

    private List<Integer> gailvs;

    private List<Float> stalls;

    private String title;

    private String content;

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public void loadData(Element root) {
        id = Integer.valueOf(XmlUtils.getAttribute(root, "cost"));
        reward = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "reward"));
        round = Integer.valueOf(XmlUtils.getAttribute(root, "round"));
        gailvs = StringUtil.getGailvs(XmlUtils.getAttribute(root, "gailv"));
        stalls = StringUtil.getBeilvs(XmlUtils.getAttribute(root, "beilv"));
        title = XmlUtils.getAttribute(root, "title");
        content = XmlUtils.getAttribute(root, "content");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public DropData getReward() {
        return reward;
    }

    public void setReward(DropData reward) {
        this.reward = reward;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public List<Integer> getGailvs() {
        return gailvs;
    }

    public void setGailvs(List<Integer> gailvs) {
        this.gailvs = gailvs;
    }

    public List<Float> getStalls() {
        return stalls;
    }

    public void setStalls(List<Float> stalls) {
        this.stalls = stalls;
    }


}
