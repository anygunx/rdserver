package com.rd.model;

import com.rd.define.EAttrType;
import com.rd.model.data.ReinModelData;
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
 * 转生数据
 *
 * @author U-Demon
 */
public class ReinModel {

    private static Logger logger = Logger.getLogger(ReinModel.class);

    private static final String PATH = "gamedata/zhuansheng.xml";
    private static final String NAME = "zhuanshengModel";

    private static Map<Short, ReinModelData> map;

    /**
     * 最高转
     **/
    private static byte reinMax = 0;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadRein(path);
    }

    public static void loadRein(String path) {
        final File file = new File(path, PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, ReinModelData> temp = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    //加载奖励
                    Element[] elements = XmlUtils.getChildrenByName(root, "skill");
                    for (int i = 0; i < elements.length; i++) {
                        Element elem = elements[i];
                        ReinModelData data = new ReinModelData();
                        data.setId(Short.valueOf(XmlUtils.getAttribute(elem, "id")));
                        data.setCost(StringUtil.getRewardDropData(XmlUtils.getAttribute(elem, "cost")));
                        data.setAttr(EAttrType.getAttr(elem, "effect"));
                        temp.put(data.getId(), data);

                        //获得最高转生
                        if (reinMax < data.getId()) {
                            reinMax = (byte) data.getId();
                        }
                    }
                    map = temp;
                } catch (Exception e) {
                    logger.error("加载转生数据出错...", e);
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

    public static ReinModelData getData(int id) {
        return map.get((short) id);
    }

    public static ReinModelData getData(short id) {
        return map.get(id);
    }

    /**
     * 获得最高转
     *
     * @return
     */
    public static byte getReinMax() {
        return reinMax;
    }
}
