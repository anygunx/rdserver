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
import java.util.List;
import java.util.Map;

public class PuzzleLogicData extends BaseActivityLogicData {
    private byte id;

    private DropData cost;

    private Map<Integer, List<DropData>> rewards = new HashMap<>();

    @Override
    public String getKey() {
        return cost.getG() + "";
    }

    @Override
    public void loadData(Element root) {
        this.id = Byte.valueOf(XmlUtils.getAttribute(root, "id"));
        this.cost = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "cost"));
        loadRewardData(GameDefine.RES_PATH);
    }

    private void loadRewardData(String path) {
        final File file = new File(path, "gamedata/pintureward.xml");
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
                    Element[] elements = XmlUtils.getChildrenByName(root, "logic");
                    for (Element element : elements) {
                        int id = Integer.valueOf(XmlUtils.getAttribute(element, "id"));
                        List<DropData> reward = StringUtil.getRewardDropList(XmlUtils.getAttribute(element, "reward"));
                        rewards.put(id, reward);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "pintureward";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public byte getId() {
        return id;
    }

    public DropData getCost() {
        return cost;
    }

    public void setCost(DropData cost) {
        this.cost = cost;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public Map<Integer, List<DropData>> getRewards() {
        return rewards;
    }

    public void setRewards(Map<Integer, List<DropData>> rewards) {
        this.rewards = rewards;
    }
}
