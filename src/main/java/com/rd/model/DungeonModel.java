package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.bean.dungeon.*;
import com.rd.common.GameCommon;
import com.rd.define.EAttrType;
import com.rd.model.data.DungeonGangData;
import com.rd.model.data.DungeonMaterialData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
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

public class DungeonModel {

    static Logger log = Logger.getLogger(DungeonModel.class.getName());

    private static Map<Byte, List<Byte>> dungeonIdListMap;
    private static Map<Byte, DungeonData> dungeonDataMap;
    private static Map<Byte, DungeonBossData> dungeonBossDataMap;
    private static Map<Byte, DungeonMaterialData> dungeonMaterialDataMap;
    private static Map<Short, DungeonDekaronData> dungeonDekaronDataMap;
    private static Map<Short, DungeonGangData> dungeonGangDataMap;
    private static Map<Short, DungeonFengmoData> dungeonFengmoDataMap;
    private static Map<Short, DungeonZhuzaishilianData> dungeonZhuzaishilianDataMap;
    private static Map<Short, DungeonZhuzaisaodangData> dungeonZhuzaisaodangDataMap;


    private DungeonModel() {

    }

    public static void loadData(String path) {
        loadDungeon(path);
        loadDungeonBoss(path);
        loadDungeonMaterial(path);
        loadDungeonDekaron(path);
        loadDungeonGang(path);
        loadDungeonFengmota(path);
        loadDungeonZhuzaishilian(path);
        loadDungeonZhuzaisaodang(path);
    }

    private static void loadDungeon(String path) {
        final File file = new File(path, "gamedata/copy.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, List<Byte>> idMap = new HashMap<>();
                    Map<Byte, DungeonData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "copy");
                    for (int i = 0; i < elements.length; i++) {
                        DungeonData data = new DungeonData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setType(Byte.parseByte(XmlUtils.getAttribute(elements[i], "type")));
                        data.setLevelLimit(Short.parseShort(XmlUtils.getAttribute(elements[i], "lvlimit")));
                        data.setVipLimit(Byte.parseByte(XmlUtils.getAttribute(elements[i], "viplimit")));
                        data.setPrice(Integer.parseInt(XmlUtils.getAttribute(elements[i], "price")));
                        data.setPriceAdd(Byte.parseByte(XmlUtils.getAttribute(elements[i], "priceAdd")));
                        data.setTimeLimit(Short.parseShort(XmlUtils.getAttribute(elements[i], "time")));
                        String[] times = XmlUtils.getAttribute(elements[i], "nums").split(";");
                        for (String t : times) {
                            String[] n = t.split(",");
                            byte[] num = new byte[2];
                            num[0] = Byte.parseByte(n[0]);
                            num[1] = Byte.parseByte(n[1]);
                            data.getTimesList().add(num);
                        }
                        tmpMap.put(data.getId(), data);

                        if (!idMap.containsKey(data.getType())) {
                            idMap.put(data.getType(), new ArrayList<Byte>());
                        }
                        idMap.get(data.getType()).add(data.getId());
                    }
                    dungeonDataMap = tmpMap;
                    dungeonIdListMap = idMap;
                } catch (Exception e) {
                    log.error("加载副本模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "dungeonModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadDungeonBoss(String path) {
        final File file = new File(path, "gamedata/gerenboss.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, DungeonBossData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "map");
                    for (int i = 0; i < elements.length; i++) {
                        DungeonBossData data = new DungeonBossData();
                        data.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        data.setBossId(Short.parseShort(XmlUtils.getAttribute(elements[i], "bossId")));
                        data.setRewards(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "reward")));
                        data.setQualityChance(GameCommon.parseShortChance(XmlUtils.getAttribute(elements[i], "gailv")));
                        tmpMap.put(data.getId(), data);
                    }
                    dungeonBossDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载个人boss副本模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "dungeonBossModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadDungeonMaterial(String path) {
        final File file = new File(path, "gamedata/cailiaofuben.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, DungeonMaterialData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "map");
                    for (int i = 0; i < elements.length; i++) {
                        byte id = Byte.parseByte(XmlUtils.getAttribute(elements[i], "id"));
                        short bossDrop = Short.parseShort(XmlUtils.getAttribute(elements[i], "bossDrop"));
                        DropData reward = GameCommon.parseDropData(XmlUtils.getAttribute(elements[i], "reward"));
                        DungeonMaterialData data = new DungeonMaterialData(bossDrop, reward);
                        tmpMap.put(id, data);
                    }
                    dungeonMaterialDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载材料副本模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "dungeonMaterialModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadDungeonDekaron(String path) {
        final File file = new File(path, "gamedata/zhuxiantai.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, DungeonDekaronData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "map");
                    int[] addAttr = new int[EAttrType.ATTR_SIZE];
                    for (int i = 0; i < elements.length; i++) {
                        DungeonDekaronData data = new DungeonDekaronData();
                        data.setId(Short.parseShort(XmlUtils.getAttribute(elements[i], "id")));
                        data.setFightId(Short.parseShort(XmlUtils.getAttribute(elements[i], "fightId")));
                        data.setDropData(GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "reward")));
                        data.setAddAttr(GameCommon.paraseSumAttr(addAttr, XmlUtils.getAttribute(elements[i], "shuxing")));
                        tmpMap.put(data.getId(), data);

                        addAttr = data.getAddAttr();
                    }
                    dungeonDekaronDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载挑战副本模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "dungeonDekaronModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadDungeonFengmota(String path) {
        final File file = new File(path, "gamedata/fengmota.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, DungeonFengmoData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "map");
//                    int[] addAttr=new int[EAttrType.ATTR_SIZE];
                    for (int i = 0; i < elements.length; i++) {
                        short id = Short.parseShort(XmlUtils.getAttribute(elements[i], "id"));
                        short fightId = Short.parseShort(XmlUtils.getAttribute(elements[i], "fightId"));
                        List<DropData> battleReward = GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "reward"));
                        List<DropData> dailyReward = GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "eachreward"));

                        DungeonFengmoData data = new DungeonFengmoData(id, fightId, battleReward, dailyReward);
                        tmpMap.put(data.getId(), data);
//                        addAttr=data.getAddAttr();
                    }
                    dungeonFengmoDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载封魔塔副本模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "fengmotaModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadDungeonZhuzaishilian(String path) {
        final File file = new File(path, "gamedata/zhuzaishilian.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, DungeonZhuzaishilianData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuzaishilian");
//                    int[] addAttr=new int[EAttrType.ATTR_SIZE];
                    for (int i = 0; i < elements.length; i++) {
                        short id = Short.parseShort(XmlUtils.getAttribute(elements[i], "id"));
                        short fightId = Short.parseShort(XmlUtils.getAttribute(elements[i], "fightId"));
                        List<DropData> battleReward = GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "reward"));
                        DungeonZhuzaishilianData data = new DungeonZhuzaishilianData(id, fightId, battleReward);
                        tmpMap.put(data.getId(), data);
                    }
                    dungeonZhuzaishilianDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载主宰试炼副本模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "zhuzaishilianModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadDungeonZhuzaisaodang(String path) {
        final File file = new File(path, "gamedata/zhuzaisaodang.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, DungeonZhuzaisaodangData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuzaisaodang");
                    for (int i = 0; i < elements.length; i++) {
                        short id = Short.parseShort(XmlUtils.getAttribute(elements[i], "id"));
                        String[] tips = (XmlUtils.getAttribute(elements[i], "tips")).split("-");
                        List<DropData> reward = GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "reward"));
                        DropData cost = GameCommon.parseDropData(XmlUtils.getAttribute(elements[i], "cost"));
                        DungeonZhuzaisaodangData data = new DungeonZhuzaisaodangData(id, tips, reward, cost);
                        tmpMap.put(data.getId(), data);
                    }
                    dungeonZhuzaisaodangDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载主宰试炼副本扫荡模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "zhuzaisaodangModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    private static void loadDungeonGang(String path) {
        final File file = new File(path, "gamedata/guildfuben.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, DungeonGangData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "map");
                    for (int i = 0; i < elements.length; i++) {
                        DungeonGangData data = new DungeonGangData();
                        data.setId(Short.parseShort(XmlUtils.getAttribute(elements[i], "id")));
                        data.setFightId(Short.parseShort(XmlUtils.getAttribute(elements[i], "fightId")));
                        data.setRewards(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "first")));
                        tmpMap.put(data.getId(), data);
                    }
                    dungeonGangDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载帮会副本模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "dungeonGangModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static DungeonData getDungeonData(byte id) {
        return dungeonDataMap.get(id);
    }

    public static List<Byte> getDungeonIdList(byte type) {
        return dungeonIdListMap.get(type);
    }

    public static DungeonBossData getDungeonBossData(byte id) {
        return dungeonBossDataMap.get(id);
    }

    public static DungeonMaterialData getDungeonMaterialBossDrop(byte id) {
        return dungeonMaterialDataMap.get(id);
    }

    public static DungeonDekaronData getDungeonDekaronData(short id) {
        return dungeonDekaronDataMap.get(id);
    }

    public static DungeonGangData getDungeonGangData(short id) {
        return dungeonGangDataMap.get(id);
    }

    public static DungeonFengmoData getFengmoData(short id) {
        return dungeonFengmoDataMap.get(id);
    }

    public static DungeonZhuzaishilianData getDungeonZhuzaishilianData(short id) {
        return dungeonZhuzaishilianDataMap.get(id);
    }

    public static DungeonZhuzaisaodangData getDungeonZhuzaisaodangData(short id) {
        return dungeonZhuzaisaodangDataMap.get(id);
    }

    public static List<DungeonZhuzaishilianData> getDungeonZhuzaishilianSweepData(short id) {
        List<DungeonZhuzaishilianData> shilianData = new ArrayList<DungeonZhuzaishilianData>();
        for (DungeonZhuzaishilianData map : dungeonZhuzaishilianDataMap.values()) {
            if (map.getId() <= id)
                shilianData.add(map);
        }
        return shilianData;
    }
}
