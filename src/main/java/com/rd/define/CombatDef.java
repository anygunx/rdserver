package com.rd.define;

/**
 * @author ---
 * @version 1.0
 * @date 2018年4月19日下午1:54:33
 */
public class CombatDef {

    public final static byte LOSE = 0;

    public final static byte WIN = 1;

    //战斗间隔
    public final static int INTERVAL = 5000;
    //战斗间隔上浮动
    public final static int FLOATING = 5000;

    //被击状态 普通
    public final static byte BEATK_NORMAL = 0;
    //被击状态 闪避
    public final static byte BEATK_DODGE = 1;
    //被击状态 暴击
    public final static byte BEATK_CRIT = 2;

    //怪物
    public final static byte CAMP_MONSTER = 0;
    //己方
    public final static byte CAMP_FRIENDS = 1;
    //敌方
    public final static byte CAMP_ENEMY = 2;
    //下一轮
    public final static byte CAMP_ROUND = 3;
    //结束
    public final static byte CAMP_END = 4;

    //待机
    public final static byte STATE_STAND = 1;
    //准备
    public final static byte STATE_READY = 2;
    //死亡
    public final static byte STATE_DEAD = 3;

    //英雄位置
    public final static byte POS_HERO = 0;
    //仙侣1位置
    public final static byte POS_MATE1 = 2;
    //仙侣2位置
    public final static byte POS_MATE2 = 3;
    //天女位置
    public final static byte POS_FAIRY = 4;
    //宠物位置
    public final static byte POS_PET = 6;

    //boss位置
    public final static byte POS_BOSS = 0;

    //队长位置
    public final static byte TEAM_POS_LEADER = 0;
    //队长宠物位置
    public final static byte TEAM_POS_LEADER_PET = 6;
    //队员1位置
    public final static byte TEAM_POS_MEM1 = 4;
    //队员1宠物位置
    public final static byte TEAM_POS_MEM1_PET = 7;
    //队员2位置
    public final static byte TEAM_POS_MEM2 = 1;
    //队员2宠物位置
    public final static byte TEAM_POS_MEM2_PET = 5;
    //天女1位置
    public final static byte TEAM_POS_FAIRY1 = 3;
    //天女2位置
    public final static byte TEAM_POS_FAIRY2 = 2;
    //组队位置
    public final static byte[][] TEAM_POS = {{TEAM_POS_LEADER, TEAM_POS_LEADER_PET}, {TEAM_POS_MEM1, TEAM_POS_MEM1_PET}, {TEAM_POS_MEM2, TEAM_POS_MEM2_PET}};

    public final static byte ROUND_FIVE = 50;

    /**
     * 天女VIP开启等级
     */
    public final static byte FAIRY_VIP_OP = 7;
    /**
     * 天女开启等级
     */
    public final static byte FAIRY_LV_OP = 100;

    public final static double TEN_THOUSAND = 10000;

    public final static double SERIAL_ATK_INCREASE = 0.05;

    public final static byte NORMAL_ATTACK = 1;

    public final static byte DOUBLE_ATTACK = 2;

    public final static byte NO_COUNTER = 0;

    public final static byte COUNTER_ATTACK = 1;
}
