package com.rd.combat.buff;

import com.rd.combat.CombatEffect;
import com.rd.model.data.BuffData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年7月3日下午1:39:51
 */
public class AllHurtDownBuff extends Buff {

    public AllHurtDownBuff(BuffData data) {
        super(data);
    }

    protected void launchEffect(CombatEffect effect) {
        effect.addHurtRate(data.getValue());
    }
}
