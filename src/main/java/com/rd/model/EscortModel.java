package com.rd.model;

import com.rd.model.data.EscortModelData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.GameUtil;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 渡劫数据
 *
 * @author Created by U-Demon on 2016年11月30日 下午5:22:49
 * @version 1.0.0
 */
public class EscortModel {

    private static Logger logger = Logger.getLogger(EscortModel.class);

    //渡劫数据
    private static final String ESCORT_PATH = "gamedata/dujie.xml";
    private static final String ESCORT_NAME = "dujieModel";
    private static Map<Byte, EscortModelData> escortMap;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadEscort(path);
    }

    public static void loadEscort(String path) {
        final File file = new File(path, ESCORT_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, EscortModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "duhun");
                    for (int i = 0; i < elements.length; i++) {
                        EscortModelData data = new EscortModelData();
                        data.setId(Byte.valueOf(XmlUtils.getAttribute(elements[i], "id")));
                        data.setKeeptime(Integer.valueOf(XmlUtils.getAttribute(elements[i], "keeptime")));
                        data.setRate(Integer.valueOf(XmlUtils.getAttribute(elements[i], "gailv")));
                        data.setReward(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "jiangli")));
                        tmpMap.put(data.getId(), data);
                    }
                    escortMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载渡劫数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return ESCORT_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static EscortModelData getEscortModel(byte quality) {
        return escortMap.get(quality);
    }

    public static int getRandomModel() {
        try {
            int ran = GameUtil.getRangedRandom(1, 100);
            int total = 0;
            for (EscortModelData model : escortMap.values()) {
                total += model.getRate();
                if (ran <= total) {
                    return model.getId();
                }
            }
        } catch (Exception e) {
            logger.error("随机渡劫品质数据发生异常...", e);
        }
        return 1;
    }

}
