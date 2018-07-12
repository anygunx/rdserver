package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.define.GameDefine;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.DiceUtil.Ele;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wh
 */
public class MonopolyLogicData1 extends BaseActivityLogicData {
    private int id;

    private int group;

    private int chance;

    private int stepNum;//步数

    private DropData reward;//奖励

    private List<DropData> prices0;//元宝

    private List<Ele> eles1 = new ArrayList<>();//一个筛子的概率

    private Map<Integer, StepInfo> stepInfos = new HashMap<>(); //步数

    private Map<Integer, LevelInfo> levelInfos = new HashMap<>();//层数

    private Map<Integer, JubaogeInfo> jubaogeInfos = new HashMap<>();

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public void loadData(Element root) {
        id = Integer.valueOf(XmlUtils.getAttribute(root, "id"));
        group = Integer.valueOf(XmlUtils.getAttribute(root, "group"));
        chance = Integer.valueOf(XmlUtils.getAttribute(root, "chance"));
        stepNum = Integer.valueOf(XmlUtils.getAttribute(root, "bushu"));
        reward = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "reward"));
        prices0 = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "prices"));
        loadDiceData(GameDefine.RES_PATH);
        loadLevelData(GameDefine.RES_PATH);
        loadStepData(GameDefine.RES_PATH);
        loadJubaogeData(GameDefine.RES_PATH);
    }

    private void loadJubaogeData(String path) {
        final File file = new File(path, "gamedata/jubaoge.xml");
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
                        int type = Integer.valueOf(XmlUtils.getAttribute(element, "type"));
                        List<DropData> cost = StringUtil.getRewardDropList(XmlUtils.getAttribute(element, "cost"));
                        List<DropData> reward = StringUtil.getRewardDropList(XmlUtils.getAttribute(element, "reward"));
                        JubaogeInfo jubaoge = new JubaogeInfo(id, type, cost, reward);
                        jubaogeInfos.put(id, jubaoge);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "jubaoge";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private void loadStepData(String path) {
        final File file = new File(path, "gamedata/dafuwengbushu1.xml");
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
                        int step = Integer.valueOf(XmlUtils.getAttribute(element, "bushu"));
                        StepInfo si = new StepInfo(id, step, reward);
                        stepInfos.put(id, si);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "dafuwengbushu1";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private void loadLevelData(String path) {
        final File file = new File(path, "gamedata/dafuwengcengshu1.xml");
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
                        int level = Integer.valueOf(XmlUtils.getAttribute(element, "cengshu"));
                        List<DropData> reward = StringUtil.getRewardDropList(XmlUtils.getAttribute(element, "reward"));
                        LevelInfo levelInfo = new LevelInfo(id, level, reward);
                        levelInfos.put(id, levelInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "dafuwengcengshu1";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private void loadDiceData(String path) {
        final File file = new File(path, "gamedata/dafuwengshaizi1.xml");
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
                        int num = Integer.valueOf(XmlUtils.getAttribute(element, "num"));
                        int diaoluo = Integer.valueOf(XmlUtils.getAttribute(element, "diaoluo"));
//                    	int type = Integer.valueOf(XmlUtils.getAttribute(element, "type"));
                        Ele ele = new Ele(num, diaoluo);
                        eles1.add(ele);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "dafuwengshaizi1";
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

    public DropData getReward() {
        return reward;
    }

    public void setReward(DropData reward) {
        this.reward = reward;
    }

    public List<DropData> getPrices0() {
        return prices0;
    }

    public void setPrices0(List<DropData> prices0) {
        this.prices0 = prices0;
    }

    public Map<Integer, StepInfo> getStepInfos() {
        return stepInfos;
    }

    public void setStepInfos(Map<Integer, StepInfo> stepInfos) {
        this.stepInfos = stepInfos;
    }

    public Map<Integer, LevelInfo> getLevelInfos() {
        return levelInfos;
    }

    public void setLevelInfos(Map<Integer, LevelInfo> levelInfos) {
        this.levelInfos = levelInfos;
    }

    public List<Integer> getAllDices() {
        List<Integer> list = new ArrayList<>();
        for (Ele ele : eles1) {
            list.add(ele.getId());
        }
        return list;
    }

    public List<Ele> getEles1() {
        return eles1;
    }

    public void setEles1(List<Ele> eles1) {
        this.eles1 = eles1;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public int getStepNum() {
        return stepNum;
    }

    public void setStepNum(int stepNum) {
        this.stepNum = stepNum;
    }

    public Map<Integer, JubaogeInfo> getJubaogeInfos() {
        return jubaogeInfos;
    }

    public void setJubaogeInfos(Map<Integer, JubaogeInfo> jubaogeInfos) {
        this.jubaogeInfos = jubaogeInfos;
    }


    public static class StepInfo {
        private int id;
        private int step;
        private List<DropData> reward;

        public StepInfo(int id, int step, List<DropData> reward) {
            super();
            this.id = id;
            this.step = step;
            this.reward = reward;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public List<DropData> getReward() {
            return reward;
        }

        public void setReward(List<DropData> reward) {
            this.reward = reward;
        }
    }

    public static class JubaogeInfo {
        private int id;
        private int type;
        private List<DropData> consume;
        private List<DropData> reward;

        public JubaogeInfo(int id, int type, List<DropData> consume, List<DropData> reward) {
            super();
            this.id = id;
            this.type = type;
            this.consume = consume;
            this.reward = reward;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<DropData> getConsume() {
            return consume;
        }

        public void setConsume(List<DropData> consume) {
            this.consume = consume;
        }

        public List<DropData> getReward() {
            return reward;
        }

        public void setReward(List<DropData> reward) {
            this.reward = reward;
        }
    }

    public static class LevelInfo {
        private int id;
        private int level;
        private List<DropData> reward;

        public LevelInfo(int id, int level, List<DropData> reward) {
            super();
            this.id = id;
            this.level = level;
            this.reward = reward;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public List<DropData> getReward() {
            return reward;
        }

        public void setReward(List<DropData> reward) {
            this.reward = reward;
        }
    }


}
