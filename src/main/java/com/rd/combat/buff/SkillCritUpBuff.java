package com.rd.combat.buff;

import com.rd.combat.CombatEffect;
import com.rd.define.CombatDef;
import com.rd.model.data.BuffData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年7月3日下午1:41:48
 */
public class SkillCritUpBuff extends Buff {

    public SkillCritUpBuff(BuffData data) {
        super(data);
    }

    protected void launchEffect(CombatEffect effect) {
        effect.addSkillCritUp(data.getValue() / CombatDef.TEN_THOUSAND);
    }
}
