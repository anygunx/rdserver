package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.model.data.richang.NRiChang300Data;
import com.rd.model.data.richang.NZhongKuiData;
import com.rd.model.data.richang.NZuDuiData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.StringUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NRiChangModel {
    private static Logger logger = Logger.getLogger(NRiChangModel.class);
    private static Map<Integer, NZhongKuiData> zhongkuiMap;
    private static Map<Integer, NRiChang300Data> richang300Map;
    private static Map<Integer, NZuDuiData> zuDuiMap;

    public static void loadData(String path) {
        loadZhongKui(path);
        loadRiChang300(path);
        loadZuDui(path);

    }

    private static void loadZhongKui(String path) {
        final File file = new File(path, "gamedata/zhongkui.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NZhongKuiData> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);

                        int id = Integer.parseInt(key);
                        int lv = value.getInt("lv");

                        int exp = value.getInt("exp");
                        List<DropData> reward = GameCommon.parseDropDataList(value.getString("reward"));

                        NZhongKuiData data =
                                new NZhongKuiData(id, (short) lv, reward, exp);
                        temp.put(lv, data);
                    }

                    zhongkuiMap = temp;
                } catch (IOException e) {
                    logger.error("加载钟馗数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "chongkuirichangmodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    private static void loadRiChang300(String path) {
        final File file = new File(path, "gamedata/meiri300.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NRiChang300Data> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);

                        int id = Integer.parseInt(key);
                        int lv = value.getInt("lv");


                        String expStr = value.getString("exp");
                        String[] expS = expStr.split(StringUtil.SEMIC);
                        int[] exps = new int[expS.length];
                        int i = 0;
                        for (String string : expS) {
                            int exp = Integer.parseInt(string);
                            exps[i] = exp;
                            i++;
                        }


                        String targetStr = value.getString("target");
                        int[] targets = StringUtil.getIntList(targetStr);
                        String rewardStr = value.getString("reward");
                        String[] strs = rewardStr.split(StringUtil.SEMIC);
                        List<List<DropData>> tempList = new ArrayList<>();
                        for (String string : strs) {
                            List<DropData> reward = GameCommon.parseDropDataList(string);
                            tempList.add(reward);
                        }


                        NRiChang300Data data =
                                new NRiChang300Data(id, targets, exps, tempList);
                        temp.put(lv, data);
                    }

                    richang300Map = temp;
                } catch (IOException e) {
                    logger.error("加载日常300数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "chongkuirichangmodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    private static void loadZuDui(String path) {
        final File file = new File(path, "gamedata/zuduililian.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NZuDuiData> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);

                        int id = Integer.parseInt(key);
                        int lv = value.getInt("lv");

                        int exp = value.getInt("exp");
                        List<DropData> reward = GameCommon.parseDropDataList(value.getString("reward"));

                        NZuDuiData data =
                                new NZuDuiData(id, (short) lv, reward, exp);
                        temp.put(lv, data);
                    }

                    zuDuiMap = temp;
                } catch (IOException e) {
                    logger.error("加载组队日常数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "zuduirichangmodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    public static NZhongKuiData getNZhongKuiData(int level) {
        return zhongkuiMap.get(level);
    }

    public static NRiChang300Data getNRiChang300Data(int level) {
        return richang300Map.get(level);
    }


    public static NZuDuiData getNZuDuiData(int level) {
        return zuDuiMap.get(level);
    }

}
