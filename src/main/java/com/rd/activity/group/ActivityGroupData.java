package com.rd.activity.group;

import com.google.common.collect.ImmutableMap;
import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.BaseActivityLogicData;
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
 * 活动组数据
 * 包含N轮数据 活动将在几轮数据间切换
 *
 * @author Created by U-Demon on 2016年11月3日 下午1:29:01
 * @version 1.0.0
 */
public class ActivityGroupData<T extends BaseActivityLogicData> {

    private static Logger logger = Logger.getLogger(ActivityGroupData.class);

    //活动ID
    private EActivityType activityId;

    /**
     * 活动每轮的逻辑数据
     */
    private Map<Integer, Map<String, T>> roundDatas;

    public ActivityGroupData(EActivityType activityId) {
        this.activityId = activityId;
    }

    /**
     * 根据时间获取轮次数据
     *
     * @param playerId
     * @param currTime
     * @return
     */
    public Map<String, T> getCurrRound(int playerId, long currTime) {
        BaseActivityConfig configData = ActivityService.getActivityConfig(activityId);
        ActivityRoundConfig currRound = configData.getCurrRound(playerId, currTime);
        if (currRound == null || currRound.isEnd(currTime)) {
            return null;
        }
        Map<String, T> round = getRound(currRound.getRound());
        return round;
    }

    /**
     * 获取某一轮次的数据
     *
     * @param round
     * @return
     */
    public Map<String, T> getRound(int round) {
        return roundDatas.get(getDataRound(round));
    }

    /**
     * 获取轮次对应的数据轮次
     *
     * @return
     */
    public int getDataRound(int round) {
        return round % roundDatas.size();
    }

    public void loadXml(String path, String xml) {
        final String xmlName = xml.substring(xml.indexOf("/") + 1, xml.indexOf("."));
        final File file = new File(path, xml);
        ResourceListener listener = new ResourceListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Integer, Map<String, T>> temp = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "logic");
                    for (Element ele : elements) {
                        BaseActivityLogicData logic = activityId.getClazz().newInstance();
                        //轮次
                        String round = XmlUtils.getAttribute(ele, "round");
                        if (activityId.getRound() != -1) {
                            if (round == null || round.length() == 0) {
                                continue;
                            }
                            int rd = Integer.parseInt(round);
                            if (activityId.getRound() != rd) {
                                continue;
                            }
                            logic.setRound(rd);
                        } else {
                            if (round == null || round.length() == 0)
                                logic.setRound(0);
                            else
                                logic.setRound(Integer.valueOf(round));
                        }
                        //逻辑数据
                        logic.loadData(ele);
                        if (temp.get(logic.getRound()) == null)
                            temp.put(logic.getRound(), new HashMap<>());
                        temp.get(logic.getRound()).put(logic.getKey(), (T) logic);
                    }
                    roundDatas = ImmutableMap.copyOf(temp);
                } catch (Exception e) {
                    logger.error("加载活动逻辑数据出错..." + xmlName, e);
                }
            }

            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public String toString() {
                return xmlName;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public EActivityType getActivityId() {
        return activityId;
    }

}
