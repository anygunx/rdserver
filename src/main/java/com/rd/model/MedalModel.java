package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.goods.EGoodsType;
import com.rd.define.EAttrType;
import com.rd.model.data.MedalModelData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 勋章模板
 * Created by XingYun on 2017/10/31.
 */
public class MedalModel {

    static Logger log = Logger.getLogger(MedalModel.class.getName());

    private static Map<Byte, MedalModelData> dataMap;

    private MedalModel() {

    }

    public static void loadData(String path) {
        loadDragonBall(path);
    }

    private static void loadDragonBall(String path) {
        final File file = new File(path, "gamedata/medal.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, MedalModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "data");
                    for (int i = 0; i < elements.length; i++) {
                        byte level = Byte.parseByte(XmlUtils.getAttribute(elements[i], "level"));
                        int num = Integer.parseInt(XmlUtils.getAttribute(elements[i], "num"));
                        short[] attr = EAttrType.getShortAttr(elements[i]);
                        DropData consume = new DropData(EGoodsType.GOLD, 0, num);
                        short levelLimit = Short.parseShort(XmlUtils.getAttribute(elements[i], "levelLimit"));
                        int achievementLimit = Integer.parseInt(XmlUtils.getAttribute(elements[i], "achievementLimit"));
                        MedalModelData modelData = new MedalModelData(level, consume, attr, levelLimit, achievementLimit);
                        tmpMap.put(level, modelData);
                    }
                    dataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载勋章数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "medalModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static MedalModelData getData(byte level) {
        MedalModelData data = dataMap.get(level);
        return data;
    }
}
