package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.model.data.taskadvanced.NTaskAdvancedData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/****
 *
 *
 * 培养成就系统
 */
public class NTaskAdvancedModel {
    private static Logger logger = Logger.getLogger(NTaskAdvancedModel.class);
    private static Map<String, NTaskAdvancedData> taskAdvanceMap;
    private static Map<Integer, NTaskAdvancedData> taskAdvacedIdMap;

    public static void loadModelData(String path) {
        loadTaskAdvancedModelData(path);

    }

    /**
     * @param path
     */
    public static void loadTaskAdvancedModelData(String path) {
        final File file = new File(path, "gamedata/TaskAdvanced.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<String, NTaskAdvancedData> tempUpGradeIdMap = new HashMap<>();
                Map<Integer, NTaskAdvancedData> tempIdMap = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        int param = value.getInt("param");
                        int type = value.getInt("type");
                        List<DropData> rewards = GameCommon.parseDropDataList(value.getString("reward"));
                        NTaskAdvancedData data = new NTaskAdvancedData(id, type, param, rewards);
                        tempUpGradeIdMap.put(getKey(type, id), data);
                        tempIdMap.put(id, data);
                    }

                    taskAdvanceMap = tempUpGradeIdMap;
                    taskAdvacedIdMap = tempIdMap;
                } catch (IOException e) {
                    logger.error("加培养成就数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "taskAdvanceModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static String getKey(int type, int id) {
        return type + "_" + id;
    }

    public static Map<String, NTaskAdvancedData> getNTaskAdvancedDataMap() {
        return taskAdvanceMap;
    }

    public static NTaskAdvancedData getNTaskAdvancedDataMap(int type, int id) {
        return taskAdvanceMap.get(getKey(type, id));
    }

    public static NTaskAdvancedData getNTaskAdvancedDataIdMap(int id) {
        return taskAdvacedIdMap.get(id);
    }


}
