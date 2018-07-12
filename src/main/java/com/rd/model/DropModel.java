package com.rd.model;

import com.rd.bean.drop.DropGroupData;
import com.rd.bean.drop.DropParamsData;
import com.rd.common.goods.EGoodsType;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DropModel {

    static Logger log = Logger.getLogger(DropModel.class.getName());

    private static Map<Short, DropGroupData> dropGroupDataMap;

    private DropModel() {

    }

    public static void loadDrop(String path) {
        final File file = new File(path, "gamedata/gain.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, DropGroupData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "gain");
                    for (int i = 0; i < elements.length; i++) {
                        Map<Byte, DropParamsData> params = new HashMap<>();
                        for (EGoodsType goodsType : EGoodsType.values()) {
                            DropParamsData paramData = getParamsData(elements[i], goodsType.getId(), goodsType.getDropName());
                            if (paramData != null) {
                                params.put(goodsType.getId(), paramData);
                            }
                        }

                        DropGroupData data = new DropGroupData();
                        data.setId(Short.parseShort(XmlUtils.getAttribute(elements[i], "id")));
                        data.setDropType(Byte.parseByte(XmlUtils.getAttribute(elements[i], "dropType")));
                        data.setParamMap(Collections.unmodifiableMap(params));
                        tmpMap.put(data.getId(), data);
                    }
                    dropGroupDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载掉落模型数据出错...");
                    e.printStackTrace();
                }
            }

            private DropParamsData getParamsData(Element element, byte type, String name) {
                String numStr = XmlUtils.getAttribute(element, name + "dropNum");
                byte dropNum = numStr.isEmpty() ? 0 : Byte.valueOf(numStr);
                String dataStr = XmlUtils.getAttribute(element, name);
                String percentStr = XmlUtils.getAttribute(element, name + "_percent");
                if (dataStr.isEmpty() || dataStr.equals("0") || percentStr.equals("0") || dropNum == 0) {
                    return null;
                }
                return DropParamsData.createWithStringParams(type, dropNum, percentStr, dataStr);
            }

            @Override
            public String toString() {
                return "dropModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static DropGroupData getDropGroupData(short id) {
        return dropGroupDataMap.get(id);
    }
}
