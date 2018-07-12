package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.enumeration.EAttr;
import com.rd.game.event.EGameEventType;
import com.rd.model.data.task.NLiLianData;
import com.rd.model.data.task.NTaskLiLianData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NTaskModel {


    private static Logger logger = Logger.getLogger(NFaBaoModel.class);
    private static Map<Integer, NTaskLiLianData> TaskLiLiLianMap;
    private static Map<Integer, NLiLianData> liLianMap;

    public static void loadModelData(String path) {
        loadTaskLiLianModelData(path);
        loadLiLianModelData(path);

    }

    /**
     * @param path
     */
    public static void loadTaskLiLianModelData(String path) {
        final File file = new File(path, "gamedata/task_lilian.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NTaskLiLianData> temp = new HashMap<>();
                Set<Byte> posTemp = new HashSet<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        int eventtype = value.getInt("eventtype");
                        int target = value.getInt("target");
                        int lilianExp = value.getInt("lilian_exp");
                        //DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        NTaskLiLianData data = new NTaskLiLianData(id, eventtype, target, lilianExp);
                        temp.put(id, data);
                        EGameEventType eventType = EGameEventType.getEventType(eventtype);
                        eventType.setDailyMission((byte) data.getId());
                    }
                    TaskLiLiLianMap = temp;

                } catch (IOException e) {
                    logger.error("加任务数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "tasklilianModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }


    /**
     * @param path
     */
    public static void loadLiLianModelData(String path) {
        final File file = new File(path, "gamedata/lilian.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NLiLianData> temp = new HashMap<>();

                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        int needExp = value.getInt("need_exp");

                        List<DropData> rewards = GameCommon.parseDropDataList(value.getString("reward"));
                        int[] attr = EAttr.getIntAttr(value);

                        NLiLianData data = new NLiLianData(id, needExp, rewards, attr);
                        temp.put(id, data);
                    }
                    liLianMap = temp;
                } catch (IOException e) {
                    logger.error("加任务历练数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "lilianModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }

    public static NTaskLiLianData getTaskLiLianDailyData(int id) {
        return TaskLiLiLianMap.get(id);
    }

    public static Map<Integer, NTaskLiLianData> getTaskLiLianDailyDataMap() {
        return TaskLiLiLianMap;
    }

    public static NLiLianData getNLiLianData(int id) {
        return liLianMap.get(id);
    }


    public static Map<Integer, NLiLianData> getNLiLianDataMap() {
        return liLianMap;
    }
}
