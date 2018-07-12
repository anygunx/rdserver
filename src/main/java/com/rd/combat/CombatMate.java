package com.rd.combat;

import com.rd.bean.player.Player;
import com.rd.enumeration.ECombater;
import com.rd.enumeration.EGrow;
import com.rd.model.data.GrowSeedData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月2日上午11:43:24
 */
public class CombatMate extends Combater {

    public CombatMate(byte camp, byte pos, Player player, short mate) {
        super(camp, pos, player.getAttr());

        GrowSeedData data = EGrow.MATE.getGrowDataMap().get(mate);
        this.atkNum = data.getAtkNum();

        this.skill.setSkillActiveData(ECombater.MATE.getSkillActive(data.getActiveSkill()));
    }

}
