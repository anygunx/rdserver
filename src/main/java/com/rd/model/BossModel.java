package com.rd.model;

import com.rd.bean.player.Player;
import com.rd.model.data.*;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BossModel {

    private static Logger logger = Logger.getLogger(BossModel.class);

    //BOSS奖励数据
//	private static final String BOSS_REWARD_PATH = "gamedata/deletedbossrewards.xml";
//	private static final String BOSS_REWARD_NAME = "deletedModel";
    private static Map<Integer, BossRewardsData> rewardMap = new HashMap<>();

    private static final String BOSS_CONFIG_PATH = "gamedata/bossConfig.xml";
    private static final String BOSS_CONFIG_NAME = "bossConfigModel";

    //全民BOSS
    private static final String BOSS_CITIZEN_PATH = "gamedata/quanminboss.xml";
    private static final String BOSS_CITIZEN_NAME = "quanminbossModel";
    private static Map<Short, BossCitData> citMap;

    //转生BOSS
    private static final String BOSS_REIN_PATH = "gamedata/boss.xml";
    private static final String BOSS_REIN_NAME = "zhuanshengbossModel";
    private static Map<Short, BossReinData> reinMap;

    //转生BOSS奖励
    private static final String REIN_REWARD_PATH = "gamedata/bossrewards.xml";
    private static final String REIN_REWARD_NAME = "reinRewardModel";
    private static Map<Byte, ReinRewardsData> reinRewardMap = new HashMap<>();

    //秘境BOSS
    private static final String BOSS_MYSTERY_PATH = "gamedata/mijingboss.xml";
    private static final String BOSS_MYSTERY_NAME = "mysterybossModel";
    private static Map<Short, BossMysteryData> mysteryMap;

    //BOSS之家
    private static final String BOSS_VIP_PATH = "gamedata/vipBoss.xml";
    private static final String BOSS_VIP_NAME = "vipBossModel";
    private static Map<Byte, Map<Short, VipBossData>> vipBossMap;

    //探索的体力上限
    public static int[][] EXPLORE_POWER_MAX;
    //每日探索出BOSS的规则
    public static int[][] EXPLORE_BOSS_RULE;
    //同一时间BOSS最多存在多少个
    public static int EXPLORE_BOSS_EXIST;
    //体力恢复时间
    public static long EXPLORE_POWER_RECOVER;
    //死亡后多长时间消失
    public static long DISAPPEAR_TIME;
    //BOSS存在时间
    public static long EXIST_TIME;
    //BOSS的攻击CD
    public static int ATK_CD_TIME;
    //BOSS战次数上限
    public static int BOSS_FIGHT_MAX;
    //次数恢复时间
    public static int BOSS_FIGHT_TIME;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadBossConfig(path);
        loadBossCit(path);
        loadBossRein(path);
        loadReinReward(path);
        loadBossMystery(path);
        loadVipBoss(path);
    }

    private static void loadVipBoss(String path) {

        final File file = new File(path, BOSS_VIP_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, Map<Short, VipBossData>> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "vipboss");
                    for (int i = 0; i < elements.length; i++) {
                        VipBossData data = new VipBossData();
                        byte layer = Byte.valueOf(XmlUtils.getAttribute(elements[i], "layer"));
                        data.setId(Short.valueOf(XmlUtils.getAttribute(elements[i], "id")));
                        data.setLimitLv(Integer.valueOf(XmlUtils.getAttribute(elements[i], "limitLevel")));
                        data.setModelId(Short.valueOf(XmlUtils.getAttribute(elements[i], "modelId")));
                        data.setRewards(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "reward")));

                        if (tmpMap.containsKey(layer)) {
                            tmpMap.get(layer).put(data.getId(), data);
                        } else {
                            Map<Short, VipBossData> layerMap = new HashMap<>();
                            layerMap.put(data.getId(), data);
                            tmpMap.put(layer, layerMap);
                        }
                    }
                    vipBossMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载BOSS之家数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return BOSS_VIP_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }

    private static void loadBossMystery(String path) {

        final File file = new File(path, BOSS_MYSTERY_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, BossMysteryData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "mijingboss");
                    for (int i = 0; i < elements.length; i++) {
                        BossMysteryData data = new BossMysteryData();
                        data.setId(Short.valueOf(XmlUtils.getAttribute(elements[i], "id")));
                        data.setLimitLv(Integer.valueOf(XmlUtils.getAttribute(elements[i], "limitLevel")));
                        data.setFuhuoTime(Integer.valueOf(XmlUtils.getAttribute(elements[i], "fuhuotime")));
                        data.setModelId(Short.valueOf(XmlUtils.getAttribute(elements[i], "modelId")));
                        data.setRewards(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "reward")));
                        data.setLevelScope(XmlUtils.getAttribute(elements[i], "levelScope"));
                        data.setDropid(Short.valueOf(XmlUtils.getAttribute(elements[i], "dropid")));
                        tmpMap.put(data.getId(), data);
                    }
                    mysteryMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载秘境BOSS数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return BOSS_MYSTERY_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadBossConfig(String path) {
        final File file = new File(path, BOSS_CONFIG_PATH);
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
                    Element config = XmlUtils.getChildrenByName(root, "config")[0];
                    String rule = XmlUtils.getAttribute(config, "EXPLORE_BOSS_RULE");
                    String[] rules = rule.split("#");
                    int[][] tempArr = new int[rules.length][3];
                    for (int i = 0; i < rules.length; i++) {
                        String[] ruleArr = rules[i].split(",");
                        for (int j = 0; j < ruleArr.length; j++) {
                            tempArr[i][j] = Integer.valueOf(ruleArr[j]);
                        }
                    }
                    EXPLORE_BOSS_RULE = tempArr;
                    String powerStr = XmlUtils.getAttribute(config, "EXPLORE_POWER_MAX");
                    String[] powers = powerStr.split("#");
                    int[][] tempPower = new int[powers.length][2];
                    for (int i = 0; i < powers.length; i++) {
                        String[] powerArr = powers[i].split(",");
                        for (int j = 0; j < powerArr.length; j++) {
                            tempPower[i][j] = Integer.valueOf(powerArr[j]);
                        }
                    }
                    EXPLORE_POWER_MAX = tempPower;
                    EXPLORE_BOSS_EXIST = Integer.valueOf(XmlUtils.getAttribute(config, "EXPLORE_BOSS_EXIST"));
                    EXPLORE_POWER_RECOVER = Integer.valueOf(XmlUtils.getAttribute(config, "EXPLORE_POWER_RECOVER")) * 1000;
                    DISAPPEAR_TIME = Integer.valueOf(XmlUtils.getAttribute(config, "DISAPPEAR_TIME")) * 1000;
                    EXIST_TIME = Integer.valueOf(XmlUtils.getAttribute(config, "EXIST_TIME")) * 1000;
                    ATK_CD_TIME = Integer.valueOf(XmlUtils.getAttribute(config, "ATK_CD_TIME")) * 1000;
                    BOSS_FIGHT_MAX = Integer.valueOf(XmlUtils.getAttribute(config, "BOSS_FIGHT_MAX"));
                    BOSS_FIGHT_TIME = Integer.valueOf(XmlUtils.getAttribute(config, "BOSS_FIGHT_TIME")) * 1000;
                } catch (Exception e) {
                    logger.error("加载BOSS配置数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return BOSS_CONFIG_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadReinReward(String path) {
        final File file = new File(path, REIN_REWARD_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, ReinRewardsData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "bossReward");
                    for (int i = 0; i < elements.length; i++) {
                        ReinRewardsData data = new ReinRewardsData();
                        data.setId(Byte.valueOf(XmlUtils.getAttribute(elements[i], "id")));
                        data.setFirstReward(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "firstReward")));
                        data.setFirstTitle(XmlUtils.getAttribute(elements[i], "firstTitle"));
                        data.setFirstContent(XmlUtils.getAttribute(elements[i], "firstContent"));
                        data.setSecondReward(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "secondReward")));
                        data.setSecondTitle(XmlUtils.getAttribute(elements[i], "secondTitle"));
                        data.setSecondContent(XmlUtils.getAttribute(elements[i], "secondContent"));
                        data.setReward345(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "Reward345")));
                        data.setTitle345(XmlUtils.getAttribute(elements[i], "Title345"));
                        data.setContent345(XmlUtils.getAttribute(elements[i], "Content345"));
                        data.setCanyuReward(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "canyuReward")));
                        data.setCanyuTitle(XmlUtils.getAttribute(elements[i], "canyuTitle"));
                        data.setCanyuContent(XmlUtils.getAttribute(elements[i], "canyuContent"));
                        data.setKillReward(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "killReward")));
                        data.setKillTitle(XmlUtils.getAttribute(elements[i], "killTitle"));
                        data.setKillContent(XmlUtils.getAttribute(elements[i], "killContent"));
                        tmpMap.put(data.getId(), data);
                    }
                    reinRewardMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载转生BOSS奖励数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return REIN_REWARD_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadBossCit(String path) {
        final File file = new File(path, BOSS_CITIZEN_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, BossCitData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "boss");
                    for (int i = 0; i < elements.length; i++) {
                        BossCitData data = new BossCitData();
                        data.setId(Short.valueOf(XmlUtils.getAttribute(elements[i], "id")));
                        data.setLimitLv(Integer.valueOf(XmlUtils.getAttribute(elements[i], "limitLevel")));
                        data.setFuhuoTime(Integer.valueOf(XmlUtils.getAttribute(elements[i], "fuhuotime")));
                        data.setModelId(Short.valueOf(XmlUtils.getAttribute(elements[i], "modelId")));
                        data.setRewards(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "reward")));
                        data.setRedLev(Short.valueOf(XmlUtils.getAttribute(elements[i], "redlev")));
                        data.setOwnredpro(Short.valueOf(XmlUtils.getAttribute(elements[i], "ownredpro")));
                        data.setJoinredpro(Short.valueOf(XmlUtils.getAttribute(elements[i], "joinredpro")));
                        tmpMap.put(data.getId(), data);
                    }
                    citMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载全民BOSS数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return BOSS_CITIZEN_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadBossRein(String path) {
        final File file = new File(path, BOSS_REIN_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, BossReinData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "boss");
                    for (int i = 0; i < elements.length; i++) {
                        BossReinData data = new BossReinData();
                        data.setId(Short.valueOf(XmlUtils.getAttribute(elements[i], "id")));
                        data.setLimitLv(Integer.valueOf(XmlUtils.getAttribute(elements[i], "limitLevel")));
                        data.setStartTime(XmlUtils.getAttribute(elements[i], "startTime"));
                        data.setDurationTime(Integer.valueOf(XmlUtils.getAttribute(elements[i], "durationTime")));
                        data.setFightCD(Integer.valueOf(XmlUtils.getAttribute(elements[i], "fightCD")));
                        data.setModelId(Short.valueOf(XmlUtils.getAttribute(elements[i], "modelId")));
                        data.setRewardWin(Short.valueOf(XmlUtils.getAttribute(elements[i], "rewardWin")));
                        data.setRewardLose(Short.valueOf(XmlUtils.getAttribute(elements[i], "rewardLose")));
                        tmpMap.put(data.getId(), data);

                        //data.setStartTime("22:18");
                        //data.setDurationTime(6000);
                    }
                    reinMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载转生BOSS数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return BOSS_REIN_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static BossRewardsData getReward(int id) {
        return rewardMap.get(id);
    }

    public static ReinRewardsData getReinReward(int id) {
        return reinRewardMap.get((byte) id);
    }

    public static Map<Short, BossCitData> getCitMap() {
        return citMap;
    }

    public static Map<Short, BossReinData> getReinMap() {
        return reinMap;
    }

    public static BossReinData getReinData(Player player) {
        for (int i = reinMap.size(); i >= 0; i--) {
            BossReinData data = reinMap.get((short) i);
            if (data == null)
                continue;
            if (player.lvValidate(data.getLimitLv()))
                return data;
        }
        return null;
    }


    public static Map<Byte, Map<Short, VipBossData>> getVipBossMap() {
        return vipBossMap;
    }

    public static Map<Short, BossMysteryData> getMysteryMap() {
        return mysteryMap;
    }

}
