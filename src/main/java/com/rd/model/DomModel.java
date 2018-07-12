package com.rd.model;

import com.rd.define.EAttrType;
import com.rd.model.data.DomLvData;
import com.rd.model.data.DomRankData;
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
 * 主宰数据
 *
 * @author U-Demon
 */
public class DomModel {

    private static Logger logger = Logger.getLogger(DomModel.class);

    private static final String PATH_LV = "gamedata/shanggutaozhuang.xml";
    private static final String NAME_LV = "shangguLvModel";

    private static Map<Byte, Map<Short, DomLvData>> lvMap;

    private static final String PATH_RANK = "gamedata/shanggujinjie.xml";
    private static final String NAME_RANK = "shangguRankModel";

    private static Map<Byte, Map<Short, DomRankData>> rankMap;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadLvData(path);
        loadRankData(path);
    }

    public static void loadLvData(String path) {
        final File file = new File(path, PATH_LV);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, Map<Short, DomLvData>> temp = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    //加载奖励
                    Element[] elements = XmlUtils.getChildrenByName(root, "equipment");
                    for (int i = 0; i < elements.length; i++) {
                        Element elem = elements[i];
                        DomLvData data = new DomLvData();
                        data.setPos(Byte.valueOf(XmlUtils.getAttribute(elem, "position")));
                        data.setLv(Short.valueOf(XmlUtils.getAttribute(elem, "level")));
                        data.setCost(StringUtil.getRewardDropData(XmlUtils.getAttribute(elem, "cost")));
                        data.setRate(Integer.valueOf(XmlUtils.getAttribute(elem, "gailv")));
                        data.setAttr(EAttrType.getAttr(elem, "effect"));
                        if (!temp.containsKey(data.getPos()))
                            temp.put(data.getPos(), new HashMap<Short, DomLvData>());
                        temp.get(data.getPos()).put(data.getLv(), data);
                    }
                    lvMap = temp;
                } catch (Exception e) {
                    logger.error("加载主宰等级数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return NAME_LV;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadRankData(String path) {
        final File file = new File(path, PATH_RANK);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, Map<Short, DomRankData>> temp = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    //加载奖励
                    Element[] elements = XmlUtils.getChildrenByName(root, "jinjie");
                    for (int i = 0; i < elements.length; i++) {
                        Element elem = elements[i];
                        DomRankData data = new DomRankData();
                        data.setPos(Byte.valueOf(XmlUtils.getAttribute(elem, "position")));
                        data.setRank(Short.valueOf(XmlUtils.getAttribute(elem, "jieji")));
                        data.setCost(StringUtil.getRewardDropData(XmlUtils.getAttribute(elem, "cost")));
                        data.setAttr(EAttrType.getIntAttr(elem));
                        if (!temp.containsKey(data.getPos()))
                            temp.put(data.getPos(), new HashMap<Short, DomRankData>());
                        temp.get(data.getPos()).put(data.getRank(), data);
                    }
                    rankMap = temp;
                } catch (Exception e) {
                    logger.error("加载主宰阶级数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return NAME_RANK;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static DomLvData getLvData(byte pos, int lv) {
        Map<Short, DomLvData> map = lvMap.get(pos);
        if (map == null)
            return null;
        return map.get((short) lv);
    }

    public static DomRankData getRankData(byte pos, short rank) {
        Map<Short, DomRankData> map = rankMap.get(pos);
        if (map == null)
            return null;
        return map.get((short) rank);
    }

}
