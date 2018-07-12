package com.rd.define;

public class SkillDefine {

    /**
     * 技能数量
     */
    public final static byte SKILL_NUM = 5;
    /**
     * 技能开启等级
     */
    public final static byte[] SKILL_OPEN_LEVEL = {1, 5, 20, 25, 30};
    /**
     * 技能对应战斗力
     */
    public final static short[] SKILL_FIGHTING = {100, 110, 120, 130, 140};

    /**
     * 心法装备位
     */
    public final static byte HEART_SKILL_SLOT_NUM = 8;

    /**
     * 心法装备开启转生等级
     */
    public final static byte[] HEART_SKILL_SLOT_OPEN_REIN = {1, 2, 3, 4, 5, 6, 7, 8};

    /**
     * 心法装备开启VIP等级
     */
    public final static byte[] HEART_SKILL_SLOT_OPEN_VIP = {2, 2, 3, 4, 5, 6, 7, 8};

    public static int getSkillFighting(int index, short level) {
        if (level > 80) {
            int fighting = SKILL_FIGHTING[index] * 80;
            fighting += SKILL_FIGHTING[index] * 1 * (level - 80);
            return fighting;
        } else {
            return SKILL_FIGHTING[index] * level;
        }
    }

    /**
     * 物理攻击
     */
    public final static byte SKILL_HURT_TYPE_PHY = 0;
    /**
     * 魔法攻击
     */
    public final static byte SKILL_HURT_TYPE_MAGIC = 1;

    /**
     * 0-敌方，1-自己, 2-敌方全体, 3-已方全体
     */
    public final static byte SKILL_TARGET_TYPE_ENEMY = 0;
    public final static byte SKILL_TARGET_TYPE_SELF = 1;
    public final static byte SKILL_TARGET_TYPE_ENEMYALL = 2;
    public final static byte SKILL_TARGET_TYPE_SELFALL = 3;

    //增益类型
    public final static byte SKILL_STATEDIS_TYPE_ADD = 0;
    //减益类型
    public final static byte SKILL_STATEDIS_TYPE_SUB = 1;

    //再生
    public final static byte HEART_SKILL_TYPE_HP = 1;
    //强力
    public final static byte HEART_SKILL_TYPE_AMP = 2;
    //铁壁
    public final static byte HEART_SKILL_TYPE_DR = 3;
    //反击
    public final static byte HEART_SKILL_TYPE_BACK = 4;
    //致命
    public final static byte HEART_SKILL_TYPE_HURT = 5;
    //残废
    public final static byte HEART_SKILL_TYPE_ATTCK = 6;
    //腐蚀
    public final static byte HEART_SKILL_TYPE_DEF = 7;
    //神佑
    public final static byte HEART_SKILL_TYPE_REVIVE = 8;
}
