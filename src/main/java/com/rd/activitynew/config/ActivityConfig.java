package com.rd.activitynew.config;

import com.rd.activitynew.config.data.ActivityCumulatePayData;
import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动配置
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月3日下午3:12:06
 */
public class ActivityConfig {

    private final static Logger logger = Logger.getLogger(ActivityConfig.class.getName());

    private static Map<Byte, Map<Byte, ActivityCumulatePayData>> cumulatePayConfigMap;

    public static void init(String path) {
        loadCumulatePayData(path);
    }

    private static void loadCumulatePayData(String path) {
        final File file = new File(path, "gamedata/dingshichongzhi.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, Map<Byte, ActivityCumulatePayData>> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "config");

                    for (Element element : elements) {
                        byte type = Byte.parseByte(XmlUtils.getAttribute(element, "type"));

                        tmpMap.put(type, new HashMap<>());
                        Element[] logics = XmlUtils.getChildrenByName(root, "logic");
                        for (Element logic : logics) {
                            byte id = Byte.parseByte(XmlUtils.getAttribute(logic, "id"));
                            List<DropData> list = GameCommon.parseDropDataList(XmlUtils.getAttribute(logic, "reward"));
                            int cost = Integer.parseInt(XmlUtils.getAttribute(logic, "cost"));
                            String title = XmlUtils.getAttribute(logic, "title");
                            String content = XmlUtils.getAttribute(logic, "content");

                            tmpMap.get(type).put(id, new ActivityCumulatePayData(id, list, cost, title, content));
                        }
                    }
                    cumulatePayConfigMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载累计充值数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "dingshichongzhi";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static Map<Byte, ActivityCumulatePayData> getActivityCumulatePayConfig(byte type) {
        return cumulatePayConfigMap.get(type);
    }
}
