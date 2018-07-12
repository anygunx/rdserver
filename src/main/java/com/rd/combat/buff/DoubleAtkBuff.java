package com.rd.combat.buff;

import com.rd.combat.CombatEffect;
import com.rd.model.data.BuffData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年7月3日下午1:44:12
 */
public class DoubleAtkBuff extends Buff {

    public DoubleAtkBuff(BuffData data) {
        super(data);
    }

    protected void launchEffect(CombatEffect effect) {
        effect.setSerialAtk((byte) data.getValue());
    }
}
