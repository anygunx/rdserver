package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.common.ParseCommon;
import com.rd.enumeration.EAttr;
import com.rd.enumeration.EGrow;
import com.rd.model.data.*;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.*;

/**
 * @author ---
 * @version 1.0
 * @date 2018年5月3日下午8:05:12
 */
public class GrowModel {

    private static Logger log = Logger.getLogger(GrowModel.class);

    private static Set<String> growLevelUpPath = new HashSet<String>();

    private static Set<String> growAptitudePath = new HashSet<String>();

    private static Set<String> growUpPath = new HashSet<String>();

    public static void loadData(String path) {
        //宠物
        loadSeedPet(path, "pet", EGrow.PET);
        //宠物升级
        loadSeedLevelUp(path, "petlevelup_", EGrow.PET);
        //宠物资质
        loadAptitude(path, "zizhiup_", EGrow.PET);
        //宠物飞升
        loadFlyUp(path, "petsg", EGrow.PET);

        //宠物属性
        loadSeedUpAttr(path, "petup_", EGrow.PET);

        //宠物通灵升级
        loadPsychicLevelUp(path, "petring", EGrow.PET);
        //宠物通灵技能
        loadPsychicSkill(path, "petringskill", EGrow.PET);
        //宠物通灵属性丹
        loadPsychicPill(path, "petringpro", EGrow.PET);
        //宠物通灵装备
        loadPsychicEquip(path, "petringequip", EGrow.PET);

        //宠物兽魂升级
        loadSoulLevelUp(path, "petsoul", EGrow.PET);
        //宠物兽魂技能
        loadSoulSkill(path, "petsoulskill", EGrow.PET);
        //宠物兽魂属性丹
        loadSoulPill(path, "petsoulpro", EGrow.PET);
        //宠物兽魂装备
        loadSoulEquip(path, "petsoulequip", EGrow.PET);

        //宠物被动技能
        loadSkillPassive(path, "petpassive", EGrow.PET);
        //宠物洗炼
        loadWashing(path, "petstar", EGrow.PET);

        //仙侣
        loadSeedMate(path, "xianlv", EGrow.MATE);
        //仙侣升级
        loadSeedLevelUp(path, "xianlvlevelup_", EGrow.MATE);

        //仙侣升星
        loadStarUp(path, "xianlvskill", EGrow.MATE);

        //仙侣仙位升级
        loadPsychicLevelUp(path, "xianpos", EGrow.MATE);
        //仙侣仙位技能
        loadPsychicSkill(path, "xianposskill", EGrow.MATE);
        //仙侣仙位属性丹
        loadPsychicPill(path, "xianpospro", EGrow.MATE);
        //仙侣仙位装备
        loadPsychicEquip(path, "xianposequip", EGrow.MATE);

        //仙侣法阵升级
        loadSoulLevelUp(path, "xianzhen", EGrow.MATE);
        //仙侣法阵技能
        loadSoulSkill(path, "xianzhenskill", EGrow.MATE);
        //仙侣法阵属性丹
        loadSoulPill(path, "xianzhenpro", EGrow.MATE);
        //仙侣法阵装备
        loadSoulEquip(path, "xianzhenequip", EGrow.MATE);

        //仙侣属性
        loadSeedUpAttr(path, "xianlvup_", EGrow.MATE);

        //天女

        //天女天女升级
        loadPsychicLevelUp(path, "tiannv", EGrow.FAIRY);
        //天女天女技能
        loadPsychicSkill(path, "tiannvskill", EGrow.FAIRY);
        //天女天女属性丹
        loadPsychicPill(path, "tiannvpro", EGrow.FAIRY);
        //天女天女装备
        loadPsychicEquip(path, "tiannvequip", EGrow.FAIRY);

        //天女仙器升级
        loadSoulLevelUp(path, "xianqi", EGrow.FAIRY);
        //天女仙器技能
        loadSoulSkill(path, "tiannvskill", EGrow.FAIRY);
        //天女仙器属性丹
        loadSoulPill(path, "xianqipro", EGrow.FAIRY);
        //天女仙器装备
        loadSoulEquip(path, "xianqiequip", EGrow.FAIRY);

        //天女花辇升级
        loadLevelUp3(path, "huanian", EGrow.FAIRY);
        //天女花辇技能
        loadSkill3(path, "tiannvskill", EGrow.FAIRY);
        //天女花辇属性丹
        loadPill3(path, "huanianpro", EGrow.FAIRY);
        //天女花辇装备
        loadEquip3(path, "huanianequip", EGrow.FAIRY);

        //天女灵气升级
        loadLevelUp4(path, "lingqi", EGrow.FAIRY);
        //天女灵气技能
        loadSkill4(path, "tiannvskill", EGrow.FAIRY);
        //天女灵气属性丹
        loadPill4(path, "lingqipro", EGrow.FAIRY);
        //天女灵气装备
        loadEquip4(path, "lingqiequip", EGrow.FAIRY);

        //法宝

        //天仙法宝升级
        loadPsychicLevelUp(path, "fabao", EGrow.GODDESS);
        //天仙法宝技能
        loadPsychicSkill(path, "fabaoskill", EGrow.GODDESS);
        //天仙法宝属性丹
        loadPsychicPill(path, "fabaopro", EGrow.GODDESS);
        //天仙法宝装备
        loadPsychicEquip(path, "fabaoequip", EGrow.GODDESS);

        //天仙神兵升级
        loadSoulLevelUp(path, "shenbing", EGrow.GODDESS);
        //天仙神兵技能
        loadSoulSkill(path, "shenbingskill", EGrow.GODDESS);
        //天仙神兵属性丹
        loadSoulPill(path, "shenbingpro", EGrow.GODDESS);
        //天仙神兵装备
        loadSoulEquip(path, "shenbingequip", EGrow.GODDESS);

        //角色

        //角色翅膀升级
        loadPsychicLevelUp(path, "wing", EGrow.ROLE);
        //角色翅膀技能
        loadPsychicSkill(path, "wingskill", EGrow.ROLE);
        //角色翅膀属性丹
        loadPsychicPill(path, "wingpro", EGrow.ROLE);
        //角色翅膀装备
        loadPsychicEquip(path, "wingequip", EGrow.ROLE);

        //角色坐骑升级
        loadSoulLevelUp(path, "zuoqi", EGrow.ROLE);
        //角色坐骑技能
        loadSoulSkill(path, "zuoqiskill", EGrow.ROLE);
        //角色坐骑属性丹
        loadSoulPill(path, "zuoqipro", EGrow.ROLE);
        //角色坐骑装备
        loadSoulEquip(path, "zuoqiequip", EGrow.ROLE);
    }

    private static void loadSeedPet(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, GrowSeedData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        short id = Short.parseShort(key);
                        byte quality = value.getNumber("quality").byteValue();
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        byte levelUp = value.getNumber("petlevelup").byteValue();
                        byte up = value.getNumber("petup").byteValue();
                        byte aptitude = value.getNumber("zizhiup").byteValue();
                        byte activeSkill = value.getNumber("zdskill").byteValue();
                        byte atkNum = value.getNumber("atk_num").byteValue();
                        short[] passiveSkill = ParseCommon.parseCommaShort(value.getString("bdskill"));

                        tmp.put(id, new GrowSeedData(id, quality, cost, levelUp, up, aptitude, activeSkill, atkNum, passiveSkill));

                        growLevelUpPath.add(quality + "_" + levelUp);
                        growAptitudePath.add(quality + "_" + aptitude);
                        growUpPath.add(quality + "_" + up);
                    }
                    grow.setGrowDataMap(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "数据出错...", e);
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

    private static void loadSeedLevelUp(String path, String name, EGrow grow) {
        Map<String, Map<Short, GrowSeedLevelUpData>> tmp = new HashMap<>();
        for (String k : growLevelUpPath) {
            final File file = new File(path, "gamedata/" + name + k + ".json");
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

                            short level = Short.parseShort(key);
                            DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                            DropData costLimit = GameCommon.parseDropData(value.getString("cost_item1"));
                            DropData costGold = GameCommon.parseDropData(value.getString("cost_gold"));
                            byte exp = value.getNumber("every_exp").byteValue();
                            short expMax = value.getNumber("need_exp").shortValue();
                            int[] attr = EAttr.getIntAttr(value);

                            GrowSeedLevelUpData data = new GrowSeedLevelUpData(level, cost, costLimit, costGold, exp, expMax, attr);

                            if (!tmp.containsKey(k)) {
                                tmp.put(k, new HashMap<>());
                            }
                            tmp.get(k).put(data.getLevel(), data);
                        }
                    } catch (Exception e) {
                        log.error("加载" + name + k + "升级数据出错...", e);
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
        growLevelUpPath.clear();

        grow.setLevelUpDataMap(tmp);
    }

    private static void loadSoulPill(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Byte, GrowCostData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        byte level = value.getNumber("lv").byteValue();
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        int[] attr = EAttr.getIntAttr(value);
                        GrowCostData data = new GrowCostData(level, cost, attr);
                        tmp.put(level, data);
                    }

                    grow.setSoulPillDataMap(tmp);
                } catch (Exception e) {
                    log.error("加载兽魂属性丹数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "petsoulpro";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadSoulLevelUp(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, GrowSeedLevelUpData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        short level = Short.parseShort(key);
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        DropData costLimit = GameCommon.parseDropData(value.getString("cost_item1"));
                        DropData costGold = GameCommon.parseDropData(value.getString("cost_gold"));
                        byte exp = value.getNumber("every_exp").byteValue();
                        short expMax = value.getNumber("need_exp").shortValue();
                        int[] attr = EAttr.getIntAttr(value);
                        GrowSeedLevelUpData data = new GrowSeedLevelUpData(level, cost, costLimit, costGold, exp, expMax, attr);
                        tmp.put(data.getLevel(), data);
                    }
                    grow.setSoulLevelUpDataMap(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "升级数据出错...", e);
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

    private static void loadSoulSkill(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<String, GrowSkillData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        byte level = value.getNumber("lv").byteValue();
                        byte pos = value.getNumber("pos").byteValue();
                        byte needLevel = value.getNumber("need_lv").byteValue();
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        int[] attr = EAttr.getIntAttr(value);

                        GrowSkillData data = new GrowSkillData(level, pos, needLevel, cost, attr);
                        tmp.put(level + "_" + pos, data);
                    }
                    grow.setSoulSkillDataMap(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "技能数据出错...", e);
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

    private static void loadSoulEquip(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, GrowEquipData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        short id = Short.parseShort(key);
                        byte pos = value.getNumber("pos").byteValue();
                        byte needLevel = value.getNumber("lv").byteValue();
                        byte quality = value.getNumber("quality").byteValue();
                        int[] attr = EAttr.getIntAttr(value);

                        GrowEquipData data = new GrowEquipData(id, pos, needLevel, quality, attr);
                        tmp.put(id, data);
                    }

                    grow.setSoulEquipDataMap(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "兽魂装备数据出错...", e);
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

    private static void loadPsychicPill(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Byte, GrowCostData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        byte level = value.getNumber("lv").byteValue();
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        int[] attr = EAttr.getIntAttr(value);
                        GrowCostData data = new GrowCostData(level, cost, attr);
                        tmp.put(level, data);
                    }

                    grow.setPsychicPillDataMap(tmp);
                } catch (Exception e) {
                    log.error("加载兽魂属性丹数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "petsoulpro";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadPsychicLevelUp(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, GrowSeedLevelUpData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        short level = Short.parseShort(key);
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        DropData costLimit = GameCommon.parseDropData(value.getString("cost_item1"));
                        DropData costGold = GameCommon.parseDropData(value.getString("cost_gold"));
                        byte exp = value.getNumber("every_exp").byteValue();
                        short expMax = value.getNumber("need_exp").shortValue();
                        int[] attr = EAttr.getIntAttr(value);

                        GrowSeedLevelUpData data = new GrowSeedLevelUpData(level, cost, costLimit, costGold, exp, expMax, attr);
                        tmp.put(data.getLevel(), data);
                    }
                    grow.setPsychicLevelUpDataMap(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "升级数据出错...", e);
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

    private static void loadPsychicSkill(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<String, GrowSkillData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        byte level = value.getNumber("lv").byteValue();
                        byte pos = value.getNumber("pos").byteValue();
                        byte needLevel = value.getNumber("need_lv").byteValue();
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        int[] attr = EAttr.getIntAttr(value);

                        GrowSkillData data = new GrowSkillData(level, pos, needLevel, cost, attr);
                        tmp.put(level + "_" + pos, data);
                    }
                    grow.setPsychicSkillDataMap(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "技能数据出错...", e);
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

    private static void loadPsychicEquip(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, GrowEquipData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        short id = Short.parseShort(key);
                        byte pos = value.getNumber("pos").byteValue();
                        byte needLevel = value.getNumber("lv").byteValue();
                        byte quality = value.getNumber("quality").byteValue();
                        int[] attr = EAttr.getIntAttr(value);

                        GrowEquipData data = new GrowEquipData(id, pos, needLevel, quality, attr);
                        tmp.put(id, data);
                    }

                    grow.setPsychicEquipDataMap(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "通灵装备数据出错...", e);
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

    private static void loadFlyUp(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, GrowSeedLevelUpData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        short level = Short.parseShort(key);
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        DropData costGold = GameCommon.parseDropData(value.getString("cost_gold"));
                        byte exp = value.getNumber("every_exp").byteValue();
                        short expMax = value.getNumber("need_exp").shortValue();
                        int[] attr = EAttr.getIntAttr(value);

                        GrowSeedLevelUpData data = new GrowSeedLevelUpData(level, cost, null, costGold, exp, expMax, attr);
                        tmp.put(data.getLevel(), data);
                    }
                    grow.setFlyUpDataMap(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "飞升数据出错...", e);
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

    private static void loadAptitude(String path, String name, EGrow grow) {
        Map<String, Map<Short, GrowSeedLevelUpData>> tmp = new HashMap<>();
        for (String k : growAptitudePath) {
            final File file = new File(path, "gamedata/" + name + k + ".json");
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

                            short level = Short.parseShort(key);
                            DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                            int[] attr = EAttr.getIntAttr(value);

                            GrowSeedLevelUpData data = new GrowSeedLevelUpData(level, cost, null, null, (byte) 0, (short) 0, attr);

                            if (!tmp.containsKey(k)) {
                                tmp.put(k, new HashMap<>());
                            }
                            tmp.get(k).put(data.getLevel(), data);
                        }
                    } catch (Exception e) {
                        log.error("加载" + name + "资质数据出错...", e);
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
        growAptitudePath.clear();

        EGrow.PET.setAptitudeDataMap(tmp);
    }

    private static void loadSkillPassive(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, SkillPassiveData> tmp = new HashMap<>();
                Map<Byte, List<SkillPassiveData>> tmp2 = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        short id = Short.parseShort(key);
                        byte level = value.getNumber("lv").byteValue();
                        byte quality = value.getNumber("quality").byteValue();
                        byte largeType = value.getNumber("skill_type").byteValue();
                        byte smallType = value.getNumber("property_type").byteValue();
                        int[] attr = EAttr.getIntAttr(value);
                        short buff = value.getNumber("buff").shortValue();

                        SkillPassiveData data = new SkillPassiveData(id, level, quality, largeType, smallType, attr, buff);

                        tmp.put(data.getId(), data);

                        if (!tmp2.containsKey(level)) {
                            tmp2.put(level, new ArrayList<>());
                        }
                        tmp2.get(level).add(data);
                    }

                    EGrow.PET.setSkillPassiveMap(tmp);
                    EGrow.PET.setSkillPassiveLevelMap(tmp2);
                } catch (Exception e) {
                    log.error("加载" + name + "被动技能数据出错...", e);
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

    private static void loadWashing(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Byte, WashingData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONArray jsonArray = new JSONArray(content);
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        byte level = object.getNumber("level").byteValue();
                        short[] rate = new short[7];
                        byte[] num = new byte[7];
                        for (int j = 1; j < 8; ++j) {
                            rate[(j - 1)] = object.getNumber("passive" + j).shortValue();
                            num[(j - 1)] = object.getNumber("passive" + j + "_num").byteValue();
                        }
                        WashingData data = new WashingData(level, rate, num);

                        tmp.put(data.getLevel(), data);
                    }

                    EGrow.PET.setWashingMap(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "洗炼数据出错...", e);
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

    private static void loadSeedMate(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, GrowSeedData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        short id = Short.parseShort(key);
                        byte quality = value.getNumber("quality").byteValue();
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        byte levelUp = value.getNumber("levelup").byteValue();
                        byte up = value.getNumber("xianlvup").byteValue();
                        byte aptitude = 0;//value.getNumber("zizhiup").byteValue();
                        byte activeSkill = value.getNumber("zdskill").byteValue();
                        byte atkNum = value.getNumber("atk_num").byteValue();
                        short[] passiveSkill = null;//GameCommon.parseByteArray(value.getString("bdskill"));

                        tmp.put(id, new GrowSeedData(id, quality, cost, levelUp, up, aptitude, activeSkill, atkNum, passiveSkill));

                        growLevelUpPath.add(quality + "_" + levelUp);
                        growUpPath.add(quality + "_" + up);
                    }
                    grow.setGrowDataMap(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "数据出错...", e);
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

    private static void loadLevelUp3(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, GrowSeedLevelUpData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        short level = Short.parseShort(key);
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        DropData costLimit = GameCommon.parseDropData(value.getString("cost_item1"));
                        DropData costGold = GameCommon.parseDropData(value.getString("cost_gold"));
                        byte exp = value.getNumber("every_exp").byteValue();
                        short expMax = value.getNumber("need_exp").shortValue();
                        int[] attr = EAttr.getIntAttr(value);

                        GrowSeedLevelUpData data = new GrowSeedLevelUpData(level, cost, costLimit, costGold, exp, expMax, attr);
                        tmp.put(data.getLevel(), data);
                    }
                    grow.setLevelUp3DataMap(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "升级数据出错...", e);
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

    private static void loadSkill3(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<String, GrowSkillData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        byte level = value.getNumber("lv").byteValue();
                        byte pos = value.getNumber("pos").byteValue();
                        byte needLevel = value.getNumber("need_lv").byteValue();
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        int[] attr = EAttr.getIntAttr(value);

                        GrowSkillData data = new GrowSkillData(level, pos, needLevel, cost, attr);
                        tmp.put(level + "_" + pos, data);
                    }
                    grow.setSkillData3Map(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "技能数据出错...", e);
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

    private static void loadPill3(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Byte, GrowCostData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        byte level = value.getNumber("lv").byteValue();
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        int[] attr = EAttr.getIntAttr(value);
                        GrowCostData data = new GrowCostData(level, cost, attr);
                        tmp.put(level, data);
                    }

                    grow.setPillData3Map(tmp);
                } catch (Exception e) {
                    log.error("加载兽魂属性丹数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "petsoulpro";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadEquip3(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, GrowEquipData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        short id = Short.parseShort(key);
                        byte pos = value.getNumber("pos").byteValue();
                        byte needLevel = value.getNumber("lv").byteValue();
                        byte quality = value.getNumber("quality").byteValue();
                        int[] attr = EAttr.getIntAttr(value);

                        GrowEquipData data = new GrowEquipData(id, pos, needLevel, quality, attr);
                        tmp.put(id, data);
                    }

                    grow.setEquipData3Map(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "通灵装备数据出错...", e);
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

    private static void loadLevelUp4(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, GrowSeedLevelUpData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        short level = Short.parseShort(key);
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        DropData costLimit = GameCommon.parseDropData(value.getString("cost_item1"));
                        DropData costGold = GameCommon.parseDropData(value.getString("cost_gold"));
                        byte exp = value.getNumber("every_exp").byteValue();
                        short expMax = value.getNumber("need_exp").shortValue();
                        int[] attr = EAttr.getIntAttr(value);

                        GrowSeedLevelUpData data = new GrowSeedLevelUpData(level, cost, costLimit, costGold, exp, expMax, attr);
                        tmp.put(data.getLevel(), data);
                    }
                    grow.setLevelUp4DataMap(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "升级数据出错...", e);
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

    private static void loadSkill4(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<String, GrowSkillData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        byte level = value.getNumber("lv").byteValue();
                        byte pos = value.getNumber("pos").byteValue();
                        byte needLevel = value.getNumber("need_lv").byteValue();
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        int[] attr = EAttr.getIntAttr(value);

                        GrowSkillData data = new GrowSkillData(level, pos, needLevel, cost, attr);
                        tmp.put(level + "_" + pos, data);
                    }
                    grow.setSkillData4Map(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "技能数据出错...", e);
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

    private static void loadPill4(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Byte, GrowCostData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        byte level = value.getNumber("lv").byteValue();
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));
                        int[] attr = EAttr.getIntAttr(value);
                        GrowCostData data = new GrowCostData(level, cost, attr);
                        tmp.put(level, data);
                    }

                    grow.setPillData4Map(tmp);
                } catch (Exception e) {
                    log.error("加载兽魂属性丹数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "petsoulpro";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadEquip4(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, GrowEquipData> tmp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        short id = Short.parseShort(key);
                        byte pos = value.getNumber("pos").byteValue();
                        byte needLevel = value.getNumber("lv").byteValue();
                        byte quality = value.getNumber("quality").byteValue();
                        int[] attr = EAttr.getIntAttr(value);

                        GrowEquipData data = new GrowEquipData(id, pos, needLevel, quality, attr);
                        tmp.put(id, data);
                    }

                    grow.setEquipData4Map(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "通灵装备数据出错...", e);
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

    private static void loadStarUp(String path, String name, EGrow grow) {
        final File file = new File(path, "gamedata/" + name + ".json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                String content;
                try {
                    Map<Byte, StarUpSkillData> tmp = new HashMap<>();
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();

                        JSONObject value = jsonObject.getJSONObject(key);

                        byte id = Byte.parseByte(key);
                        short level = value.getNumber("lv").shortValue();
                        DropData cost = GameCommon.parseDropData(value.getString("cost_item"));

                        StarUpSkillData data = new StarUpSkillData(cost);

                        tmp.put(id, data);
                    }

                    grow.setStarUpMap(tmp);
                } catch (Exception e) {
                    log.error("加载" + name + "升星数据出错...", e);
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

    private static void loadSeedUpAttr(String path, String name, EGrow grow) {
        Map<String, Map<Short, GrowAttrData>> tmp = new HashMap<>();
        for (String k : growUpPath) {
            final File file = new File(path, "gamedata/" + name + k + ".json");
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

                            short level = Short.parseShort(key);
                            int[] attr = EAttr.getIntAttr(value);

                            GrowAttrData data = new GrowAttrData(attr);

                            if (!tmp.containsKey(k)) {
                                tmp.put(k, new HashMap<>());
                            }
                            tmp.get(k).put(level, data);
                        }
                    } catch (Exception e) {
                        log.error("加载" + name + k + "升级属性数据出错...", e);
                    }
                }

                @Override
                public String toString() {
                    return k;
                }
            };
            listener.onResourceChange(file);
            ResourceManager.getInstance().addResourceListener(listener);
        }
        growUpPath.clear();

        grow.setUpAttrMap(tmp);
    }
}
