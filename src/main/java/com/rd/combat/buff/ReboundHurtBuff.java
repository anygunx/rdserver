package com.rd.combat.buff;

import com.rd.combat.CombatEffect;
import com.rd.model.data.BuffData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年7月2日下午9:24:22
 */
public class ReboundHurtBuff extends Buff {

    public ReboundHurtBuff(BuffData data) {
        super(data);
    }

    protected void launchEffect(CombatEffect effect) {
        effect.addHurtRate(data.getValue());
    }
}
