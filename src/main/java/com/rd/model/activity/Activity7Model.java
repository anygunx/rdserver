package com.rd.model.activity;

import com.rd.model.data.DaBiaoData;
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
 * 七日狂欢
 *
 * @author Created by U-Demon on 2016年12月5日 下午2:51:41
 * @version 1.0.0
 */
public class Activity7Model {

    private static Logger logger = Logger.getLogger(Activity7Model.class);

    //活动的总限时天数
    public static final int ACTIVE_DAY = 7;

    //达标活动奖励数据
    private static final String PATH_DABIAO = "gamedata/open_meiridabiao.xml";
    private static final String NAME_DABIAO = "open_meiridabiao";
    private static Map<Byte, DaBiaoData> dabiaoMap;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadDaBiao(path);
    }

    private static void loadDaBiao(String path) {
        final File file = new File(path, PATH_DABIAO);
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
                    Map<Byte, DaBiaoData> tmpMap = new HashMap<>();
                    Element[] elements = XmlUtils.getChildrenByName(root, "logic");
                    for (int i = 0; i < elements.length; i++) {
                        DaBiaoData data = new DaBiaoData();
                        Integer id = Integer.valueOf(XmlUtils.getAttribute(elements[i], "round"));
                        data.setId(id.byteValue());
                        data.setFirstReward(StringUtil.getRewardDropList(
                                XmlUtils.getAttribute(elements[i], "firstReward")));
                        data.setFirstTitle(XmlUtils.getAttribute(elements[i], "firstTitle"));
                        data.setFirstContent(XmlUtils.getAttribute(elements[i], "firstContent"));
                        data.setSecondReward(StringUtil.getRewardDropList(
                                XmlUtils.getAttribute(elements[i], "secondReward")));
                        data.setSecondTitle(XmlUtils.getAttribute(elements[i], "secondTitle"));
                        data.setSecondContent(XmlUtils.getAttribute(elements[i], "secondContent"));
                        data.setThirdReward(StringUtil.getRewardDropList(
                                XmlUtils.getAttribute(elements[i], "thirdReward")));
                        data.setThirdTitle(XmlUtils.getAttribute(elements[i], "thirdTitle"));
                        data.setThirdContent(XmlUtils.getAttribute(elements[i], "thirdContent"));
                        data.setForthReward(StringUtil.getRewardDropList(
                                XmlUtils.getAttribute(elements[i], "forthReward")));
                        data.setForthTitle(XmlUtils.getAttribute(elements[i], "forthTitle"));
                        data.setForthContent(XmlUtils.getAttribute(elements[i], "forthContent"));
                        tmpMap.put(data.getId(), data);
                    }
                    dabiaoMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载达标活动奖励数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return NAME_DABIAO;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static DaBiaoData getDaBiaoReward(int id) {
        return dabiaoMap.get((byte) id);
    }

}
