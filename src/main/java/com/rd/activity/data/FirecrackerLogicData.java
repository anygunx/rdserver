package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.define.GameDefine;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wh
 */
public class FirecrackerLogicData extends BaseActivityLogicData {
    private int id;

    private int round;//轮次

    private int type;

    private int diaoluo;//权值

    private int cost;//抽取次数 cost=1 : 抽取[0,1]次

    private DropData reward;//奖励

    private DropData prices0;//抽一次消耗

    private DropData prices1;//抽五次消耗

    private Map<Integer, DropData> luck_reward = new HashMap<>();//幸运值奖励

    private Map<Integer, Integer> luck_score = new HashMap<>();//幸运值奖励

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public void loadData(Element root) {
        id = Integer.valueOf(XmlUtils.getAttribute(root, "id"));
        round = Integer.valueOf(XmlUtils.getAttribute(root, "round"));
        type = Integer.valueOf(XmlUtils.getAttribute(root, "type"));
        cost = Integer.valueOf(XmlUtils.getAttribute(root, "cost"));
        diaoluo = Integer.valueOf(XmlUtils.getAttribute(root, "diaoluo"));
        reward = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "reward"));
        prices0 = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "prices"));
        prices1 = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "prices2"));
        loadLuckData(GameDefine.RES_PATH);
    }

    private void loadLuckData(String path) {
        final File file = new File(path, "gamedata/bianpaodabiao.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "xinfahc");
                    for (Element element : elements) {
                        int score = Integer.valueOf(XmlUtils.getAttribute(element, "id"));
                        DropData dd = StringUtil.getRewardDropData(XmlUtils.getAttribute(element, "reward"));
                        luck_reward.put(score, dd);
                    }
                    for (Element element : elements) {
                        int id = Integer.valueOf(XmlUtils.getAttribute(element, "id"));
                        int cishu = Integer.valueOf(XmlUtils.getAttribute(element, "cishu"));
                        luck_score.put(id, cishu);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "xingyundabiao";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDiaoluo() {
        return diaoluo;
    }

    public void setDiaoluo(int diaoluo) {
        this.diaoluo = diaoluo;
    }

    public DropData getReward() {
        return reward;
    }

    public void setReward(DropData reward) {
        this.reward = reward;
    }

    public DropData getPrices0() {
        return prices0;
    }

    public void setPrices0(DropData prices0) {
        this.prices0 = prices0;
    }

    public DropData getPrices1() {
        return prices1;
    }

    public void setPrices1(DropData prices1) {
        this.prices1 = prices1;
    }

    public Map<Integer, DropData> getLuck_reward() {
        return luck_reward;
    }

    public void setLuck_reward(Map<Integer, DropData> luck_reward) {
        this.luck_reward = luck_reward;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Map<Integer, Integer> getLuck_score() {
        return luck_score;
    }

    public void setLuck_score(Map<Integer, Integer> luck_score) {
        this.luck_score = luck_score;
    }


}
