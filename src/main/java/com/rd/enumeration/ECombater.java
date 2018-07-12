package com.rd.enumeration;

import com.rd.model.data.SkillActiveData;

import java.util.Map;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月1日下午3:20:32
 */
public enum ECombater {

    PET,
    MATE,
    FAIRY,
    HERO,;

    private Map<Short, SkillActiveData> skillActiveMap;

    ECombater() {

    }

    public Map<Short, SkillActiveData> getSkillActiveMap() {
        return skillActiveMap;
    }

    public void setSkillActiveMap(Map<Short, SkillActiveData> skillActiveMap) {
        this.skillActiveMap = skillActiveMap;
    }

    public SkillActiveData getSkillActive(int id) {
        return skillActiveMap.get((short) id);
    }
}
