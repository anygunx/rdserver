package com.rd.define;

public class ErrorDefine {

    /**
     * 无错误
     */
    public static final short ERROR_NONE = -1;
    /**
     * 参数错误
     */
    public static final short ERROR_PARAMETER = 0;
    /**
     * 金币不足
     */
    public static final short ERROR_GOLD_LESS = 1;
    /**
     * 钻石不足
     */
    public static final short ERROR_DIAMOND_LESS = 2;
    /**
     * 物品不足
     */
    public static final short ERROR_GOODS_LESS = 3;
    /**
     * 角色等级不足
     */
    public static final short ERROR_LEVEL_LESS = 4;
    /**
     * VIP等级不足
     */
    public static final short ERROR_VIP_LEVEL_LESS = 5;
    /**
     * 剩余次数不足
     */
    public static final short ERROR_TIMES_LESS = 6;
    /**
     * 已达最高级
     */
    public static final short ERROR_MAX_LEVEL = 7;
    /**
     * 已达最高购买次数
     */
    public static final short ERROR_MAX_BUY = 8;
    /**
     * 装备槽无装备
     */
    public static final short ERROR_EQUIP_EMPTY = 9;
    /**
     * 装备融魂等级达到上限
     */
    public static final short ERROR_EQUIP_RUNHUN_MAX = 10;
    /**
     * 装备强化等级达到上限
     */
    public static final short ERROR_EQUIP_STRENGTH_MAX = 11;
    //神器已经激活了
    public static final short ERROR_GOD_ARTIFACT_ACTIVATED = 12;
    //神器未激活
    public static final short ERROR_GOD_ARTIFACT_UNACTIVATED = 13;
    //未达到神器激活条件
    public static final short ERROR_GOD_ARTIFACT_NOT_ACTIVATE = 14;
    //神器已经到达顶级
    public static final short ERROR_GOD_ARTIFACT_MAX = 15;
    /**
     * 未通关
     */
    public static final short ERROR_NOT_PASS = 16;
    /**
     * 操作成功
     */
    public static final short ERROR_SUCCESS = 17;
    //经脉已经到达顶级
    public static final short ERROR_MERIDIAN_MAX = 18;
    /**
     * 天梯次数不足
     **/
    public static final short ERROR_LADDER_COUNT_LESS = 19;
    /**
     * 天梯次数已达上限
     **/
    public static final short ERROR_LADDER_COUNT_FULL = 20;
    /**
     * 天梯还未到开放时间
     **/
    public static final short ERROR_LADDER_CLOSE = 21;
    /**
     * 天梯榜单正在发生激烈变化，请稍后重试
     **/
    public static final short ERROR_LADDER_TOPLIST = 22;
    /**
     * 天梯今日购买次数已达上限
     **/
    public static final short ERROR_LADDER_BUY_COUNT_FULL = 23;
    /**
     * 未达到天梯开放等级
     **/
    public static final short ERROR_LADDER_LV = 24;
    /**
     * 操作失败
     **/
    public static final short ERROR_OPERATION_FAILED = 25;
    /**
     * 已经在副本中
     **/
    public static final short ERROR_COPY_ALREADY_IN = 26;
    /**
     * 背包空间不足
     **/
    public static final short ERROR_BAG_FULL = 27;
    //邮件已失效
    public static final short ERROR_MAIL_EXPIRED = 28;
    //邮件已领取过
    public static final short ERROR_MAIL_REWARDED = 29;
    /**
     * 禁言中
     */
    public static final short ERROR_STATE_SHUT_UP = 30;
    /**
     * 非法字符
     */
    public static final short ERROR_INVALID_STRING = 31;
    /**
     * 内容过长
     */
    public static final short ERROR_CHAT_LENGTH_LONG = 32;
    //元魂已升到顶级
    public static final short ERROR_SPIRIT_LV_MAX = 33;
    //背包空间不足，快去熔炼
    public static final short ERROR_BAG_FULL_MELT = 34;
    /**
     * 经验不足
     */
    public static final short ERROR_EXP_LESS = 35;
    /**
     * 文字超长
     **/
    public static final short ERROR_STRING_LENGTH_LIMIT = 36;
    /**
     * 名称已存在
     **/
    public static final short ERROR_NAME_DUPLICATE = 37;
    /**
     * 当前阶数不足
     */
    public static final short ERROR_BLESS_LEVEL_LESS = 38;
    //还有正在押运中的镖车
    public static final short ERROR_ESCORT_UNCOMPLETE = 39;
    //押镖次数已达上限
    public static final short ERROR_ESCORT_DISPATCH_MAX = 40;
    //已达最高品质
    public static final short ERROR_ESCORT_QUALITY_MAX = 41;
    //劫鏢次數達到上限
    public static final short ERROR_ESCORT_ROB_MAX = 42;
    //运镖时间未到
    public static final short ERROR_ESCORT_TIME_LESS = 43;
    //BOSS已经被击杀
    public static final short ERROR_BOSS_DEAD = 44;
    //BOSS已经消失
    public static final short ERROR_BOSS_DISAPPEAR = 45;
    //探索BOSS体力不足
    public static final short ERROR_BOSS_EXPLORE_POWER = 46;
    //达到每日攻击BOSS数量上限
    public static final short ERROR_BOSS_FIGHT_MAX = 47;
    //攻击BOSS冷却中
    public static final short ERROR_BOSS_FIGHT_CD = 48;
    //未达到元魂装备的等级
    public static final short ERROR_SPIRIT_LV_LIMIT = 49;
    //没有离线经验
    public static final short ERROR_OFFLINE_EXP_NULL = 50;
    //通关奖励已经领取过
    public static final short ERROR_MAP_REWARDED = 51;
    //通关奖励还不能领取
    public static final short ERROR_MAP_UNREWARD = 52;
    //七日登录奖励不能领取
    public static final short ERROR_SIGN7_UNREWARD = 53;
    //七日活动奖励不能领取
    public static final short ERROR_ACTIVITY7_UNREWARD = 54;
    //七日活动奖励已经领取
    public static final short ERROR_ACTIVITY7_REWARDED = 55;
    //您的等级未达到主线条件
    public static final short ERROR_LEVEL_UN_MAIN_CONDITION = 56;
    //请先通关再继续扫荡
    public static final short ERROR_CANNOT_SWEEP = 57;
    //月卡已过期
    public static final short ERROR_MONTHLYCARD_OUTTIME = 58;
    //月卡已领取过
    public static final short ERROR_MONTHLYCARD_REWARDED = 59;
    //服务器战斗验证失败
    public static final short ERROR_SERVER_FIGHT_FAIL = 60;
    /**
     * 竞技场 未开放
     */
    public static final short ERROR_CHALLENGER_IN_BATTLE = 61;
    /**
     * 竞技场 玩家排行发生变化
     */
    public static final short ERROR_ARENA_CHALLENGER_ALTERED = 62;
    /**
     * 竞技场 没有参战资格
     */
    public static final short ERROR_ARENA_NO_PICK = 63;
    //战斗超时
    public static final short ERROR_BATTLE_TIMEOUT = 64;
    /**
     * 竞技场今日购买次数已达上限
     **/
    public static final short ERROR_ARENA_BUY_COUNT_FULL = 65;
    /**
     * 竞技场今日参战次数已达上限
     **/
    public static final short ERROR_ARENA_FIGHT_COUNT_FULL = 66;
    //未购买上一档的百倍返利
    public static final short ERROR_REBATE_PRE = 67;
    //已购买该档的百倍返利
    public static final short ERROR_REBATE_BUY = 68;
    //已购买过投资计划
    public static final short ERROR_INVEST_BUY = 69;
    //未购买过投资计划
    public static final short ERROR_INVEST_UNBUY = 70;
    //已领取过投资计划
    public static final short ERROR_INVEST_REWARD = 71;
    //还不能领取投资计划
    public static final short ERROR_INVEST_UNFIT = 72;
    //已领取过限时有礼
    public static final short ERROR_TLGIFT_REWARD = 73;
    //还不能领取限时有礼
    public static final short ERROR_TLGIFT_UNFIT = 74;
    /**
     * 帮派申请人数已达上限
     **/
    public static final short ERROR_GANG_APPLICATIONS_FULL = 75;
    /**
     * 帮派已申请，请等待
     **/
    public static final short ERROR_GANG_ALREADY_APPLY = 76;
    /**
     * 帮派成员人数已达上限
     **/
    public static final short ERROR_GANG_MEMBER_FULL = 77;
    /**
     * 您操作太快,请先休息一下
     */
    public static final short ERROR_OPERATION_OVER_QUICK = 78;
    /**
     * 请先加入帮派
     */
    public static final short ERROR_GANG_NONE = 79;
    /**
     * 您没有帮派权限
     */
    public static final short ERROR_GANG_PERMISSION_NONE = 80;
    /**
     * 该玩家已加入其它公会
     */
    public static final short ERROR_GANG_PLAYER_JOINED = 81;
    /**
     * 该职位人员已满
     */
    public static final short ERROR_GANG_POSITION_FULL = 82;
    /**
     * 帮贡不足
     */
    public static final short ERROR_DONATE_LESS = 83;
    /**
     * 公会等级不足
     */
    public static final short ERROR_GANG_LEVEL_LESS = 84;
    /**
     * 今日上香已完成，请明天再来
     */
    public static final short ERROR_GANG_INCENSE_LESS = 85;
    //当前没有奖励可领取
    public static final short ERROR_REWARD_NO = 86;
    /**
     * 本阶段已使用过该物品
     */
    public static final short ERROR_BOX_BLESS_USED = 87;
    /**
     * 本阶段不能使用该物品
     */
    public static final short ERROR_BOX_BLESS_CANT = 88;
    /**
     * 修真：满级
     */
    public static final short ERROR_XIUZHEN_MAX = 89;
    /**
     * 元气不足
     */
    public static final short ERROR_YUANQI_LESS = 90;
    /**
     * 神通领悟未开启
     */
    public static final short ERROR_AVATAR_NOT_OPEN = 91;
    /**
     * 神通背包空间不足
     */
    public static final short ERROR_AVATAR_BAG_FULL = 92;
    /**
     * 找不到该宝石
     */
    public static final short ERROR_JEWEL_NO = 93;
    /**
     * 宝石数量不足
     */
    public static final short ERROR_JEWEL_LESS = 94;
    /**
     * 宝石位置不对
     */
    public static final short ERROR_JEWEL_POS = 95;
    //已达到购买上限
    public static final short ERROR_BUY_MAX = 96;
    //购买次数不足
    public static final short ERROR_BUY_COUNT = 97;
    //已经领取过该奖励
    public static final short ERROR_REWARD_REPEAT = 98;
    //角色转生等级不足
    public static final short ERROR_REIN_LESS = 99;
    //未参加BOSS战斗
    public static final short ERROR_BOSS_CITIZEN_START = 100;
    //BOSS已经死亡
    public static final short ERROR_BOSS_DEADED = 101;
    /**
     * 已达次数上限
     */
    public static final short ERROR_NUM_MAX = 102;
    /**
     * 战斗未复活
     */
    public static final short ERROR_FIGHT_DEAD = 103;
    /**
     * 修为不足
     */
    public static final short ERROR_XIUWEI_LESS = 104;
    /**
     * 宝石等级已达上限
     */
    public static final short ERROR_EQUIP_JEWEL_MAX = 105;
    /**
     * 转生已达上限
     */
    public static final short ERROR_REIN_MAX = 106;
    /**
     * 已经激活该翅膀
     */
    public static final short ERROR_WING_REPEAT = 107;
    /**
     * 还未激活该翅膀
     */
    public static final short ERROR_WING_NO_ACTIVE = 108;
    /**
     * 还未激活该称号
     */
    public static final short ERROR_TITLE_NO_ACTIVE = 109;
    /**
     * 其他角色已佩戴该称号
     */
    public static final short ERROR_TITLE_REPEAT = 110;
    /**
     * 已经复仇过
     */
    public static final short ERROR_ESCORT_REVENGE_REPEAT = 111;
    /**
     * BOSS正在挑战中
     */
    public static final short ERROR_GANG_BOSS_FIGHTING = 112;
    /**
     * BOSS已经死亡
     */
    public static final short ERROR_GANG_BOSS_DEAD = 113;
    /**
     * BOSS战未在开放时间内
     */
    public static final short ERROR_GANG_BOSS_CLOSED = 114;
    /**
     * 开服第三天开启摇钱树
     */
    public static final short ERROR_GOLDTREE_CLOSED = 115;
    /**
     * 好友已达上限
     */
    public static final short ERROR_PLAYER_FRIEND_MAX = 116;
    /**
     * 对方给好友已满
     */
    public static final short ERROR_OTHER_FRIEND_MAX = 117;
    /**
     * 已添加
     */
    public static final short ERROR_FRIEND_ALREADY_EXISTED = 118;
    /**
     * 已被对方拉黑
     */
    public static final short ERROR_OTHER_BLACK_LIST = 119;
    /**
     * 已申请
     */
    public static final short ERROR_FRIEND_ALREADY_APPLY = 120;
    /**
     * 已拉黑
     **/
    public static final short ERROR_IN_BLACK_LIST = 121;
    /**
     * 不是对方好友
     **/
    public static final short ERROR_OTHER_FRIEND_LIMIT = 122;
    /**
     * 不是您的好友
     **/
    public static final short ERROR_PLAYER_FRIEND_LIMIT = 123;
    /**
     * 玩家已离线
     **/
    public static final short ERROR_ONLINE_LIMIT = 124;
    /**
     * 已经激活该武器
     */
    public static final short ERROR_WEAPON_REPEAT = 125;
    /**
     * 还未激活该武器
     */
    public static final short ERROR_WEAPON_NO_ACTIVE = 126;
    /**
     * 未达到充值元宝目标
     */
    public static final short ERROR_CHARGE_NO_ARRIVE = 127;
    /**
     * 已经激活该装备
     */
    public static final short ERROR_ARMOR_REPEAT = 128;
    /**
     * 还未激活该装备
     */
    public static final short ERROR_ARMOR_NO_ACTIVE = 129;
    /**
     * 已经激活该坐骑
     */
    public static final short ERROR_MOUNT_REPEAT = 130;
    /**
     * 还未激活该坐骑
     */
    public static final short ERROR_MOUNT_NO_ACTIVE = 131;
    /**
     * 已达到最高阶
     */
    public static final short ERROR_STAGE_MAX = 132;
    /**
     * 角色成就不足
     */
    public static final short ERROR_ACHIEVEMENT_LESS = 133;
    /**
     * 龙珠碎片不足
     */
    public static final short ERROR_DRAGONBALL_PIECE_LESS = 134;
    /**
     * 战纹槽未激活
     */
    public static final short ERROR_ZHANWEN_NO_ACTIVE = 135;
    /**
     * 战纹已升到顶级
     */
    public static final short ERROR_ZHANWEN_LV_MAX = 136;
    /**
     * 该玩家正在被攻击中
     */
    public static final short ERROR_BE_ATTACK = 137;
    /**
     * 全民boss次数已达上限
     */
    public static final short ALL_BOSS_COUNT_MAX = 138;
    /**
     * 活动未开启
     **/
    public static final short ERROR_INVALID_ACTIVITY = 139;
    /**
     * 传世争霸未开启
     */
    public static final short ERROR_STARCRAFT_UNOPEN = 140;
    /**
     * 目标已死亡
     */
    public static final short ERROR_TARGET_DEAD = 141;
    /**
     * 重复挑战
     */
    public static final short ERROR_ALREADY_ATTACK = 142;
    /**
     * 战功不足
     */
    public static final short ERROR_FEAT_LESS = 143;
    /**
     * 公会战中不能退出
     */
    public static final short ERROR_GANG_FIGHTING_EXIT = 144;
    /**
     * 幸运鉴宝幸运值收取失败
     */
    public static final short ERROR_LUCK_SORCE_RECEIVE = 145;
    /**
     * 每日领取奖品收取失败
     */
    public static final short ERROR_PAY_DAILY_FIRST_RECEIVE = 146;
    /**
     * 节日期间每日充值奖品收取失败
     */
    public static final short ERROR_FEST_PAY_DAILY_RECEIVE = 147;
    /**
     * 鞭炮幸运值奖品收取失败
     */
    public static final short ERROR_FIRECRACKER_LUCK_RECEIVE = 148;
    /**
     * 累计消费奖品已领取
     */
    public static final short ERROR_CONSUM_CUMULATE_RECEIVED = 149;
    /**
     * 累计消费未达标
     */
    public static final short ERROR_CONSUM_CUMULATE_NO_COST = 150;
    /**
     * 今日已激活过
     **/
    public static final short ERROR_ALREADY_ACTIVATION = 151;
    /**
     * 元宝不足
     **/
    public static final short ERROR_DIAMOND_NOT_ENOUGHT = 152;
    /**
     * 大富翁奖励未达标
     */
    public static final short ERROR_MONOPOLY_NO_COST = 151;
    /**
     * 大富翁奖励已领取
     */
    public static final short ERROR_MONOPOLY_RECEIVED = 152;
    /**
     * 坑位已占
     */
    public static final short ERROR_PUZZLE_RECEIVED = 153;
    /**
     * 拼图未完成
     */
    public static final short ERROR_PUZZLE_NOT_FINISH = 154;
    /**
     * 拼图resetNum not Enough
     */
    public static final short ERROR_PUZZLE_NO_RESETNUM = 155;
    /**
     * 大富翁可玩次数不足
     **/
    public static final short ERROR_MONOPOLY_PLAYER_TIME_LESS = 156;
    /**
     * 激活条件不足
     **/
    public static final short ERROR_CONDITION_NOT_ENOUGHT = 157;
    /**
     * 当前已挑战至今日最高关卡
     **/
    public static final short ERROR_CURR_MAX_PASS = 158;
    /**
     * 通关主宰试炼关卡数不足
     **/
    public static final short ERROR_GUANKA_NOT_ENOUGHT = 159;
    /**
     * 秘宝代金券已领取
     **/
    public static final short ERROR_TREASURE_VOUCHERS_RECEIVED = 160;
    /**
     * 秘宝积分不足
     **/
    public static final short ERROR_TREASURE_INTEGRAL_NOT_ENOUGH = 161;
    /**
     * 秘宝代金券不足
     **/
    public static final short ERROR_TREASURE_VOUCHERS_NOT_ENOUGH = 162;
    /**
     * 战力不足
     **/
    public static final short ERROR_FIGHTING_LESS = 163;
    /**
     * 已经激活该光环
     */
    public static final short ERROR_HALO_REPEAT = 164;
    /**
     * 还未激活该光环
     */
    public static final short ERROR_HALO_NO_ACTIVE = 165;
    /**
     * 坐宠已达到满级
     */
    public static final short ERROR_MOUNT_PET_SKILL_IS_FULL = 166;
    /**
     * 宠物技能未激活
     **/
    public static final short ERROR_PET_BABY_INACTIVITED = 167;
    /**
     * 通灵不存在
     **/
    public static final short ERROR_SPIRIT_NOT_EXIT = 168;
    /**
     * 通灵等级不足
     **/
    public static final short ERROR_SPIRIT_LEVEL_LESS = 169;
    /**
     * 未激活
     **/
    public static final short ERROR_NOT_ACTIVE = 170;
    /**
     * 已激活
     **/
    public static final short ERROR__ALREADY_ACTIVATED = 171;
    /**
     * 仙侣最多出战2个
     **/
    public static final short ERROR_MAX_NUM = 172;
    /**
     * 请进行解锁
     */
    public static final short ERROR_UNLOCK = 173;
    /**
     * 没有可扫荡的关卡
     **/
    public static final short ERROR_NOHAVE_GUANQIA = 174;

    /**
     * 今日已经扫荡的关卡
     **/
    public static final short ERROR_PASS_GUANQIA = 175;


    /**
     * 今日已经扫荡的关卡
     **/
    public static final short ERROR_TIAOGUAN_PASS = 176;


    public static final short ERROR_FRIEND_BLACK_LIMITE = 180;


}
