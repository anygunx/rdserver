package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.define.EAttrType;
import com.rd.model.data.MeridianModelData;
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
 * 经脉数据
 *
 * @author Created by U-Demon on 2016年11月3日 下午7:43:20
 * @version 1.0.0
 */
public class MeridianModel {

    private static Logger logger = Logger.getLogger(MeridianModel.class);

    //经脉数据
    private static final String MERIDIAN_PATH = "gamedata/jingmai.xml";
    private static final String MERIDIAN_NAME = "meridianModel";
    private static Map<Integer, MeridianModelData> meridianMap;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadMeridian(path);
    }

    private static void loadMeridian(String path) {
        final File file = new File(path, MERIDIAN_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Integer, MeridianModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhuling");
                    for (Element ele : elements) {
                        int lv = Integer.valueOf(XmlUtils.getAttribute(ele, "id"));
                        DropData cost = StringUtil.getRewardDropData(XmlUtils.getAttribute(ele, "cost"));
                        int[] attr = EAttrType.getAttr(ele);
                        MeridianModelData data = new MeridianModelData(lv, cost, attr);
                        tmpMap.put(data.getLv(), data);
                    }
                    meridianMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载经脉数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return MERIDIAN_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static MeridianModelData getMeridianData(int lv) {
        return meridianMap.get(lv);
    }

}
