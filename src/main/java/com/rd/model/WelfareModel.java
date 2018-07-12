package com.rd.model;

import com.rd.model.data.WelfareModelData;
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
 * 每日福利数据
 *
 * @author Created by U-Demon on 2016年12月24日 下午3:03:46
 * @version 1.0.0
 */
public class WelfareModel {

    private static Logger logger = Logger.getLogger(WelfareModel.class);

    //每日福利数据
    private static final String PATH = "gamedata/meirifuli.xml";
    private static final String NAME = "meirifuliModel";
    private static Map<Byte, WelfareModelData> welfareMap;
    private static Map<Byte, List<Byte>> loop_idMap;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadWelfare(path);
    }

    public static void loadWelfare(String path) {
        final File file = new File(path, PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, WelfareModelData> tmpMap = new HashMap<>();
                    Map<Byte, List<Byte>> liTmp = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "baibeifanli");
                    for (int i = 0; i < elements.length; i++) {
                        WelfareModelData data = new WelfareModelData();
                        byte id = Byte.valueOf(XmlUtils.getAttribute(elements[i], "id"));
                        data.setId(id);
                        data.setPrice(Integer.valueOf(XmlUtils.getAttribute(elements[i], "price")));
                        data.setRewards(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "rewards")));
                        byte loop = Byte.valueOf(XmlUtils.getAttribute(elements[i], "loop"));
                        data.setLoop(loop);
                        data.setTitle(XmlUtils.getAttribute(elements[i], "title"));
                        data.setContent(XmlUtils.getAttribute(elements[i], "content"));
                        tmpMap.put(data.getId(), data);
                        if (!liTmp.containsKey(loop))
                            liTmp.put(loop, new ArrayList<Byte>());
                        liTmp.get(loop).add(id);
                    }
                    welfareMap = tmpMap;
                    loop_idMap = liTmp;
                } catch (Exception e) {
                    logger.error("加载每日福利数据出错...", e);
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

    public static WelfareModelData getModel(byte id) {
        return welfareMap.get(id);
    }

    public static List<Byte> getIdsByLoop(int loop) {
        return loop_idMap.get((byte) loop);
    }

    public static byte getIdByLoop(int loop, byte id) {
        WelfareModelData idModel = welfareMap.get(id);
        if (idModel == null)
            return -1;
        List<Byte> ids = loop_idMap.get((byte) loop);
        for (byte i : ids) {
            WelfareModelData model = welfareMap.get(i);
            if (model != null && model.getPrice() == idModel.getPrice()) {
                return i;
            }
        }
        return -1;
    }

    public static int getLoopSize() {
        return loop_idMap.size();
    }

}
