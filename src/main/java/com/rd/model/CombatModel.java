package com.rd.model;

import com.rd.enumeration.EAttr;
import com.rd.model.data.HeroLevelData;
import com.rd.model.data.HeroSkillData;
import com.rd.model.data.MonsterData;
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

/**
 * @author ---
 * @version 1.0
 * @date 2018年4月14日下午4:22:12
 */
public class CombatModel {

    private static Logger logger = Logger.getLogger(CombatModel.class);

    private static Map<Integer, MonsterData> monsterMap = new HashMap<>();

    private static Map<Short, HeroLevelData> heroLevelMap;

    private static Map<Short, HeroSkillData> heroSkillMap;

    private CombatModel() {

    }

    public static void loadData(String path) {
        loadMonster(path);
        loadHeroLevel(path);
        loadHeroSkill(path);
    }

    private static void loadMonster(String path) {
        int i = 1;
        while (new File(path, "gamedata/monster_" + i + ".json").exists()) {
            logger.info("加载怪物表" + i);
            final File file = new File(path, "gamedata/monster_" + i + ".json");
            ResourceListener listener = new ResourceListener() {

                @Override
                public File listenedFile() {
                    return file;
                }

                @Override
                public void onResourceChange(File file) {
                    String content;
                    try {
                        content = FileUtils.readFileToString(file, "UTF-8");

                        JSONObject jsonObject = new JSONObject(content);
                        Iterator<String> iterator = jsonObject.keys();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            JSONObject value = jsonObject.getJSONObject(key);

                            int id = Integer.parseInt(key);

                            byte atkNum = value.getNumber("atk_num").byteValue();
                            short skillId = value.getNumber("skill").shortValue();
                            int[] attr = EAttr.getIntAttr(value);
                            MonsterData data = new MonsterData(id, atkNum, skillId, attr);
                            monsterMap.put(data.getId(), data);
                        }
                    } catch (Exception e) {
                        logger.error("加载怪物" + file.getName() + "数据出错...", e);
                    }
                }

                @Override
                public String toString() {
                    return "monster";
                }
            };
            listener.onResourceChange(file);
            ResourceManager.getInstance().addResourceListener(listener);
            ++i;
        }
    }

    private static void loadHeroLevel(String path) {
        final File file = new File(path, "gamedata/levelup.json");
        ResourceListener listener = new ResourceListener() {

            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                String content;
                try {
                    Map<Short, HeroLevelData> temp = new HashMap<>();
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);

                        short id = Short.parseShort(key);
                        int[] attr = EAttr.getIntAttr(value);
                        HeroLevelData data = new HeroLevelData(id, attr);
                        temp.put(id, data);
                    }
                    heroLevelMap = temp;
                } catch (IOException e) {
                    logger.error("加载角色升级数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "levelup";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadHeroSkill(String path) {
        final File file = new File(path, "gamedata/skill.json");
        ResourceListener listener = new ResourceListener() {

            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                String content;
                try {
                    Map<Short, HeroSkillData> temp = new HashMap<>();
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);

                        short id = Short.parseShort(key);
                        short needLevel = value.getNumber("need_lv").shortValue();
                        byte atkNum = value.getNumber("atk_num").byteValue();
                        byte atkType = value.getNumber("atk").byteValue();
                        float hurtRate = value.getNumber("hurt_P").floatValue();
                        short hurtFix = value.getNumber("hurt_D").shortValue();

                        HeroSkillData data = new HeroSkillData(id, needLevel, atkNum, atkType, hurtRate, hurtFix);
                        temp.put(id, data);
                    }
                    heroSkillMap = temp;
                } catch (IOException e) {
                    logger.error("加载角色升级数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "skill";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static MonsterData getMonsterData(int id) {
        return monsterMap.get(id);
    }

    public static HeroLevelData getHeroLevelData(short lv) {
        return heroLevelMap.get(lv);
    }

    public static HeroSkillData getHeroSkillData(short id) {
        return heroSkillMap.get(id);
    }
}
