package com.rd.model;

import com.rd.define.EAttrType;
import com.rd.define.EGoodsQuality;
import com.rd.model.data.SpiritModelData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 元魂相关数据
 *
 * @author Created by U-Demon on 2016年11月10日 下午5:28:17
 * @version 1.0.0
 */
public class SpiritModel {

    private static Logger logger = Logger.getLogger(SpiritModel.class);

    //元神模型数据
    private static final String YUANHUN_PATH = "gamedata/yuanshen.xml";
    private static final String YUANHUN_NAME = "yuanshenModel";
    private static Map<Short, SpiritModelData> spiritMap;
    private static Map<String, SpiritModelData> spiritTQL;
    private static List<Short> oranges;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadSpirit(path);
    }

    public static void loadSpirit(String path) {
        final File file = new File(path, YUANHUN_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, SpiritModelData> tmpMap = new HashMap<>();
                    List<Short> tmpList = new ArrayList<>();
                    Map<String, SpiritModelData> tmpTQL = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "shenqi");
                    for (Element ele : elements) {
                        SpiritModelData data = new SpiritModelData();
                        data.setId(Short.valueOf(XmlUtils.getAttribute(ele, "id")));
                        data.setLv(Short.valueOf(XmlUtils.getAttribute(ele, "lv")));
                        data.setCost(StringUtil.getRewardDropData(XmlUtils.getAttribute(ele, "cost")));
                        data.setType(Byte.valueOf(XmlUtils.getAttribute(ele, "type")));
                        data.setFenjie(StringUtil.getRewardDropData(XmlUtils.getAttribute(ele, "fenjie")));
                        data.setPinzhi(Byte.valueOf(XmlUtils.getAttribute(ele, "pinzhi")));
                        data.setAttr(EAttrType.getAttr(ele));
                        tmpMap.put(data.getId(), data);
                        tmpTQL.put(data.getTQL(), data);
                        if (data.getPinzhi() == EGoodsQuality.ORANGE.getValue())
                            tmpList.add(data.getId());
                    }
                    spiritMap = tmpMap;
                    oranges = tmpList;
                    spiritTQL = tmpTQL;
                } catch (Exception e) {
                    logger.error("加载元神模型数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return YUANHUN_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static SpiritModelData getSpirit(short id) {
        return spiritMap.get(id);
    }

    public static List<Short> getOranges() {
        return oranges;
    }

    public static SpiritModelData getSpiritTQL(byte t, byte q, int l) {
        SpiritModelData data = new SpiritModelData();
        data.setType(t);
        data.setPinzhi(q);
        data.setLv((short) l);
        return spiritTQL.get(data.getTQL());
    }

}
