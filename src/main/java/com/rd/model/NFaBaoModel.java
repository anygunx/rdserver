package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.enumeration.EAttr;
import com.rd.model.data.fabao.NDanYaoData;
import com.rd.model.data.fabao.NPulseData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 法宝系统
 *
 * @author MyPC
 */
public class NFaBaoModel {
    private static Logger logger = Logger.getLogger(NFaBaoModel.class);
    private static Map<Short, NPulseData> nPulseModelMap;
    private static Map<String, NDanYaoData> nDanYaoModelMap;
    private static Set<Byte> danYaoPos;

    public static void loadModelData(String path) {
        loadPulseModelData(path);
        loadDanYaoModelData(path);

    }

    /**
     * 经脉
     *
     * @param path
     */
    public static void loadPulseModelData(String path) {
        final File file = new File(path, "gamedata/pulse.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, NPulseData> temp = new HashMap<>();

                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        short id = Short.parseShort(key);
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        int[] attr = EAttr.getIntAttr(value);
                        NPulseData data = new NPulseData(id, cost, attr);
                        temp.put(id, data);
                    }
                    nPulseModelMap = temp;

                } catch (IOException e) {
                    logger.error("加经脉数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "pulseModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }


    /**
     * 丹药
     *
     * @param path
     */
    public static void loadDanYaoModelData(String path) {
        final File file = new File(path, "gamedata/danyao.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<String, NDanYaoData> temp = new HashMap<>();
                Set<Byte> posTemp = new HashSet<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);

                        int pos = value.getInt("pos");
                        int level = value.getInt("level");
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        int[] attr = EAttr.getIntAttr(value);
                        byte temppos = (byte) pos;
                        NDanYaoData data = new NDanYaoData(temppos, cost, (byte) level, attr);
                        temp.put(pos + "_" + level, data);
                        posTemp.add(temppos);
                    }
                    nDanYaoModelMap = temp;
                    danYaoPos = posTemp;
                } catch (IOException e) {
                    logger.error("加丹药数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "danyaoModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }

    public static NPulseData getNPulseData(short id) {
        return nPulseModelMap.get(id);
    }

    /**
     * @return
     */
    public static NDanYaoData getNDanYaoData(byte pos, byte grade) {
        return nDanYaoModelMap.get(pos + "_" + grade);
    }

    /**
     * 是否存在丹药位置
     *
     * @param pos
     * @return
     */
    public static boolean isNDanYaoData(byte pos) {
        return danYaoPos.contains(pos);
    }

    public static int getNDanYaoSizeData() {
        return danYaoPos.size();
    }


}
