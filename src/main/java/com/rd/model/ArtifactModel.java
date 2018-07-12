package com.rd.model;

import com.rd.define.EAttrType;
import com.rd.model.data.ArtifactBossModelData;
import com.rd.model.data.ArtifactPiecesModelData;
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
 * 关卡神器
 * Created by XingYun on 2017/11/28.
 */
public class ArtifactModel {

    private static Logger logger = Logger.getLogger(ArtifactModel.class);

    private static final String ARTIFACT_PATH = "gamedata/artifact.xml";
    private static final String ARTIFACT_NAME = "artifactModel";
    /**
     * 关卡神器模板数据
     **/
    private static Map<Byte, ArtifactBossModelData> artifactMap;

    private static final String ARTIFACT_PIECES_PATH = "gamedata/artifactpieces.xml";
    private static final String ARTIFACT_PIECES_NAME = "artifactpiecesModel";
    private static Map<Short, ArtifactPiecesModelData> piecesMap;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadArtifact(path);
        loadPieces(path);
    }

    private static void loadArtifact(String path) {
        final File file = new File(path, ARTIFACT_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, ArtifactBossModelData> tmpArtifactMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "artifact");

                    for (Element element : elements) {
                        byte id = Byte.parseByte(XmlUtils.getAttribute(element, "id"));
                        int[] attr = EAttrType.getIntAttr(element);
                        Map<Short, Byte> pieces = parseFrom(XmlUtils.getAttribute(element, "from"));
                        int fighting = Integer.parseInt(XmlUtils.getAttribute(element, "power"));
                        ArtifactBossModelData data = new ArtifactBossModelData(id, attr, pieces, fighting);
                        tmpArtifactMap.put(id, data);
                    }
                    artifactMap = tmpArtifactMap;
                } catch (Exception e) {
                    logger.error("加载关卡神器数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return ARTIFACT_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static Map<Short, Byte> parseFrom(String from) {
        Map<Short, Byte> map = new HashMap<>();
        String[] array = StringUtil.getStringList(from, "#");
        for (String ele : array) {
            try {
                String[] idValue = StringUtil.getStringList(ele, ",");
                map.put(Short.valueOf(idValue[0]), Byte.valueOf(idValue[1]));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return map;
    }

    public static void loadPieces(String path) {
        final File file = new File(path, ARTIFACT_PIECES_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, ArtifactPiecesModelData> tmpArtifactMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "artifact");
                    for (Element element : elements) {
                        short id = Short.parseShort(XmlUtils.getAttribute(element, "id"));
                        short[] attr = EAttrType.getShortAttr(element);
                        ArtifactPiecesModelData data = new ArtifactPiecesModelData(id, attr);
                        tmpArtifactMap.put(id, data);
                    }
                    piecesMap = tmpArtifactMap;
                } catch (Exception e) {
                    logger.error("加载关卡神器碎片数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return ARTIFACT_PIECES_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static ArtifactPiecesModelData getPieceData(short id) {
        ArtifactPiecesModelData modelData = piecesMap.get(id);
        return modelData;
    }

    public static ArtifactBossModelData getArtifactData(byte id) {
        ArtifactBossModelData modelData = artifactMap.get(id);
        return modelData;
    }


    // 固定起点不可变更
    public static byte getFirst() {
        return 1;
    }
}
