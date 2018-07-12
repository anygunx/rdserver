package com.rd.define;

/**
 * <p>Title: 公会定义</p>
 * <p>Description: 公会定义</p>
 * <p>Company: 北京万游畅想科技有限公司</p>
 *
 * @author ---
 * @version 1.0
 * @data 2016年12月28日 下午4:00:49
 */
public class GangDefine {

    /**
     * 公会职位：帮主
     **/
    public final static byte GANG_POSITION_PRESIDENT = 1;
    /**
     * 公会职位：副帮主
     **/
    public final static byte GANG_POSITION_VICE_PRESIDENT = 2;
    /**
     * 公会职位：长老
     **/
    public final static byte GANG_POSITION_MANAGER = 3;
    /**
     * 公会职位：护法
     **/
    public final static byte GANG_POSITION_CHARGEMAN = 4;
    /**
     * 公会职位：帮众
     **/
    public final static byte GANG_POSITION_MEMBER = 5;

    /**
     * 公会职位人数：副帮主3
     **/
    public final static byte GANG_VICE_PRESIDENT_NUM = 3;
    /**
     * 公会职位人数：长老5
     **/
    public final static byte GANG_MANAGER_NUM = 5;
    /**
     * 公会职位人数：护法10
     **/
    public final static byte GANG_CHARGEMAN_NUM = 10;

    /**
     * 公会名字限制：8个字
     **/
    public final static byte GANG_LIMIT_NAME_LENGTH = 8;
    /**
     * 创建公会消耗绑元
     **/
    public final static short CREATE_GANG_DIAMOND = 1000;
    /**
     * 离开公会惩罚时间
     **/
    public final static int GANG_EXIT_PUNISH_TIME = 86400000;
    /**
     * 申请人数上限
     **/
    public final static byte GANG_APPLY_CAPACITY = 20;
    /**
     * 宣言字数上限
     **/
    public final static byte GANG_DECLARATION_MAX_LENGTH = 10;
    /**
     * 公告字数上限
     **/
    public final static byte GANG_NOTICE_MAX_LENGTH = 30;

    /**
     * 公会状态
     **/
    public final static byte GANG_STATE_NORMAL = 1;
    /**
     * 公会解散
     **/
    public final static byte GANG_STATE_DISBAND = 2;

    /**
     * 公会列表每页数量
     **/
    public final static byte GANG_LIST_PAGE_NUM = 10;

    /**
     * 公会日志数量
     **/
    public final static byte GANG_LOG_NUM = 20;

    /**
     * 公会转盘掉落id
     **/
    public final static byte GANG_TURNTABLE_DROPID = 17;

    /**
     * 每日普通上香次数
     **/
    public final static byte GANG_INCENSE_NUM = 10;

    /**
     * 帮会副本排行榜数量
     **/
    public final static byte GANG_DUNGEON_RANK_NUM = 20;

    /**
     * 公会战参赛人数数量
     */
    public static final byte GANG_FIGHT_MEMBER_NUM = 20;
    /**
     * 公会战目标人数数量
     */
    public static final byte GANG_FIGHT_TARGET_MEMBER_NUM = 5;
    /**
     * 公会战状态:未开战
     */
    public static final byte GANG_FIGHT_STATE_NONE = 0;
    /**
     * 公会战状态:备战中
     */
    public static final byte GANG_FIGHT_STATE_READY = 1;
    /**
     * 公会战状态:战斗中
     */
    public static final byte GANG_FIGHT_STATE_FIGHT = 2;
    /**
     * 公会战状态:显示结果
     */
    public static final byte GANG_FIGHT_STATE_SHOW = 3;

    /**
     * 公会战公会状态:战斗中
     */
    public static final byte GANG_FIGHT_GANG_STATE_FIGHT = 0;
    /**
     * 公会战公会状态:胜利
     */
    public static final byte GANG_FIGHT_GANG_STATE_WIN = 1;
    /**
     * 公会战公会状态:出局
     */
    public static final byte GANG_FIGHT_GANG_STATE_OUT = 2;

    /**
     * 公会战玩家状态:未被打败
     */
    public static final byte GANG_FIGHT_PLAYER_STATE_NONE = 0;
    /**
     * 公会战玩家状态:被打1档
     */
    public static final byte GANG_FIGHT_PLAYER_STATE_ONE = 1;
    /**
     * 公会战玩家状态:被打2档
     */
    public static final byte GANG_FIGHT_PLAYER_STATE_TWO = 2;
    /**
     * 公会战玩家状态:被打3档
     */
    public static final byte GANG_FIGHT_PLAYER_STATE_THREE = 3;

    /**
     * 公会战对战时间毫秒
     */
    public static final int GANG_FIGHT_TIME = 600000;

    /**
     * 公会战攻击次数
     */
    public static final byte GANG_FIGHT_ATTACK_NUM = 3;
    /**
     * 公会战星数
     */
    public static final byte[] GANG_FIGHT_STAR = {0, 1, 3, 6};
    /**
     * 公会战积分
     */
    public static final float[] GANG_FIGHT_SCORE = {0, 1.0f, 1.3f, 1.6f};

    /**
     * 公会战积分系数
     */
    public static final int GANG_FIGHT_SCORE_RATIO = 99999;
    /**
     * 公会战个人排名显示人数
     */
    public static final byte GANG_FIGHT_MEMBER_RANK_NUM = 15;
    /**
     * 公会战公会数
     */
    public static final byte GANG_FIGHT_GANG_NUM = 8;

    /**
     * 传世争霸 开服几天后开启
     */
    public final static byte STARCRAFT_OS_AFTER = 4;        //开服4天后开启
    /**
     * 传世争霸 周几开启
     */
    public final static byte STARCRAFT_WEEK = 7;            //周六开启
    /**
     * 传世争霸 开启时间
     */
    public final static String STARCRAFT_START_TIME = "20:00:00";
    /**
     * 传世争霸 结束时间
     */
    public final static String STARCRAFT_END_TIME = "20:20:00";
    /**
     * 传世争霸 状态 未开启
     */
    public final static byte STARCRAFT_STATE_UNOPEN = 0;
    /**
     * 传世争霸 状态  城门
     */
    public final static byte STARCRAFT_STATE_DOOR = 1;
    /**
     * 传世争霸 状态  混战
     */
    public final static byte STARCRAFT_STATE_FIGHT = 2;
    /**
     * 传世争霸 状态  结束
     */
    public final static byte STARCRAFT_STATE_END = 3;
    /**
     * 传世争霸 区域类型 ：城门
     */
    public final static byte STARCRAFT_AREA_DOOR = 1;
    /**
     * 传世争霸 区域类型 ：城内
     */
    public final static byte STARCRAFT_AREA_INSIDE = 2;
    /**
     * 传世争霸 区域类型 ：殿前
     */
    public final static byte STARCRAFT_AREA_FRONT = 3;
    /**
     * 传世争霸 区域类型 ：皇宫
     */
    public final static byte STARCRAFT_AREA_PALACE = 4;
    /**
     * 传世争霸 城门BOSS血量
     */
    public final static int STARCRAFT_DOOR_HP = 100000000;
    /**
     * 传世争霸 目标最大数量
     */
    public final static byte[] STARCRAFT_TARGET_MAX = {0, 0, 3, 4, 4};
    /**
     * 传世争霸 城门boss积分
     */
    public final static byte STARCRAFT_DOOR_SCORE = 50;
    /**
     * 传世争霸 排行榜刷新时间
     */
    public final static byte STARCRAFT_RANK_REFRESH = 5;
    /**
     * 传世争霸 目标刷新时间
     */
    public final static byte STARCRAFT_TARGET_REFRESH = 10;
    /**
     * 传世争霸 复活时间
     */
    public final static short STARCRAFT_REVIVE_TIME = 10000;
    /**
     * 传世争霸 殿前所需战功
     */
    public final static byte STARCRAFT_AREA_FRONT_NEED_FEAT = 15;
    /**
     * 传世争霸 战斗冷却时间
     */
    public final static short STARCRAFT_FIGHT_DOWNTIME = 5000;
    /**
     * 传世争霸 被击杀积分
     */
    public final static byte STARCRAFT_BEATTACK_SCORE = 6;
    /**
     * 传世争霸 击杀积分
     */
    public final static byte STARCRAFT_ATTACK_SCORE = 12;
    /**
     * 传世争霸 击杀守卫积分
     */
    public final static byte STARCRAFT_ATTACK_GUARD_SCORE = 3;
    /**
     * 传世争霸 击杀战功
     */
    public final static byte STARCRAFT_ATTACK_FEAT = 5;
    /**
     * 传世争霸 击杀守卫战功
     */
    public final static byte STARCRAFT_ATTACK_GUARD_FEAT = 3;
    /**
     * 传世争霸 采旗时间
     */
    public final static int STARCRAFT_COLLECT_TIME = 300000;
}
