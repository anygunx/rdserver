package com.rd.model;

import com.rd.define.EAttrType;
import com.rd.model.data.GongFaModelData;
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

public class GongFaModel {

    private static Logger logger = Logger.getLogger(GongFaModel.class);

    //功法数据
    private static final String PATH = "gamedata/gongfa.xml";
    private static final String NAME = "gongfaModel";
    private static Map<Byte, Map<Short, GongFaModelData>> gongFaMap;
    private static Map<Short, GongFaModelData> map;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadGongFa(path);
    }

    public static void loadGongFa(String path) {
        final File file = new File(path, PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, Map<Short, GongFaModelData>> tmpMap = new HashMap<>();
                    Map<Short, GongFaModelData> dataMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "shenqi");
                    for (int i = 0; i < elements.length; i++) {
                        GongFaModelData data = new GongFaModelData();
                        data.setId(Short.valueOf(XmlUtils.getAttribute(elements[i], "id")));
                        data.setType(Byte.valueOf(XmlUtils.getAttribute(elements[i], "type")));
                        data.setLv(Short.valueOf(XmlUtils.getAttribute(elements[i], "lv")));
                        data.setRank(Short.valueOf(XmlUtils.getAttribute(elements[i], "jieduan")));
                        data.setCost(StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "cost")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        if (!tmpMap.containsKey(data.getType()))
                            tmpMap.put(data.getType(), new HashMap<Short, GongFaModelData>());
                        tmpMap.get(data.getType()).put(data.getLv(), data);
                        dataMap.put(data.getId(), data);
                    }
                    gongFaMap = tmpMap;
                    map = dataMap;
                } catch (Exception e) {
                    logger.error("加载功法数据出错...", e);
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

    public static GongFaModelData getGongFa(int type, short lv) {
        Map<Short, GongFaModelData> map = gongFaMap.get((byte) type);
        if (map == null)
            return null;
        return map.get(lv);
    }

    public static GongFaModelData getGongFa(short id) {
        return map.get(id);
    }

}
