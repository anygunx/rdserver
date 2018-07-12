package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.common.goods.EGoodsType;
import com.rd.define.EAttrType;
import com.rd.define.GoodsDefine;
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

public class EquipModel {

    private static Logger logger = Logger.getLogger(EquipModel.class);

    //注灵数据
    private static final String ZHULING_PATH = "gamedata/ronghun.xml";
    private static final String ZHULING_NAME = "ronghunModel";
    private static Map<String, ZhuLingModelData> zhuLingMap;

    //强化数据
    private static final String STR_PATH = "gamedata/qianghua.xml";
    private static final String STR_NAME = "strengthModel";
    private static Map<String, EquipStrModelData> strMap;

//	//熔炼数据
//	private static final String RL_PATH = "gamedata/ronglian.xml";
//	private static final String RL_NAME = "ronglianModel";
//	private static Map<Integer, EquipRlModelData> rlMap;

    //铸魂
    private static final String ZHUHUN_PATH = "gamedata/lianhua.xml";
    private static final String ZHUHUN_NAME = "lianhuaModel";
    private static Map<String, EquipZhuHunModelData> zhuhunMap;

    //宝石
    private static final String JEWEL_PATH = "gamedata/baoshi.xml";
    private static final String JEWEL_NAME = "baoshiModel";
    private static Map<String, JewelModelData> jewelMap;

    //铜镜玉笛
    private static Map<String, EquipAttrData> tongjingYudiMap;
    //左右眼
    private static Map<String, EquipAttrPlusData> zuoYouYanMap;

    //淬炼
    private static final String CUILIAN_PATH = "gamedata/cuilian.xml";
    private static final String CUILIAN_NAME = "cuilianModel";
    private static Map<Byte, EquipCuiLianModelData> cuilianMap;

    //翅膀幻化
    private static Map<Byte, FashionModelData> wingMap;
    //武器幻化
    private static Map<Byte, FashionOccupModelData> weaponDataMap;
    //装备幻化
    private static Map<Byte, FashionOccupModelData> armorFashionMap;
    //坐骑幻化
    private static Map<Byte, FashionModelData> mountFashionMap;
    //光环幻化
    private static Map<Byte, FashionModelData> haloFashionMap;


    //合击符文
    private static Map<Byte, CombineRuneData> combineRuneMap;
    //合击符文套装
    private static Map<Short, CombineRuneSuitsData> combineRuneSuitsMap;

    //镇魂装备
    private static Map<String, TownSoulData> townSoulDataMap;
    //镇魂装备套装
    private static Map<Byte, int[]> townSoulSuitMap;
    //镇魂宝库转盘概率
    private static Map<Byte, TownSoulTurntableProbailityData> townSoulTreasureTurntableMap;
    //镇魂宝库数据
    private static TownSoulTreasureData townSoulTreasureData;

    public static short MAX_TONGJINGYUDI_LEVEL = 0;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadZhuLing(path);
        loadEquipStr(path);
//		loadEquipRl(path);
        loadEquipZhuHun(path);
        loadEquipTongjingYudi(path);
        loadEquipZuoYouYan(path);
        loadJewel(path);
        loadWing(path);
        loadCuiLian(path);
        loadWeapon(path);
        loadArmor(path);
        loadMount(path);
        loadCombineRune(path);
        loadCombineRuneSuits(path);
        loadTownSoul(path);
        loadTownSoulSuit(path);
        loadTownSoulTreasure(path);
        loadTownSoulTreasureTurntable(path);
        loadHalo(path);
    }

    private static void loadCuiLian(String path) {
        final File file = new File(path, CUILIAN_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, EquipCuiLianModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "activity");
                    for (int i = 0; i < elements.length; i++) {
                        Element element = elements[i];
                        byte level = Byte.parseByte(XmlUtils.getAttribute(element, "id"));
                        int exp = Integer.parseInt(XmlUtils.getAttribute(element, "exp"));
                        int num = Integer.parseInt(XmlUtils.getAttribute(element, "cost"));
                        int addPercent = Integer.parseInt(XmlUtils.getAttribute(element, "effect"));
                        DropData cost = new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_CUILIANDAN, num);
                        EquipCuiLianModelData data = new EquipCuiLianModelData(level, exp, addPercent, cost);
                        tmpMap.put(level, data);
                    }
                    cuilianMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载淬炼数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return CUILIAN_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadEquipZhuHun(String path) {
        final File file = new File(path, ZHUHUN_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<String, EquipZhuHunModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuhun");
                    for (int i = 0; i < elements.length; i++) {
                        Element element = elements[i];
                        byte type = Byte.valueOf(XmlUtils.getAttribute(elements[i], "type"));
                        byte level = Byte.valueOf(XmlUtils.getAttribute(elements[i], "level"));
                        int[] attr = EAttrType.getAttr(elements[i]);
                        DropData dropData = StringUtil.getRewardDropData(XmlUtils.getAttribute(element, "consume"));
                        EquipZhuHunModelData data = new EquipZhuHunModelData(type, level, attr, dropData);
                        tmpMap.put(data.getType() + "_" + data.getLevel(), data);
                    }
                    zhuhunMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载铸魂数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return ZHUHUN_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadWing(String path) {
        final File file = new File(path, "gamedata/wings.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, FashionModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuling");
                    for (int i = 0; i < elements.length; i++) {
                        FashionModelData data = new FashionModelData();
                        data.setId(Byte.valueOf(XmlUtils.getAttribute(elements[i], "id")));
                        data.setCost(GameCommon.parseDropData(XmlUtils.getAttribute(elements[i], "cost")));
                        data.setTime(Integer.valueOf(XmlUtils.getAttribute(elements[i], "time")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        tmpMap.put(data.getId(), data);
                    }
                    wingMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载翅膀数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "wingsModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadZhuLing(String path) {
        final File file = new File(path, ZHULING_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<String, ZhuLingModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuling");
                    for (int i = 0; i < elements.length; i++) {
                        ZhuLingModelData data = new ZhuLingModelData();
                        data.setLv(Integer.valueOf(XmlUtils.getAttribute(elements[i], "level")));
                        data.setPos(Integer.valueOf(XmlUtils.getAttribute(elements[i], "postion")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        data.setCost(StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "cost")));
                        tmpMap.put(data.getLv() + "_" + data.getPos(), data);
                    }
                    zhuLingMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载注灵数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return ZHULING_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadEquipStr(String path) {
        final File file = new File(path, STR_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<String, EquipStrModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuling");
                    for (int i = 0; i < elements.length; i++) {
                        EquipStrModelData data = new EquipStrModelData();
                        data.setLv(Integer.valueOf(XmlUtils.getAttribute(elements[i], "level")));
                        data.setPos(Integer.valueOf(XmlUtils.getAttribute(elements[i], "postion")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        data.setCost(StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "cost")));
                        tmpMap.put(data.getLv() + "_" + data.getPos(), data);
                    }
                    strMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载装备强化数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return STR_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

//	public static void loadEquipRl(String path){
//        final File file = new File(path, RL_PATH);
//        ResourceListener listener = new ResourceListener() {
//            @Override
//            public File listenedFile() {
//                return file;
//            }
//
//            @Override
//            public void onResourceChange(File file) {
//                try {
//                	Map<Integer, EquipRlModelData> tmpMap =new HashMap<>();
//                    Document doc = XmlUtils.load(file);
//                    Element root = doc.getDocumentElement();
//                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuling");
//                    for(int i = 0; i < elements.length; i++){
//                    	EquipRlModelData data=new EquipRlModelData();
//                    	data.setLv(Integer.valueOf(XmlUtils.getAttribute(elements[i], "level")));
//                    	data.setExp(Integer.valueOf(XmlUtils.getAttribute(elements[i], "exp")));
//                    	data.setAttr(EAttrType.getAttr(elements[i]));
//                    	tmpMap.put(data.getLv(), data);
//                    }
//                    rlMap = tmpMap;
//                } catch (Exception e) {
//                    logger.error("加载装备熔炼数据出错...", e);
//                }
//            }
//            
//            @Override
//            public String toString() {
//                return RL_NAME;
//            }
//        };
//        listener.onResourceChange(file);
//        ResourceManager.getInstance().addResourceListener(listener);
//    }

    private static void loadEquipTongjingYudi(String path) {
        final File file = new File(path, "gamedata/tongjingyudi.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<String, EquipAttrData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuling");
                    for (int i = 0; i < elements.length; i++) {
                        EquipAttrData data = new EquipAttrData();
                        data.setLevel(Short.parseShort(XmlUtils.getAttribute(elements[i], "level")));
                        data.setPosition(Byte.parseByte(XmlUtils.getAttribute(elements[i], "postion")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        data.setCostData(GameCommon.parseDropData(XmlUtils.getAttribute(elements[i], "cost")));
                        tmpMap.put(data.getLevel() + "_" + data.getPosition(), data);

                        if (MAX_TONGJINGYUDI_LEVEL < data.getLevel()) {
                            MAX_TONGJINGYUDI_LEVEL = data.getLevel();
                        }
                    }
                    tongjingYudiMap = tmpMap;
                } catch (Exception e) {
                    logger.error("铜镜玉笛数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "tongjingyudiModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadEquipZuoYouYan(String path) {
        final File file = new File(path, "gamedata/zuoyouyan.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<String, EquipAttrPlusData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuling");
                    for (int i = 0; i < elements.length; i++) {
                        EquipAttrPlusData data = new EquipAttrPlusData();
                        data.setLevel(Short.parseShort(XmlUtils.getAttribute(elements[i], "level")));
                        data.setPosition(Byte.parseByte(XmlUtils.getAttribute(elements[i], "postion")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        String[] cost = XmlUtils.getAttribute(elements[i], "cost").split(",");
                        data.setCostData(new DropData(Byte.parseByte(cost[0]), Integer.parseInt(cost[1]), Integer.parseInt(cost[2])));
//                        if(data.getPosition()==EquipDefine.EQUIP_ZUOYAN){
//                        	data.setAttrPlus(Integer.parseInt(XmlUtils.getAttribute(elements[i], "attPlus"))/10000.0f);
//                        }else if(data.getPosition()==EquipDefine.EQUIP_YOUYAN){
//                        	data.setAttrPlus(Integer.parseInt(XmlUtils.getAttribute(elements[i], "hpPlus"))/10000.0f);
//                        }
                        String tStr = XmlUtils.getAttribute(elements[i], "paralysis");
                        if (!StringUtil.isEmpty(tStr)) {
                            data.setParalysis(Integer.parseInt(tStr));
                        }
                        tStr = XmlUtils.getAttribute(elements[i], "paralysisre");
                        if (!StringUtil.isEmpty(tStr)) {
                            data.setParalysisre(Integer.parseInt(tStr));
                        }
                        tmpMap.put(data.getLevel() + "_" + data.getPosition(), data);
                    }
                    zuoYouYanMap = tmpMap;
                } catch (Exception e) {
                    logger.error("左右眼数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "zuoyouyanModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadJewel(String path) {
        final File file = new File(path, JEWEL_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<String, JewelModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuling");
                    for (int i = 0; i < elements.length; i++) {
                        JewelModelData data = new JewelModelData();
                        data.setLv(Integer.valueOf(XmlUtils.getAttribute(elements[i], "level")));
                        data.setPos(Integer.valueOf(XmlUtils.getAttribute(elements[i], "postion")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        data.setCost(StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "cost")));
                        tmpMap.put(data.getLv() + "_" + data.getPos(), data);
                    }
                    jewelMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载宝石升级数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return JEWEL_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadWeapon(String path) {
        final File file = new File(path, "gamedata/weapon.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, FashionOccupModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuling");
                    for (int i = 0; i < elements.length; i++) {
                        FashionOccupModelData data = new FashionOccupModelData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setOccupation(Byte.parseByte(XmlUtils.getAttribute(elements[i], "zhiye")));
                        data.setCost(GameCommon.parseDropData(XmlUtils.getAttribute(elements[i], "cost")));
                        data.setTime(Integer.parseInt(XmlUtils.getAttribute(elements[i], "time")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        tmpMap.put(data.getId(), data);
                    }
                    weaponDataMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载武器数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "weaponModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadArmor(String path) {
        final File file = new File(path, "gamedata/zhuangbeihuanhua.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, FashionOccupModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuling");
                    for (int i = 0; i < elements.length; i++) {
                        FashionOccupModelData data = new FashionOccupModelData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setOccupation(Byte.parseByte(XmlUtils.getAttribute(elements[i], "zhiye")));
                        data.setCost(GameCommon.parseDropData(XmlUtils.getAttribute(elements[i], "cost")));
                        data.setTime(Integer.parseInt(XmlUtils.getAttribute(elements[i], "time")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        tmpMap.put(data.getId(), data);
                    }
                    armorFashionMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载装备幻化数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "armorFashionModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadMount(String path) {
        final File file = new File(path, "gamedata/zuoqihuanhua.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, FashionModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuling");
                    for (int i = 0; i < elements.length; i++) {
                        FashionModelData data = new FashionModelData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setCost(GameCommon.parseDropData(XmlUtils.getAttribute(elements[i], "cost")));
                        data.setTime(Integer.parseInt(XmlUtils.getAttribute(elements[i], "time")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        tmpMap.put(data.getId(), data);
                    }
                    mountFashionMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载坐骑幻化数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "mountFashionModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadCombineRune(String path) {
        final File file = new File(path, "gamedata/resultantpieces.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, CombineRuneData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "artifact");
                    for (int i = 0; i < elements.length; i++) {
                        CombineRuneData data = new CombineRuneData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setLevel(Short.parseShort(XmlUtils.getAttribute(elements[i], "level")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        data.setDecompose(Short.parseShort(XmlUtils.getAttribute(elements[i], "dis")));
                        data.setCompose(Short.parseShort(XmlUtils.getAttribute(elements[i], "compos")));
                        data.setInto(Byte.parseByte(XmlUtils.getAttribute(elements[i], "into")));
                        tmpMap.put(data.getId(), data);
                    }
                    combineRuneMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载合击符文数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "CombineRuneModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadCombineRuneSuits(String path) {
        final File file = new File(path, "gamedata/suits.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, CombineRuneSuitsData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "artifact");
                    for (int i = 0; i < elements.length; i++) {
                        CombineRuneSuitsData data = new CombineRuneSuitsData();
                        data.setLevel(Short.parseShort(XmlUtils.getAttribute(elements[i], "level")));
                        data.setPvpindamage(Integer.parseInt(XmlUtils.getAttribute(elements[i], "pvpindamage")));
                        data.setPveindamage(Integer.parseInt(XmlUtils.getAttribute(elements[i], "pveindamage")));
                        data.setReangery(Integer.parseInt(XmlUtils.getAttribute(elements[i], "reangery")));
                        tmpMap.put(data.getLevel(), data);
                    }
                    combineRuneSuitsMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载合击符文套装数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "CombineRuneSuitsModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadTownSoul(String path) {
        final File file = new File(path, "gamedata/zhenhunzhuangbei.xml");
        ResourceListener listener = new ResourceListener() {

            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<String, TownSoulData> tmpMap = new HashMap<>();
                try {
                    Document document = XmlUtils.load(file);
                    Element root = document.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "z");
                    for (Element element : elements) {
                        String post = XmlUtils.getAttribute(element, "pos");
                        byte lv = Byte.parseByte(XmlUtils.getAttribute(element, "lv"));
                        DropData cost = GameCommon.parseDropData(XmlUtils.getAttribute(element, "cost"));
                        DropData compose = GameCommon.parseDropData(XmlUtils.getAttribute(element, "compos"));
                        DropData decompose = GameCommon.parseDropData(XmlUtils.getAttribute(element, "dis"));
                        int[] attribute = EAttrType.getIntAttr(element);
                        int holyPower = Integer.parseInt(XmlUtils.getAttribute(element, "sspower"));
                        TownSoulData townSoulData = new TownSoulData(lv, cost, compose, decompose, attribute, holyPower);
                        tmpMap.put(post + "_" + lv, townSoulData);
                    }
                    townSoulDataMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载镇魂装备数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "zhenhunzhuangbei";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadTownSoulSuit(String path) {
        final File file = new File(path, "gamedata/zhenhunfujia.xml");
        ResourceListener listener = new ResourceListener() {

            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, int[]> tmpMap = new HashMap<>();
                    Document document = XmlUtils.load(file);
                    Element root = document.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "shenqi");
                    for (Element element : elements) {
                        byte id = Byte.parseByte(XmlUtils.getAttribute(element, "id"));
                        int[] attribute = EAttrType.getIntAttr(element);
                        tmpMap.put(id, attribute);
                    }
                    townSoulSuitMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载镇魂装备套装数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "zhenhunfujia";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadTownSoulTreasure(String path) {
        final File file = new File(path, "gamedata/zhenhunbaoku.xml");
        ResourceListener listener = new ResourceListener() {

            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Document root = XmlUtils.load(file);
                    Element[] elements = XmlUtils.getChildrenByName(root.getDocumentElement(), "ku");
                    Map<Short, DropData> map1 = new HashMap<>();
                    Map<Short, TownSoulTreasureRewardData> map2 = new HashMap<>();
                    for (Element element : elements) {
                        short id = Short.parseShort(XmlUtils.getAttribute(element, "id"));
                        short order = Short.parseShort(XmlUtils.getAttribute(element, "order"));
                        DropData goods = GameCommon.parseDropData(XmlUtils.getAttribute(element, "goods"));
                        if (order > 0) {
                            map1.put(id, goods);
                        } else {
                            map2.put(id, new TownSoulTreasureRewardData(id, goods));
                        }
                    }
                    townSoulTreasureData = new TownSoulTreasureData(map1, map2);
                } catch (Exception e) {
                    logger.error("加载镇魂宝库数据出错...");
                    e.printStackTrace();
                }
            }

            public String toString() {
                return "zhenhunbaoku";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadTownSoulTreasureTurntable(String path) {
        final File file = new File(path, "gamedata/zhenhunbaokuzhuanpan.xml");
        ResourceListener listener = new ResourceListener() {

            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, TownSoulTurntableProbailityData> tmpMap = new HashMap<>();
                    short timeMax = Short.MAX_VALUE;
                    Document root = XmlUtils.load(file);
                    Element[] elements = XmlUtils.getChildrenByName(root.getDocumentElement(), "group");
                    for (int i = elements.length - 1; i > -1; --i) {
                        byte id = Byte.parseByte(XmlUtils.getAttribute(elements[i], "id"));
                        byte[] probaility = new byte[12];
                        probaility[0] = Byte.parseByte(XmlUtils.getAttribute(elements[i], "reward_1"));
                        probaility[1] = Byte.parseByte(XmlUtils.getAttribute(elements[i], "reward_2"));
                        probaility[2] = Byte.parseByte(XmlUtils.getAttribute(elements[i], "reward_3"));
                        probaility[3] = Byte.parseByte(XmlUtils.getAttribute(elements[i], "reward_4"));
                        probaility[4] = Byte.parseByte(XmlUtils.getAttribute(elements[i], "reward_5"));
                        probaility[5] = Byte.parseByte(XmlUtils.getAttribute(elements[i], "reward_6"));
                        probaility[6] = Byte.parseByte(XmlUtils.getAttribute(elements[i], "reward_7"));
                        probaility[7] = Byte.parseByte(XmlUtils.getAttribute(elements[i], "reward_8"));
                        probaility[8] = Byte.parseByte(XmlUtils.getAttribute(elements[i], "reward_9"));
                        probaility[9] = Byte.parseByte(XmlUtils.getAttribute(elements[i], "reward_10"));
                        probaility[10] = Byte.parseByte(XmlUtils.getAttribute(elements[i], "reward_11"));
                        probaility[11] = Byte.parseByte(XmlUtils.getAttribute(elements[i], "reward_12"));
                        short time = Short.parseShort(XmlUtils.getAttribute(elements[i], "times"));
                        String targetStr = XmlUtils.getAttribute(elements[i], "target");
                        String[] targetArr = targetStr.split(",");
                        byte[] target = new byte[targetArr.length];
                        for (int j = 0; j < target.length; ++j) {
                            target[j] = Byte.parseByte(targetArr[j]);
                        }
                        TownSoulTurntableProbailityData data = new TownSoulTurntableProbailityData(id, probaility, time, timeMax, target);
                        tmpMap.put(id, data);
                        timeMax = time;
                    }
                    townSoulTreasureTurntableMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载镇魂宝库转盘数据出错...");
                    e.printStackTrace();
                }
            }

            public String toString() {
                return "zhenhunbaokuzhuanpan";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadHalo(String path) {
        final File file = new File(path, "gamedata/jingjiehuanhua.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, FashionModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "jingjiehuanhua");
                    for (int i = 0; i < elements.length; i++) {
                        FashionModelData data = new FashionModelData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setCost(GameCommon.parseDropData(XmlUtils.getAttribute(elements[i], "cost")));
                        data.setTime(Integer.parseInt(XmlUtils.getAttribute(elements[i], "time")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        tmpMap.put(data.getId(), data);
                    }
                    haloFashionMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载光环幻化数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "haloFashionModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

//	public static EquipRlModelData getRlData(int lv) {
//		return rlMap.get(lv);
//	}

    public static EquipStrModelData getStrData(int lv, byte pos) {
        return strMap.get(lv + "_" + pos);
    }

    public static JewelModelData getJewelData(int lv, byte pos) {
        return jewelMap.get(lv + "_" + pos);
    }

    public static ZhuLingModelData getZhuLingData(int lv, byte pos) {
        return zhuLingMap.get(lv + "_" + pos);
    }

    public static EquipZhuHunModelData getZhuHunData(byte type, byte level) {
        return zhuhunMap.get(type + "_" + level);
    }

    public static EquipAttrData getTongjingYudiData(short level, byte type) {
        return tongjingYudiMap.get(level + "_" + type);
    }

    public static EquipAttrPlusData getZuoYouYanData(short level, byte type) {
        return zuoYouYanMap.get(level + "_" + type);
    }

    public static EquipCuiLianModelData getCuiLianData(byte type) {
        return cuilianMap.get(type);
    }

    public static FashionModelData getWingFashionData(byte id) {
        return wingMap.get(id);
    }

    public static FashionOccupModelData getWeaponFashionData(byte id) {
        return weaponDataMap.get(id);
    }

    public static FashionOccupModelData getArmorFashionData(byte id) {
        return armorFashionMap.get(id);
    }

    public static FashionModelData getMountFashionData(byte id) {
        return mountFashionMap.get(id);
    }

    public static FashionModelData getHaloFashionData(byte id) {
        return haloFashionMap.get(id);
    }

    public static CombineRuneData getCombineRuneData(byte id) {
        return combineRuneMap.get(id);
    }

    public static CombineRuneSuitsData getCombineRuneSuitsData(short level) {
        return combineRuneSuitsMap.get(level);
    }

    public static TownSoulData getTownSoulData(byte pos, byte lv) {
        return townSoulDataMap.get(pos + "_" + lv);
    }

    public static TownSoulData getTownSoulData(short itemId) {
        for (TownSoulData data : townSoulDataMap.values()) {
            if (data.getCost().getG() == itemId) {
                return data;
            }
        }
        return null;
    }

    public static int[] getTownSoulSuit(int id) {
        return townSoulSuitMap.get((byte) id);
    }

    public static int getTownSoulSuitMax() {
        return townSoulSuitMap.size();
    }

    public static TownSoulTreasureData getTownSoulTreasureData() {
        return townSoulTreasureData;
    }

    public static TownSoulTurntableProbailityData getTownSoulTurntableProbailityData(byte key) {
        return townSoulTreasureTurntableMap.get(key);
    }
}
