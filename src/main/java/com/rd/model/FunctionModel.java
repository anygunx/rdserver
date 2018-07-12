package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.bean.function.FunctionData;
import com.rd.common.GameCommon;
import com.rd.define.EAttrType;
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

public class FunctionModel {

    static Logger log = Logger.getLogger(FunctionModel.class.getName());

    private static Map<Byte, FunctionData> functionDataMap;
    private static Map<Byte, ShareModelData> shareModelDataMap;

    private static Map<Byte, FiveElementsData> fiveElementsDataMap;
    private static Map<Byte, FiveSkillData> fiveSkillDataMap;
    private static Map<Byte, FiveElementsDungeonData> fiveElementsDungeonData;
    private static Map<Byte, FiveElementsActivityData> fiveElementsActivityData;
    private static Map<Byte, LingSuiModelData> lingSuiModelData;
    private static Map<String, FaZhenModelData> faZhenModelData;
    private static byte holyGoodsMaxStage;
    private static byte holyGoodsMaxStar;
    private static Map<String, HolyGoodsData> holyGoodsDataMap;
    private static byte ambitMaxStage;
    private static byte ambitMaxStar;
    private static Map<String, AmbitData> ambitDataMap;
    private static Map<Byte, HolyLinesData> holyLinesMap;

    /**
     * 圣纹大师 圣纹阶段 - 大师列表
     **/
    private static Map<Byte, HolyLinesMasterData> holyLinesMasterMap;

    /**
     * 主宰试炼倒计时
     **/
    public static final byte ZHUAZAISHILIAN_TIME = 30;

    private static int[] hp;
    private static int[] attack;
    private static int[] phyDef;
    private static int[] magicDef;

    private FunctionModel() {

    }

    public static void loadFunction(String path) {
        loadFunctionData(path);
        loadShareData(path);
        loadFiveElementsData(path);
        loadFiveAttr();
        loadFiveSkill(path);
        loadFiveElementsDungeonData(path);
        loadFiveElementsActivityData(path);
        loadLingSuiData(path);
        loadFaZhenData(path);
        loadShengWuData(path);
        loadShengWenData(path);
        loadShengWenMasterData(path);
        loadJingJieData(path);
    }

    private static void loadFiveAttr() {
        hp = new int[ConstantModel.CARD_MAX_LEVEL];
        attack = new int[ConstantModel.CARD_MAX_LEVEL];
        phyDef = new int[ConstantModel.CARD_MAX_LEVEL];
        magicDef = new int[ConstantModel.CARD_MAX_LEVEL];
        for (int lv = 0; lv < ConstantModel.CARD_MAX_LEVEL; ++lv) {
            hp[lv] = 10000 + ((int) ((lv - 1) / 5.0D) + 1) * 3400 + (lv - ((int) ((lv - 1) / 5.0D) + 1)) * 2975;
            attack[lv] = (int) (hp[lv] / 5.0D);
            phyDef[lv] = (int) (hp[lv] / 10.0D);
            magicDef[lv] = (int) (hp[lv] / 10.0D);
        }
    }

    private static void loadFunctionData(String path) {
        final File file = new File(path, "gamedata/functionLv.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, FunctionData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "functionLv");
                    for (int i = 0; i < elements.length; i++) {
                        FunctionData data = new FunctionData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setLevel(Short.parseShort(XmlUtils.getAttribute(elements[i], "level")));
                        data.setGuanqia(Short.parseShort(XmlUtils.getAttribute(elements[i], "guanqia")));
                        data.setDays(Short.parseShort(XmlUtils.getAttribute(elements[i], "days")));
                        tmpMap.put(data.getId(), data);
                    }
                    functionDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载功能模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "functionModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadShareData(String path) {
        final File file = new File(path, "gamedata/fenxiang.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, ShareModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "logic");
                    for (int i = 0; i < elements.length; i++) {
                        ShareModelData data = new ShareModelData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setTimes(Byte.parseByte(XmlUtils.getAttribute(elements[i], "times")));
                        data.setRewardList(GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "reward")));
                        tmpMap.put(data.getId(), data);
                    }
                    shareModelDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载分享数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "shareModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadFiveElementsData(String path) {
        final File file = new File(path, "gamedata/fiveline.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, FiveElementsData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "logic");
                    for (int i = 0; i < elements.length; i++) {
                        byte id = Byte.parseByte(XmlUtils.getAttribute(elements[i], "id"));
                        byte vip = Byte.parseByte(XmlUtils.getAttribute(elements[i], "vip"));
                        byte day = Byte.parseByte(XmlUtils.getAttribute(elements[i], "day"));
                        int[] attribute = EAttrType.getAttr(elements[i]);

                        FiveElementsData data = new FiveElementsData(id, vip, day, attribute);
                        tmpMap.put(data.getId(), data);
                    }
                    fiveElementsDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载五行模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "fivelineModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadFiveSkill(String path) {
        final File file = new File(path + "gamedata/fiveSkill.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, FiveSkillData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "fiveSkill");
                    for (int i = 0; i < elements.length; i++) {
                        byte id = Byte.parseByte(XmlUtils.getAttribute(elements[i], "id"));
                        short lv = Short.parseShort(XmlUtils.getAttribute(elements[i], "lv"));
                        int[] attribute = EAttrType.getAttr(elements[i]);
                        FiveSkillData fiveSkillData = new FiveSkillData(id, lv, attribute);
                        tmpMap.put(fiveSkillData.getId(), fiveSkillData);
                    }
                    fiveSkillDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载五行技能模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "fiveSkillModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadFiveElementsDungeonData(String path) {
        final File file = new File(path, "gamedata/wuxingfuben.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, FiveElementsDungeonData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "map");
                    for (int i = 0; i < elements.length; i++) {
                        FiveElementsDungeonData data = new FiveElementsDungeonData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setCost(Short.parseShort(XmlUtils.getAttribute(elements[i], "cost")));
                        data.setReward(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "rewards")));
                        tmpMap.put(data.getId(), data);
                    }
                    fiveElementsDungeonData = tmpMap;
                } catch (Exception e) {
                    log.error("加载五行副本数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "wuxingfubenModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadFiveElementsActivityData(String path) {
        final File file = new File(path, "gamedata/wuxinghuodong.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, FiveElementsActivityData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "map");
                    for (int i = 0; i < elements.length; i++) {
                        FiveElementsActivityData data = new FiveElementsActivityData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setCost(StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "cost")));
                        data.setReward(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "reward")));
                        tmpMap.put(data.getId(), data);
                    }
                    fiveElementsActivityData = tmpMap;
                } catch (Exception e) {
                    log.error("加载五行活动数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "wuxinghuodongModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadFaZhenData(String path) {
        final File file = new File(path, "gamedata/shengwufazhen.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<String, FaZhenModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "shengwufazhen");
                    for (int i = 0; i < elements.length; i++) {
                        short id = Short.parseShort(XmlUtils.getAttribute(elements[i], "id"));
                        byte type = Byte.parseByte(XmlUtils.getAttribute(elements[i], "type"));
                        short lev = Short.parseShort(XmlUtils.getAttribute(elements[i], "level"));
                        DropData cost = StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "cost"));
                        int[] attr = EAttrType.getAttr(elements[i]);

                        FaZhenModelData data = new FaZhenModelData(id, type, lev, cost, attr);
                        tmpMap.put(data.getType() + "_" + data.getLev(), data);
                    }
                    faZhenModelData = tmpMap;
                } catch (Exception e) {
                    log.error("加载法阵数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "fazhenModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadShengWuData(String path) {
        final File file = new File(path, "gamedata/shengwu.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<String, HolyGoodsData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "shengwu");
                    for (int i = 0; i < elements.length; i++) {
                        byte stage = Byte.parseByte(XmlUtils.getAttribute(elements[i], "ordelv"));
                        byte star = Byte.parseByte(XmlUtils.getAttribute(elements[i], "starlv"));
                        DropData cost = StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "cost"));
                        short exp = Short.parseShort(XmlUtils.getAttribute(elements[i], "exp"));
                        int[] attr = EAttrType.getAttr(elements[i]);

                        HolyGoodsData data = new HolyGoodsData(stage, star, exp, cost, attr);
                        tmpMap.put(data.getStage() + "_" + data.getStar(), data);

                        if (stage > ambitMaxStage) {
                            ambitMaxStage = stage;
                        }
                        if (star > ambitMaxStar) {
                            ambitMaxStar = star;
                        }
                    }
                    holyGoodsDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载圣物数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "shengwuModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadLingSuiData(String path) {
        final File file = new File(path, "gamedata/lingsui.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, LingSuiModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "lingsui");
                    for (int i = 0; i < elements.length; i++) {
                        byte id = Byte.parseByte(XmlUtils.getAttribute(elements[i], "id"));
                        String name = XmlUtils.getAttribute(elements[i], "name");
                        byte lv = Byte.parseByte(XmlUtils.getAttribute(elements[i], "lv"));
                        List<DropData> cost = StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "cost"));
                        short needlv = Short.parseShort(XmlUtils.getAttribute(elements[i], "needlv"));
                        byte num = Byte.parseByte(XmlUtils.getAttribute(elements[i], "copynum"));
                        int[] attr = EAttrType.getAttr(elements[i]);
                        LingSuiModelData datas = new LingSuiModelData(id, lv, needlv, num, cost, attr, name);
                        tmpMap.put(datas.getId(), datas);
                    }
                    lingSuiModelData = tmpMap;
                } catch (Exception e) {
                    log.error("加载灵髓数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "lingsuiModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadShengWenData(String path) {
        final File file = new File(path, "gamedata/shengwen.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, HolyLinesData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "shengwen");
                    for (Element element : elements) {
                        byte id = Byte.parseByte(XmlUtils.getAttribute(element, "id"));
                        byte pos = Byte.parseByte(XmlUtils.getAttribute(element, "pos"));
                        byte level = Byte.parseByte(XmlUtils.getAttribute(element, "level"));
                        byte stageLimit = Byte.parseByte(XmlUtils.getAttribute(element, "limit"));
                        int[] attr = EAttrType.getAttr(element);
                        DropData combineCost = StringUtil.getRewardDropData(XmlUtils.getAttribute(element, "cost"));
                        DropData convertCost = StringUtil.getRewardDropData(XmlUtils.getAttribute(element, "yuanbao"));
                        HolyLinesData holyLinesData = new HolyLinesData(id, pos, level, stageLimit, combineCost, convertCost, attr);
                        tmpMap.put(id, holyLinesData);
                    }
                    holyLinesMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载圣纹装备模型数据出错...");
                }
            }

            @Override
            public String toString() {
                return "shengwenModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadShengWenMasterData(String path) {
        final File file = new File(path, "gamedata/shengwenjt.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, HolyLinesMasterData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "shengwenjt");
                    for (Element element : elements) {
                        byte stage = Byte.parseByte(XmlUtils.getAttribute(element, "level"));
                        int addPercent = Integer.valueOf(XmlUtils.getAttribute(element, "add"));
                        HolyLinesMasterData modelData = new HolyLinesMasterData(stage, 1 + addPercent / GameCommon.PERCENT_DIVIDEND);
                        tmpMap.put(stage, modelData);
                    }
                    holyLinesMasterMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载圣纹大师模型数据出错...");
                }
            }

            @Override
            public String toString() {
                return "shengwenmasterModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadJingJieData(String path) {
        final File file = new File(path, "gamedata/jingjie.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<String, AmbitData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "jingjie");
                    for (int i = 0; i < elements.length; i++) {
                        byte stage = Byte.parseByte(XmlUtils.getAttribute(elements[i], "ordelv"));
                        byte star = Byte.parseByte(XmlUtils.getAttribute(elements[i], "starlv"));
                        DropData cost = StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "cost"));
                        short exp = Short.parseShort(XmlUtils.getAttribute(elements[i], "exp"));
                        int[] attr = EAttrType.getAttr(elements[i]);

                        AmbitData data = new AmbitData(stage, star, cost, exp, attr);
                        tmpMap.put(data.getStage() + "_" + data.getStar(), data);

                        if (stage > ambitMaxStage) {
                            ambitMaxStage = stage;
                        }
                        if (star > ambitMaxStar) {
                            ambitMaxStar = star;
                        }
                    }
                    ambitDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载境界数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "jingjieModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static FiveElementsData getFiveElementsData(byte id) {
        return fiveElementsDataMap.get(id);
    }

    public static FiveElementsDungeonData getFiveElementsDungeonData(byte id) {
        return fiveElementsDungeonData.get(id);
    }

    public static FiveElementsActivityData getFiveElementsActivityData(byte id) {
        return fiveElementsActivityData.get(id);
    }

    public static Map<Byte, FiveSkillData> getFiveSkillData() {
        return fiveSkillDataMap;
    }

    public static FiveSkillData getFiveSkillData(byte id) {
        return fiveSkillDataMap.get(id);
    }

    public static FunctionData getFunctionData(byte id) {
        return functionDataMap.get(id);
    }

    public static Map<Byte, FunctionData> getFunctionDataMap() {
        return functionDataMap;
    }

    public static ShareModelData getShareModelData(byte id) {
        return shareModelDataMap.get(id);
    }

    public static LingSuiModelData getLingSuiModelData(byte id) {
        return lingSuiModelData.get(id);
    }

    public static short getCuilianDay() {
        return functionDataMap.get(((byte) 22)).getDays();
    }

    public static FaZhenModelData getFaZhenModelData(byte type, short lev) {
        return faZhenModelData.get(type + "_" + lev);
    }

    public static HolyLinesMasterData getHolyLinesMasterData(byte id) {
        return holyLinesMasterMap.get(id);
    }

    public static byte getHolyGoodsMaxStage() {
        return holyGoodsMaxStage;
    }

    public static byte getHolyGoodsMaxStar() {
        return holyGoodsMaxStar;
    }

    public static HolyGoodsData getHolyGoodsData(byte stage, byte star) {
        return holyGoodsDataMap.get(stage + "_" + star);
    }

    public static HolyLinesData getHolyLinesData(byte id) {
        return holyLinesMap.get(id);
    }

    public static Byte getAmbitMaxStage() {
        return ambitMaxStage;
    }

    public static Byte getAmbitMaxStar() {
        return ambitMaxStar;
    }

    public static AmbitData getAmbitData(byte stage, byte star) {
        return ambitDataMap.get(stage + "_" + star);
    }

    public static int[] getHp() {
        return hp;
    }

    public static int[] getAttack() {
        return attack;
    }

    public static int[] getPhyDef() {
        return phyDef;
    }

    public static int[] getMagicDef() {
        return magicDef;
    }

}
