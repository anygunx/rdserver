package com.rd.enumeration;

import com.rd.model.data.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ---
 * @version 1.0
 * @date 2018年5月7日下午7:21:01
 */
public enum EGrow {

    PET(0, EEquip.PET_PSYCHIC, EEquip.PET_SOUL, null, null),                        //宠物通灵  	    宠物兽魂
    MATE(1, EEquip.MATE_XW, EEquip.MATE_FZ, null, null),                            //仙侣仙位 	    仙侣法阵
    FAIRY(2, EEquip.FAIRY_TN, EEquip.FAIRY_XQ, EEquip.FAIRY_HN, EEquip.FAIRY_LQ),    //天女天女	    天女仙器	天女花辇	天女灵气
    GODDESS(3, EEquip.MAGIC, EEquip.WEAPON, null, null),                            //天仙法宝	    天仙神兵
    ROLE(4, EEquip.WING, EEquip.MOUNTS, null, null),                                //角色翅膀        角色坐骑
    ;

    private final static Map<Byte, EGrow> eGrowMap;

    static {
        eGrowMap = new HashMap<>();
        for (EGrow grow : EGrow.values()) {
            eGrowMap.put(grow.I(), grow);
        }
    }

    private byte i;

    private EEquip type1;

    private EEquip type2;

    private EEquip type3;

    private EEquip type4;

    private Map<Short, GrowSeedData> growDataMap;

    private Map<String, Map<Short, GrowSeedLevelUpData>> levelUpDataMap;

    private Map<Short, GrowSeedLevelUpData> soulLevelUpDataMap;

    private Map<String, GrowSkillData> soulSkillDataMap;

    private Map<Byte, GrowCostData> soulPillDataMap;

    private Map<Short, GrowEquipData> soulEquipDataMap;

    private Map<Short, GrowSeedLevelUpData> psychicLevelUpDataMap;

    private Map<String, GrowSkillData> psychicSkillDataMap;

    private Map<Byte, GrowCostData> psychicPillDataMap;

    private Map<Short, GrowEquipData> psychicEquipDataMap;

    private Map<Short, GrowSeedLevelUpData> levelUp3DataMap;

    private Map<String, GrowSkillData> skillData3Map;

    private Map<Byte, GrowCostData> pillData3Map;

    private Map<Short, GrowEquipData> equipData3Map;

    private Map<Short, GrowSeedLevelUpData> levelUp4DataMap;

    private Map<String, GrowSkillData> skillData4Map;

    private Map<Byte, GrowCostData> pillData4Map;

    private Map<Short, GrowEquipData> equipData4Map;

    private Map<Short, GrowSeedLevelUpData> flyUpDataMap;

    private Map<String, Map<Short, GrowSeedLevelUpData>> aptitudeDataMap;

    private Map<Short, SkillPassiveData> skillPassiveMap;

    private Map<Byte, List<SkillPassiveData>> skillPassiveLevelMap;

    private Map<Byte, WashingData> washingMap = new HashMap<>();

    private Map<Byte, StarUpSkillData> starUpMap = new HashMap<>();

    private Map<String, Map<Short, GrowAttrData>> upAttrMap;

    EGrow(int i, EEquip type1, EEquip type2, EEquip type3, EEquip type4) {
        this.i = (byte) i;
        this.type1 = type1;
        this.type2 = type2;
        this.type3 = type3;
        this.type4 = type4;
    }

    public static EGrow type(byte key) {
        return eGrowMap.get(key);
    }

    public byte I() {
        return i;
    }

    public EEquip getType1() {
        return type1;
    }

    public EEquip getType2() {
        return type2;
    }

    public EEquip getType3() {
        return type3;
    }

    public EEquip getType4() {
        return type4;
    }

    public Map<Short, GrowSeedData> getGrowDataMap() {
        return growDataMap;
    }

    public void setGrowDataMap(Map<Short, GrowSeedData> growDataMap) {
        this.growDataMap = growDataMap;
    }

    public Map<String, Map<Short, GrowSeedLevelUpData>> getLevelUpDataMap() {
        return levelUpDataMap;
    }

    public void setLevelUpDataMap(Map<String, Map<Short, GrowSeedLevelUpData>> levelUpDataMap) {
        this.levelUpDataMap = levelUpDataMap;
    }

    public Map<Short, GrowSeedLevelUpData> getSoulLevelUpDataMap() {
        return soulLevelUpDataMap;
    }

    public void setSoulLevelUpDataMap(Map<Short, GrowSeedLevelUpData> soulLevelUpDataMap) {
        this.soulLevelUpDataMap = soulLevelUpDataMap;
    }

    public Map<String, GrowSkillData> getSoulSkillDataMap() {
        return soulSkillDataMap;
    }

    public void setSoulSkillDataMap(Map<String, GrowSkillData> soulSkillDataMap) {
        this.soulSkillDataMap = soulSkillDataMap;
    }

    public Map<Byte, GrowCostData> getSoulPillDataMap() {
        return soulPillDataMap;
    }

    public Map<Short, GrowEquipData> getSoulEquipDataMap() {
        return soulEquipDataMap;
    }

    public void setSoulEquipDataMap(Map<Short, GrowEquipData> soulEquipDataMap) {
        this.soulEquipDataMap = soulEquipDataMap;
    }

    public void setSoulPillDataMap(Map<Byte, GrowCostData> soulPillDataMap) {
        this.soulPillDataMap = soulPillDataMap;
    }

    public Map<Short, GrowSeedLevelUpData> getPsychicLevelUpDataMap() {
        return psychicLevelUpDataMap;
    }

    public void setPsychicLevelUpDataMap(Map<Short, GrowSeedLevelUpData> psychicLevelUpDataMap) {
        this.psychicLevelUpDataMap = psychicLevelUpDataMap;
    }

    public Map<String, GrowSkillData> getPsychicSkillDataMap() {
        return psychicSkillDataMap;
    }

    public void setPsychicSkillDataMap(Map<String, GrowSkillData> psychicSkillDataMap) {
        this.psychicSkillDataMap = psychicSkillDataMap;
    }

    public Map<Byte, GrowCostData> getPsychicPillDataMap() {
        return psychicPillDataMap;
    }

    public void setPsychicPillDataMap(Map<Byte, GrowCostData> psychicPillDataMap) {
        this.psychicPillDataMap = psychicPillDataMap;
    }

    public Map<Short, GrowEquipData> getPsychicEquipDataMap() {
        return psychicEquipDataMap;
    }

    public void setPsychicEquipDataMap(Map<Short, GrowEquipData> psychicEquipDataMap) {
        this.psychicEquipDataMap = psychicEquipDataMap;
    }

    public Map<Short, GrowSeedLevelUpData> getFlyUpDataMap() {
        return flyUpDataMap;
    }

    public void setFlyUpDataMap(Map<Short, GrowSeedLevelUpData> flyUpDataMap) {
        this.flyUpDataMap = flyUpDataMap;
    }

    public Map<String, Map<Short, GrowSeedLevelUpData>> getAptitudeDataMap() {
        return aptitudeDataMap;
    }

    public void setAptitudeDataMap(Map<String, Map<Short, GrowSeedLevelUpData>> aptitudeDataMap) {
        this.aptitudeDataMap = aptitudeDataMap;
    }

    public Map<Short, SkillPassiveData> getSkillPassiveMap() {
        return skillPassiveMap;
    }

    public void setSkillPassiveMap(Map<Short, SkillPassiveData> skillPassiveMap) {
        this.skillPassiveMap = skillPassiveMap;
    }

    public Map<Byte, List<SkillPassiveData>> getSkillPassiveLevelMap() {
        return skillPassiveLevelMap;
    }

    public void setSkillPassiveLevelMap(Map<Byte, List<SkillPassiveData>> skillPassiveLevelMap) {
        this.skillPassiveLevelMap = skillPassiveLevelMap;
    }

    public Map<Byte, WashingData> getWashingMap() {
        return washingMap;
    }

    public void setWashingMap(Map<Byte, WashingData> washingMap) {
        this.washingMap = washingMap;
    }

    public Map<Short, GrowSeedLevelUpData> getLevelUp3DataMap() {
        return levelUp3DataMap;
    }

    public void setLevelUp3DataMap(Map<Short, GrowSeedLevelUpData> levelUp3DataMap) {
        this.levelUp3DataMap = levelUp3DataMap;
    }

    public Map<String, GrowSkillData> getSkillData3Map() {
        return skillData3Map;
    }

    public void setSkillData3Map(Map<String, GrowSkillData> skillData3Map) {
        this.skillData3Map = skillData3Map;
    }

    public Map<Byte, GrowCostData> getPillData3Map() {
        return pillData3Map;
    }

    public void setPillData3Map(Map<Byte, GrowCostData> pillData3Map) {
        this.pillData3Map = pillData3Map;
    }

    public Map<Short, GrowEquipData> getEquipData3Map() {
        return equipData3Map;
    }

    public void setEquipData3Map(Map<Short, GrowEquipData> equipData3Map) {
        this.equipData3Map = equipData3Map;
    }

    public Map<Short, GrowSeedLevelUpData> getLevelUp4DataMap() {
        return levelUp4DataMap;
    }

    public void setLevelUp4DataMap(Map<Short, GrowSeedLevelUpData> levelUp4DataMap) {
        this.levelUp4DataMap = levelUp4DataMap;
    }

    public Map<String, GrowSkillData> getSkillData4Map() {
        return skillData4Map;
    }

    public void setSkillData4Map(Map<String, GrowSkillData> skillData4Map) {
        this.skillData4Map = skillData4Map;
    }

    public Map<Byte, GrowCostData> getPillData4Map() {
        return pillData4Map;
    }

    public void setPillData4Map(Map<Byte, GrowCostData> pillData4Map) {
        this.pillData4Map = pillData4Map;
    }

    public Map<Short, GrowEquipData> getEquipData4Map() {
        return equipData4Map;
    }

    public void setEquipData4Map(Map<Short, GrowEquipData> equipData4Map) {
        this.equipData4Map = equipData4Map;
    }

    public Map<Byte, StarUpSkillData> getStarUpMap() {
        return starUpMap;
    }

    public void setStarUpMap(Map<Byte, StarUpSkillData> starUpMap) {
        this.starUpMap = starUpMap;
    }

    public void setUpAttrMap(Map<String, Map<Short, GrowAttrData>> upAttrMap) {
        this.upAttrMap = upAttrMap;
    }

    public Map<String, Map<Short, GrowAttrData>> getUpAttrMap() {
        return upAttrMap;
    }

}
