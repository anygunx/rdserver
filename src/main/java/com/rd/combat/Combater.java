package com.rd.combat;

import com.rd.combat.buff.Buff;
import com.rd.define.CombatDef;
import com.rd.enumeration.EAttr;
import com.rd.enumeration.EBuff;
import com.rd.enumeration.ETarget;
import com.rd.enumeration.ETrigger;
import com.rd.model.SkillModel;
import com.rd.model.data.BuffData;
import com.rd.model.data.SkillActiveData;
import com.rd.model.data.SkillPassiveData;
import com.rd.net.message.Message;

import java.util.*;

/**
 * @author ---
 * @version 1.0
 * @date 2018年4月18日下午5:28:20
 */
public class Combater implements Comparable<Combater> {

    protected int id;
    //阵营
    protected byte camp;
    //摆放位置
    protected byte pos;
    //属性
    protected int[] attr;
    //生命值
    protected int hp;
    //被击
    protected byte beHit;
    //伤害值
    protected int damage;
    //状态
    protected byte state;
    //主动技能
    protected CombatSkill skill = new CombatSkill();
    //被动技能
    protected List<SkillPassiveData> passiveSkill;
    //buff
    protected Map<Byte, Buff> buffMap = new HashMap<>();
    //攻击数量
    protected byte atkNum;

    protected short down;

    protected CombatEffect effect = new CombatEffect();

    public Combater() {

    }

    public Combater(byte camp, byte pos, int[] attr) {
        this.camp = camp;
        this.pos = pos;
        this.attr = Arrays.copyOf(attr, attr.length);
        this.hp = attr[EAttr.HP.ordinal()];
        this.state = CombatDef.STATE_STAND;
    }

    public void setBeHit(byte beHit) {
        this.beHit = beHit;
        if (beHit == CombatDef.BEATK_DODGE) {
            this.damage = 0;
        }
        this.state = CombatDef.STATE_STAND;
    }

    public int at(int index) {
        return attr[index] + (attr[index] + (int) (effect.getAttrRate()[index] + effect.getAllAttrRate() / CombatDef.TEN_THOUSAND));
    }

    public boolean hurt(int damage) {
        this.damage = damage;
        this.hp -= damage;
        if (this.hp <= 0) {
            return dead();
        }
        return false;
    }

    public void addHp(int hp) {
        this.hp += hp;
    }

    public boolean dead() {
        this.state = CombatDef.STATE_DEAD;
        return true;
    }

    @Override
    public int compareTo(Combater o) {
        if (this.attr[EAttr.LUCK.ordinal()] > o.attr[EAttr.LUCK.ordinal()]) {
            return -1;
        } else if (this.attr[EAttr.LUCK.ordinal()] < o.attr[EAttr.LUCK.ordinal()]) {
            return 1;
        } else {
            return (this.pos < o.pos) ? -1 : ((this.pos == o.pos) ? 0 : 1);
        }
    }

    public void getMessage(Message message) {
        message.setByte(pos);
        message.setShort(id);
        message.setInt(hp);
    }

    public byte getCamp() {
        return camp;
    }

    public byte getPos() {
        return pos;
    }

    public int[] getAttr() {
        return attr;
    }

    public int getHp() {
        return hp;
    }

    public byte getBeHit() {
        return beHit;
    }

    public int getDamage() {
        return damage;
    }

    public byte getState() {
        return state;
    }

    public CombatSkill getSkill() {
        return skill;
    }

    public List<SkillPassiveData> getPassiveSkill() {
        return passiveSkill;
    }

    public Map<Byte, Buff> getBuffMap() {
        return buffMap;
    }

    public byte getAtkNum() {
        return atkNum;
    }

    public void setAtkNum(byte atkNum) {
        this.atkNum = atkNum;
    }

    public short getDown() {
        return down;
    }

    public void setDown(short down) {
        this.down = down;
    }

    public CombatEffect getEffect() {
        return effect;
    }

    public byte getEffectState() {
        return effect.getState();
    }

    public void getHpMessage(Message message) {
        message.setInt(0);
    }

    public Buff launchSkill() {
        Buff buff = null;
        if (skill.getSkillActiveData().getBuff() > 0) {
            BuffData buffData = SkillModel.getBuffMap().get(skill.getSkillActiveData().getBuff());
            buff = EBuff.getBuff(buffData.getType()).getFunc().apply(buffData);
            if (buff != null && buffData.getTarget() == ETarget.SELF.ordinal()) {
                this.buffMap.put(buff.getType(), buff);
            }
        }
        triggerBuff(ETrigger.ATK);
        return buff;
    }

    public void addBuff(Buff buff) {
        this.buffMap.put(buff.getType(), buff);
    }

    public void addRound() {
        Iterator<Buff> it = this.buffMap.values().iterator();
        while (it.hasNext()) {
            Buff buff = it.next();
            buff.addRound();
            if (buff.isInvalid()) {
                it.remove();
            }
        }
        effect.reset();
    }

    public void triggerBuff(ETrigger trigger) {
        for (Buff buff : buffMap.values()) {
            buff.launch(trigger, effect);
        }
    }

    protected SkillActiveData counter(ETrigger trigger) {
        return null;
    }
}
