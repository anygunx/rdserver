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
import java.util.*;

/**
 * @author wh
 */
public class MonopolyLogicData extends BaseActivityLogicData {
    private int id;

    private int group;

    private DropData reward;//奖励

    private List<DropData> prices0;//元宝
    private List<DropData> prices1;//元宝

    private Map<Integer, Integer> rechargeMap = new LinkedHashMap<>();

    /**
     * 筛子概率
     * key = type[一次：1，两次：2]
     * value = [[1,2,3,4,5,6][2,3,4,5,6,7,8,9,10,11,12]]
     */
    private Map<Integer, List<Ele>> eleMap = new HashMap<>();//筛子的概率

    private List<Ele> eles1 = new ArrayList<>();//一个筛子的概率

    private List<Ele> eles2 = new ArrayList<>();//两个筛子的概率

    private Map<Integer, Map<Integer, DropData>> rewards = new HashMap<>(); //层数下步数奖励信息

    private Map<Integer, StepInfo> stepInfos = new HashMap<>(); //步数

    private Map<Integer, List<DropData>> levelInfos = new HashMap<>();//层数

    private Map<Integer, NumInfo> numInfos = new HashMap<>();//大富翁次数奖励


    @Override
    public String getKey() {
        return String.valueOf(group);
    }

    @Override
    public void loadData(Element root) {
        id = Integer.valueOf(XmlUtils.getAttribute(root, "id"));
        group = Integer.valueOf(XmlUtils.getAttribute(root, "group"));
        prices0 = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "prices"));
        prices1 = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "prices2"));
        loadDiceData(GameDefine.RES_PATH);
        loadNumDiceData(GameDefine.RES_PATH);
        loadLevelData(GameDefine.RES_PATH);
        loadStepDiceData(GameDefine.RES_PATH);
        loadRechargeData(GameDefine.RES_PATH);
    }

    private void loadLevelData(String path) {
        final File file = new File(path, "gamedata/dafuwengcengshu.xml");
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
                        int id = Integer.valueOf(XmlUtils.getAttribute(element, "cengshu"));
                        List<DropData> reward = StringUtil.getRewardDropList(XmlUtils.getAttribute(element, "reward"));
                        levelInfos.put(id, reward);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "dafuwengcengshu";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private void loadDiceData(String path) {
        final File file = new File(path, "gamedata/dafuwengshaizi.xml");
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
                        int type = Integer.valueOf(XmlUtils.getAttribute(element, "type"));
                        Ele ele = new Ele(num, diaoluo);
                        if (type == 1) {
                            eles1.add(ele);
                            eleMap.put(type, eles1);
                        } else if (type == 2) {
                            eles2.add(ele);
                            eleMap.put(type, eles2);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "dafuwengshaizi";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private void loadStepDiceData(String path) {
        final File file = new File(path, "gamedata/dafuwengcishu.xml");
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
                        int cishu = Integer.valueOf(XmlUtils.getAttribute(element, "cishu"));
                        List<DropData> reward = StringUtil.getRewardDropList(XmlUtils.getAttribute(element, "reward"));
                        NumInfo numInfo = new NumInfo(id, cishu, reward);
                        numInfos.put(id, numInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "dafuwengcishu";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private void loadNumDiceData(String path) {
        final File file = new File(path, "gamedata/dafuwengbushu.xml");
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
                        int group = Integer.valueOf(XmlUtils.getAttribute(element, "group"));
                        int step = Integer.valueOf(XmlUtils.getAttribute(element, "bushu"));
                        DropData reward = StringUtil.getRewardDropData(XmlUtils.getAttribute(element, "reward"));
                        StepInfo si = new StepInfo(id, step, group, reward);
                        stepInfos.put(id, si);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "dafuwengbushu";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private void loadRechargeData(String path) {
        final File file = new File(path, "gamedata/dafuwengchongzhi.xml");
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
                        int money = Integer.valueOf(XmlUtils.getAttribute(element, "cznum"));
                        int num = Integer.valueOf(XmlUtils.getAttribute(element, "cishu"));
                        rechargeMap.put(money, num);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "dafuwengchongzhi";
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

    public List<DropData> getPrices1() {
        return prices1;
    }

    public void setPrices1(List<DropData> prices1) {
        this.prices1 = prices1;
    }

    public Map<Integer, Map<Integer, DropData>> getRewards() {
        return rewards;
    }

    public void setRewards(Map<Integer, Map<Integer, DropData>> rewards) {
        this.rewards = rewards;
    }

    public Map<Integer, StepInfo> getStepInfos() {
        return stepInfos;
    }

    public void setStepInfos(Map<Integer, StepInfo> stepInfos) {
        this.stepInfos = stepInfos;
    }

    public Map<Integer, List<DropData>> getLevelInfos() {
        return levelInfos;
    }

    public void setLevelInfos(Map<Integer, List<DropData>> levelInfos) {
        this.levelInfos = levelInfos;
    }

    public Map<Integer, List<Ele>> getEleMap() {
        return eleMap;
    }

    public void setEleMap(Map<Integer, List<Ele>> eleMap) {
        this.eleMap = eleMap;
    }

    public Map<Integer, NumInfo> getNumInfos() {
        return numInfos;
    }

    public void setNumInfos(Map<Integer, NumInfo> numInfos) {
        this.numInfos = numInfos;
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

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public Map<Integer, Integer> getRechargeMap() {
        return rechargeMap;
    }

    public void setRechargeMap(Map<Integer, Integer> rechargeMap) {
        this.rechargeMap = rechargeMap;
    }


    public static class StepInfo {
        private int id;
        private int step;
        private int group;
        private DropData reward;

        public StepInfo(int id, int step, int group, DropData reward) {
            super();
            this.id = id;
            this.step = step;
            this.group = group;
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

        public DropData getReward() {
            return reward;
        }

        public void setReward(DropData reward) {
            this.reward = reward;
        }

        public int getGroup() {
            return group;
        }

        public void setGroup(int group) {
            this.group = group;
        }
    }


    public static class NumInfo {
        private int id;
        private int num;
        private List<DropData> dd;

        public NumInfo(int id, int num, List<DropData> dd) {
            super();
            this.id = id;
            this.num = num;
            this.dd = dd;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public List<DropData> getDd() {
            return dd;
        }

        public void setDd(List<DropData> dd) {
            this.dd = dd;
        }
    }
}
