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
public class KamPoLogicData extends BaseActivityLogicData {
    private int id;

    private int day;//服务器开服天数

    private int diaoluo;//权值

    private DropData reward;//奖励

    private DropData prices;//元宝

    private int luckScore;//幸运值

    private Map<Integer, DropData> luck_reward = new HashMap<>();//幸运值奖励

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public void loadData(Element root) {
        id = Integer.valueOf(XmlUtils.getAttribute(root, "id"));
        day = Integer.valueOf(XmlUtils.getAttribute(root, "round"));
        diaoluo = Integer.valueOf(XmlUtils.getAttribute(root, "diaoluo"));
        reward = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "reward"));
        prices = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "prices"));
        luckScore = Integer.valueOf(XmlUtils.getAttribute(root, "xingyunzhi"));
        loadLuckData(GameDefine.RES_PATH);
    }

    private void loadLuckData(String path) {
        final File file = new File(path, "gamedata/xingyundabiao.xml");
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
                    Element[] elements = XmlUtils.getChildrenByName(root, "data");
                    for (Element element : elements) {
                        int score = Integer.valueOf(XmlUtils.getAttribute(element, "score"));
                        DropData dd = StringUtil.getRewardDropData(XmlUtils.getAttribute(element, "reward"));
                        luck_reward.put(score, dd);
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
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

    public DropData getPrices() {
        return prices;
    }

    public void setPrices(DropData prices) {
        this.prices = prices;
    }

    public int getLuckScore() {
        return luckScore;
    }

    public void setLuckScore(int luckScore) {
        this.luckScore = luckScore;
    }

    public Map<Integer, DropData> getLuck_reward() {
        return luck_reward;
    }

    public void setLuck_reward(Map<Integer, DropData> luck_reward) {
        this.luck_reward = luck_reward;
    }
}
