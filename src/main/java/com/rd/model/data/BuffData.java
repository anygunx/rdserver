package com.rd.model.data;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月5日下午8:07:07
 */
public class BuffData {

    private short id;

    private byte type;

    /**
     * 触发几率 万分比 0:必触发
     **/
    private short pr;

    /**
     * 效果值
     **/
    private short value;

    /**
     * 持续回合数 0:本回合 1：下一回合 2：下两回合
     **/
    private byte cd;

    /**
     * 0:自身 1：敌人 2:友军全体 3:敌方全体
     **/
    private byte target;

    /**
     * 0:攻击触发 1:受单体攻击触发 2：受全体攻击触发 3:受击触发
     **/
    private byte trigger;

    private short skill;

    public BuffData(short id, byte type, short pr, short value, byte cd, byte target, byte trigger, short skill) {
        this.id = id;
        this.type = type;
        this.pr = pr;
        this.value = value;
        this.cd = cd;
        this.target = target;
        this.trigger = trigger;
        this.skill = skill;
    }

    public short getId() {
        return id;
    }

    public byte getType() {
        return type;
    }

    public short getPr() {
        return pr;
    }

    public short getValue() {
        return value;
    }

    public byte getCd() {
        return cd;
    }

    public byte getTarget() {
        return target;
    }

    public byte getTrigger() {
        return trigger;
    }

    public short getSkill() {
        return skill;
    }

}
