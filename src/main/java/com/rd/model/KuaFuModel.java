package com.rd.model;

import com.rd.model.data.ArenaPersonModelData;
import com.rd.model.data.ArenaServerModelData;
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

/**
 * 跨服数据
 *
 * @author Created by U-Demon on 2016年11月3日 下午7:43:20
 * @version 1.0.0
 */
public class KuaFuModel {

    private static Logger logger = Logger.getLogger(KuaFuModel.class);

    //全服奖励数据
    private static final String ARENA_SERVER_PATH = "gamedata/kuafureward.xml";
    private static final String ARENA_SERVER_NAME = "kuafuqfModel";
    private static Map<Byte, ArenaServerModelData> serverMap;

    //个人奖励数据
    private static final String ARENA_PERSON_PATH = "gamedata/kuafujingjichang.xml";
    private static final String ARENA_PERSON_NAME = "kuafugrModel";
    private static Map<Byte, ArenaPersonModelData> personMap;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadArenaServer(path);
        loadArenaPerson(path);
    }

    private static void loadArenaServer(String path) {
        final File file = new File(path, ARENA_SERVER_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, ArenaServerModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "map");
                    for (Element ele : elements) {
                        ArenaServerModelData data = new ArenaServerModelData();
                        data.setId(Byte.valueOf(XmlUtils.getAttribute(ele, "id")));
                        data.setRewards(StringUtil.getRewardDropList(XmlUtils.getAttribute(ele, "reward")));
                        data.setTitle(XmlUtils.getAttribute(ele, "title"));
                        data.setContent(XmlUtils.getAttribute(ele, "content"));
                        tmpMap.put(data.getId(), data);
                    }
                    serverMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载竞技场全服奖励数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return ARENA_SERVER_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadArenaPerson(String path) {
        final File file = new File(path, ARENA_PERSON_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, ArenaPersonModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "bossReward");
                    for (Element ele : elements) {
                        ArenaPersonModelData data = new ArenaPersonModelData();
                        data.setId(Byte.valueOf(XmlUtils.getAttribute(ele, "id")));
                        data.setMin(Byte.valueOf(XmlUtils.getAttribute(ele, "min")));
                        data.setMax(Byte.valueOf(XmlUtils.getAttribute(ele, "max")));
                        data.setRewards(StringUtil.getRewardDropList(XmlUtils.getAttribute(ele, "reward")));
                        data.setTitle(XmlUtils.getAttribute(ele, "title"));
                        data.setContent(XmlUtils.getAttribute(ele, "content"));
                        tmpMap.put(data.getId(), data);
                    }
                    personMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载竞技场个人奖励数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return ARENA_PERSON_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static ArenaServerModelData getArenaServerReward(int rank) {
        return serverMap.get((byte) rank);
    }

    public static ArenaPersonModelData getArenaPersonReward(int rank) {
        for (ArenaPersonModelData model : personMap.values()) {
            if (rank >= model.getMin() && rank <= model.getMax()) {
                return model;
            }
        }
        return null;
    }

}
