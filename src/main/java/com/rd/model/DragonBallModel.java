package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.define.EAttrType;
import com.rd.model.data.DragonBallModelData;
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
 * 龙珠模板
 * Created by XingYun on 2017/10/30.
 */
public class DragonBallModel {

    static Logger log = Logger.getLogger(DragonBallModel.class.getName());

    /**
     * 月卡加成比
     **/
    public static float MOTH_CARD_ADDITION = 0.2f;
    /**
     * 月卡加成上限
     **/
    public static int MOTH_CARD_MAX = 8000;
    private static Map<Short, DragonBallModelData> dataMap;

    private DragonBallModel() {

    }

    public static void loadData(String path) {
        loadDragonBall(path);
    }

    private static void loadDragonBall(String path) {
        final File file = new File(path, "gamedata/dragonball.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, DragonBallModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
//                    Element element = XmlUtils.getChildByName(root, "monthcard");
//                    MOTH_CARD_ADDITION = Float.parseFloat(XmlUtils.getAttribute(element, "addition"));
//                    MOTH_CARD_MAX = Integer.parseInt(XmlUtils.getAttribute(element, "max"));
                    Element[] elements = XmlUtils.getChildrenByName(root, "data");
                    for (int i = 0; i < elements.length; i++) {
                        short level = Short.parseShort(XmlUtils.getAttribute(elements[i], "level"));
                        short[] attr = EAttrType.getShortAttr(elements[i]);
                        DropData consume = StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "consume"));
                        DragonBallModelData modelData = new DragonBallModelData(level, consume, attr);
                        tmpMap.put(level, modelData);
                    }
                    dataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载龙珠数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "dragonballModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static DragonBallModelData getData(short level) {
        DragonBallModelData data = dataMap.get(level);
        return data;
    }
}
