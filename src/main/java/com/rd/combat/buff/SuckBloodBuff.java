package com.rd.combat.buff;

import com.rd.combat.CombatEffect;
import com.rd.model.data.BuffData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年7月3日上午11:52:25
 */
public class SuckBloodBuff extends Buff {

    public SuckBloodBuff(BuffData data) {
        super(data);
    }

    protected void launchEffect(CombatEffect effect) {
        effect.addBlood(data.getValue());
    }
}
