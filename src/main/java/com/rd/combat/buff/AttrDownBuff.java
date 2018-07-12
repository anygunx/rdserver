package com.rd.combat.buff;

import com.rd.combat.CombatEffect;
import com.rd.model.data.BuffData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年7月3日上午11:42:59
 */
public class AttrDownBuff extends Buff {

    public AttrDownBuff(BuffData data) {
        super(data);
    }

    protected void launchEffect(CombatEffect effect) {
        effect.addAllAttrRate(data.getValue());
    }
}
