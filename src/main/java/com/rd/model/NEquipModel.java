package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.define.NEquipUpGradeType;
import com.rd.enumeration.EAttr;
import com.rd.model.data.equip.*;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NEquipModel {
    private static Logger logger = Logger.getLogger(NEquipModel.class);

    public static void loadModelData(String path) {
        loadEquipModelData(path);
    }

    /**
     * 装备强化 精炼
     *
     * @param path
     */
    private static void loadEquipModelData(String path) {
        for (NEquipUpGradeType equipUpGradeType : NEquipUpGradeType.values()) {
            switch (equipUpGradeType) {
                case EQUIP_QH:
                    loadEquipStrModelData(path, equipUpGradeType);
                    break;
                case EQUIQ__JL:
                    loadEquipJLModelData(path, equipUpGradeType);
                    break;
                case EQUIQ__DL:
                    loadEquipDLModelData(path, equipUpGradeType);
                    break;
                case EQUIQ__BS:
                    loadEquipBSModelData(path, equipUpGradeType);
                    break;
                case EQUIQ__QHDS:
                    loadEquipQHDSModelData(path, equipUpGradeType);
                    break;
                case EQUIQ__JLDS:
                    loadEquipJLDSModelData(path, equipUpGradeType);
                    break;
                case EQUIQ__DLDS:
                    loadEquipDLDSModelData(path, equipUpGradeType);
                    break;
                case EQUIQ__BSDS:
                    loadEquipBSDSModelData(path, equipUpGradeType);
                    break;

                default:
                    break;
            }
        }


    }


    /**
     * 装备强化数据
     *
     * @param path
     * @param json
     */
    private static void loadEquipStrModelData(String path, NEquipUpGradeType equipUpGradeType) {
        final File file = new File(path, "gamedata/" + equipUpGradeType.getJson() + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<String, com.rd.model.data.equip.NEquipStrModelData> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int lv = value.getInt("lv");
                        int postion = value.getInt("postion");
                        DropData cost = GameCommon.parseDropData(value.getString("cost"));
                        int[] attr = EAttr.getIntAttr(value);
                        com.rd.model.data.equip.NEquipStrModelData data =
                                new com.rd.model.data.equip.NEquipStrModelData(lv, postion, cost, attr);

                        temp.put(data.getLv() + "_" + data.getPos(), data);
                    }
                    //strMap = temp;
                    equipUpGradeType.setDatas(temp);
                } catch (IOException e) {
                    logger.error("加载装备强化数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "strengthModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    /**
     * 装备精炼数据
     *
     * @param path
     * @param json
     */
    private static void loadEquipJLModelData(String path, NEquipUpGradeType equipUpGradeType) {
        final File file = new File(path, "gamedata/" + equipUpGradeType.getJson() + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<String, NEquipJingLianModelData> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int lv = value.getInt("lv");
                        int postion = value.getInt("postion");
                        DropData cost = GameCommon.parseDropData(value.getString("cost"));
                        int[] attr = EAttr.getIntAttr(value);
                        NEquipJingLianModelData data =
                                new NEquipJingLianModelData(lv, postion, cost, attr);

                        temp.put(data.getLv() + "_" + data.getPos(), data);
                    }
                    //jLMap = temp;
                    equipUpGradeType.setDatas(temp);
                } catch (IOException e) {
                    logger.error("加载装备精炼数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "strengthModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    /**
     * 装备锻炼数据
     *
     * @param path
     * @param json
     */
    private static void loadEquipDLModelData(String path, NEquipUpGradeType equipUpGradeType) {
        final File file = new File(path, "gamedata/" + equipUpGradeType.getJson() + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<String, NEquipDLModelData> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int lv = value.getInt("lv");
                        int postion = value.getInt("postion");
                        DropData cost = GameCommon.parseDropData(value.getString("cost"));
                        int[] attr = EAttr.getIntAttr(value);
                        NEquipDLModelData data =
                                new NEquipDLModelData(lv, postion, cost, attr);
                        temp.put(data.getLv() + "_" + data.getPos(), data);
                    }
                    //strMap = temp;
                    equipUpGradeType.setDatas(temp);
                } catch (Exception e) {
                    logger.error("加载" + equipUpGradeType.getJson() + "装备锻炼数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "nDuanlianModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    /**
     * 装备宝石数据
     *
     * @param path
     * @param json
     */
    private static void loadEquipBSModelData(String path, NEquipUpGradeType equipUpGradeType) {
        final File file = new File(path, "gamedata/" + equipUpGradeType.getJson() + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<String, NEquipBSModelData> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int lv = value.getInt("lv");
                        int postion = value.getInt("postion");
                        DropData cost = GameCommon.parseDropData(value.getString("cost"));
                        int[] attr = EAttr.getIntAttr(value);
                        NEquipBSModelData data =
                                new NEquipBSModelData(lv, postion, cost, attr);

                        //data.setAttr(EAttrType.getAttr(elements[i]));
                        temp.put(data.getLv() + "_" + data.getPos(), data);
                    }
                    //strMap = temp;
                    equipUpGradeType.setDatas(temp);
                } catch (IOException e) {
                    logger.error("加载装备宝石数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "nBaoshiModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    /**
     * 加载装备强化大师数据
     *
     * @param path
     */
    public static void loadEquipQHDSModelData(String path, NEquipUpGradeType equipUpGradeType) {
        final File file = new File(path, "gamedata/" + equipUpGradeType.getJson() + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, NEquiQHDSModelData> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int lv = value.getInt("lv");
                        int target = value.getInt("target");
                        int[] attr = EAttr.getIntAttr(value);
                        NEquiQHDSModelData data =
                                new NEquiQHDSModelData(lv, target, attr);

                        temp.put((short) data.getTarget(), data);
                    }
                    //equiQHDSModelMap1 = temp;
                    equipUpGradeType.setdSData(temp);
                } catch (IOException e) {
                    logger.error("加载装备强化大师数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "equijldsmodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    /**
     * 加载装备精炼大师数据
     *
     * @param path
     */
    public static void loadEquipJLDSModelData(String path, NEquipUpGradeType equipUpGradeType) {
        final File file = new File(path, "gamedata/" + equipUpGradeType.getJson() + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, NEquipJLDSModelData> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int lv = value.getInt("lv");
                        int target = value.getInt("target");
                        int[] attr = EAttr.getIntAttr(value);
                        NEquipJLDSModelData data =
                                new NEquipJLDSModelData(lv, target, attr);

                        temp.put((short) data.getTarget(), data);
                    }
                    //equiQHDSModelMap1 = temp;
                    equipUpGradeType.setdSData(temp);
                } catch (IOException e) {
                    logger.error("加载装备精炼大师数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "equijldsmodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    /**
     * 加载装备锻炼大师数据
     *
     * @param path
     */
    public static void loadEquipDLDSModelData(String path, NEquipUpGradeType equipUpGradeType) {
        final File file = new File(path, "gamedata/" + equipUpGradeType.getJson() + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, NEquiDLDSModelData> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int lv = value.getInt("lv");
                        int target = value.getInt("target");
                        int[] attr = EAttr.getIntAttr(value);

                        NEquiDLDSModelData data =
                                new NEquiDLDSModelData(lv, target, attr);

                        temp.put((short) data.getTarget(), data);
                    }
                    //equiQHDSModelMap1 = temp;
                    equipUpGradeType.setdSData(temp);
                } catch (IOException e) {
                    logger.error("加载装备锻炼大师数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "nDuanLianDaShiModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    /**
     * 加载装备锻炼大师数据
     *
     * @param path
     */
    public static void loadEquipBSDSModelData(String path, NEquipUpGradeType equipUpGradeType) {
        final File file = new File(path, "gamedata/" + equipUpGradeType.getJson() + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, NEquiBSDSModelData> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int lv = value.getInt("lv");
                        int target = value.getInt("target");
                        int[] attr = EAttr.getIntAttr(value);
                        NEquiBSDSModelData data =
                                new NEquiBSDSModelData(lv, target, attr);

                        temp.put((short) data.getTarget(), data);
                    }

                    equipUpGradeType.setdSData(temp);
                } catch (IOException e) {
                    logger.error("加载装备宝石大师数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "nBaoShiDaShiModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


}
