package com.rd.combat.buff;

import com.rd.combat.CombatEffect;
import com.rd.model.data.BuffData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年7月3日上午11:47:11
 */
public class SerialAtkBuff extends Buff {

    public SerialAtkBuff(BuffData data) {
        super(data);
    }

    protected void launchEffect(CombatEffect effect) {
        effect.setSerialAtk((byte) data.getValue());
    }
}
