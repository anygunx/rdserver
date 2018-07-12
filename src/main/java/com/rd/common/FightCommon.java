package com.rd.common;

import com.rd.bean.player.Player;
import com.rd.bean.skill.Skill;
import com.rd.define.EAttrType;
import com.rd.define.FightDefine;
import com.rd.define.GameDefine;
import com.rd.define.SkillDefine;

/**
 * @author ---
 * @version 1.0
 */

public class FightCommon {

    private FightCommon() {

    }

    /**
     * 玩家对战玩家
     *
     * @param player 玩家
     * @return
     */
    public static byte playerVsPlayer(Player playerA, Player playerB) {
/*		byte result=FightDefine.FIGHT_RESULT_FAIL;
        //对战前双方状态重置
		playerA.resetFight();
		playerB.resetFight();
		//获取心法属性
		for(Character characterA:playerA.getCharacterList()){
			characterA.updateHeartSkillAttr();
		}
		for(Character characterB:playerB.getCharacterList()){
			characterB.updateHeartSkillAttr();
		}
		//一直打到一方没有血 或者 时间超时
		int timeout=0;
		//合击技准备
		CombineRuneSkill crSkillA = new CombineRuneSkill(playerA);
		CombineRuneSkill crSkillB = new CombineRuneSkill(playerB);
		outer:while(!playerA.getDead() && !playerB.getDead() && timeout<FightDefine.FIGHT_OVER_TIME){
			if(crSkillA.isHave() && crSkillA.updateDownTime(FightDefine.FIGHT_FRAME_TIME)){
				int damage = crSkillA.launch();
				for(Character character:playerB.getCharacterList()){
					character.setHurt(damage);
				}
			}
			if(crSkillB.isHave() && crSkillB.updateDownTime(FightDefine.FIGHT_FRAME_TIME)){
				int damage = crSkillB.launch();
				for(Character character:playerA.getCharacterList()){
					character.setHurt(damage);
				}
			}
			//挑战者先出手
			
			//buff更新 心法更新
			for(Character characterA:playerA.getCharacterList()){
				characterA.updateBuff(FightDefine.FIGHT_FRAME_TIME);
				characterA.updateHeartSkillTime(FightDefine.FIGHT_FRAME_TIME);
			}
			for(Character characterB:playerB.getCharacterList()){
				characterB.updateBuff(FightDefine.FIGHT_FRAME_TIME);
				characterB.updateHeartSkillTime(FightDefine.FIGHT_FRAME_TIME);
			}
			timeout+=FightDefine.FIGHT_FRAME_TIME;
		}
		//被挑战者死亡 胜利
		if(playerB.getDead() && !playerA.getDead()){
			result=FightDefine.FIGHT_RESULT_SUCCESS;
			if(!GameDefine.ISPUBLISH){
				System.out.println("己方胜利 被挑战者："+playerB.getId());
			}
		//挑战者死亡 失败 
		}else if(playerA.getDead() && !playerB.getDead()){
			result=FightDefine.FIGHT_RESULT_FAIL;
			if(!GameDefine.ISPUBLISH){
				System.out.println("己方失败 被挑战者："+playerB.getId());
			}
		}else{
			result=FightDefine.FIGHT_RESULT_SUCCESS;
			if(!GameDefine.ISPUBLISH){
				System.out.println("双方平局 被挑战者："+playerB.getId());
			}
		}
		return result;*/
        return 1;
    }

    private static boolean launchSkill(Player playerA, Character characterA, Player playerB, Skill skill) {

        return false;
    }

    /**
     * 计算伤害
     *
     * @param A       攻击者
     * @param B       被击者
     * @param lvA     攻击者等级
     * @param lvB     被击者等级
     * @param skillId 技能ID
     * @return
     */
    private static int calculateDamage(int[] A, int[] B, short lvA, short lvB, Skill skill) {
        int damage = 0;
        //命中计算 命中率=max(1+自身命中-目标闪避,0)
        int hit = Math.max(10000 + A[EAttrType.HIT.getId()] - B[EAttrType.DODGE.getId()], 0);
        //是否命中
        if (hit > Math.random() * 10000) {
            //基础伤害 如果 max(攻击*(1+攻击力加成)-max(目标防御-自身防御穿刺,0),0)>0
            //则 基础伤害 s = max(攻击*(1+攻击力加成)-max(目标防御-自身防御穿刺,0),0)*random(0.95,1.05)
            //否则 s = 0.1*攻击
            int defValue = skill.getData().hurtType == SkillDefine.SKILL_HURT_TYPE_PHY ? B[EAttrType.PHYDEF.getId()] : B[EAttrType.MAGICDEF.getId()];//防御值 物防还是法防
            int ctureValue = skill.getData().hurtType == SkillDefine.SKILL_HURT_TYPE_PHY ? A[EAttrType.PHPUNCTURE.getId()] : A[EAttrType.MAGPUNCTURE.getId()];//防御值 物穿还是法穿

            int damageValue = (int) (Math.max(A[EAttrType.ATTACK.getId()] * (1 + (A[EAttrType.ATTACKRADIO.getId()] / 10000)) - Math.max(defValue - ctureValue, 0), 0) * ((100 + Math.random() * 10 - 5) / 100));
            if (damageValue < A[EAttrType.ATTACK.getId()] * 0.1) {
                damageValue = (int) (A[EAttrType.ATTACK.getId()] * 0.1);
            }

            //暴击计算 暴击率=max(自身暴击-目标抗暴,0)
            int crit = Math.max(A[EAttrType.CRIT.getId()] - B[EAttrType.DUCT.getId()], 0);
            if (crit > Math.random() * 10000) {
                //暴击伤害
                damageValue = damageValue * Math.max((A[EAttrType.CRITDAM.getId()] - B[EAttrType.CRITRES.getId()]) / 10000, 1);
            }
            damageValue = (damageValue * Math.max(1 + (A[EAttrType.AMP.getId()] - B[EAttrType.DR.getId()]) / 10000, 0));
            //添加技能系数 *技能基础伤害+技能额外伤害
            damage = (int) (damageValue * skill.getData().valueBase / 10000.0f + skill.getData().valueAdd);
        }
        return damage;
    }

    public static int getJudge(int random, int... percentArray) {
        int rate = 0;
        for (int i = 0; i < percentArray.length; ++i) {
            if (percentArray[i] < 1) {
                continue;
            }
            rate += percentArray[i];
            if (rate > random) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 玩家对战怪物
     *
     * @param monster 怪物
     * @return
     */
    public static byte playerVsEnemy(Player playerA, Player playerB) {
        return -1;
    }

    /**
     * 玩家对战玩家公式
     *
     * @param player 玩家
     * @return
     */
    public static byte playerVsPlayerFormula(Player playerA, Player playerB) {
        playerA.updateFighting();
        playerB.updateFighting();
        long fightHigh = 0;
        long fightLow = 0;
        if (playerA.getFighting() > playerB.getFighting()) {
            fightHigh = playerA.getFighting();
            fightLow = playerB.getFighting();
        } else {
            fightHigh = playerB.getFighting();
            fightLow = playerA.getFighting();
        }
        double rate = (double) (fightHigh - fightLow) / fightLow * 10000;
        int winRate = 0;
        if (fightHigh < 100000) {
            if (rate <= 500) {
                winRate = (int) (5000 + rate);
            } else if (rate <= 1000) {
                winRate = (int) (5000 + rate);
            } else if (rate <= 2000) {
                winRate = (int) (5000 + rate);
            } else if (rate <= 3000) {
                winRate = (int) (6000 + rate);
            } else {
                winRate = (int) (7000 + rate);
            }
        } else if (fightHigh < 500000) {
            if (rate <= 500) {
                winRate = (int) (5000 + rate);
            } else if (rate <= 1000) {
                winRate = (int) (5000 + rate);
            } else if (rate <= 2000) {
                winRate = (int) (6000 + rate);
            } else if (rate <= 3000) {
                winRate = (int) (7000 + rate);
            } else {
                winRate = (int) (7000 + rate);
            }
        } else if (fightHigh < 1000000) {
            if (rate <= 500) {
                winRate = (int) (5000 + rate);
            } else if (rate <= 1000) {
                winRate = (int) (5000 + rate);
            } else if (rate <= 2000) {
                winRate = (int) (6000 + rate);
            } else if (rate <= 3000) {
                winRate = (int) (7000 + rate);
            } else {
                winRate = (int) (7000 + rate);
            }
        } else if (fightHigh < 2000000) {
            if (rate <= 500) {
                winRate = (int) (5500 + rate);
            } else if (rate <= 1000) {
                winRate = (int) (6000 + rate);
            } else if (rate <= 2000) {
                winRate = (int) (7500 + rate);
            } else if (rate <= 3000) {
                winRate = (int) (8000 + rate);
            } else {
                winRate = (int) (8000 + rate);
            }
        } else if (fightHigh < 3000000) {
            if (rate <= 500) {
                winRate = (int) (6000 + rate);
            } else if (rate <= 1000) {
                winRate = (int) (6500 + rate);
            } else if (rate <= 2000) {
                winRate = (int) (8000 + rate);
            } else {
                winRate = (int) (8000 + rate);
            }
        } else {
            if (rate <= 500) {
                winRate = (int) (7000 + rate);
            } else if (rate <= 1000) {
                winRate = (int) (7500 + rate);
            } else if (rate <= 2000) {
                winRate = (int) (8000 + rate);
            } else {
                winRate = (int) (8000 + rate);
            }
        }
        byte result = FightDefine.FIGHT_RESULT_FAIL;
        int random = (int) (Math.random() * 10000);
        if (winRate > random) {
            if (playerA.getFighting() > playerB.getFighting()) {
                result = FightDefine.FIGHT_RESULT_SUCCESS;
            }
        } else {
            if (playerA.getFighting() <= playerB.getFighting()) {
                result = FightDefine.FIGHT_RESULT_SUCCESS;
            }
        }
        if (!GameDefine.ISPUBLISH) {
            System.out.println("高战胜率：" + winRate + " 随机点：" + random);
        }
        return result;
    }
}
