package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

/**
 * 对战数据
 *
 * @author ---
 * @version 1.0
 * @date 2018年1月3日下午6:16:12
 */
public class BattleModel {

    static Logger log = Logger.getLogger(GangModel.class.getName());

    private static List<Entry<Short, DropData>> scoreReachData;

    private static Map<Byte, List<DropData>> gangRankData;

    private static Map<Byte, List<DropData>> gangMemberRankData;

    private static List<DropData> gangRewardData;

    private BattleModel() {

    }

    public static void loadData(String path) {
        loadScoreReachData(path);
        loadGangRankData(path);
        loadGangMemberRankData(path);
        loadGangRewardData(path);
    }

    private static void loadScoreReachData(String path) {
        final File file = new File(path, "gamedata/jifendabiao.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    List<Entry<Short, DropData>> data = new ArrayList<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "biqiscore");
                    for (int i = 0; i < elements.length; i++) {
                        short score = Short.parseShort(XmlUtils.getAttribute(elements[i], "score"));
                        DropData feats = GameCommon.parseDropData(XmlUtils.getAttribute(elements[i], "feats"));

                        data.add(new AbstractMap.SimpleEntry<>(score, feats));
                    }
                    scoreReachData = data;
                } catch (Exception e) {
                    log.error("加载传世争霸积分达标模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "scoreReachModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadGangRankData(String path) {
        final File file = new File(path, "gamedata/gonghuirank.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, List<DropData>> data = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "gonghuirank");
                    for (int i = 0; i < elements.length; i++) {
                        byte rank = Byte.parseByte(XmlUtils.getAttribute(elements[i], "rank"));
                        List<DropData> reward = GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "reward"));

                        data.put(rank, reward);
                    }
                    gangRankData = data;
                } catch (Exception e) {
                    log.error("加载传世争霸公会排名奖励模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "gangRankModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadGangMemberRankData(String path) {
        final File file = new File(path, "gamedata/jifenrank.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, List<DropData>> data = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "jifenrank");
                    for (int i = 0; i < elements.length; i++) {
                        byte rank = Byte.parseByte(XmlUtils.getAttribute(elements[i], "rank"));
                        List<DropData> reward = GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "reward"));

                        data.put(rank, reward);
                    }
                    gangMemberRankData = data;
                } catch (Exception e) {
                    log.error("加载传世争霸公会成员排名奖励模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "gangMemberRankModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadGangRewardData(String path) {
        final File file = new File(path, "gamedata/meirijiangli.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    List<DropData> data = new ArrayList<DropData>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "jifenrank");
                    for (int i = 0; i < elements.length; i++) {
                        data = GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "reward"));
                    }
                    gangRewardData = data;
                } catch (Exception e) {
                    log.error("加载传世争霸公会每日奖励模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "gangRewardDataModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static List<DropData> getGangRewardData() {
        return gangRewardData;
    }

    public static DropData getScoreReach(int score) {
        int mark = 0;
        DropData dropData = null;
        for (Entry<Short, DropData> entry : scoreReachData) {
            if (entry.getKey() > mark && entry.getKey() <= score) {
                dropData = entry.getValue();
                mark = entry.getKey();
            }
        }
        return dropData;
    }

    public static List<DropData> getGangRankReward(byte rank) {
        return gangRankData.get(rank);
    }

    public static List<DropData> getGangMemberRankReward(byte rank) {
        return gangMemberRankData.get(rank);
    }
}
