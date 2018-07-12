package com.rd.model;

import com.rd.define.EAttrType;
import com.rd.model.data.ShenQiModelData;
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
 * 神器数据
 *
 * @author Created by U-Demon on 2016年11月2日 上午11:23:34
 * @version 1.0.0
 */
public class ShenQiModel {

    private static Logger logger = Logger.getLogger(ShenQiModel.class);

    //神器数据
    private static final String SHENQI_PATH = "gamedata/shenqi.xml";
    private static final String SHENQI_NAME = "shenQiModel";
    private static Map<Integer, Map<Integer, ShenQiModelData>> shenQiMap;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadShenQi(path);
    }

    public static void loadShenQi(String path) {
        final File file = new File(path, SHENQI_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Integer, Map<Integer, ShenQiModelData>> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "shenqi");
                    for (int i = 0; i < elements.length; i++) {
                        ShenQiModelData data = new ShenQiModelData();
                        data.setId(Integer.valueOf(XmlUtils.getAttribute(elements[i], "id")));
                        data.setLv(Integer.valueOf(XmlUtils.getAttribute(elements[i], "lv")));
                        data.setName(XmlUtils.getAttribute(elements[i], "name"));
                        data.setCost(StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "cost")));
                        data.setPinzhi(Integer.valueOf(XmlUtils.getAttribute(elements[i], "pinzhi")));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        if (tmpMap.get(data.getId()) == null)
                            tmpMap.put(data.getId(), new HashMap<Integer, ShenQiModelData>());
                        tmpMap.get(data.getId()).put(data.getLv(), data);
                    }
                    shenQiMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载神器数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return SHENQI_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static ShenQiModelData getData(int id, int lv) {
        Map<Integer, ShenQiModelData> map = shenQiMap.get(id);
        if (map == null)
            return null;
        return map.get(lv);
    }

}
