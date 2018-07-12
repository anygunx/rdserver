package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.common.ParseCommon;
import com.rd.model.data.MapData;
import com.rd.model.data.MapStageData;
import com.rd.model.data.MapStageRewardData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.XmlUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author ---
 * @version 1.0
 * @date 2018年4月17日下午1:42:10
 */
public class MapModel {

    static Logger log = Logger.getLogger(MapModel.class.getName());

    private static Map<Short, com.rd.bean.map.MapData> mapDataMap;
    private static Map<Short, MapStageRewardData> mapStageRewardDataMap;

    private static Map<Short, MapData> mapMap;

    private MapModel() {

    }

    public static void loadData(String path) {
        loadMap1(path);
        loadMapStageReward(path);

        loadMap(path);
    }

    private static void loadMap(String path) {
        final File file = new File(path, "gamedata/map.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, MapData> tmpMap = new HashMap<>();
                    String content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        short id = Short.parseShort(key);
                        JSONObject item = jsonObject.getJSONObject(key);
                        DropData exp = GameCommon.parseDropData(item.getString("exp"));
                        DropData gold = GameCommon.parseDropData(item.getString("gold"));

                        Map<Short, MapStageData> stageMap = new HashMap<>();
                        loadMapStage(path, id, stageMap);
                        MapData data = new MapData(id, exp, gold, stageMap);
                        tmpMap.put(id, data);
                    }
                    mapMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载地图模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "mapModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadMapStage(String path, short id, Map<Short, MapStageData> stageMap) {
        final File file = new File(path, "gamedata/map_" + id + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    String content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        short id = Short.parseShort(key);
                        int boss = value.getInt("boss");
                        int[] monster = ParseCommon.parseCommaInt(value.getString("monster"));
                        DropData exp = GameCommon.parseDropData(value.getString("exp"));
                        DropData gold = GameCommon.parseDropData(value.getString("gold"));
                        MapStageData data = new MapStageData(id, boss, monster, exp, gold, !iterator.hasNext());
                        stageMap.put(id, data);
                    }
                } catch (Exception e) {
                    log.error("加载地图关卡数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "map" + id + "Model";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadMap1(String path) {
        final File file = new File(path, "gamedata/map.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    short startStage = 0;
                    Map<Short, com.rd.bean.map.MapData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "map");
                    for (int i = 0; i < elements.length; i++) {
                        com.rd.bean.map.MapData data = new com.rd.bean.map.MapData();
                        short id = Short.parseShort(XmlUtils.getAttribute(elements[i], "id"));
                        if (id > 10000) {
                            continue;
                        }
                        data.setId(id);
                        data.setNextId(Short.parseShort(XmlUtils.getAttribute(elements[i], "nextid")));
                        data.setStageCount(Short.parseShort(XmlUtils.getAttribute(elements[i], "boshu")));
                        tmpMap.put(data.getId(), data);

                        data.setStartStage(startStage);
                        startStage = data.getStageCount();
                    }
                    mapDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载地图模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "mapModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadMapStageReward(String path) {
        final File file = new File(path, "gamedata/exp.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, MapStageRewardData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "copy");
                    for (int i = 0; i < elements.length; i++) {
                        MapStageRewardData data = new MapStageRewardData();
                        data.setId(Short.parseShort(XmlUtils.getAttribute(elements[i], "id")));
                        data.setMonsterExp(Short.parseShort(XmlUtils.getAttribute(elements[i], "exp")));
                        data.setMonsterGold(Short.parseShort(XmlUtils.getAttribute(elements[i], "gold")));
                        data.setBossExp(Integer.parseInt(XmlUtils.getAttribute(elements[i], "bossExp")));
                        data.setBossGold(Integer.parseInt(XmlUtils.getAttribute(elements[i], "bossGold")));
                        data.setRewardList(GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "artifact")));
                        tmpMap.put(data.getId(), data);
                    }
                    mapStageRewardDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载地图关卡奖励模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "mapStageRewardModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static com.rd.bean.map.MapData getMapDataById(short id) {
        return mapDataMap.get(id);
    }

    public static short getMapStageBossId(short mapStage) {
        return (short) (mapStage + 1000);
    }

    public static MapStageRewardData getMapStageRewardDataById(short id) {
        return mapStageRewardDataMap.get(id);
    }

    public static MapStageData getMapStageData(short mapId, short stageId) {
        return mapMap.get(mapId).getStageMap().get(stageId);
    }
}
