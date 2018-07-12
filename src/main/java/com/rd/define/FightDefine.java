package com.rd.define;

import com.rd.util.DateUtil;

public class FightDefine {

    /**
     * 战斗结果：失败
     */
    public final static byte FIGHT_RESULT_FAIL = 0;
    /**
     * 战斗结果：成功
     */
    public final static byte FIGHT_RESULT_SUCCESS = 1;
    /**
     * 战斗结果：平局
     */
    public final static byte FIGHT_RESULT_TIE = 2;

    /**
     * 对战更新时间间隔毫秒
     */
    public final static short FIGHT_FRAME_TIME = 100;
    /**
     * 对战超时时间毫秒
     */
    public final static int FIGHT_OVER_TIME = 180000;

    /**
     * 基础系数
     */
    public final static float BASE_RATIO = 1.0f;
    /**
     * 防御系数
     */
    public final static float DEF_RATIO = 1.1f;
    /**
     * 攻击系数
     */
    public final static float ATTACK_RATIO = 0.1f;
    /**
     * 暴击系数
     */
    public final static float CRIT_RATIO = 2.0f;
    /**
     * 招架系数
     */
    public final static float BLOCK_RATIO = 0.5f;

    /**
     * 闪避判定
     */
    public final static byte JUDGE_DODGE = 0;
    /**
     * 暴击判定
     */
    public final static byte JUDGE_CRIT = 1;
    /**
     * 招架判定
     */
    public final static byte JUDGE_BLOCK = 2;

    /**
     * PVP增伤战斗力系数
     */
    public final static short FIGHT_FACTOR_PVP = 100;

    /**
     * 出生地图ID
     */
    public final static byte MAP_BIRTH_ID = 1;
    /**
     * 地图波数：3
     */
    public final static byte MAP_WAVE_NUM = 2;
    /****地图波数  小怪波数 +boss 波数**/
    public final static byte MAP_WAVE_MAX_NUM = 3;

    /**
     * 随机基础值
     **/
    public final static short RANDOM_BASE = 10000;
    /**
     * 随机百分之基础值
     **/
    public final static short RANDOM_HUNDRED_BASE = 100;

    /**
     * 血战可进入次数
     */
    public static final byte BLOODY_BATTLE_ENTER_NUM = 3;

    /**
     * 离线2分钟 开始计算离线战斗
     */
    public static final int OFFLINE_FIGHT_TIME = (int) (2 * DateUtil.MINUTE);
    /**
     * 5秒钟一波怪
     */
    public static final short OFFLINE_FIGHT_WAVE_TIME = (short) (5 * DateUtil.SECOND);
    /**
     * 24小时最大波数
     */
    public static final short OFFLINE_FIGHT_WAVE_24H = 17280;
    /**
     * 8小时最大波数
     */
    public static final short OFFLINE_FIGHT_WAVE_8H = 5760;


    /**
     * 复活时间
     */
    public static final long PLAYER_REVIVE_TIME = 120 * DateUtil.SECOND;
    /**
     * 战斗时间 与客户端配合
     */
    public static final long PVP_BATTLE_TIME = 5 * DateUtil.SECOND;

    private FightDefine() {

    }
}
