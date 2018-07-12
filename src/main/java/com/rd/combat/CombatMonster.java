package com.rd.combat;

import com.rd.enumeration.EAttr;
import com.rd.enumeration.ECombater;
import com.rd.model.CombatModel;
import com.rd.model.data.MonsterData;
import com.rd.net.message.Message;

import java.util.Arrays;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月2日下午4:06:58
 */
public class CombatMonster extends Combater {

    public CombatMonster(byte camp, byte pos, int monster) {
        this.camp = camp;
        this.pos = pos;
        this.id = monster;

        MonsterData data = CombatModel.getMonsterData(monster);
        this.attr = Arrays.copyOf(data.getAttr(), data.getAttr().length);
        //this.attr[EAttr.HP.ordinal()]+=1000000;
        //this.attr[EAttr.ATK.ordinal()]+=1000000;
        this.hp = this.attr[EAttr.HP.ordinal()];

        this.atkNum = data.getAtkNum();

        this.skill.setSkillActiveData(ECombater.PET.getSkillActive(data.getSkillId()));
    }


    //测试方法
    public CombatMonster(byte camp, byte pos, int monster, int hp) {
        this.camp = camp;
        this.pos = pos;

        MonsterData data = CombatModel.getMonsterData(monster);
        this.attr = Arrays.copyOf(data.getAttr(), data.getAttr().length);
        this.hp = hp;
        this.atkNum = data.getAtkNum();
        this.skill.setSkillActiveData(ECombater.PET.getSkillActive(data.getSkillId()));
    }

    @Override
    public void getMessage(Message message) {
        message.setByte(pos);
        message.setInt(id);
        message.setInt(hp);
    }
}
