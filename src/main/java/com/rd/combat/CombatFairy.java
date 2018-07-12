package com.rd.combat;

import com.rd.bean.grow.Grow;
import com.rd.bean.grow.GrowSuit;
import com.rd.bean.player.Player;
import com.rd.combat.buff.Buff;
import com.rd.enumeration.EBuff;
import com.rd.enumeration.ECombater;
import com.rd.enumeration.EGrow;
import com.rd.enumeration.ETarget;
import com.rd.model.SkillModel;
import com.rd.model.data.BuffData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月2日下午12:10:28
 */
public class CombatFairy extends Combater {

    private List<Byte> skills;

    public CombatFairy(byte camp, byte pos, Player player) {
        super(camp, pos, player.getAttr());

        this.skills = new ArrayList<>();

        Grow grow = player.getGrowList().get(EGrow.FAIRY.I());
        for (GrowSuit suit : grow.getSuit()) {
            if (suit.getSkill()[0] > 0) {
                this.skills.add(suit.getSkill()[0]);
            }
        }
    }

    public byte getAtkNum() {
        return this.skill.getSkillActiveData().getAtkNum();
    }

    public Buff launchSkill() {
        //this.skill.setSkillActiveData(ECombater.FAIRY.getSkillActive(skills.get(RandomUtils.nextInt(0, skills.size()))));
        this.skill.setSkillActiveData(ECombater.FAIRY.getSkillActive(60));
        if (this.skill.getSkillActiveData().getBuff() > 0) {
            BuffData buffData = SkillModel.getBuffMap().get(this.skill.getSkillActiveData().getBuff());
            Buff buff = EBuff.getBuff(buffData.getType()).getFunc().apply(buffData);
            if (buff != null && buffData.getTarget() == ETarget.SELF.ordinal()) {
                this.buffMap.put(buff.getType(), buff);
            }
            return buff;
        }
        return null;
    }
}