package com.rd.model;

import com.rd.define.EVipType;
import com.rd.model.data.MonthlyCardModelData;
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
 * 月卡数据
 *
 * @author Created by U-Demon on 2016年12月19日 下午2:51:19
 * @version 1.0.0
 */
public class MonthlyCardModel {

    private static Logger logger = Logger.getLogger(MonthlyCardModel.class);

    //押镖数据
    private static final String PATH = "gamedata/yueka.xml";
    private static final String NAME = "yuekaModel";
    private static Map<Byte, MonthlyCardModelData> cardMap;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadMonthlyCard(path);
    }

    public static void loadMonthlyCard(String path) {
        final File file = new File(path, PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, MonthlyCardModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "yueka");
                    for (int i = 0; i < elements.length; i++) {
                        MonthlyCardModelData data = new MonthlyCardModelData();
                        data.setId(Byte.valueOf(XmlUtils.getAttribute(elements[i], "id")));
                        data.setReward(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "reward")));
                        data.setKeepDay(Integer.valueOf(XmlUtils.getAttribute(elements[i], "keepday")));
                        data.setAddUp(Byte.valueOf(XmlUtils.getAttribute(elements[i], "diejia")));
                        data.setTitle(XmlUtils.getAttribute(elements[i], "title"));
                        data.setContent(XmlUtils.getAttribute(elements[i], "content"));
                        String tqStr = XmlUtils.getAttribute(elements[i], "tequan");
                        if (tqStr != null) {
                            String[] weals = tqStr.split("#");
                            for (String weal : weals) {
                                String[] wealDatas = weal.split(",");
                                if (wealDatas.length == 2) {
                                    EVipType type = EVipType.getType(Integer.valueOf(wealDatas[0]));
                                    if (type != null) {
                                        data.getTequan().put(type, Integer.valueOf(wealDatas[1]));
                                    }
                                }
                            }
                        }
                        tmpMap.put(data.getId(), data);
                    }
                    cardMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载月卡数据出错...", e);
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

    public static MonthlyCardModelData getModel(int id) {
        return cardMap.get((byte) id);
    }

}
