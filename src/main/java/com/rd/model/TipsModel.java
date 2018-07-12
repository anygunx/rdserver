package com.rd.model;

import com.rd.bean.chat.PeriodBroadcast;
import com.rd.common.ChatService;
import com.rd.model.data.TipsModelData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.DateUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 游戏上方滚动的公告
 *
 * @author Created by U-Demon on 2016年12月14日 下午6:03:40
 * @version 1.0.0
 */
public class TipsModel {

    private static Logger logger = Logger.getLogger(TipsModel.class);

    //TIPS数据
    private static final String PATH = "gamedata/tips.xml";
    private static final String NAME = "tipsModel";
    private static Map<Integer, TipsModelData> tipsMap;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadTips(path);
    }

    public static void loadTips(String path) {
        final File file = new File(path, PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Integer, TipsModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "tips");
                    for (int i = 0; i < elements.length; i++) {
                        TipsModelData data = new TipsModelData();
                        data.setId(Integer.valueOf(XmlUtils.getAttribute(elements[i], "id")));
//                    	data.setDesc(XmlUtils.getAttribute(elements[i], "desc"));
                        data.setStartTime(Integer.valueOf(XmlUtils.getAttribute(elements[i], "startTime")));
                        data.setSpaceTime(Integer.valueOf(XmlUtils.getAttribute(elements[i], "jiangeTime")));
                        data.setCount(Integer.valueOf(XmlUtils.getAttribute(elements[i], "cishu")));
                        tmpMap.put(data.getId(), data);
                    }
                    tipsMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载TIPS数据出错...", e);
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

    /**
     * 广播TIPS
     */
    public static void broadcastTips() {
        long curr = System.currentTimeMillis();
        for (TipsModelData data : tipsMap.values()) {
            long startTime = curr + data.getStartTime() * DateUtil.SECOND;
            long endTime = startTime + data.getCount() * data.getSpaceTime() * DateUtil.SECOND;
            PeriodBroadcast broadcast = PeriodBroadcast.build("[GameTips#" + data.getId() + "]",
                    startTime, endTime, data.getSpaceTime() * DateUtil.SECOND);
            ChatService.broadcastSystemMsg(broadcast);
        }
    }

    public static Map<Integer, TipsModelData> getTipsMap() {
        return tipsMap;
    }

}
