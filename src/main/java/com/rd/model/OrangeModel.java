package com.rd.model;

import com.rd.define.EAttrType;
import com.rd.define.GameDefine;
import com.rd.model.data.OrangeModelData;
import com.rd.model.data.RedModelData;
import com.rd.model.data.XunBaoModelData;
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
 * 橙装数据
 *
 * @author Created by U-Demon on 2016年11月23日 下午2:04:00
 * @version 1.0.0
 */
public class OrangeModel {

    private static Logger logger = Logger.getLogger(OrangeModel.class);

    //橙装数据
    private static final String PATH = "gamedata/chengzhuang.xml";
    private static final String NAME = "chengzhuang";
    private static Map<Short, OrangeModelData> lvMap;

    //红装数据
    private static final String PATH_RED = "gamedata/tianshenzhuangbei.xml";
    private static final String NAME_RED = "hongzhuang";
    private static Map<Short, RedModelData> redMap;
    private static Map<Short, Map<Byte, RedModelData>> redLvMap;

    //寻宝数据
    private static final String PATH_XUNBAO = "gamedata/xunbao.xml";
    private static final String NAME_XUNBAO = "xunbao";
    private static XunBaoModelData xunbaoData = new XunBaoModelData();

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadOrange(path);
        loadRed(path);
        loadXunbao(path);
    }

    public static void loadOrange(String path) {
        final File file = new File(path, PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, OrangeModelData> tepLv = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuling");
                    for (int i = 0; i < elements.length; i++) {
                        OrangeModelData data = new OrangeModelData();
                        data.setLevel(Short.valueOf(XmlUtils.getAttribute(elements[i], "level")));
                        data.setNext(Short.valueOf(XmlUtils.getAttribute(elements[i], "next")));
                        data.setHecheng(Integer.valueOf(XmlUtils.getAttribute(elements[i], "hecheng")));
                        tepLv.put(data.getLevel(), data);
                    }
                    lvMap = tepLv;
                } catch (Exception e) {
                    logger.error("加载橙装数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadRed(String path) {
        final File file = new File(path, PATH_RED);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, RedModelData> tepMap = new HashMap<>();
                    Map<Short, Map<Byte, RedModelData>> tepLv = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "equipment");
                    for (int i = 0; i < elements.length; i++) {
                        RedModelData data = new RedModelData();
                        data.setId(Short.valueOf(XmlUtils.getAttribute(elements[i], "id")));
                        data.setLv(Short.valueOf(XmlUtils.getAttribute(elements[i], "level")));
                        data.setCost(Integer.valueOf(XmlUtils.getAttribute(elements[i], "cost")));
                        data.setType(Byte.valueOf(XmlUtils.getAttribute(elements[i], "position")));
                        data.setAttr(EAttrType.getAttr(elements[i], "effct"));
                        data.setAddAttr(EAttrType.getAttr(elements[i], "addEffect"));
                        data.setName(XmlUtils.getAttribute(elements[i], "name"));
                        tepMap.put(data.getId(), data);
                        if (!tepLv.containsKey(data.getLv()))
                            tepLv.put(data.getLv(), new HashMap<Byte, RedModelData>());
                        tepLv.get(data.getLv()).put(data.getType(), data);
                    }
                    redMap = tepMap;
                    redLvMap = tepLv;
                } catch (Exception e) {
                    logger.error("加载红装数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return NAME_RED;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadXunbao(String path) {
        final File file = new File(path, PATH_XUNBAO);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "logic");
                    for (int i = 0; i < elements.length; i++) {
                        xunbaoData.setId(Short.valueOf(XmlUtils.getAttribute(elements[i], "id")));
                        xunbaoData.setOneKeyCount(Integer.valueOf(XmlUtils.getAttribute(elements[i], "oneKeyCount")));
                        xunbaoData.setCost(StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "cost")));
                        xunbaoData.setCostOneKey(StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "costOneKey")));
                        xunbaoData.setReward(StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "reward")));
                    }
                } catch (Exception e) {
                    logger.error("加载寻宝数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return NAME_XUNBAO;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static RedModelData getFitRed(short lv, byte type) {
        while (lv > GameDefine.REIN_LV) {
            Map<Byte, RedModelData> typeMap = redLvMap.get(lv);
            if (typeMap == null) {
                lv -= 10;
                continue;
            } else {
                return typeMap.get(type);
            }
        }
        return null;
    }

    public static Map<Byte, RedModelData> getRedMap(short lv) {
        return redLvMap.get(lv);
    }

    public static RedModelData getRed(short id) {
        return redMap.get(id);
    }

    public static OrangeModelData getOrange(int lv) {
        return lvMap.get((short) lv);
    }

    public static XunBaoModelData getXunbaoData() {
        return xunbaoData;
    }

}
