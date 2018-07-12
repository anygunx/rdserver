package com.rd.combat;

import com.rd.bean.player.Player;
import com.rd.combat.buff.Buff;
import com.rd.enumeration.EBuff;
import com.rd.enumeration.ECombater;
import com.rd.enumeration.ETarget;
import com.rd.model.SkillModel;
import com.rd.model.data.BuffData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月1日下午7:10:44
 */
public class CombatHero extends Combater {

    List<Short> skills;

    private byte[] order;

    private int index;

    public CombatHero(byte camp, byte pos, Player player) {
        super(camp, pos, player.getAttr());

        this.skills = new ArrayList<>();
        for (short skill : player.getSkill()) {
            if (skill > 0) {
                this.skills.add(skill);
            }
        }

        this.order = new byte[this.skills.size()];
        for (int i = 0; i < this.skills.size(); ++i) {
            this.order[i] = player.getSkillPos()[i];
        }

        //临时代码
        this.skills.add((short) 1);
        this.skills.add((short) 301);
        this.skills.add((short) 601);
        this.skills.add((short) 901);
        this.skills.add((short) 1201);

        this.order = new byte[]{0, 1, 2, 3, 4};
    }

    public byte getAtkNum() {
        return skill.getSkillActiveData().getAtkNum();
    }

    public Buff launchSkill() {
        this.skill.setSkillActiveData(ECombater.HERO.getSkillActive(this.skills.get(this.order[this.index % this.skills.size()])));
        ++this.index;

        if (skill.getSkillActiveData().getBuff() > 0) {
            BuffData buffData = SkillModel.getBuffMap().get(skill.getSkillActiveData().getBuff());
            Buff buff = EBuff.getBuff(buffData.getType()).getFunc().apply(buffData);
            if (buff != null && buffData.getTarget() == ETarget.SELF.ordinal()) {
                this.addBuff(buff);
            }
            return buff;
        }
        return null;
    }

}
