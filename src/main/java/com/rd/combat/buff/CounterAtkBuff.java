package com.rd.combat.buff;

import com.rd.combat.CombatEffect;
import com.rd.model.data.BuffData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月7日下午7:59:02
 */
public class CounterAtkBuff extends Buff {

    public CounterAtkBuff(BuffData data) {
        super(data);
    }

    protected void launchEffect(CombatEffect effect) {
        effect.setCounterSkill(data.getValue());
    }
}
