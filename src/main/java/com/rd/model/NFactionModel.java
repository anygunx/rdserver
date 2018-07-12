package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.enumeration.EAttr;
import com.rd.game.event.EGameEventType;
import com.rd.model.data.faction.*;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

/***
 *
 * 帮会加载表
 * */
public class NFactionModel {
    private static Logger logger = Logger.getLogger(NFactionModel.class);
    private static Map<Short, NFactionLevelData> factionLevelMap;
    private static Map<Short, NFactionMeiRiData> factionMeiRiMap;
    private static Map<Integer, NFactionSXData> factionShangXiangMap;
    private static Map<Integer, NFactionSkillData> factionSkillMap;
    private static Map<String, NFactionSkillData> factionSkillKeyMap;
    private static Map<Integer, NFactionActiveData> factionActiveMap;
    private static Map<Integer, NFactionTaskData> factionTaskMap;

    private static Map<Integer, NFactionSXRewardData> factionSXRewardsMap;

    public static void loadModelData(String path) {
        loadFactionModelData(path);
        loadFactionMeiRiModelData(path);
        loadFactionShangXiangModelData(path);
        loadFactionSkillModelData(path);
        loadFactionActiveModelData(path);
        loadFactionSXRewardsData(path);
        loadFactionTaskModelData(path);
    }

    /**
     * 帮派升级
     *
     * @param path
     */
    public static void loadFactionModelData(String path) {
        final File file = new File(path, "gamedata/clublevel.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, NFactionLevelData> temp = new HashMap<>();

                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        short lv = Short.parseShort(key);
                        int exp = value.getInt("exp");
                        int num = value.getInt("num");
                        NFactionLevelData data = new NFactionLevelData(lv, exp, num);
                        temp.put(lv, data);
                    }
                    factionLevelMap = temp;

                } catch (IOException e) {
                    logger.error("加经帮会升级数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "factionlevelModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }


    /**
     * 帮派每日
     *
     * @param path
     */
    public static void loadFactionMeiRiModelData(String path) {
        final File file = new File(path, "gamedata/clubmeiri.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, NFactionMeiRiData> temp = new HashMap<>();

                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        short num = (short) value.getInt("num");
                        List<DropData> rewards = new ArrayList<>();//.parseDropDataList(value.getString("reward"));
                        NFactionMeiRiData data = new NFactionMeiRiData(id, num, rewards);
                        temp.put(num, data);
                    }
                    factionMeiRiMap = temp;

                } catch (IOException e) {
                    logger.error("加经帮会每日数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "factionMeiRiModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }


    /**
     * 帮派上香
     *
     * @param path
     */
    public static void loadFactionShangXiangModelData(String path) {
        final File file = new File(path, "gamedata/clubfrag.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NFactionSXData> temp = new HashMap<>();

                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        DropData cost = GameCommon.parseDropData(value.getString("cost"));
                        List<DropData> reward_capital = GameCommon.parseDropDataList(value.getString("reward_capital"));
                        DropData reward_gong = GameCommon.parseDropData(value.getString("reward_gong"));
                        DropData reward_fire = GameCommon.parseDropData(value.getString("reward_fire"));
                        NFactionSXData data = new NFactionSXData(id, cost, reward_capital, reward_gong, reward_fire);
                        temp.put(id, data);
                    }
                    factionShangXiangMap = temp;

                } catch (IOException e) {
                    logger.error("加经帮会上香数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "factionShangXiangModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }

    /**
     * 帮派技能
     *
     * @param path
     */
    public static void loadFactionSkillModelData(String path) {
        final File file = new File(path, "gamedata/clubskill.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NFactionSkillData> temp = new HashMap<>();
                Map<String, NFactionSkillData> keyTemp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        byte pos = (byte) value.getInt("pos");
                        int[] attr = EAttr.getIntAttr(value);
                        short level = (short) value.getInt("level");
                        List<DropData> cost_item = GameCommon.parseDropDataList(value.getString("cost_item"));

                        NFactionSkillData data = new NFactionSkillData(id, pos, cost_item, attr);
                        temp.put(id, data);
                        keyTemp.put(getKey(pos, level), data);
                    }
                    factionSkillMap = temp;
                    factionSkillKeyMap = keyTemp;

                } catch (IOException e) {
                    logger.error("加经帮会技能数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "factionskillModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }


    /**
     * 帮派活跃
     *
     * @param path
     */
    public static void loadFactionActiveModelData(String path) {
        final File file = new File(path, "gamedata/clubactive.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NFactionActiveData> temp = new HashMap<>();

                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        int exp = value.getInt("exp");
                        short level = (short) value.getInt("lv");
                        int[] attr = EAttr.getIntAttr(value);
                        List<DropData> reward = GameCommon.parseDropDataList(value.getString("reward"));
                        NFactionActiveData data = new NFactionActiveData(level, exp, reward, attr);
                        temp.put(id, data);

                    }
                    factionActiveMap = temp;
                } catch (IOException e) {
                    logger.error("加经帮会活跃数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "factionactiveModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }


    /**
     * 帮派任务
     *
     * @param path
     */
    public static void loadFactionTaskModelData(String path) {
        final File file = new File(path, "gamedata/clubtask.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NFactionTaskData> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        int num = value.getInt("num");
                        int type = value.getInt("type");
                        int reward = value.getInt("reward");
                        //List<DropData> rewards=GameCommon.parseDropDataList(value.getString("reward"));
                        NFactionTaskData data = new NFactionTaskData(id, num, reward);
                        temp.put(id, data);
                        EGameEventType eventType = EGameEventType.getEventType(type);
                        eventType.setGangMission((byte) data.getId());
                    }
                    factionTaskMap = temp;
                } catch (IOException e) {
                    logger.error("加经帮会任务数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "factionTaskModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    /**
     * 帮派上香 奖励
     *
     * @param path
     */
    public static void loadFactionSXRewardsData(String path) {
        final File file = new File(path, "gamedata/clubfragreward.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NFactionSXRewardData> temp = new HashMap<>();

                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);

                        int needFrag = value.getInt("need_frag");
                        List<DropData> rewards = GameCommon.parseDropDataList(value.getString("reward"));
                        NFactionSXRewardData data = new NFactionSXRewardData(id, rewards, needFrag);
                        temp.put(id, data);

                    }
                    factionSXRewardsMap = temp;
                } catch (IOException e) {
                    logger.error("加经帮会上香奖励数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "factionfragRewardModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }


    private static String getKey(byte pos, short level) {
        return pos + "_" + level;

    }

    public static NFactionLevelData getNFactionLevelData(short lv) {
        return factionLevelMap.get(lv);
    }

    public static Map<Short, NFactionLevelData> getNFactionLevelMap() {
        return factionLevelMap;
    }


    public static Map<Short, NFactionMeiRiData> getNFactionMeiRiDataMap() {
        return factionMeiRiMap;
    }

    public static Map<Integer, NFactionSXData> getNFactionSXDataMap() {
        return factionShangXiangMap;
    }

    public static Map<Integer, NFactionSkillData> getNFactionSkillDataMap() {

        return factionSkillMap;
    }

    public static Map<String, NFactionSkillData> getfactionSkillKeyMap() {

        return factionSkillKeyMap;
    }

    public static NFactionSkillData getfactionSkillKey(byte pos, short level) {

        return factionSkillKeyMap.get(getKey(pos, level));
    }

    public static Map<Integer, NFactionActiveData> getNFactionActiveMap() {
        return factionActiveMap;
    }


    public static NFactionActiveData getNFactionActiveData(int level) {
        return factionActiveMap.get(level);
    }


    public static NFactionTaskData getNFactionTaskMap(int type) {
        return factionTaskMap.get(type);
    }

    public static NFactionSXRewardData getNFactionSXRewardDataMap(int id) {
        return factionSXRewardsMap.get(id);
    }


}
