package com.rd.model;

import com.rd.common.GameCommon;
import com.rd.define.EAttrType;
import com.rd.game.event.EGameEventType;
import com.rd.model.data.*;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.DiceUtil.Ele;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: 公会模型数据</p>
 * <p>Description: 公会模型数据</p>
 * <p>Company: 北京万游畅想科技有限公司</p>
 *
 * @author ---
 * @version 1.0
 * @data 2016年12月29日 上午11:24:18
 */
public class GangModel {

    static Logger log = Logger.getLogger(GangModel.class.getName());

    private static Map<Short, GangData> gangDataMap;
    private static Map<Byte, GangIncenseData> gangIncenseMap;
    private static Map<String, GangSkillData> gangSkillMap;
    private static Map<String, GangSkill2Data> gangSkill2Map;
    private static Map<Byte, GangMissionData> gangMissionMap;
    private static Map<Byte, GangBossModelData> gangBossMap;
    private static Map<Byte, GangFightRewardData> gangFightRewardDataMap;
    private static Map<Byte, GangFightRankData> gangFightRankDataMap;
    private static GangFightRankData gangFightChampionMasterRewardData;
    private static GangFightRankData gangFightChampionMemberRewardData;
    private static Map<Short, GangTongGuanData> gangTongGuanRewardMap;
    private static Map<Short, GangSweepData> gangSweepRewardMap;
    private static Map<Short, GangShopModelData> gangShopMap;

    private GangModel() {

    }

    public static void loadGang(String path) {
        loadGangData(path);
        loadGangIncenseData(path);
        loadGangSkillData(path);
        loadGangSkill2Data(path);
        loadGangMissionData(path);
        loadGangBossData(path);
        loadGangFightRewardData(path);
        loadGangFightRankData(path);
        loadGangFightChampionRewardData(path);
        loadGangTongGuanRewardData(path);
        loadGangSweepRewardData(path);
        loadGangShopData(path);
    }

    private static void loadGangData(String path) {
        final File file = new File(path, "gamedata/guildLv.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, GangData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "guildLv");
                    for (int i = 0; i < elements.length; i++) {
                        GangData data = new GangData();
                        data.setLevel(Short.parseShort(XmlUtils.getAttribute(elements[i], "level")));
                        data.setExp(Integer.parseInt(XmlUtils.getAttribute(elements[i], "exp")));
                        data.setMaxMember(Short.parseShort(XmlUtils.getAttribute(elements[i], "renshuMax")));
                        data.setTurntableMax(Short.parseShort(XmlUtils.getAttribute(elements[i], "zhuanpanMax")));
                        data.setSkillMax(Short.parseShort(XmlUtils.getAttribute(elements[i], "skillMax")));
                        data.setSkill2Max(Short.parseShort(XmlUtils.getAttribute(elements[i], "skill2Max")));
                        tmpMap.put(data.getLevel(), data);
                    }
                    gangDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载公会模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "gangModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadGangBossData(String path) {
        final File file = new File(path, "gamedata/guildBoss.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, GangBossModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "boss");
                    for (int i = 0; i < elements.length; i++) {
                        GangBossModelData data = new GangBossModelData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setRewards(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "reward")));
                        tmpMap.put(data.getId(), data);
                    }
                    gangBossMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载公会Boss数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "gangBossModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadGangIncenseData(String path) {
        final File file = new File(path, "gamedata/guildShangxiang.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, GangIncenseData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "guildTask");
                    for (int i = 0; i < elements.length; i++) {
                        GangIncenseData data = new GangIncenseData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setGangExp(Short.parseShort(XmlUtils.getAttribute(elements[i], "guildExp")));
                        data.setDonate(Short.parseShort(XmlUtils.getAttribute(elements[i], "banggong")));
                        data.setCost(GameCommon.parseDropData(XmlUtils.getAttribute(elements[i], "cost")));
                        tmpMap.put(data.getId(), data);
                    }
                    gangIncenseMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载公会上香数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "guildShangxiang";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadGangSkillData(String path) {
        final File file = new File(path, "gamedata/guildSkill.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<String, GangSkillData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "guildSkell");
                    for (int i = 0; i < elements.length; i++) {
                        GangSkillData data = new GangSkillData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setLevel(Short.parseShort(XmlUtils.getAttribute(elements[i], "level")));
                        data.setCostList(GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "cost")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        tmpMap.put(data.getId() + "_" + data.getLevel(), data);
                    }
                    gangSkillMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载公会技能数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "guildSkill";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadGangSkill2Data(String path) {
        final File file = new File(path, "gamedata/guildSkill2.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<String, GangSkill2Data> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "guildSkell");
                    for (int i = 0; i < elements.length; i++) {
                        GangSkill2Data data = new GangSkill2Data();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setLevel(Short.parseShort(XmlUtils.getAttribute(elements[i], "level")));
                        data.setCostList(GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "cost")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        data.setExpMax(Integer.parseInt(XmlUtils.getAttribute(elements[i], "expMax")));
                        data.setExp(Short.parseShort(XmlUtils.getAttribute(elements[i], "exp")));
                        tmpMap.put(data.getId() + "_" + data.getLevel(), data);
                    }
                    gangSkill2Map = tmpMap;
                } catch (Exception e) {
                    log.error("加载公会技能2数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "guildSkill2";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadGangMissionData(String path) {
        final File file = new File(path, "gamedata/guildTask.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, GangMissionData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "taskDaily");
                    for (int i = 0; i < elements.length; i++) {
                        GangMissionData data = new GangMissionData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setEventType(Byte.parseByte(XmlUtils.getAttribute(elements[i], "eventType")));
                        data.setCount(Byte.parseByte(XmlUtils.getAttribute(elements[i], "count")));
                        data.setGangExp(Byte.parseByte(XmlUtils.getAttribute(elements[i], "guildExp")));
                        data.setGangDonate(Byte.parseByte(XmlUtils.getAttribute(elements[i], "banggong")));
                        tmpMap.put(data.getId(), data);

                        // 关联事件 注：不可热更
                        EGameEventType eventType = EGameEventType.getEventType(data.getEventType());
                        eventType.setGangMission(data.getId());
                    }
                    gangMissionMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载公会任务数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "guildMission";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadGangFightRewardData(String path) {
        final File file = new File(path, "gamedata/guildreward.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, GangFightRewardData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "map");
                    for (int i = 0; i < elements.length; i++) {
                        GangFightRewardData data = new GangFightRewardData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setMemberTitle(XmlUtils.getAttribute(elements[i], "tongyongTitle"));
                        data.setMemberContent(XmlUtils.getAttribute(elements[i], "tongyongContent"));
                        data.setMemberReward(GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "tongyongReward")));
                        data.setStoreReward(GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "cangkuReward")));
                        tmpMap.put(data.getId(), data);
                    }
                    gangFightRewardDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载公会战奖励模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "gangFightRewardModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadGangFightRankData(String path) {
        final File file = new File(path, "gamedata/guildgerenreward.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, GangFightRankData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "map");
                    for (int i = 0; i < elements.length; i++) {
                        GangFightRankData data = new GangFightRankData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setTitle(XmlUtils.getAttribute(elements[i], "title"));
                        data.setContent(XmlUtils.getAttribute(elements[i], "content"));
                        data.setReward(GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "reward")));
                        tmpMap.put(data.getId(), data);
                    }
                    gangFightRankDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载公会战排名奖励模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "gangFightRankModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadGangFightChampionRewardData(String path) {
        final File file = new File(path, "gamedata/guildspecialreward.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    GangFightRankData masterData = new GangFightRankData();
                    GangFightRankData memberData = new GangFightRankData();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "map");
                    for (int i = 0; i < elements.length; i++) {
                        masterData.setTitle(XmlUtils.getAttribute(elements[i], "huizhangTitle"));
                        masterData.setContent(XmlUtils.getAttribute(elements[i], "huizhangContent"));
                        masterData.setReward(GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "huizhangReward")));
                        memberData.setTitle(XmlUtils.getAttribute(elements[i], "huiyuanTitle"));
                        memberData.setContent(XmlUtils.getAttribute(elements[i], "huiyuanContent"));
                        memberData.setReward(GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "huiyuanReward")));
                    }
                    gangFightChampionMasterRewardData = masterData;
                    gangFightChampionMemberRewardData = memberData;
                } catch (Exception e) {
                    log.error("加载公会战冠军奖励模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "gangFightChampionModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadGangTongGuanRewardData(String path) {
        final File file = new File(path, "gamedata/guildtongguan.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, GangTongGuanData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "copy");
                    for (int i = 0; i < elements.length; i++) {
                        GangTongGuanData gtgd = new GangTongGuanData();
                        gtgd.setId(Short.valueOf((XmlUtils.getAttribute(elements[i], "id"))));
                        gtgd.setGuanqia(Short.valueOf((XmlUtils.getAttribute(elements[i], "guanqia"))));
                        gtgd.setRewards(GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "one")));
                        tmpMap.put(gtgd.getId(), gtgd);
                    }
                    gangTongGuanRewardMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载公会通关奖励模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "gangTongGuanModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadGangSweepRewardData(String path) {
        final File file = new File(path, "gamedata/guildsaodang.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, GangSweepData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "logic");
                    for (int i = 0; i < elements.length; i++) {
                        GangSweepData gsd = new GangSweepData();
                        gsd.setId(Short.valueOf((XmlUtils.getAttribute(elements[i], "id"))));
                        gsd.setChance(Short.valueOf((XmlUtils.getAttribute(elements[i], "diaoluo"))));
                        gsd.setRewards(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "reward")));
                        tmpMap.put(gsd.getId(), gsd);
                    }
                    gangSweepRewardMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载公会扫荡奖励模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "gangSweepModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadGangShopData(String path) {
        final File file = new File(path, "gamedata/guildshop.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, GangShopModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "logic");
                    for (int i = 0; i < elements.length; i++) {
                        GangShopModelData gsmd = new GangShopModelData();
                        gsmd.setId(Short.valueOf((XmlUtils.getAttribute(elements[i], "id"))));
                        gsmd.setCosts(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "cost")));
                        gsmd.setRewards(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "reward")));
                        gsmd.setLimitNum(Short.valueOf((XmlUtils.getAttribute(elements[i], "xiangou"))));
                        tmpMap.put(gsmd.getId(), gsmd);
                    }
                    gangShopMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载公会商店信息模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "gangShopModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static GangData getGangData(short level) {
        return gangDataMap.get(level);
    }

    public static GangIncenseData getGangIncenseData(byte id) {
        return gangIncenseMap.get(id);
    }

    public static GangSkillData getGangSkillData(byte id, short level) {
        return gangSkillMap.get(id + "_" + level);
    }

    public static GangSkill2Data getGangSkill2Data(byte id, short level) {
        return gangSkill2Map.get(id + "_" + level);
    }

    public static GangMissionData getGangMissionData(byte id) {
        return gangMissionMap.get(id);
    }

    public static GangBossModelData getGangBossData(byte id) {
        return gangBossMap.get(id);
    }

    public static Map<Byte, GangBossModelData> getGangBossMap() {
        return gangBossMap;
    }

    public static GangFightRewardData getGangFightReward(byte id) {
        return gangFightRewardDataMap.get(id);
    }

    public static GangFightRankData getGangFightRank(byte id) {
        return gangFightRankDataMap.get(id);
    }

    public static GangFightRankData getGangFightChampionMasterReward() {
        return gangFightChampionMasterRewardData;
    }

    public static GangFightRankData getGangFightChampionMemberReward() {
        return gangFightChampionMemberRewardData;
    }

    public static short getMaxLevel() {
        return (short) gangDataMap.size();
    }

    public static Map<Short, GangTongGuanData> getGangTongGuanRewardMap() {
        return gangTongGuanRewardMap;
    }

    public static List<Short> getGangTongGuanRewardList(short startId, short endId) {
        List<Short> list = new ArrayList<>();
        if (gangTongGuanRewardMap == null) return list;
        for (GangTongGuanData gtgd : gangTongGuanRewardMap.values()) {
            if (gtgd.getGuanqia() > startId && gtgd.getGuanqia() <= endId) {
                list.add(gtgd.getId());
            }
        }
        return list;
    }

    public static Map<Short, GangSweepData> getGangSweepRewardMap() {
        return gangSweepRewardMap;
    }

    public static List<Ele> getGangSweepRewardList() {
        List<Ele> list = new ArrayList<>();
        if (gangSweepRewardMap == null) return list;
        for (GangSweepData gsd : gangSweepRewardMap.values()) {
            Ele ele = new Ele(gsd.getId(), gsd.getChance());
            list.add(ele);
        }
        return list;
    }

    public static Map<Short, GangShopModelData> getGangShopMap() {
        return gangShopMap;
    }
}
