package com.rd.combat;

import com.rd.bean.player.Player;
import com.rd.bean.skin.NSkin;
import com.rd.combat.buff.Buff;
import com.rd.combat.data.CombatDungeonData;
import com.rd.define.CombatDef;
import com.rd.define.NSkinType;
import com.rd.define.SkillDef;
import com.rd.enumeration.*;
import com.rd.model.data.SkillActiveData;
import com.rd.net.message.Message;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ---
 * @version 1.0
 * @date 2018年4月18日下午2:58:17
 */
public class CombatSystem {

    private CombatSystem() {

    }

    /**
     * 野外关卡小怪战斗
     *
     * @param message
     * @param player
     * @param monster
     * @param roundMax
     * @return
     */
    public static boolean pveStageMonster(Message message, Player player, int[] monster, byte roundMax) {
        List<Combater> heroList = getPlayerCombater(message, player);

        List<Combater> enemyList = new ArrayList<>();

        Combater enemy;
        for (byte i = 0; i < monster.length; ++i) {
            enemy = new CombatMonster(CombatDef.CAMP_ENEMY, i, monster[i]);
            enemyList.add(enemy);
        }
        getMonsterCombater(message, enemyList);
        return combat(message, heroList, enemyList, roundMax);
    }

    /**
     * 野外关卡boss战斗
     *
     * @param message
     * @param player
     * @param boss
     * @param monster
     * @param roundMax
     * @return
     */
    public static boolean pveStageBoss(Message message, Player player, int boss, int[] monster, byte roundMax) {
        List<Combater> heroList = getPlayerCombater(message, player);

        List<Combater> enemyList = new ArrayList<>();

        Combater enemy = new CombatMonster(CombatDef.CAMP_ENEMY, CombatDef.POS_BOSS, boss);
        enemyList.add(enemy);

        for (byte i = 1; i <= monster.length; ++i) {
            enemy = new CombatMonster(CombatDef.CAMP_ENEMY, i, monster[i - 1]);
            enemyList.add(enemy);
        }
        getMonsterCombater(message, enemyList);
        return combat(message, heroList, enemyList, roundMax);
    }

    /**
     * 副本战斗
     *
     * @param message
     * @param player
     * @param boss
     * @param monster
     * @param roundMax
     * @return
     */
    public static boolean pveDungeon(Message message, Player player, int boss, int[] monster, byte roundMax) {
        List<Combater> heroList = getPlayerCombater(message, player);

        List<Combater> enemyList = new ArrayList<>();

        Combater enemy = new CombatMonster(CombatDef.CAMP_ENEMY, CombatDef.POS_BOSS, boss);
        enemyList.add(enemy);

        for (byte i = 1; i <= monster.length; ++i) {
            enemy = new CombatMonster(CombatDef.CAMP_ENEMY, i, monster[i - 1]);
            enemyList.add(enemy);
        }
        getMonsterCombater(message, enemyList);
        return combat(message, heroList, enemyList, roundMax);
    }

    /**
     * PVE组队战斗
     *
     * @param message
     * @param player
     * @param boss
     * @param monster
     * @param roundMax
     * @return
     */
    public static boolean pveTeam(Message message, List<Player> player, int boss, int[] monster, byte roundMax) {
        List<Combater> heroList = getTeamPlayerCombater(message, player);

        List<Combater> enemyList = new ArrayList<>();

        Combater enemy = new CombatMonster(CombatDef.CAMP_ENEMY, CombatDef.POS_BOSS, monster[0]);
        enemyList.add(enemy);

        for (byte i = 1; i <= monster.length; ++i) {
            enemy = new CombatMonster(CombatDef.CAMP_ENEMY, i, monster[i - 1]);
            enemyList.add(enemy);
        }
        getMonsterCombater(message, enemyList);
        return combat(message, heroList, enemyList, roundMax);
    }

    /**
     * PVE组队生死劫
     *
     * @param message
     * @param player
     * @param monster
     * @param roundMax
     * @return
     */
    public static boolean pveTeamLadd(Message message, List<Player> player, CombatDungeonData[] monster, byte roundMax) {
        List<Combater> heroList = getTeamPlayerCombater(message, player);

        List<Combater> enemyList = new ArrayList<>();

        message.setByte(monster.length);

        for (int i = 0; i < monster.length; ++i) {
            Combater enemy = new CombatMonster(CombatDef.CAMP_ENEMY, CombatDef.POS_BOSS, monster[i].getBoss());
            enemyList.add(enemy);

            for (byte j = 1; j <= monster[i].getMonster().length; ++j) {
                enemy = new CombatMonster(CombatDef.CAMP_ENEMY, j, monster[i].getMonster()[j - 1]);
                enemyList.add(enemy);
            }
            getMonsterCombater(message, enemyList);
            if (!combat(message, heroList, enemyList, roundMax)) {
                return false;
            }
        }
        return true;
    }

    /**
     * boss
     *
     * @param message
     * @param player
     * @param boss
     * @param monster
     * @param hp
     * @param round
     * @return
     */
    public static int pveDungeonN(Message message, Player player, int boss, int[] monster, int hp, byte roundMax) {
        List<Combater> heroList = getPlayerCombater(message, player);

        List<Combater> enemyList = new ArrayList<>();
        Combater enemy = new CombatMonster(CombatDef.CAMP_ENEMY, CombatDef.POS_BOSS, boss, hp);
        enemyList.add(enemy);
        getMonsterCombater(message, enemyList);
        combat(message, heroList, enemyList, roundMax);
        return enemy.getHp();
    }

    /**
     * PVP战斗
     *
     * @param message
     * @param playerA
     * @param playerB
     * @param roundMax
     * @return
     */
    public static boolean pvp(Message message, Player playerA, Player playerB, byte roundMax) {
        List<Combater> heroList = getPlayerCombater(message, playerA);
        playerB.getHeroData(message);
        List<Combater> enemyList = getPlayerCombater(message, playerB, CombatDef.CAMP_ENEMY);
        return combat(message, heroList, enemyList, roundMax);
    }

    /**
     * 玩家信息
     *
     * @param message
     * @param player
     * @return
     */
    private static List<Combater> getPlayerCombater(Message message, Player player) {
        return getPlayerCombater(message, player, CombatDef.CAMP_FRIENDS);
    }

    /**
     * 玩家信息
     *
     * @param message
     * @param player
     * @return
     */
    private static List<Combater> getPlayerCombater(Message message, Player player, byte camp) {
        List<Combater> heroList = new ArrayList<>();
        //主角
        Combater hero = new CombatHero(camp, CombatDef.POS_HERO, player);
        heroList.add(hero);

        message.setInt(hero.getHp());

        //通灵
        message.setShort(player.getGrowList().get(EGrow.PET.I()).getSuit()[0].getLevel());
        //兽魂
        message.setShort(player.getGrowList().get(EGrow.PET.I()).getSuit()[1].getLevel());

        for (short go : player.getGrowList().get(EGrow.PET.I()).getGo()) {
            message.setShort(go);
        }

        //宠物
        if (player.getGrowList().get(EGrow.PET.I()).getGo()[0] != 0) {
            CombatPet pet = new CombatPet(camp, CombatDef.POS_PET, player);
            heroList.add(pet);

            message.setInt(pet.getHp());
            if (player.getGrowList().get(EGrow.PET.I()).getGo()[1] != 0) {
                message.setInt(pet.getPetHp(player.getGrowList().get(EGrow.PET.I()).getGo()[1]));
                if (player.getGrowList().get(EGrow.PET.I()).getGo()[2] != 0) {
                    message.setInt(pet.getPetHp(player.getGrowList().get(EGrow.PET.I()).getGo()[2]));
                }
            }
        }
        //仙位
        message.setShort(player.getGrowList().get(EGrow.MATE.I()).getSuit()[0].getLevel());
        //法阵
        message.setShort(player.getGrowList().get(EGrow.MATE.I()).getSuit()[1].getLevel());
        //仙侣1
        if (player.getGrowList().get(EGrow.MATE.I()).getGo()[0] != 0) {
            Combater mate1 = new CombatMate(camp, CombatDef.POS_MATE1, player, player.getGrowList().get(EGrow.MATE.I()).getGo()[0]);
            heroList.add(mate1);
        }
        //仙侣2
        if (player.getGrowList().get(EGrow.MATE.I()).getGo()[1] != 0) {
            Combater mate2 = new CombatMate(camp, CombatDef.POS_MATE2, player, player.getGrowList().get(EGrow.MATE.I()).getGo()[1]);
            heroList.add(mate2);
        }

        for (short go : player.getGrowList().get(EGrow.MATE.I()).getGo()) {
            message.setShort(go);
        }

        //天女
        if (player.getVipLevel() >= CombatDef.FAIRY_VIP_OP || player.getLevel() >= CombatDef.FAIRY_LV_OP) {
            Combater fairy = new CombatFairy(camp, CombatDef.POS_FAIRY, player);
            heroList.add(fairy);

            NSkin skin = player.getPiFuMap().get(NSkinType.TIANNV);
            if (skin != null) {
                message.setShort(skin.getCurrHZId());
            } else {
                message.setShort(1);
            }
        } else {
            message.setShort(0);
        }

        return heroList;
    }

    /**
     * 怪物信息
     *
     * @param message
     * @param monster
     */
    private static void getMonsterCombater(Message message, List<Combater> monster) {
        message.setByte(monster.size());
        for (Combater combater : monster) {
            combater.getMessage(message);
        }
    }

    /**
     * 组队玩家信息
     *
     * @param message
     * @param players
     * @return
     */
    private static List<Combater> getTeamPlayerCombater(Message message, List<Player> players) {
        List<Combater> heroList = new ArrayList<>();
        message.setByte(players.size());
        for (int i = 0; i < players.size(); ++i) {
            Player player = players.get(i);
            //主角
            Combater hero = new CombatHero(CombatDef.CAMP_FRIENDS, CombatDef.TEAM_POS[i][0], player);
            heroList.add(hero);

            message.setInt(hero.getHp());
            for (short go : player.getGrowList().get(EGrow.PET.I()).getGo()) {
                message.setShort(go);
            }
            //宠物
            if (player.getGrowList().get(EGrow.PET.I()).getGo()[0] != 0) {
                Combater pet = new CombatPet(CombatDef.CAMP_FRIENDS, CombatDef.TEAM_POS[i][1], player);
                heroList.add(pet);

                message.setInt(pet.getHp());
            }
        }
        return heroList;
    }

//	private static List<Byte> randomPos(int num){
//		List<Byte> posList = new ArrayList<>();
//		while(posList.size()!=num){
//			byte b = (byte)RandomUtils.nextInt(1, 10);
//			if(!posList.contains(b)){
//				posList.add(b);
//			}
//		}
//		return posList;
//	}

    private static void randomTarget(List<Combater> targetList, List<Combater> beAtkList, int num) {
        while (targetList.size() != num) {
            if (targetList.size() == beAtkList.size()) {
                break;
            }
            Combater combater = beAtkList.get(RandomUtils.nextInt(0, beAtkList.size()));
            if (!targetList.contains(combater)) {
                targetList.add(combater);
            }
        }
    }

    private static Combater randomTarget(List<Combater> beAtkList) {
        if (beAtkList.size() == 0) {
            return null;
        }
        return beAtkList.get(RandomUtils.nextInt(0, beAtkList.size()));
    }

    private static boolean combat(Message message, List<Combater> hero, List<Combater> enemy, byte roundMax) {

        List<Combater> all = new ArrayList<>();
        all.addAll(hero);
        all.addAll(enemy);

        Collections.sort(all);

        List<Combater> targetList = new ArrayList<>();

        int round = 1;
        for (; round <= roundMax; ++round) {
            message.setByte(round);
            for (int i = 0; i < all.size(); ++i) {
                Combater combater = all.get(i);
                if (combater.getState() != CombatDef.STATE_DEAD && combater.getEffectState() != SkillDef.VERTIGO) {
                    targetList.clear();
                    List<Combater> listA = null;
                    List<Combater> listB = null;
                    Buff buff = combater.launchSkill();
                    switch (combater.getCamp()) {
                        case CombatDef.CAMP_FRIENDS:
                            randomTarget(targetList, enemy, combater.getAtkNum());
                            listA = hero;
                            listB = enemy;
                            if (buff != null) {
                                if (buff.getData().getTarget() == ETarget.GROUP_FRIEND.ordinal()) {
                                    for (Combater c : hero) {
                                        c.addBuff(buff);
                                    }
                                } else if (buff.getData().getTarget() == ETarget.GROUP_ENEMY.ordinal()) {
                                    for (Combater c : enemy) {
                                        c.addBuff(buff);
                                    }
                                }
                            }
                            break;
                        case CombatDef.CAMP_ENEMY:
                            randomTarget(targetList, hero, combater.getAtkNum());
                            listA = enemy;
                            listB = hero;
                            if (buff != null) {
                                if (buff.getData().getTarget() == ETarget.GROUP_FRIEND.ordinal()) {
                                    for (Combater c : enemy) {
                                        c.addBuff(buff);
                                    }
                                } else if (buff.getData().getTarget() == ETarget.GROUP_ENEMY.ordinal()) {
                                    for (Combater c : hero) {
                                        c.addBuff(buff);
                                    }
                                }
                            }
                            break;
                    }
                    message.setByte(combater.getCamp());
                    message.setByte(combater.getPos());
                    message.setShort(combater.getSkill().getSkillActiveData().getId());
                    message.setByte(targetList.size());
                    for (Combater target : targetList) {
                        calculationDamage(message, combater, target, listA, listB, combater.getAtkNum(), (short) 0, buff);

                        if (target.getState() == CombatDef.STATE_DEAD) {
                            switch (target.getCamp()) {
                                case CombatDef.CAMP_FRIENDS:
                                    hero.remove(target);
                                    break;
                                case CombatDef.CAMP_ENEMY:
                                    enemy.remove(target);
                                    break;
                            }
                        }
                        if (combater.getState() == CombatDef.STATE_DEAD) {
                            switch (combater.getCamp()) {
                                case CombatDef.CAMP_FRIENDS:
                                    hero.remove(target);
                                    break;
                                case CombatDef.CAMP_ENEMY:
                                    enemy.remove(target);
                                    break;
                            }
                        }
                    }
                }
                if (enemy.size() == 0) {
                    message.setByte(CombatDef.CAMP_END);
                    message.setByte(CombatDef.CAMP_FRIENDS);
                    return true;
                }
                if (hero.size() == 0) {
                    message.setByte(CombatDef.CAMP_END);
                    message.setByte(CombatDef.CAMP_ENEMY);
                    return false;
                }
                if (i + 1 == all.size()) {
                    if (round == roundMax) {
                        message.setByte(CombatDef.CAMP_END);
                        message.setByte(CombatDef.CAMP_ENEMY);
                    } else {
                        message.setByte(CombatDef.CAMP_ROUND);
                    }

                }
            }
            for (Combater combater : all) {
                combater.addRound();
            }
        }
        return false;
    }

    //PVE
    //闪避率		闪避率=max（防御方闪避+500-攻击方命中，0）/10000
    //防御免伤值	伤害减免值=防御方防御*(1-max(攻击方防御减免-防御方减免无视,0）/10000)
    //基础伤害		基础伤害=max（攻击方攻击-防御方防御免伤值，攻击方攻击*0.1）
    //技能伤害		技能伤害=基础伤害*攻击方技能百分比+攻击方技能额外伤害
    //百分比伤免修正	最终伤害=技能伤害*max（1+攻击方伤害增加-防御方伤害减少，0）
    //PVE伤害		最终伤害*max（1+攻击方pvp伤害增加-防御方pvp伤害减少,0）
    //暴击率		暴击率=max（攻击方暴击+500-防御方抗暴，0）/10000
    //暴击伤害		暴击伤害=最终伤害*max（初始暴击伤害百分比+攻击方暴伤增加-防御方暴伤减免,1）
    private static void calculationDamage(Message message, Combater A, Combater B, List<Combater> listA, List<Combater> listB, byte atkNum, short counter, Buff buff) {

        if (buff != null && buff.getData().getTarget() == ETarget.ENEMY.ordinal()) {
            B.addBuff(buff);
        }
        ETrigger trigger = ETrigger.SINGLE_HIT;
        if (atkNum > 1) {
            trigger = ETrigger.GROUP_HIT;
        }
        B.triggerBuff(trigger);

/*		//计算闪避
		if(B.at(EAttr.DODGE.ordinal())+500-A.at(EAttr.HIT.ordinal())>RandomUtils.nextInt(0, 10000)){
			B.setBeHit(CombatDef.BEATK_DODGE);
			
			message.setByte(B.getPos());
			message.setByte(B.getBeHit());
			message.setInt(B.getDamage());
			B.getHpMessage(message);
			message.setByte(0);
			message.setByte(0);
			message.setByte(0);
		}else{
			//计算防御免伤值
			int immune = B.at(EAttr.DEF.ordinal())*(1-Math.max(A.at(EAttr.REDEF.ordinal())-B.at(EAttr.INDEF.ordinal()),0)/10000);
			
			//计算基础伤害
			int damage = (int)Math.max(A.at(EAttr.ATK.ordinal()) - immune , A.at(EAttr.ATK.ordinal())*0.1D);
			
			//计算技能伤害
			SkillActiveData skill = A.getSkill();
			
			if(counter>0){
				skill = ECombater.PET.getSkillActive(counter);
			}
			
			//技能伤害提升buff
			damage = (int)(damage*(1+skill.getPercentHurt()+A.effect.getSkillHurtUp()))+skill.getFixedHurt();
			
			//计算伤免伤害
			damage = damage * Math.max(1 + A.at(EAttr.INDAMAGE.ordinal())/10000 - B.at(EAttr.REDAMAGE.ordinal())/10000, 0);
			
			//计算PVE伤害
			damage = damage * Math.max(1 + A.at(EAttr.PVEDAMAGE.ordinal())/10000 - B.at(EAttr.REPVEDAMAGE.ordinal())/10000, 0);
			
			//计算暴击
			if(A.at(EAttr.CRIT.ordinal()) + 500 - B.at(EAttr.SUBCRIT.ordinal())>RandomUtils.nextInt(0, 10000)){
				//计算暴击伤害 添加技能暴击buff
				damage *= Math.max(1.5D + + A.effect.getSkillCritUp() + A.at(EAttr.CRITHURT.ordinal())/10000 - B.at(EAttr.CRITREDUCE.ordinal())/10000, 1);
				B.setBeHit(CombatDef.BEATK_CRIT);
			}else{
				B.setBeHit(CombatDef.BEATK_NORMAL);
			}
			
			byte isDouble = 0;
			
			//伤害倍率buff
			damage += damage * (A.effect.getHurtRate()/CombatDef.TEN_THOUSAND);
			
			//吸血buff 
			int suckBlood = (int)(damage * (A.effect.getBlood()/CombatDef.TEN_THOUSAND));
			
			B.hurt(damage);
			
			message.setByte(B.getPos());
			message.setByte(B.getBeHit());
			message.setInt(B.getDamage());
			B.getHpMessage(message);
			
			//伤害反弹buff
			int reDamage = (int)(damage * (A.effect.getReboundHurtRate()/CombatDef.TEN_THOUSAND));
			
			//反击技能buff
			if(counter==0 && B.effect.getCounterSkill()>0){
			
				message.setByte(1);
			
				calculationDamage(message,B,A,listB,listA,atkNum,B.effect.getCounterSkill(),buff);
			}else{
				message.setByte(0);
			}
			
			//计算连击
			message.setByte(isDouble);
			if(isDouble==1){
				message.setInt(damage);
				B.hurt(damage);
			}
			
			//连续攻击
			int serialAtk = A.effect.getSerialAtk();
			message.setByte(serialAtk);
			if(serialAtk>0){
				for(int i=1;i<=serialAtk;++i){
					int d = damage + (int)(damage * (i * CombatDef.SERIAL_ATK_INCREASE));
					message.setInt(d);
				}
			}
		}*/

        CombatSkill skill = A.getSkill();

        boolean isNormal = true;

        if (skill.getBuffData() != null) {
            if (skill.getBuffData().getType() == EBuff.SERIAL_ATTACK.ordinal()) {
                int serialNum = skill.getBuffData().getValue();
                //攻击次数
                message.setByte(serialNum);
                for (int i = 0; i < skill.getBuffData().getValue(); ++i) {
                    if (B.getState() == CombatDef.STATE_DEAD) {
                        listB.remove(B);
                        if (listB.size() == 0) {
                            message.setByte(-1);
                        } else {
                            B = randomTarget(listB);
                            calculationAttack(message, A, B, listA, listB, serialNum, trigger, null);
                        }
                    } else {
                        calculationAttack(message, A, B, listA, listB, serialNum, trigger, null);
                    }
                }

                isNormal = false;
            } else if (skill.getBuffData().getType() == EBuff.DOUBLE_ATTACK.ordinal()) {
                if (EBuff.isWin(skill.getBuffData())) {
                    //攻击次数
                    message.setByte(CombatDef.DOUBLE_ATTACK);
                    for (int i = 0; i < skill.getBuffData().getValue(); ++i) {
                        if (B.getState() == CombatDef.STATE_DEAD) {
                            listB.remove(B);
                            if (listB.size() == 0) {
                                message.setByte(-1);
                            } else {
                                B = randomTarget(listB);
                                calculationAttack(message, A, B, listA, listB, CombatDef.DOUBLE_ATTACK, trigger, null);
                            }
                        } else {
                            calculationAttack(message, A, B, listA, listB, CombatDef.DOUBLE_ATTACK, trigger, null);
                        }
                    }

                    isNormal = false;
                }
            }
        }
        if (isNormal) {
            //攻击次数
            message.setByte(1);
            calculationAttack(message, A, B, listA, listB, 1, trigger, null);
        }
    }

    private static void calculationAttack(Message message, Combater A, Combater B, List<Combater> listA, List<Combater> listB, int atkNum, ETrigger trigger, SkillActiveData counterSkill) {

//		ETrigger trigger = ETrigger.SINGLE_HIT;
//		if(atkNum>1){
//			trigger = ETrigger.GROUP_HIT;
//		}
//		B.triggerBuff(trigger);

        //计算闪避
        if (B.at(EAttr.DODGE.ordinal()) + 500 - A.at(EAttr.HIT.ordinal()) > RandomUtils.nextInt(0, 10000)) {
            B.setBeHit(CombatDef.BEATK_DODGE);

            message.setByte(B.getPos());
            message.setByte(B.getBeHit());
            message.setInt(B.getDamage());
            message.setByte(0);
            //B.getHpMessage(message);
            //message.setByte(0);
            //message.setByte(0);
            //message.setByte(0);
        } else {
            //计算防御免伤值
            int immune = B.at(EAttr.DEF.ordinal()) * (1 - Math.max(A.at(EAttr.REDEF.ordinal()) - B.at(EAttr.INDEF.ordinal()), 0) / 10000);

            //计算基础伤害
            int damage = (int) Math.max(A.at(EAttr.ATK.ordinal()) - immune, A.at(EAttr.ATK.ordinal()) * 0.1D);

            //计算技能伤害
            SkillActiveData skill = A.getSkill().getSkillActiveData();
            if (counterSkill != null) {
                skill = counterSkill;
            }

            //技能伤害提升buff
            damage = (int) (damage * (1 + skill.getPercentHurt() + A.effect.getSkillHurtUp())) + skill.getFixedHurt();

            //计算伤免伤害
            damage = damage * Math.max(1 + A.at(EAttr.INDAMAGE.ordinal()) / 10000 - B.at(EAttr.REDAMAGE.ordinal()) / 10000, 0);

            //计算PVE伤害
            damage = damage * Math.max(1 + A.at(EAttr.PVEDAMAGE.ordinal()) / 10000 - B.at(EAttr.REPVEDAMAGE.ordinal()) / 10000, 0);

            //计算暴击
            if (A.at(EAttr.CRIT.ordinal()) + 500 - B.at(EAttr.SUBCRIT.ordinal()) > RandomUtils.nextInt(0, 10000)) {
                //计算暴击伤害 添加技能暴击buff
                damage *= Math.max(1.5D + +A.effect.getSkillCritUp() + A.at(EAttr.CRITHURT.ordinal()) / 10000 - B.at(EAttr.CRITREDUCE.ordinal()) / 10000, 1);
                B.setBeHit(CombatDef.BEATK_CRIT);
            } else {
                B.setBeHit(CombatDef.BEATK_NORMAL);
            }

            //伤害倍率buff
            damage += damage * (A.effect.getHurtRate() / CombatDef.TEN_THOUSAND);

            //吸血buff
            int suckBlood = (int) (damage * (A.effect.getBlood() / CombatDef.TEN_THOUSAND));

            boolean isDead = B.hurt(damage);

            message.setByte(B.getPos());
            message.setByte(B.getBeHit());
            message.setInt(B.getDamage());
            //B.getHpMessage(message);

            //伤害反弹buff
            int reDamage = (int) (damage * (A.effect.getReboundHurtRate() / CombatDef.TEN_THOUSAND));

            //反击
            //message.setByte(0);

            //计算连击
            //message.setByte(0);

            //连续攻击
            //message.setByte(0);

            if (counterSkill == null && !isDead) {
                //计算反击
                SkillActiveData counter = B.counter(trigger);
                if (atkNum == CombatDef.NORMAL_ATTACK && counter != null) {
                    message.setByte(CombatDef.COUNTER_ATTACK);
                    message.setShort(counter.getId());

                    calculationAttack(message, B, A, listB, listA, 1, ETrigger.getETrigger(counter.getAtkNum()), counter);
                } else {
                    message.setByte(CombatDef.NO_COUNTER);
                }
            } else {
                message.setByte(CombatDef.NO_COUNTER);
            }
        }
    }
}
