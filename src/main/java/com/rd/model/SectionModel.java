package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.define.EAttrType;
import com.rd.define.LevelDefine;
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
import java.util.List;
import java.util.Map;

public class SectionModel {

    static Logger log = Logger.getLogger(FunctionModel.class.getName());

    private static Map<String, MountData> mountDataMap;
    private static Map<String, MagicLevelData> magicLevelDataMap;
    private static Map<String, MagicStageData> magicStageDataMap;
    private static Map<Byte, MagicTurntableData> magicTurntableDataMap;
    /**
     * 神羽装备 装备位-装备等级map
     **/
    private static Map<Byte, Map<Byte, WingGodModelData>> wingPos2GodMap;
    private static Map<Short, WingGodModelData> wingGodMap;
    /**
     * 神羽技能 神羽阶段 - 技能列表
     **/
    private static Map<Byte, WingSkillModelData> wingStage2SkillMap;
    private static Map<Short, WingSkillModelData> wingSkillMap;
    /**
     * 神羽大师 神羽阶段 - 大师列表
     **/
    private static Map<Byte, WingMasterModelData> wingStage2MasterMap;
    private static Map<Short, WingMasterModelData> wingMasterMap;

    public SectionModel() {

    }

    public static void loadSection(String path) {
        loadMountData(path);
        loadMagicLevelData(path);
        loadMagicStageData(path);
        loadMagicTurntableData(path);
        loadWingGodData(path);
        loadWingSkillData(path);
        loadWingMasterData(path);
    }

    private static void loadWingMasterData(String path) {
        final File file = new File(path, "gamedata/wingmaster.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, WingMasterModelData> tmpStageMap = new HashMap<>();
                    Map<Short, WingMasterModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "suits");
                    for (Element element : elements) {
                        short id = Short.parseShort(XmlUtils.getAttribute(element, "id"));
                        byte level = Byte.parseByte(XmlUtils.getAttribute(element, "lv"));
                        byte wingGodLevelLimit = Byte.parseByte(XmlUtils.getAttribute(element, "limit"));
                        int addPercent = Integer.valueOf(XmlUtils.getAttribute(element, "add"));
                        WingMasterModelData modelData = new WingMasterModelData(id, level, wingGodLevelLimit, addPercent / GameCommon.PERCENT_DIVIDEND);
                        tmpStageMap.put(wingGodLevelLimit, modelData);
                        tmpMap.put(id, modelData);
                    }
                    wingStage2MasterMap = tmpStageMap;
                    wingMasterMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载神羽大师模型数据出错...");
                }
            }

            @Override
            public String toString() {
                return "wingmasterModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadWingSkillData(String path) {
        final File file = new File(path, "gamedata/wingskill.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, WingSkillModelData> tmpStageMap = new HashMap<>();
                    Map<Short, WingSkillModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "suits");
                    for (Element element : elements) {
                        short id = Short.parseShort(XmlUtils.getAttribute(element, "id"));
                        byte level = Byte.parseByte(XmlUtils.getAttribute(element, "lv"));
                        byte wingGodLevelLimit = Byte.parseByte(XmlUtils.getAttribute(element, "limit"));
                        int amp = Integer.parseInt(XmlUtils.getAttribute(element, "amp"));
                        WingSkillModelData modelData = new WingSkillModelData(id, level, wingGodLevelLimit, amp);
                        tmpStageMap.put(wingGodLevelLimit, modelData);
                        tmpMap.put(id, modelData);
                    }
                    wingStage2SkillMap = tmpStageMap;
                    wingSkillMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载神羽技能数据出错...");
                }
            }

            @Override
            public String toString() {
                return "wingskillModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadWingGodData(String path) {
        final File file = new File(path, "gamedata/winggod.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, Map<Byte, WingGodModelData>> tmpPosMap = new HashMap<>();
                    Map<Short, WingGodModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "suits");
                    for (Element element : elements) {
                        short id = Short.parseShort(XmlUtils.getAttribute(element, "id"));
                        String name = XmlUtils.getAttribute(element, "name");
                        byte pos = Byte.parseByte(XmlUtils.getAttribute(element, "pos"));
                        byte level = Byte.parseByte(XmlUtils.getAttribute(element, "lv"));
                        short mountStageLimit = Short.parseShort(XmlUtils.getAttribute(element, "limit"));
                        int[] attr = EAttrType.getAttr(element);
                        List<DropData> craftCost = StringUtil.getRewardDropList(XmlUtils.getAttribute(element, "cost"));
                        List<DropData> converseCost = StringUtil.getRewardDropList(XmlUtils.getAttribute(element, "yuanbao"));
                        WingGodModelData modelData = new WingGodModelData(id, pos, level, mountStageLimit, craftCost, converseCost, attr, name);
                        if (!tmpPosMap.containsKey(pos)) {
                            tmpPosMap.put(pos, new HashMap<>());
                        }
                        tmpPosMap.get(pos).put(level, modelData);
                        tmpMap.put(id, modelData);
                    }
                    wingPos2GodMap = tmpPosMap;
                    wingGodMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载神羽装备模型数据出错...");
                }
            }

            @Override
            public String toString() {
                return "winggodModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadMountData(String path) {
        final File file = new File(path, "gamedata/mount.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<String, MountData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuling");
                    for (int i = 0; i < elements.length; i++) {
                        MountData data = new MountData();
                        data.setStage(Short.parseShort(XmlUtils.getAttribute(elements[i], "jieduan")));
                        data.setStar(Byte.parseByte(XmlUtils.getAttribute(elements[i], "level")));
                        data.setExp(Integer.parseInt(XmlUtils.getAttribute(elements[i], "exp")));
                        data.setGoldCost(Integer.parseInt(XmlUtils.getAttribute(elements[i], "cost")));
                        data.setItemCost(StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "itemCost")));
                        data.setGoldExp(Short.parseShort(XmlUtils.getAttribute(elements[i], "goldExp")));
                        data.setItemExp(Short.parseShort(XmlUtils.getAttribute(elements[i], "itemExp")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        tmpMap.put(data.getStage() + "_" + data.getStar(), data);
                    }
                    mountDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载坐骑模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "mountModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadMagicLevelData(String path) {
        final File file = new File(path, "gamedata/fabaoshengji.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<String, MagicLevelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "fabao");
                    for (int i = 0; i < elements.length; i++) {
                        MagicLevelData data = new MagicLevelData();
                        data.setLevel(Short.parseShort(XmlUtils.getAttribute(elements[i], "Lv")));
                        data.setStar(Byte.parseByte(XmlUtils.getAttribute(elements[i], "satr")));
                        data.setCostGold(Integer.parseInt(XmlUtils.getAttribute(elements[i], "gold")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        tmpMap.put(data.getLevel() + "_" + data.getStar(), data);
                        if (data.getLevel() > LevelDefine.MAX_MAGIC_LEVEL) {
                            LevelDefine.MAX_MAGIC_LEVEL = data.getLevel();
                        }
                    }
                    magicLevelDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载法宝升级模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "magicLevelDataModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadMagicStageData(String path) {
        final File file = new File(path, "gamedata/fabaojinjie.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<String, MagicStageData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "fabao");
                    for (int i = 0; i < elements.length; i++) {
                        MagicStageData data = new MagicStageData();
                        data.setStage(Short.parseShort(XmlUtils.getAttribute(elements[i], "jieduan")));
                        data.setStar(Byte.parseByte(XmlUtils.getAttribute(elements[i], "star")));
                        data.setExp(Integer.parseInt(XmlUtils.getAttribute(elements[i], "exp")));
                        data.setItemCost(GameCommon.parseDropData(XmlUtils.getAttribute(elements[i], "itemCost")));
                        data.setItemExp(Byte.parseByte(XmlUtils.getAttribute(elements[i], "itemExp")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
//                    	data.setSpecialPower(Integer.parseInt(XmlUtils.getAttribute(elements[i], "specialPower")));
                        tmpMap.put(data.getStage() + "_" + data.getStar(), data);
                    }
                    magicStageDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载法宝升阶模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "magicStageDataModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadMagicTurntableData(String path) {
        final File file = new File(path, "gamedata/fabaozhuanpan.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, MagicTurntableData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "fabao");
                    for (int i = 0; i < elements.length; i++) {
                        MagicTurntableData data = new MagicTurntableData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setItemCost(GameCommon.parseDropData(XmlUtils.getAttribute(elements[i], "cost")));
                        data.setMultiplyValue(GameCommon.parseByteValueStruct(XmlUtils.getAttribute(elements[i], "multiply")));
                        data.setMultiplyChance(GameCommon.parseShortChanceStruct(XmlUtils.getAttribute(elements[i], "multiply")));
                        data.setNumValue(GameCommon.parseByteValueStruct(XmlUtils.getAttribute(elements[i], "num")));
                        data.setNumChance(GameCommon.parseShortChanceStruct(XmlUtils.getAttribute(elements[i], "num")));
                        data.setRewardData(GameCommon.parseDropData(XmlUtils.getAttribute(elements[i], "itme")));
                        tmpMap.put(data.getId(), data);
                    }
                    magicTurntableDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载法宝转盘模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "magicTurntableDataModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static MountData getMountData(short stage, byte star) {
        return mountDataMap.get(stage + "_" + star);
    }

    public static MagicLevelData getMagicLevelData(short level, byte star) {
        return magicLevelDataMap.get(level + "_" + star);
    }

    public static MagicStageData getMagicStageData(short stage, byte star) {
        return magicStageDataMap.get(stage + "_" + star);
    }

    public static MagicTurntableData getMagicTurntableData(byte id) {
        return magicTurntableDataMap.get(id);
    }

    public static WingMasterModelData getWingMaster(byte lv) {
        return wingStage2MasterMap.get(lv);
    }

    public static WingMasterModelData getWingMaster(short id) {
        return wingMasterMap.get(id);
    }

    public static WingSkillModelData getWingSkill(byte lv) {
        return wingStage2SkillMap.get(lv);
    }

    public static WingSkillModelData getWingSkill(short id) {
        return wingSkillMap.get(id);
    }

    public static WingGodModelData getWingGod(byte pos, byte lv) {
        if (!wingPos2GodMap.containsKey(pos)) {
            return null;
        }
        return wingPos2GodMap.get(pos).get(lv);
    }

    public static int getPosSize() {
        return wingPos2GodMap.size();
    }

    public static WingGodModelData getWingGod(short id) {
        return wingGodMap.get(id);
    }
}
