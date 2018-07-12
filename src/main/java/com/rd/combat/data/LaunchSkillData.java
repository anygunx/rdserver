package com.rd.combat.data;

import com.rd.combat.buff.Buff;
import com.rd.model.data.SkillActiveData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年7月2日下午5:56:11
 */
public class LaunchSkillData {

    private SkillActiveData skillData;

    private Buff buff;

    public LaunchSkillData(SkillActiveData skillData, Buff buff) {
        this.skillData = skillData;
        this.buff = buff;
    }

    public SkillActiveData getSkillData() {
        return skillData;
    }

    public Buff getBuff() {
        return buff;
    }

}
