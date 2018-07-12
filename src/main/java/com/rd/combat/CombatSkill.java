package com.rd.combat;

import com.rd.enumeration.EBuff;
import com.rd.model.SkillModel;
import com.rd.model.data.BuffData;
import com.rd.model.data.SkillActiveData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年7月4日下午2:52:52
 */
public class CombatSkill {

    /**
     * 主动技能
     **/
    private SkillActiveData skillActiveData;
    /**
     * 特殊技能
     **/
    private BuffData buffData;

    public CombatSkill() {

    }

    public SkillActiveData getSkillActiveData() {
        return skillActiveData;
    }

    public void setSkillActiveData(SkillActiveData skillActiveData) {
        this.skillActiveData = skillActiveData;

        if (this.skillActiveData.getBuff() > 0) {
            BuffData buffData = SkillModel.getBuffMap().get(this.skillActiveData.getBuff());
            if (buffData.getType() == EBuff.SERIAL_ATTACK.ordinal()) {
                this.buffData = buffData;
            } else if (buffData.getType() == EBuff.DOUBLE_ATTACK.ordinal()) {
                this.buffData = buffData;
            }
        } else {
            this.buffData = null;
        }
    }

    public BuffData getBuffData() {
        return buffData;
    }

    public void setBuffData(BuffData buffData) {
        this.buffData = buffData;
    }

}
