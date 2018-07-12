package com.rd.combat;

import com.rd.bean.grow.GrowSeed;
import com.rd.bean.player.Player;
import com.rd.combat.buff.Buff;
import com.rd.enumeration.*;
import com.rd.model.SkillModel;
import com.rd.model.data.BuffData;
import com.rd.model.data.GrowSeedData;
import com.rd.model.data.SkillActiveData;
import com.rd.model.data.SkillPassiveData;
import com.rd.net.message.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月2日下午12:08:50
 */
public class CombatPet extends Combater {

    private Player player;

    private List<Short> pets;

    private byte pos = -1;

    public CombatPet(byte camp, byte pos, Player player) {
        super(camp, pos, player.getAttr());

        this.player = player;

        this.pets = new ArrayList<>();
        for (short pet : player.getGrowList().get(EGrow.PET.I()).getGo()) {
            if (pet > 0) {
                this.pets.add(pet);
            }
        }

        this.comeOn();
    }

    private boolean comeOn() {
        ++pos;
        if (pos < pets.size() && pets.get(pos) > 0) {
            GrowSeedData data = EGrow.PET.getGrowDataMap().get(pets.get(pos));
            this.atkNum = data.getAtkNum();

            this.skill.setSkillActiveData(ECombater.PET.getSkillActive(data.getActiveSkill()));

			/*for(short passiveSkill:data.getPassiveSkill()){
				SkillPassiveData sk = EGrow.PET.getSkillPassiveMap().get(passiveSkill);
				if(sk.getBuff()>0){
					BuffData buff = SkillModel.getBuffMap().get(sk.getBuff());
					if(buff.getType() == EBuff.COUNTER_ATTACK.ordinal()){
						//this.counterAttack = new CounterAtkBuff(EBuff.COUNTER_ATTACK.ordinal(),(byte)-1,buff.getTrigger(),buff.getSkill());
					}
					//this.buffList.add(caBuff);
				}
			}*/

            //添加宠物洗炼技能属性
            int[] attr = new int[EAttr.SIZE];
            attr = Arrays.copyOf(player.getAttr(), player.getAttr().length);
            passiveSkill = new ArrayList<>();
            short pet = pets.get(pos);
            Map<Short, GrowSeed> map = player.getGrowList().get(EGrow.PET.I()).getMap();
            for (int i = 0; i < map.get(pet).getSkillPassive().length; ++i) {
                if (map.get(pet).getSkillPassive()[i][1] > 0) {
                    //SkillPassiveData passiveData=EGrow.PET.getSkillPassiveMap().get(map.get(pet).getSkillPassive()[i][1]);
                    //测试代码
                    SkillPassiveData passiveData = EGrow.PET.getSkillPassiveMap().get((short) 134);

                    for (int j = 0; j < EAttr.SIZE; ++j) {
                        attr[j] += passiveData.getAttr()[j];
                    }
                    if (passiveData.getBuff() > 0) {
                        passiveSkill.add(passiveData);
                    }
                }
            }
            this.attr = attr;

            this.hp = attr[EAttr.HP.ordinal()];

            return true;
        }
        return false;
    }

    public int getPetHp(short go) {
        int hp = player.getAttr()[EAttr.HP.ordinal()];
        Map<Short, GrowSeed> map = player.getGrowList().get(EGrow.PET.I()).getMap();
        for (int i = 0; i < map.get(go).getSkillPassive().length; ++i) {
            if (map.get(go).getSkillPassive()[i][1] > 0) {
                SkillPassiveData passiveData = EGrow.PET.getSkillPassiveMap().get(map.get(go).getSkillPassive()[i][1]);
                hp += passiveData.getAttr()[EAttr.HP.ordinal()];
            }
        }
        return hp;
    }

    public boolean dead() {
        if (!this.comeOn()) {
            return super.dead();
        }
        return true;
    }

    public void getHpMessage(Message message) {
        message.setInt(this.hp);
    }

    public Buff launchSkill() {
        Buff buff = super.launchSkill();
        passiveSkill(ETrigger.ATK);
        triggerBuff(ETrigger.ATK);
        return buff;
    }

    public void passiveSkill(ETrigger trigger) {
        GrowSeedData data = EGrow.PET.getGrowDataMap().get(pets.get(pos));
        for (short passiveSkill : data.getPassiveSkill()) {
            SkillPassiveData sk = EGrow.PET.getSkillPassiveMap().get(passiveSkill);
            if (sk.getBuff() > 0) {
                BuffData buffData = SkillModel.getBuffMap().get(sk.getBuff());
                Buff buff = EBuff.getBuff(buffData.getType()).getFunc().apply(buffData);
                if (buff != null && buffData.getTarget() == ETarget.SELF.ordinal()) {
                    this.buffMap.put(buff.getType(), buff);
                }
            }
        }
    }

    protected SkillActiveData counter(ETrigger trigger) {
        if (passiveSkill != null) {
            for (SkillPassiveData data : passiveSkill) {
                if (data.getBuff() > 0) {
                    BuffData buffData = SkillModel.getBuffMap().get(data.getBuff());
                    if (buffData.getType() == EBuff.COUNTER_ATTACK.ordinal()) {
                        //if(EBuff.isHitTrigger(buffData, trigger)){
                        //	return ECombater.PET.getSkillActive(buffData.getValue());
                        //}
                        //测试代码
                        return ECombater.PET.getSkillActive((short) 11);
                    }
                }
            }
        }
        return null;
    }
}