package com.rd.model;

import com.rd.define.EAttrType;
import com.rd.model.data.TitleModelData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TitleModel {

    private static final Logger logger = Logger.getLogger(TitleModel.class);

    //称号数据
    private static final String TT_PATH = "gamedata/chenghao.xml";
    private static final String TT_NAME = "titleModel";
    private static Map<Short, TitleModelData> titleMap;
    private static Map<Short, TitleModelData> costMap;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadTitle(path);
    }

    public static void loadTitle(String path) {
        final File file = new File(path, TT_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, TitleModelData> tempMap = new HashMap<>();
                    Map<Short, TitleModelData> tempCost = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    //加载奖励
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuling");
                    for (int i = 0; i < elements.length; i++) {
                        Element elem = elements[i];
                        TitleModelData data = new TitleModelData();
                        data.setId(Short.valueOf(XmlUtils.getAttribute(elem, "id")));
                        data.setTime(Integer.valueOf(XmlUtils.getAttribute(elem, "time")));
                        data.setAttr(EAttrType.getAttr(elem));
                        String cost = XmlUtils.getAttribute(elem, "cost");
                        String[] costArr = cost.split(",");
                        if (costArr.length == 1) {
                            short type = Short.valueOf(costArr[0]);
                            data.setCost((short) -type);
                        } else if (costArr.length == 3) {
                            data.setCost(Short.valueOf(costArr[1]));
                        }
                        tempMap.put(data.getId(), data);
                        tempCost.put(data.getCost(), data);
                    }
                    titleMap = tempMap;
                    costMap = tempCost;
                } catch (Exception e) {
                    logger.error("加载称号数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return TT_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static TitleModelData getTitle(short id) {
        return titleMap.get(id);
    }

    public static TitleModelData getCost(short id) {
        return costMap.get(id);
    }

}
