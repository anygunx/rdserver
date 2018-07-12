package com.rd.combat.buff;

import com.rd.combat.CombatEffect;
import com.rd.enumeration.ETrigger;
import com.rd.model.data.BuffData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月6日下午2:07:23
 */
public class Buff {

    protected BuffData data;

    protected byte type;

    protected byte currRound;

    //持续回合数
    protected byte duration;

    //数值1
    protected double value1;

    //数值2
    protected double value2;

    public Buff() {
    }

    public Buff(BuffData data) {
        this.data = data;
    }

    public Buff(int type, byte duration, double value1) {
        this.type = (byte) type;
        this.duration = duration;
        this.value1 = value1;
    }

    public Buff(int type, byte duration, double value1, double value2) {
        this.type = (byte) type;
        this.duration = duration;
        this.value1 = value1;
        this.value2 = value2;
    }

    public BuffData getData() {
        return data;
    }

    public byte getType() {
        return type;
    }

    public byte getDuration() {
        return duration;
    }

    public double getValue1() {
        return value1;
    }

    public double getValue2() {
        return value2;
    }

    public void launch() {

    }

    public void launchFront(CombatEffect effect) {

    }

    public void launchBack(CombatEffect effect) {

    }

    public void launchBeHit(CombatEffect effect) {

    }

    public void addRound() {
        ++this.currRound;
    }

    public boolean isEffect() {
        if (this.currRound == 0 && this.data.getCd() == 0) {
            return true;
        } else if (this.currRound == 1 && (this.data.getCd() == 1 || this.data.getCd() == 2)) {
            return true;
        } else if (this.currRound == 2 && this.data.getCd() == 2) {
            return true;
        }
        return false;
    }

    public boolean isInvalid() {
        if (this.currRound > 0 && this.data.getCd() == 0) {
            return true;
        } else if (this.currRound > 1 && this.data.getCd() == 1) {
            return true;
        } else if (this.currRound > 2 && this.data.getCd() == 2) {
            return true;
        }
        return false;
    }

    /**
     * 发动
     *
     * @param trigger
     */
    public void launch(ETrigger trigger, CombatEffect effect) {
        if (this.data.getTrigger() == trigger.ordinal() && isEffect()) {
            launchEffect(effect);
        } else if (this.data.getTrigger() == ETrigger.HIT.ordinal() && (trigger == ETrigger.SINGLE_HIT || trigger == ETrigger.GROUP_HIT) && isEffect()) {
            launchEffect(effect);
        }
    }

    protected void launchEffect(CombatEffect effect) {

    }
}
