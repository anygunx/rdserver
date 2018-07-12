package com.rd.combat.buff;

import com.rd.combat.CombatEffect;
import com.rd.enumeration.EAttr;
import com.rd.model.data.BuffData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月6日下午7:30:35
 */
public class RepvpUpBuff extends Buff {

    public RepvpUpBuff(BuffData data) {
        super(data);
    }

    protected void launchEffect(CombatEffect effect) {
        effect.addAttrRate(EAttr.REPVPDAMAGE, data.getValue());
    }

}
