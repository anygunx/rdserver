package com.rd.combat.buff;

import com.rd.combat.CombatEffect;
import com.rd.model.data.BuffData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年7月2日下午4:27:23
 */
public class AtkHurtBuff extends Buff {

    public AtkHurtBuff(BuffData data) {
        super(data);
    }

    protected void launchEffect(CombatEffect effect) {
        effect.addHurtRate(data.getValue());
    }

}
