package com.rd.model;

import com.lg.util.StringUtil;
import com.rd.model.data.skill.NSkillData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NskillModel {
    private static Logger logger = Logger.getLogger(NskillModel.class);
    /**
     * 按照玩家等级可以激活的技能列表
     */
    private static Map<Short, NSkillData> jiHuoMap = new HashMap<>();


    /**
     * 技能等级 key 是技能id，技能等级
     */
    private static Map<String, NSkillData> upGradeMap = new HashMap<>();

    /**
     * 技能等级
     */
    private static Map<Integer, NSkillData> idMap = new HashMap<>();


    public static void loadModelData(String path) {
        loadSkillModelData(path);
    }

    /**
     * @param path
     */
    public static void loadSkillModelData(String path) {
        final File file = new File(path, "gamedata/skill.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, NSkillData> tempjiHuoMap = new HashMap<>();
                Map<String, NSkillData> tempUpGradeMap = new HashMap<>();
                Map<Integer, NSkillData> tempUpGradeIdMap = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        int level = value.getInt("need_lv");
                        String cost_gold = value.getString("cost_gold");
                        int pos = value.getInt("pos");
                        int costType = 0;
                        int goodId = 0;
                        int num = 0;
                        if (cost_gold != null && cost_gold.trim().length() > 0) {
                            int[] arry = StringUtil.getIntList(cost_gold);
                            costType = arry[0];
                            goodId = arry[1];
                            num = arry[2];
                        }
                        //DropData cost = GameCommon.parseDropData(value.getString("cost"));
                        //int[] attr = EAttr.getIntAttr(value);
                        short lv = (short) level;
                        NSkillData data = new NSkillData((short) id, lv, (byte) pos, (byte) costType, goodId, num);
                        if (num == 0) {
                            tempjiHuoMap.put(lv, data);
                        } else {
                            tempUpGradeMap.put(pos + "_" + lv, data);
                        }
                        tempUpGradeIdMap.put(id, data);
                    }

                    jiHuoMap = tempjiHuoMap;
                    upGradeMap = tempUpGradeMap;
                    idMap = tempUpGradeIdMap;
                } catch (IOException e) {
                    logger.error("加技能数据出错...", e);
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
     * 获取可以激活的技能
     *
     * @param grade
     * @return
     */
    public static NSkillData getJiHuoSkillByGrade(short grade) {
        return jiHuoMap.get(grade);
    }

    /**
     * 获取升级技能
     *
     * @param grade
     * @return
     */
    public static NSkillData getUpGradeDataByGrade(int skillId, short grade) {
        return upGradeMap.get(skillId + "_" + grade);
    }

    /**
     * 获取升级技能
     *
     * @param grade
     * @return
     */
    public static NSkillData getUpGradeDataById(int skillId) {
        return idMap.get(skillId);
    }

}
