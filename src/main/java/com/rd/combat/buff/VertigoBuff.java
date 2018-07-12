package com.rd.combat.buff;

import com.rd.combat.CombatEffect;
import com.rd.define.SkillDef;
import com.rd.model.data.BuffData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年7月3日上午11:07:15
 */
public class VertigoBuff extends Buff {

    public VertigoBuff(BuffData data) {
        super(data);
    }

    protected void launchEffect(CombatEffect effect) {
        effect.setState(SkillDef.VERTIGO);
    }
}
