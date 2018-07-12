package com.rd.combat.buff;

import com.rd.combat.CombatEffect;
import com.rd.define.CombatDef;
import com.rd.model.data.BuffData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年7月3日下午1:48:17
 */
public class SkillHurtUpBuff extends Buff {

    public SkillHurtUpBuff(BuffData data) {
        super(data);
    }

    protected void launchEffect(CombatEffect effect) {
        effect.addSkillHurtUp(data.getValue() / CombatDef.TEN_THOUSAND);
    }
}
