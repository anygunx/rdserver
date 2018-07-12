package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.EMissionUpdateType;
import com.rd.model.data.mission.*;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.*;

public class MissionModel {

    static Logger log = Logger.getLogger(MissionModel.class.getName());

    /**
     * 任务链
     **/
    private static short firstChainMissionId;
    private static Map<Short, MissionModelData> missionChainDataMap;
    /**
     * TODO delete
     **/
    private static Map<Short, MissionDailyModelData> missionDailyDataMap;
    /**
     * 龙珠每日任务
     */
    private static Map<Short, MissionDailyModelData> dragonBallMissions;
    /**
     * 龙珠任务宝箱
     **/
    private static Map<Byte, MissionBoxModelData> dragonBallBoxMap;
    /**
     * 成就任务
     **/
    private static Map<Short, MissionModelData> achievementMission;
    /**
     * 限时任务
     **/
    private static Map<Short, MissionModelData> timeLimitMission;
    /**
     * 限时任务组
     **/
    private static Map<Byte, TLMissionGroupModelData> tlGroup;

    /**
     * 卡牌任务
     **/
    private static Map<Short, MissionModelData> cardMission;
    /**
     * 卡牌奖励组
     **/
    private static Map<Byte, CardMissionReward> cardReward;

    private MissionModel() {

    }

    public static void loadMission(String path) {
        loadChainMission(path);
        loadDailyMission(path);
        loadDragonBallMission(path);
        loadDragonBallBox(path);
        loadAchievementMission(path);
        loadTimeLimitMission(path);
        loadTimeLimitGroup(path);
        loadCardMission(path);
        loadCardReward(path);
    }

    private static void loadCardReward(String path) {
        final File file = new File(path, "gamedata/taskCardReward.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, CardMissionReward> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "data");
                    for (int i = 0; i < elements.length; i++) {
                        byte id = Byte.parseByte(XmlUtils.getAttribute(elements[i], "id"));
                        Set<Byte> tasks = StringUtil.getByteSet(XmlUtils.getAttribute(elements[i], "taskList"), ",");
                        DropData dropData = StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "reward"));
                        CardMissionReward group = new CardMissionReward(id, tasks, dropData);
                        tmpMap.put(group.getId(), group);
                    }
                    cardReward = tmpMap;
                } catch (Exception e) {
                    log.error("加载卡牌奖励模型数据出错...");
                }
            }

            @Override
            public String toString() {
                return "taskCardRewardModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadCardMission(String path) {
        final File file = new File(path, "gamedata/taskCard.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, MissionModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "task");
                    for (int i = 0; i < elements.length; i++) {
                        short id = Short.parseShort(XmlUtils.getAttribute(elements[i], "id"));
                        short param = Short.parseShort(XmlUtils.getAttribute(elements[i], "param"));
                        // 关联事件 注：不可热更
                        int eventTypeId = Integer.valueOf(XmlUtils.getAttribute(elements[i], "eventType"));
                        EGameEventType eventType = EGameEventType.getEventType(eventTypeId);
                        EMissionUpdateType updateType = null;
                        if (eventType != null) {
                            int updateTypeId = Integer.valueOf(XmlUtils.getAttribute(elements[i], "updateType"));
                            updateType = EMissionUpdateType.getEventType(updateTypeId);
                            eventType.addCardMission(id, updateType);
                        }
                        //List<IConditionModelData> conditionList = ECondition.parseConditionModelList(elements[i], "c");
                        MissionModelData data = new MissionModelData(id, param, Collections.EMPTY_LIST, (short) 0, eventType, updateType);
                        tmpMap.put(data.getId(), data);
                    }
                    cardMission = tmpMap;
                } catch (Exception e) {
                    log.error("加载卡牌任务模型数据出错...");
                }
            }

            @Override
            public String toString() {
                return "taskCardModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadTimeLimitGroup(String path) {
        final File file = new File(path, "gamedata/taskCount.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, TLMissionGroupModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "taskCount");
                    for (int i = 0; i < elements.length; i++) {
                        byte id = Byte.parseByte(XmlUtils.getAttribute(elements[i], "id"));
                        long time = Integer.parseInt(XmlUtils.getAttribute(elements[i], "time")) * DateUtil.SECOND;
                        short suit = Short.parseShort(XmlUtils.getAttribute(elements[i], "suitsid"));
                        Set<Short> missions = StringUtil.getShortSet(XmlUtils.getAttribute(elements[i], "taskLimitid"), ",");
                        TLMissionGroupModelData group = new TLMissionGroupModelData(id, missions, suit, time);
                        tmpMap.put(group.getId(), group);
                    }
                    tlGroup = tmpMap;
                } catch (Exception e) {
                    log.error("加载限时任务组模型数据出错...");
                }
            }

            @Override
            public String toString() {
                return "taskCountModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadTimeLimitMission(String path) {
        final File file = new File(path, "gamedata/taskLimit.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, MissionModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "taskLimit");
                    for (int i = 0; i < elements.length; i++) {
                        short id = Short.parseShort(XmlUtils.getAttribute(elements[i], "id"));
                        short param = Short.parseShort(XmlUtils.getAttribute(elements[i], "param"));
                        // 关联事件 注：不可热更
                        int eventTypeId = Integer.valueOf(XmlUtils.getAttribute(elements[i], "eventType"));
                        EGameEventType eventType = EGameEventType.getEventType(eventTypeId);

                        List<DropData> dropDataList = StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "reward"));

                        int updateTypeId = Integer.valueOf(XmlUtils.getAttribute(elements[i], "updateType"));
                        EMissionUpdateType updateType = EMissionUpdateType.getEventType(updateTypeId);
                        eventType.addTLMission(id, updateType);

                        MissionModelData data = new MissionModelData(id, param, dropDataList, (short) 0, eventType, updateType);
                        tmpMap.put(data.getId(), data);
                    }
                    timeLimitMission = tmpMap;
                } catch (Exception e) {
                    log.error("加载限时任务模型数据出错...");
                }
            }

            @Override
            public String toString() {
                return "taskLimitModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadDragonBallBox(String path) {
        final File file = new File(path, "gamedata/taskDragonballBox.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, MissionBoxModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "data");
                    for (int i = 0; i < elements.length; i++) {
                        byte id = Byte.parseByte(XmlUtils.getAttribute(elements[i], "id"));
                        int score = Integer.parseInt(XmlUtils.getAttribute(elements[i], "score"));
                        List<DropData> dropDataList = StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "reward"));
                        MissionBoxModelData boxData = new MissionBoxModelData(id, score, dropDataList);
                        tmpMap.put(boxData.getId(), boxData);
                    }
                    dragonBallBoxMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载龙珠任务宝箱模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "taskDragonballBoxModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadAchievementMission(String path) {
        final File file = new File(path, "gamedata/taskAchievement.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, MissionModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "data");
                    for (int i = 0; i < elements.length; i++) {
                        short id = Short.parseShort(XmlUtils.getAttribute(elements[i], "id"));
                        short param = Short.parseShort(XmlUtils.getAttribute(elements[i], "param"));
                        //short next = Short.parseShort(XmlUtils.getAttribute(elements[i], "next"));

                        // 关联事件 注：不可热更
                        int eventTypeId = Integer.valueOf(XmlUtils.getAttribute(elements[i], "eventType"));
                        EGameEventType eventType = EGameEventType.getEventType(eventTypeId);

                        List<DropData> dropDataList = StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "reward"));

                        int updateTypeId = Integer.valueOf(XmlUtils.getAttribute(elements[i], "updateType"));
                        EMissionUpdateType updateType = EMissionUpdateType.getEventType(updateTypeId);
                        eventType.addAchievementMission(id, updateType);

                        MissionModelData data = new MissionModelData(id, param, dropDataList, (short) 0, eventType, updateType);
                        tmpMap.put(data.getId(), data);
                    }
                    achievementMission = tmpMap;
                } catch (Exception e) {
                    log.error("加载成就任务模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "taskAchievementModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadDragonBallMission(String path) {
        final File file = new File(path, "gamedata/taskDragonball.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, MissionDailyModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "taskDaily");
                    for (int i = 0; i < elements.length; i++) {
                        MissionDailyModelData data = new MissionDailyModelData();
                        data.setId(Short.parseShort(XmlUtils.getAttribute(elements[i], "id")));
                        data.setCount(Short.parseShort(XmlUtils.getAttribute(elements[i], "count")));
                        data.setScore(Short.parseShort(XmlUtils.getAttribute(elements[i], "score")));

                        // 关联事件 注：不可热更
                        int eventTypeId = Integer.valueOf(XmlUtils.getAttribute(elements[i], "eventType"));
                        EGameEventType eventType = EGameEventType.getEventType(eventTypeId);
                        eventType.setDragonballMissionId(data.getId());

                        tmpMap.put(data.getId(), data);
                    }
                    dragonBallMissions = tmpMap;
                } catch (Exception e) {
                    log.error("加载龙珠日常任务模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "taskDragonBallModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadChainMission(String path) {
        final File file = new File(path, "gamedata/taskChain.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, MissionModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "taskChain");
                    for (int i = 0; i < elements.length; i++) {
                        short id = Short.parseShort(XmlUtils.getAttribute(elements[i], "id"));
                        short param = Short.parseShort(XmlUtils.getAttribute(elements[i], "param"));
                        short next = Short.parseShort(XmlUtils.getAttribute(elements[i], "next"));

                        // 关联事件 注：不可热更
                        int eventTypeId = Integer.valueOf(XmlUtils.getAttribute(elements[i], "eventType"));
                        EGameEventType eventType = EGameEventType.getEventType(eventTypeId);

                        int updateTypeId = Integer.valueOf(XmlUtils.getAttribute(elements[i], "updateType"));
                        EMissionUpdateType updateType = EMissionUpdateType.getEventType(updateTypeId);
                        eventType.addChainMissions(id, updateType);

                        List<DropData> dropDataList = StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "reward"));
                        MissionModelData data = new MissionModelData(id, param, dropDataList, next, eventType, updateType);
                        tmpMap.put(data.getId(), data);

                        if (0 == i) {
                            firstChainMissionId = id;
                        }
                    }
                    missionChainDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载支线任务模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "missionChainModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadDailyMission(String path) {
        final File file = new File(path, "gamedata/taskDaily.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, MissionDailyModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "taskDaily");
                    for (int i = 0; i < elements.length; i++) {
                        MissionDailyModelData data = new MissionDailyModelData();
                        data.setId(Short.parseShort(XmlUtils.getAttribute(elements[i], "id")));
                        data.setCount(Short.parseShort(XmlUtils.getAttribute(elements[i], "count")));
                        data.setScore(Short.parseShort(XmlUtils.getAttribute(elements[i], "score")));

                        // 关联事件 注：不可热更
                        int eventTypeId = Integer.valueOf(XmlUtils.getAttribute(elements[i], "eventType"));
                        EGameEventType eventType = EGameEventType.getEventType(eventTypeId);
                        eventType.setDailyMission(data.getId());

                        tmpMap.put(data.getId(), data);
                    }
                    missionDailyDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载日常任务模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "missionDailyModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static MissionModelData getMissionChainData(short id) {
        return missionChainDataMap.get(id);
    }

    public static short getFirstChainMissionId() {
        return firstChainMissionId;
    }

    public static Map<Short, MissionDailyModelData> getMissionDailyDataMap() {
        return missionDailyDataMap;
    }

    public static MissionDailyModelData getMissionDailyData(short id) {
        return missionDailyDataMap.get(id);
    }

    public static MissionDailyModelData getDragonballMission(short id) {
        return dragonBallMissions.get(id);
    }

    public static Map<Short, MissionDailyModelData> getDragonBallMissions() {
        return dragonBallMissions;
    }

    public static MissionBoxModelData getDragonBallBox(byte id) {
        return dragonBallBoxMap.get(id);
    }

    public static MissionModelData getAchievementMission(short id) {
        return achievementMission.get(id);
    }

    public static MissionModelData getTLMission(short id) {
        return timeLimitMission.get(id);
    }

    public static TLMissionGroupModelData getTLGroup(byte id) {
        return tlGroup.get(id);
    }

    public static MissionModelData getCardMission(short id) {
        return cardMission.get(id);
    }

    public static CardMissionReward getCardReward(byte id) {
        return cardReward.get(id);
    }
}
