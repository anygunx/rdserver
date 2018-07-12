package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.define.EAttrType;
import com.rd.model.data.ShenBingModelData;
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
 * 炼体
 * Created by XingYun on 2017/11/30.
 */
public class LianTiModel {

    private static Logger logger = Logger.getLogger(ArtifactModel.class);

    private static final int SHEN_BING_ID_OFFSET = 1000;

    private static final String SHEN_BING_PATH = "gamedata/magic.xml";
    private static final String SHEN_BING_NAME = "magicModel";
    /**
     * 关卡神器模板数据
     **/
    private static Map<Short, ShenBingModelData> shenBingMap;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadShenBing(path);
    }

    private static void loadShenBing(String path) {
        final File file = new File(path, SHEN_BING_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, ShenBingModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "magic");

                    for (Element element : elements) {
                        short id = Short.parseShort(XmlUtils.getAttribute(element, "id"));
                        byte type = Byte.parseByte(XmlUtils.getAttribute(element, "type"));
                        byte stage = Byte.parseByte(XmlUtils.getAttribute(element, "ordelv"));
                        short star = Short.parseShort(XmlUtils.getAttribute(element, "starlv"));
                        DropData consume = GameCommon.parseDropData(XmlUtils.getAttribute(element, "lstar"));
                        int exp = Integer.parseInt(XmlUtils.getAttribute(element, "exp"));
                        float bonus = Float.parseFloat(XmlUtils.getAttribute(element, "bonus"));
                        int[] attr = EAttrType.getIntAttr(element);
                        ShenBingModelData data = new ShenBingModelData(id, type, stage, star, consume, exp, bonus, attr);
                        tmpMap.put(id, data);
                    }
                    shenBingMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载关卡神器数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return SHEN_BING_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static byte getShenBingType(int id) {
        return (byte) (id / SHEN_BING_ID_OFFSET);
    }

    public static ShenBingModelData getShenBing(short id) {
        ShenBingModelData modelData = shenBingMap.get(id);
        return modelData;
    }

    public static short getFirstShenBingId(byte type) {
        return (short) (type * SHEN_BING_ID_OFFSET);
    }
}
