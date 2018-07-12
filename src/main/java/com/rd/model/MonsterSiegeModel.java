package com.rd.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.rd.bean.drop.DropData;
import com.rd.model.data.MonsterSiegeModelData;
import com.rd.model.data.RewardBoxModelData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 怪物攻城相关
 */
public class MonsterSiegeModel {
    private static final Logger logger = Logger.getLogger(MonsterSiegeModel.class);

    /**
     * 排行榜容量
     **/
    public static final int RANK_CAPACITY = 100;
    /**
     * 防守记录容量
     **/
    public static final int RECORD_CAPACITY = 20;
    /**
     * 战斗时间
     **/
    public static final long BATTLE_TIME = 30 * DateUtil.SECOND;
    /**
     * 挑战次数上限
     **/
    public static final byte MAX_TIMES = 3;
    /**
     * 次数恢复时间
     **/
    public static final long TIMES_INCREASE_INTERVAL = 2 * DateUtil.HOUR;
    /**
     * 服务器活动帧率
     **/
    public static final long UPDATE_INTERVAL = 5 * DateUtil.SECOND;

    //怪物攻城数据
    private static final String MONSTER_SIEGE_PATH = "gamedata/monstersiege.xml";
    private static final String MONSTER_SIEGE_NAME = "monstersiegeModel";

    private static Map<Short, MonsterSiegeModelData> monstersieges;
    //怪物攻城宝箱数据
    private static final String MONSTER_BOX_PATH = "gamedata/monsterbox.xml";
    private static final String MONSTER_BOX_NAME = "monsterboxModel";
    private static Map<Byte, RewardBoxModelData> monsterboxes;

    private MonsterSiegeModel() {

    }

    public static void loadData(String path) {
        loadMonsterSiege(path);
        loadMonsterBox(path);
    }

    private static void loadMonsterBox(String path) {
        final File file = new File(path, MONSTER_BOX_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, RewardBoxModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "monsterbox");
                    for (Element element : elements) {
                        byte id = Byte.parseByte(XmlUtils.getAttribute(element, "id"));
                        int score = Integer.parseInt(XmlUtils.getAttribute(element, "score"));
                        List<DropData> dropDataList = StringUtil.getRewardDropList(XmlUtils.getAttribute(element, "reward"));
                        RewardBoxModelData boxData = new RewardBoxModelData(id, score, dropDataList);
                        tmpMap.put(boxData.getId(), boxData);
                    }
                    monsterboxes = tmpMap;
                } catch (Exception e) {
                    logger.error("加载怪物攻城宝箱模型数据出错...");
                }
            }

            @Override
            public String toString() {
                return MONSTER_BOX_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    /**
     * 怪物攻城
     *
     * @param path
     */
    private static void loadMonsterSiege(String path) {
        final File file = new File(path, MONSTER_SIEGE_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, MonsterSiegeModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "monstersiege");
                    for (Element element : elements) {
                        short id = Short.parseShort(XmlUtils.getAttribute(element, "id"));
                        byte day = Byte.parseByte(XmlUtils.getAttribute(element, "day"));
                        short fighterId = Short.parseShort(XmlUtils.getAttribute(element, "fighterId"));
                        /** 逃跑时间 **/
                        int escapeTime = (int) (Integer.parseInt(XmlUtils.getAttribute(element, "escapeTime")) * DateUtil.SECOND);
                        /** 复活时间 **/
                        int deadTime = (int) (Integer.parseInt(XmlUtils.getAttribute(element, "deadTime")) * DateUtil.SECOND);
                        /** 攻击次数 **/
                        int attackTimes = Integer.parseInt(XmlUtils.getAttribute(element, "attackTimes"));
                        HashMap<Integer, Integer> scores = new HashMap<>();
                        for (int rank = 1; rank <= attackTimes; rank++) {
                            Integer score = Integer.parseInt(XmlUtils.getAttribute(element, "score" + rank));
                            scores.put(rank, score);
                        }
                        ArrayListMultimap<Integer, DropData> rewards = ArrayListMultimap.create();
                        for (int rank = 1; rank <= attackTimes; rank++) {
                            List<DropData> rewardList = StringUtil.getRewardDropList(XmlUtils.getAttribute(element, "reward" + rank));
                            rewards.putAll(rank, rewardList);
                        }
                        String title = XmlUtils.getAttribute(element, "title");
                        String content = XmlUtils.getAttribute(element, "content");
                        MonsterSiegeModelData modelData = new MonsterSiegeModelData(id, day, fighterId, escapeTime, deadTime, attackTimes, scores, title, content, rewards);
                        // checker
                        for (int rank = 1; rank <= attackTimes; rank++) {
                            List<DropData> rewardList = modelData.getRewardList(rank);
                            Preconditions.checkArgument(rewardList != null && !rewardList.isEmpty(),
                                    "GameMonsterData id=" + id + ",rank=" + rank + ",reward=null");
                        }
                        tmpMap.put(id, modelData);
                    }
                    monstersieges = tmpMap;
                } catch (Exception e) {
                    logger.error("加载攻城怪物模型数据出错...");
                }
            }

            @Override
            public String toString() {
                return MONSTER_SIEGE_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    public static RewardBoxModelData getMonsterBox(byte id) {
        return monsterboxes.get(id);
    }

    public static MonsterSiegeModelData getMonsterSiege(short id) {
        return monstersieges.get(id);
    }

    public static int getMonsterCount(int weekDay) {
        int counter = 0;
        for (MonsterSiegeModelData modelData : monstersieges.values()) {
            if (modelData.getDay() == weekDay) {
                counter++;
            }
        }
        return counter;
    }
}
