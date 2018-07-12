package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.define.GameDefine;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.DateUtil;
import com.rd.util.DiceUtil.Ele;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NoRepeatTurntableLogicData extends BaseActivityLogicData {

    public static final long REFRESH_SPACE = 48 * DateUtil.HOUR;


    private byte id;

    private int type;

    private int chance;

    private DropData rewards;

    private DropData price;

    private byte luck;

    private List<Ele> eles = new ArrayList<>();

    private List<NoRepeatTurntableTargetInfo> targetInfos = new ArrayList<>();

    @Override
    public String getKey() {
        return id + "";
    }

    @Override
    public void loadData(Element root) {
        id = Byte.valueOf(XmlUtils.getAttribute(root, "id"));
        type = Integer.valueOf(XmlUtils.getAttribute(root, "type"));
        chance = Integer.valueOf(XmlUtils.getAttribute(root, "diaoluo"));
        rewards = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "reward"));
        price = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "prices"));
        luck = Byte.valueOf(XmlUtils.getAttribute(root, "xingyunzhi"));
        this.loadTargetInfoData(GameDefine.RES_PATH);
        this.loadLuckChanceData(GameDefine.RES_PATH);
    }

    private void loadTargetInfoData(String path) {
        final File file = new File(path, "gamedata/zhizunzhuanpandabiao.xml");
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
                        int dabiao = Integer.valueOf(XmlUtils.getAttribute(element, "dabiao"));
                        int rewardId = Integer.valueOf(XmlUtils.getAttribute(element, "jiangli"));
                        NoRepeatTurntableTargetInfo nrtti = new NoRepeatTurntableTargetInfo(id, dabiao, rewardId);
                        targetInfos.add(nrtti);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "zhizunzhuanpandabiao";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private void loadLuckChanceData(String path) {
        final File file = new File(path, "gamedata/zhizunzhuanpanbaoji.xml");
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
                        int baoji = Integer.valueOf(XmlUtils.getAttribute(element, "baoji"));
                        int gailv = Integer.valueOf(XmlUtils.getAttribute(element, "gailv"));
                        Ele ele = new Ele(baoji, gailv);
                        eles.add(ele);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "zhizunzhuanpanbaoji";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public DropData getPrice() {
        return price;
    }

    public void setPrice(DropData price) {
        this.price = price;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public DropData getRewards() {
        return rewards;
    }

    public void setRewards(DropData rewards) {
        this.rewards = rewards;
    }

    public byte getLuck() {
        return luck;
    }

    public void setLuck(byte luck) {
        this.luck = luck;
    }

    public List<Ele> getEles() {
        return eles;
    }

    public void setEles(List<Ele> eles) {
        this.eles = eles;
    }

    public List<NoRepeatTurntableTargetInfo> getTargetInfos() {
        return targetInfos;
    }

    public void setTargetInfos(List<NoRepeatTurntableTargetInfo> targetInfos) {
        this.targetInfos = targetInfos;
    }


    public static class NoRepeatTurntableTargetInfo {
        private int id;
        private int segment;
        private int rewardId;

        public NoRepeatTurntableTargetInfo(int id, int segment, int rewardId) {
            super();
            this.id = id;
            this.segment = segment;
            this.rewardId = rewardId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getSegment() {
            return segment;
        }

        public void setSegment(int segment) {
            this.segment = segment;
        }

        public int getRewardId() {
            return rewardId;
        }

        public void setRewardId(int rewardId) {
            this.rewardId = rewardId;
        }

    }
}
