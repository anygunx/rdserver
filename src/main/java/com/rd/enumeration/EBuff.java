package com.rd.enumeration;

import com.rd.combat.buff.*;
import com.rd.model.data.BuffData;
import org.apache.commons.lang3.RandomUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月6日下午1:37:37
 */
public enum EBuff {

    NONE(null),
    //1.造成伤害提高%
    ATK_HURT(EBuff::atkHurt),
    //2.反弹伤害的%给攻击者
    REBOUND_HURT(EBuff::reboundHurt),
    //3.提升%命中持续x回合
    HIT_UP(EBuff::hitUp),
    //4.提升%攻击力持续x回合
    ATK_UP(EBuff::atkUp),
    //5.提升%防御力持续x回合
    DEF_UP(EBuff::defUp),
    //6.提升%PVP伤害持续x回合
    PVP_UP(EBuff::pvpUp),
    //7.提升%PVE防御持续x回合
    REPVE_UP(EBuff::repveUp),
    //8.提升%PVE伤害持续x回合
    PVE_UP(EBuff::pveUp),
    //9.提升%闪避持续x回合
    DODGE_UP(EBuff::dodgeUp),
    //10.提升%REPVP防御持续x回合
    REPVP_UP(EBuff::repvpUp),
    //11.攻击后使下一次受到伤害减少%
    HURT_DOWN(EBuff::hurtDownUp),
    //12.下回合减免%伤害
    REDUCTION_HURT(EBuff::reductionHurt),
    //13.触发眩晕
    VERTIGO(EBuff::vertigo),
    //14.使目标下回合属性降低%
    ATTR_DOWN(EBuff::attrDown),
    //15.连续攻击X次，每次伤害增加5%
    SERIAL_ATTACK(EBuff::serialAtk),
    //16.造成伤害的%转化为生命
    SUCK_BLOOD(EBuff::suckBlood),
    //17.全队伤害减少%
    ALL_HURT_DOWN(EBuff::allHurtDown),
    //18.提升当前技能%暴击
    SKILL_CRIT_UP(EBuff::skillCritUp),
    //19.触发双重攻击
    DOUBLE_ATTACK(EBuff::doubleAtk),
    //20.触发反击
    COUNTER_ATTACK(EBuff::counterAtk),
    //21.提升当前技能%伤害
    SKILL_HURT_UP(EBuff::skillHurtUp),;

    private static Map<Byte, EBuff> map = new HashMap<>();

    static {
        for (EBuff buf : EBuff.values()) {
            map.put((byte) buf.ordinal(), buf);
        }
    }

    private Function<BuffData, Buff> func;

    EBuff(Function<BuffData, Buff> func) {
        this.func = func;
    }

    public Function<BuffData, Buff> getFunc() {
        return func;
    }

    public static EBuff getBuff(byte type) {
        return map.get(type);
    }

    public static boolean isWin(BuffData data) {
        if (data.getPr() == 0 || data.getPr() > RandomUtils.nextInt(0, 10000)) {
            return true;
        }
        return false;
    }

    public static boolean isHitTrigger(BuffData data, ETrigger trigger) {
        if (data.getTrigger() == trigger.ordinal()) {
            return true;
        } else if (data.getTrigger() == ETrigger.HIT.ordinal() && (trigger == ETrigger.SINGLE_HIT || trigger == ETrigger.GROUP_HIT)) {
            return true;
        }
        return false;
    }

    /**
     * 1.造成伤害提高%
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff atkHurt(BuffData data) {
        if (isWin(data)) {
            return new AtkHurtBuff(data);
        }
        return null;
    }

    /**
     * 2.反弹伤害的%给攻击者
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff reboundHurt(BuffData data) {
        if (isWin(data)) {
            return new ReboundHurtBuff(data);
        }
        return null;
    }

    /**
     * 3.提升%命中持续x回合
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff hitUp(BuffData data) {
        if (isWin(data)) {
            return new HitUpBuff(data);
        }
        return null;
    }

    /**
     * 4.提升%攻击力持续x回合
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff atkUp(BuffData data) {
        if (isWin(data)) {
            return new AtkUpBuff(data);
        }
        return null;
    }

    /**
     * 5.提升%防御持续x回合
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff defUp(BuffData data) {
        if (isWin(data)) {
            return new DefUpBuff(data);
        }
        return null;
    }

    /**
     * 6.提升%PVP持续x回合
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff pvpUp(BuffData data) {
        if (isWin(data)) {
            return new PvpUpBuff(data);
        }
        return null;
    }

    /**
     * 7.提升%REPVE持续x回合
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff repveUp(BuffData data) {
        if (isWin(data)) {
            return new RepveUpBuff(data);
        }
        return null;
    }

    /**
     * 8.提升%PVE持续x回合
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff pveUp(BuffData data) {
        if (isWin(data)) {
            return new PveUpBuff(data);
        }
        return null;
    }

    /**
     * 9.提升%闪避持续x回合
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff dodgeUp(BuffData data) {
        if (isWin(data)) {
            return new DodgeUpBuff(data);
        }
        return null;
    }

    /**
     * 10.提升%REPVP持续x回合
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff repvpUp(BuffData data) {
        if (isWin(data)) {
            return new RepvpUpBuff(data);
        }
        return null;
    }

    /**
     * 11.攻击后使下一次受到伤害减少%
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff hurtDownUp(BuffData data) {
        if (isWin(data)) {
            return new HurtDownBuff(data);
        }
        return null;
    }

    /**
     * 12.下回合减免%伤害
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff reductionHurt(BuffData data) {
        if (isWin(data)) {
            return new HurtDownBuff(data);
        }
        return null;
    }

    /**
     * 13.触发眩晕
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff vertigo(BuffData data) {
        if (isWin(data)) {
            return new VertigoBuff(data);
        }
        return null;
    }

    /**
     * 14.使目标下回合属性降低%
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff attrDown(BuffData data) {
        if (isWin(data)) {
            return new AttrDownBuff(data);
        }
        return null;
    }

    /**
     * 15.连续攻击X次，每次伤害增加%
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff serialAtk(BuffData data) {
        if (isWin(data)) {
            return new SerialAtkBuff(data);
        }
        return null;
    }

    /**
     * 16.造成伤害的%转化为生命
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff suckBlood(BuffData data) {
        if (isWin(data)) {
            return new SuckBloodBuff(data);
        }
        return null;
    }

    /**
     * 17.全队伤害减少%
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff allHurtDown(BuffData data) {
        if (isWin(data)) {
            return new AllHurtDownBuff(data);
        }
        return null;
    }

    /**
     * 18.提升当前技能%暴击
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff skillCritUp(BuffData data) {
        if (isWin(data)) {
            return new SkillCritUpBuff(data);
        }
        return null;
    }

    /**
     * 19.触发连击
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff doubleAtk(BuffData data) {
        if (isWin(data)) {
            return new DoubleAtkBuff(data);
        }
        return null;
    }

    /**
     * 20.触发反击
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff counterAtk(BuffData data) {
        if (isWin(data)) {
            return new CounterAtkBuff(data);
        }
        return null;
    }

    /**
     * 21.提升当前技能%伤害
     *
     * @param data
     * @param trigger
     * @return
     */
    private static Buff skillHurtUp(BuffData data) {
        if (isWin(data)) {
            return new SkillHurtUpBuff(data);
        }
        return null;
    }
}
