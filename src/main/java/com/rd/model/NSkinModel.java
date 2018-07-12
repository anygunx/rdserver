package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.enumeration.EAttr;
import com.rd.model.data.skin.NSkinData;
import com.rd.model.data.skin.NTaoZhuangData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.StringUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 获取皮肤数据
 *
 * @author MyPC
 */
public class NSkinModel {
    private static Logger logger = Logger.getLogger(NSkinModel.class);
    /**
     * 皮肤
     */
    private static Map<Integer, NSkinData> strMap;

    /**
     * 套装
     */
    private static Map<String, NTaoZhuangData> tZMap;

    /**
     * 存储皮肤表中的id 所对应的是哪个套装类型
     */
    private static Map<Integer, Set<Byte>> idTypeMap;

    public static void loadModelData(String path) {

        loadSkinModelData(path);
        loadTaoZhuangData(path);
    }

    /**
     * @param path
     */
    public static void loadSkinModelData(String path) {
        final File file = new File(path, "gamedata/skin.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NSkinData> temp = new HashMap<>();

                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        int clas = value.getInt("class");

                        DropData cost = GameCommon.parseDropData(value.getString("cost"));
                        int[] attr = EAttr.getIntAttr(value);
                        NSkinData data = new NSkinData(id, cost, (byte) clas, attr);
                        temp.put(data.getId(), data);
                    }
                    strMap = temp;
                } catch (IOException e) {
                    logger.error("加皮肤数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "skinModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }

    /**
     * @param path
     */
    public static void loadTaoZhuangData(String path) {
        final File file = new File(path, "gamedata/skinsuit.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<String, NTaoZhuangData> temp = new HashMap<>();
                Map<Integer, Set<Byte>> tempIdTypeMap = new HashMap<>();
                ;

                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
//	    					int id=Integer.parseInt(key);
                        int type = value.getInt("type");
                        int level = value.getInt("level");
                        String activation = value.getString("activation");
                        int[] attr = EAttr.getIntAttr(value);
                        NTaoZhuangData data = new NTaoZhuangData((short) level, StringUtil.getIntList(activation, ","), attr);

                        temp.put(level + "_" + type, data);
                        List<Integer> list = data.getActivationList();
                        if (list != null && !list.isEmpty()) {
                            for (Integer pfId : list) {
                                Set<Byte> tempSet = tempIdTypeMap.get(pfId);
                                if (tempIdTypeMap.get(pfId) == null) {
                                    tempSet = new HashSet<Byte>();
                                    tempIdTypeMap.put(pfId, tempSet);
                                }
                                tempSet.add((byte) type);
                            }
                        }


                    }
                    tZMap = temp;
                    idTypeMap = tempIdTypeMap;
                } catch (IOException e) {
                    logger.error("加套装数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "taoZhuangModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);


    }

    /**
     * 通过 皮肤类型 和 皮肤品质 获取
     *
     * @return
     */
    public static NSkinData getPiFuByType(int id) {
        return strMap.get(id);

    }

    /**
     * 通过套装的的类型已经等级获取
     *
     * @return
     */
    public static NTaoZhuangData getTaoZhuangData(int type, int leve) {

        return tZMap.get(leve + "_" + type);

    }

    /**
     * 通过皮肤id 对应获取套装的类型
     *
     * @return
     */
    public static Set<Byte> getTZTypeBySkinId(int skinId) {
        return idTypeMap.get(skinId);
    }


}
