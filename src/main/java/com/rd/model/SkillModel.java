package com.rd.model;

import com.rd.enumeration.ECombater;
import com.rd.model.data.BuffData;
import com.rd.model.data.SkillActiveData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月5日下午8:30:44
 */
public class SkillModel {

    private static Logger logger = Logger.getLogger(SkillModel.class);

    private static Map<Short, BuffData> buffMap;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        //主角主动技能
        loadHeroSkill(path, "skill", ECombater.HERO);
        //宠物主动技能
        loadActiveSkill(path, "petskill", ECombater.PET);
        //仙侣主动技能
        loadActiveSkill(path, "xianlvskill", ECombater.MATE);
        //天女主动技能
        loadActiveSkill(path, "tiannvskill", ECombater.FAIRY);

        loadBuff(path, "buff");
    }

    private static void loadHeroSkill(String path, String name, ECombater combater) {
        final File file = new File(path, "gamedata/" + name + ".json");
//		ResourceListener listener = new ResourceListener() {
//			@Override
//			public File listenedFile() {
//				return file;
//			}
//			@Override
//			public void onResourceChange(File file) {
        Map<Short, SkillActiveData> tmp = new HashMap<>();
        String content;
        try {
            content = FileUtils.readFileToString(file, "UTF-8");

            JSONObject jsonObject = new JSONObject(content);
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();

                JSONObject value = jsonObject.getJSONObject(key);

                short id = Short.parseShort(key);
                byte type = value.getNumber("type").byteValue();
                byte atkType = value.getNumber("atk").byteValue();
                double percentHurt = value.getNumber("hurt_P").intValue() / 10000;
                int fixedHurt = value.getNumber("hurt_D").intValue();
                short buff = value.getNumber("buff").shortValue();
                byte atkNum = value.getNumber("atk_num").byteValue();

                tmp.put(id, new SkillActiveData(id, type, atkType, percentHurt, fixedHurt, buff, atkNum));
            }

            combater.setSkillActiveMap(tmp);
        } catch (Exception e) {
            logger.error("加载" + name + "主动技能数据出错...", e);
        }
//			}
//			@Override
//			public String toString() {
//				return name;
//			}
//		};
//		listener.onResourceChange(file);
//		ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadActiveSkill(String path, String name, ECombater combater) {
        final File file = new File(path, "gamedata/" + name + ".json");
//		ResourceListener listener = new ResourceListener() {
//			@Override
//			public File listenedFile() {
//				return file;
//			}
//			@Override
//			public void onResourceChange(File file) {
        Map<Short, SkillActiveData> tmp = new HashMap<>();
        String content;
        try {
            content = FileUtils.readFileToString(file, "UTF-8");

            JSONObject jsonObject = new JSONObject(content);
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();

                JSONObject value = jsonObject.getJSONObject(key);

                short id = Short.parseShort(key);
                byte type = value.getNumber("type").byteValue();
                byte atkType = value.getNumber("atk").byteValue();
                double percentHurt = value.getNumber("hurt_P").intValue() / 10000;
                int fixedHurt = value.getNumber("hurt_D").intValue();
                short buff = value.getNumber("buff").shortValue();

                tmp.put(id, new SkillActiveData(id, type, atkType, percentHurt, fixedHurt, buff));
            }

            combater.setSkillActiveMap(tmp);
        } catch (Exception e) {
            logger.error("加载" + name + "主动技能数据出错...", e);
        }
//			}
//			@Override
//			public String toString() {
//				return name;
//			}
//		};
//		listener.onResourceChange(file);
//		ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadBuff(String path, String name) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, BuffData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject object = jsonObject.getJSONObject(key);

                        short id = Short.parseShort(key);
                        byte type = object.getNumber("type").byteValue();
                        short pr = object.getNumber("trigger").shortValue();
                        short value = object.getNumber("prop").shortValue();
                        byte cd = object.getNumber("cd").byteValue();
                        byte target = object.getNumber("target").byteValue();
                        byte trigger = object.getNumber("state").byteValue();
                        short skill = object.getNumber("skill_id").shortValue();

                        tmp.put(id, new BuffData(id, type, pr, value, cd, target, trigger, skill));
                    }

                    buffMap = tmp;
                } catch (Exception e) {
                    logger.error("加载" + name + "数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return name;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static Map<Short, BuffData> getBuffMap() {
        return buffMap;
    }

}
