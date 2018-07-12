package com.rd.combat.buff;

import com.rd.combat.CombatEffect;
import com.rd.enumeration.EAttr;
import com.rd.model.data.BuffData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月6日下午7:34:48
 */
public class RepveUpBuff extends Buff {

    public RepveUpBuff(BuffData data) {
        super(data);
    }

    protected void launchEffect(CombatEffect effect) {
        effect.addAttrRate(EAttr.REPVEDAMAGE, data.getValue());
    }
}
