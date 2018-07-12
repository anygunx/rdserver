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

public class NightFightModel {

    static Logger log = Logger.getLogger(GangModel.class.getName());

    private static List<Entry<Short, DropData>> exchangeData;

    private static Map<Byte, List<DropData>> rankReward;

    private static byte maxRank;

    private NightFightModel() {

    }

    public static void loadData(String path) {
        loadExchangeData(path);
        loadRankData(path);
    }

    private static void loadExchangeData(String path) {
        final File file = new File(path, "gamedata/biqiscore.xml");
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
                    exchangeData = data;
                } catch (Exception e) {
                    log.error("加载夜战战绩领取模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "exchangeModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadRankData(String path) {
        final File file = new File(path, "gamedata/biqirank.xml");
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
                    Element[] elements = XmlUtils.getChildrenByName(root, "biqirank");
                    for (int i = 0; i < elements.length; i++) {
                        byte rank = Byte.parseByte(XmlUtils.getAttribute(elements[i], "rank"));
                        List<DropData> dropList = GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "reward"));
                        data.put(rank, dropList);
                        maxRank = rank;
                    }
                    rankReward = data;
                } catch (Exception e) {
                    log.error("加载夜战排名奖励模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "nightFightRewardModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static Entry<Short, DropData> getExchangeFeats(short score) {
        for (Entry<Short, DropData> entry : exchangeData) {
            if (entry.getKey() > score) {
                return entry;
            }
        }
        return null;
    }

    public static List<DropData> getRankReward(byte rank) {
        return rankReward.get(rank);
    }

    public static byte getMaxRank() {
        return maxRank;
    }
}
