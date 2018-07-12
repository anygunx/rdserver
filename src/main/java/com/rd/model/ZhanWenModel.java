package com.rd.model;

import com.rd.define.EAttrType;
import com.rd.model.data.ZhanWenModelData;
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
 * 战纹数据
 *
 * @author lwq
 */
public class ZhanWenModel {

    private static Logger logger = Logger.getLogger(ZhanWenModel.class);
    private static final String ZHANWEN_PATH = "gamedata/zhanwen.xml";
    private static final String ZHANWEN_NAME = "zhanWenModel";

    private static Map<Short, ZhanWenModelData> zhanWenMap;

    private static Map<String, ZhanWenModelData> zhanWenTQL;

    public static void loadData(String path) {
        loadZhanWen(path);
    }

    private static void loadZhanWen(String path) {

        final File file = new File(path + ZHANWEN_PATH);

        ResourceListener listener = new ResourceListener() {

            @Override
            public void onResourceChange(File file) {

                try {
                    Map<Short, ZhanWenModelData> tmpMap = new HashMap<>();
                    Map<String, ZhanWenModelData> tqlMap = new HashMap<>();

                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "zhanwen");
                    for (Element ele : elements) {
                        ZhanWenModelData data = new ZhanWenModelData();
                        data.setId(Short.valueOf(XmlUtils.getAttribute(ele, "id")));
                        data.setLv(Byte.valueOf(XmlUtils.getAttribute(ele, "level")));
                        data.setPinzhi(Byte.valueOf(XmlUtils.getAttribute(ele, "quality")));
                        data.setCost(StringUtil.getRewardDropData(XmlUtils.getAttribute(ele, "costid")));
                        data.setFenjie(StringUtil.getRewardDropData(XmlUtils.getAttribute(ele, "dis")));
                        data.setAttr(EAttrType.getAttr(ele));
                        data.setType(Byte.valueOf(XmlUtils.getAttribute(ele, "type")));
                        data.setPower(Integer.valueOf(XmlUtils.getAttribute(ele, "power")));
                        tmpMap.put(data.getId(), data);
                        tqlMap.put(data.getTQL(), data);
                    }
                    zhanWenMap = tmpMap;
                    zhanWenTQL = tqlMap;
                } catch (Exception e) {
                    logger.error("加载战纹模型数据出错...", e);
                }

            }

            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public String toString() {
                return ZHANWEN_NAME;
            }


        };

        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static Map<Short, ZhanWenModelData> getZhanWenMap() {
        return zhanWenMap;
    }

    public static ZhanWenModelData getZhanWenModelData(short id) {
        return zhanWenMap.get(id);
    }

    public static ZhanWenModelData getZhanWenByTQL(byte t, byte q, byte l) {
        ZhanWenModelData modelData = new ZhanWenModelData();
        modelData.setType(t);
        modelData.setPinzhi(q);
        modelData.setLv(l);
        return zhanWenTQL.get(modelData.getTQL());
    }

}
