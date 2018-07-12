package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.bean.rank.NJingJiRank;
import com.rd.common.ParseCommon;
import com.rd.define.NJingJiChangType;
import com.rd.model.data.husong.NHuSongData;
import com.rd.model.data.jingji.NJJMGroupData;
import com.rd.model.data.jingji.NJJMRandomData;
import com.rd.model.data.jingji.NJingJiAoundData;
import com.rd.model.data.jingji.NJingJiChangModel;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

/***
 * 竞技场
 * @author MyPC
 *
 */
public class NJingJiModel {
    static Logger log = Logger.getLogger(NHuSongModel.class.getName());
    private static Map<Integer, NJingJiChangModel> jingJiChangMap;
    private static Map<String, List<NJingJiAoundData>> jingjiAoundMap;
    private static List<Integer> jingJiNumList;

    private static Map<Integer, NJJMGroupData> JJMGroupIdMap;
    private static Map<String, NJJMRandomData> JJMRandomMap;

    private static List<NJingJiRank> jingJiRankTop20;

    public static void loadData(String path) {

        loadJingJiChang(path);
        loadJingJiNum(path);
        loadJingJiMingCi(path);
        loadJingJiBot(path);


    }

    private static void loadJingJiChang(String path) {
        final File file = new File(path, "gamedata/jingjichang.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NJingJiChangModel> temp = new HashMap<>();

                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        String rankStr = value.getString("rank");
                        String[] strs = rankStr.split("-");
                        int rankd_min = 0;
                        int rankd_max = 0;
                        if (strs.length > 0) {
                            rankd_min = Integer.parseInt(strs[0]);
                            rankd_max = Integer.parseInt(strs[1]);
                        }

                        List<DropData> rewards = ParseCommon.parseSemicolonDropDataList(value.getString("reward"));
                        NJingJiChangModel data = new NJingJiChangModel(id, rankd_min, rankd_max, rewards);
                        temp.put(id, data);

                    }
                    jingJiChangMap = temp;
                } catch (IOException e) {
                    log.error("加载竞技场数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "jijingchangmodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    private static void loadJingJiNum(String path) {
        final File file = new File(path, "gamedata/jingjinum.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NHuSongData> temp = new HashMap<>();
                List<Integer> list = new ArrayList<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        String rankStr = value.getString("nums");
                        String[] rankss = rankStr.split(",");
                        for (int i = 0; i < rankss.length; i++) {
                            if (rankss[i] == content) {
                                continue;
                            }

                            list.add(Integer.parseInt(rankss[i]));
                        }

                        jingJiNumList = list;
                    }

                } catch (IOException e) {
                    log.error("加载竞技NUM数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "jijingnnummodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadJingJiMingCi(String path) {
        final File file = new File(path, "gamedata/jingjimingci.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<String, List<NJingJiAoundData>> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        String rankStr = value.getString("rank");
                        String[] strs = rankStr.split("-");
                        int rankd_min = 0;
                        int rankd_max = 0;
                        if (strs.length > 0) {
                            rankd_min = Integer.parseInt(strs[0]);
                            rankd_max = Integer.parseInt(strs[1]);
                        }
                        List<NJingJiAoundData> list = new ArrayList<>();
                        for (int i = 1; i < 6; i++) {
                            String pos = value.getString("position_" + i);
                            String[] p = pos.split(",");
                            int random_min = Integer.parseInt(p[0]);
                            int random_max = Integer.parseInt(p[1]);
                            NJingJiAoundData data = new NJingJiAoundData(i, rankd_min, rankd_max, random_min, random_max);
                            list.add(data);
                        }

                        temp.put(rankStr, list);
                    }
                    jingjiAoundMap = temp;

                } catch (IOException e) {
                    log.error("加载竞技场名次数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "jijingchangmodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    private static void loadJingJiBot(String path) {
        final File file = new File(path, "gamedata/jingjibot.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NJJMGroupData> temp = new HashMap<>();
                Map<String, NJJMRandomData> tempRD = new HashMap<>();
                int monsterid = 0;
                List<NJingJiRank> temptop20 = new ArrayList<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {

                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        String rankStr = value.getString("rank");
                        String[] strs = rankStr.split("-");
                        int rankd_min = 0;
                        int rankd_max = 0;
                        if (strs.length > 0) {
                            rankd_min = Integer.parseInt(strs[0]);
                            rankd_max = Integer.parseInt(strs[1]);
                        }
                        int front = value.getInt("front");
                        int back = value.getInt("back");
                        NJJMGroupData group = new NJJMGroupData(id, front, back);
                        temp.put(id, group);
                        if (rankd_min == 1 && monsterid == 0) {
                            monsterid = front;
                        }

                        NJJMRandomData jjd = tempRD.get(rankStr);
                        if (jjd == null) {
                            jjd = new NJJMRandomData();
                            jjd.setStartnum(rankd_min);
                            jjd.setEndnum(rankd_max);
                            jjd.setRandom_num_start(id);
                            jjd.setRandom_num_end(id);
                            tempRD.put(rankStr, jjd);
                        } else {
                            int start = jjd.getRandom_num_start();
                            int end = jjd.getRandom_num_end();
                            if (start > id) {
                                jjd.setRandom_num_start(id);
                            }
                            if (end < id) {
                                jjd.setRandom_num_end(id);
                            }
                        }


                    }

                    for (int i = 0; i < 20; i++) {
                        NJingJiRank rank = new NJingJiRank();
                        rank.setId(monsterid);
                        rank.setRank(i + 1);
                        rank.setType(NJingJiChangType.TYPE_JIQI);
                        temptop20.add(rank);
                    }

                    JJMGroupIdMap = temp;
                    JJMRandomMap = tempRD;
                    jingJiRankTop20 = temptop20;

                } catch (IOException e) {
                    log.error("加载竞技场机器人数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "jijingbotmodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    public static Map<String, List<NJingJiAoundData>> getJingJiAoundMap() {

        return jingjiAoundMap;
    }

    public static Map<Integer, NJJMGroupData> getNJJMGroupDataMap() {
        return JJMGroupIdMap;
    }


    public static Map<String, NJJMRandomData> getNJJMRandomList() {
        return JJMRandomMap;
    }

    public static List<Integer> getjingJiNumList() {

        return jingJiNumList;
    }

    public static List<NJingJiRank> getjingJiRankTop20() {
        return jingJiRankTop20;
    }

    public static Map<Integer, NJingJiChangModel> getNJingJiChangModelMap() {
        return jingJiChangMap;

    }

}
