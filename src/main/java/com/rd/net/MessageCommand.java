package com.rd.net;

public class MessageCommand {

    public static final short GAME_BEAT_MESSAGE = 99;        //游戏心跳

    public static final short GAME_TICK_MESSAGE = 100;

    public static final short LOGIN_SERVER_MESSAGE = 103;    //登录游戏服

    public static final short CREATE_ROLE_MESSAGE = 104;    //创建角色

    public static final short ENTER_GAME_MESSAGE = 105;        //进入游戏

    public static final short PLAYER_MESSAGE = 106;            //玩家消息

    public static final short UPDATE_CURRENCY_MESSAGE = 107;    //更新代币

    public static final short UPDATE_EXP_MESSAGE = 108;            //更新经验

    public static final short NOVICE_GUIDE_UPDATE = 109;        //新手引导

    public static final short TIMEOUT_MESSAGE = 110;        //连接超时

    public static final short UPDATE_BAG_CAPACITY = 111;        //更新背包容量

    public static final short DAY_REFRESH_MESSAGE = 112;        //每日推送更新消息

    public static final short REPEAT_LOGIN_MESSAGE = 114;   //用户重复登录

    public static final short OFFLINE_EXP_MESSAGE = 115;    //离线经验

    public static final short FIELD_BOSS_REWARD = 116;        //野外BOSS通关奖励

    public static final short REWARD_FIELD_BOSS_REWARD = 117;    //领取野外BOSS通关奖励

    public static final short RECEIVE_ONLINE_GIFT = 118;    //领取在线礼包

    public static final short LOGIN_BAN_MESSAGE = 119;   //封号

    public static final short CDKEY_INVOKE_MESSAGE = 120;        //CDKEY兑换领奖

    public static final short MODIFY_HEAD_ICON_MESSAGE = 121;    //修改头像

    public static final short GAME_SEARCH_PLAYER_MESSAGE = 122;    //查询玩家

    public static final short GAME_RANK_LIST_MESSAGE = 130;        //排行榜

    public static final short GAME_FRIEND_DETAIL_MESSAGE = 131;    //好友信息

    public static final short GAME_RANK_SIMPLE_LIST_MESSAGE = 132;    //排行榜。地图关卡.诛仙台

    public static final short MONTHLY_CARD_INFO = 135;        //月卡信息

    public static final short MONTHLY_CARD_REWARD = 136;    //月卡领取

    public static final short GAME_PAY_MESSAGE = 140;        //充值消息

    public static final short WELFARE_INFO_MESSAGE = 141;    //每日福利信息

    public static final short REBATE_BUY_MESSAGE = 142;        //百倍返利购买

    public static final short INVEST_BUY_MESSAGE = 143;        //购买投资计划

    public static final short INVEST_REWARD_MESSAGE = 144;    //领取投资计划

    public static final short DIAL_MESSAGE = 145;            //转盘

    public static final short TLSHOP_BUY_MESSAGE = 146;        //限时商城购买

    public static final short TLGIFT_REWARD_MESSAGE = 147;    //限时有礼领取

    public static final short RANK_REWARD_INFO_MESSAGE = 148;//冲榜奖励消息

    public static final short VIPSHOPFL_BUY_MESSAGE = 149;    //VIP商城福利购买

    public static final short VIPSHOPTL_BUY_MESSAGE = 150;    //VIP商城限时购买

    public static final short CRASH_COW_MESSAGE = 151;        //摇钱树

    public static final short SPRING_WORD_EXCHANGE_MESSAGE = 152;    //新春集字兑换

    public static final short TLHORSE_EXCHANGE_MESSAGE = 153;    //限时坐骑兑换

    public static final short SPRING_SIGN_MESSAGE = 154;    //春节签到

    public static final short TLSHOP_SPRING_BUY_MESSAGE = 155;    //春节商城

    public static final short BUY_ONE_MESSAGE = 157;            //一元抢购

    public static final short SHENTONG_BUY_MESSAGE = 158;        //一折神通

    public static final short SHENTONG_REWARD_MESSAGE = 159;        //一折神通

    public static final short SHOP_PLAYER_INFO_MESSAGE = 160;        //刷新商城信息

    public static final short SHOP_PLAYER_BUY_MESSAGE = 161;        //刷新商城购买

    public static final short SHOP_PLAYER_REFRESH_MESSAGE = 162;    //刷新商城物品

    public static final short LIMITGIFT_VIP_BUY_MESSAGE = 163;        //VIP限制礼包购买
    public static final short LIMITGIFT_LV_BUY_MESSAGE = 164;        //等级限制礼包购买

    public static final short TARGET_REWARD_MESSAGE = 165;            //达标活动领取

    public static final short TARGET_TOP_MESSAGE = 166;                //达标活动排行榜

    public static final short LOGON_ACT_REWARD_MESSAGE = 167;        //登录活动领奖

    public static final short PAY_FEAST_REWARD_MESSAGE = 169;        //充值盛宴领取

    public static final short GOLDTREE_PRODUCE_MESSAGE = 170;        //摇钱树摇钱

    public static final short GOLDTREE_REWARD_MESSAGE = 171;        //摇钱树领奖

    public static final short XUNBAO_RANK_MESSAGE = 172;            //寻宝排行榜

    public static final short WISHING_WELL_MESSAGE = 173;            //许愿池

    public static final short FESTLOGON_REWARD_MESSAGE = 174;        //节日登录

    public static final short FESTLIMITGIFT_BUY_MESSAGE = 175;        //节日限购包

    public static final short FESTWISHING_WELL_MESSAGE = 176;        //节日许愿池

    public static final short FESTTARGET_REWARD_MESSAGE = 177;        //节日达标领取

    public static final short FESTTARGET_RANK_MESSAGE = 178;        //节日达标排行榜

    public static final short FESTPAYTARGET_REWARD_MESSAGE = 179;        //节日充值达标领取

    public static final short FESTPAYTARGET_RANK_MESSAGE = 180;        //节日充值达标排行榜

    public static final short WEEKENDLIMITGIFT_BUY_MESSAGE = 181;        //周末限购包

    public static final short WEEKENDLOGON_REWARD_MESSAGE = 182;        //周末登录

    public static final short WANBALOGON_REWARD_MESSAGE = 183;        //玩吧登录

    public static final short WEEKENDWISHING_WELL_MESSAGE = 184;        //周末转盘

    public static final short FUDAI_MESSAGE = 185;                //福袋

    public static final short PAY_CUMULATE_RECEIVE_MESSAGE = 186; //充值累积奖励领取

    public static final short PAY_COUNT_RECEIVE_MESSAGE = 187; //充值累计奖励领取

    public static final short PAY_CONTINUE_RECEIVE_MESSAGE = 189; //连续充值奖励领取

    public static final short PAY_CUMULATE_FIXED_RECEIVE_MESSAGE = 190; //固定时间充值累计奖励领取

    public static final short INVEST_FUND_RECEIVE_MESSAGE = 191; //投资基金领取

    public static final short SLOT_NEW_MACHINE_MESSAGE = 192; //新拉霸

    public static final short SLOT_MACHINE_MESSAGE = 193; //拉霸

    public static final short LIMIT_LIMIT_LIMIT_MESSAGE = 194; //限时限级限购

    public static final short SEVEN_DAY_MESSAGE = 195; //七日开服活动

    public static final short PLAYER_CHARACTER_UNLOCK = 201; //角色解锁

    public static final short HOLYGOODS_INFO_MESSAGE = 210; //圣物信息

    public static final short HOLYGOODS_UP_MESSAGE = 211; //圣物升级

    public static final short HOLYLINES_EQUIP_MESSAGE = 212;  //装备圣纹

    public static final short HOLYLINES_COMBINE_MESSAGE = 213;  //圣纹合成

    public static final short HOLYLINES_COMBINE_TRANSFORM_MESSAGE = 214;   //圣纹转换

    public static final short AMBIT_INFO_MESSAGE = 220;     //境界信息

    public static final short AMBIT_UP_MESSAGE = 221;    //境界-进阶

    public static final short AMBIT_HALO_ACTIVE_MESSAGE = 222; //境界-光环激活

    public static final short AMBIT_HALO_ENDUE_MESSAGE = 223; //境界-光环幻化

    public static final short AMBIT_SKILL_MESSAGE = 224; //境界-技能

    public static final short GOODS_LIST_MESSAGE = 301;        //物品列表

    public static final short GOODS_NEW_MESSAGE = 302;        //新物品

    public static final short GOODS_USE_MESSAGE = 303;        //使用物品

    public static final short GOODS_WEAR_EQUIP_MESSAGE = 304; //穿装备

    public static final short GOODS_ARTIFACT_EQUIP_MESSAGE = 305; //装备灵器

    public static final short GOODS_PREOPEN_AUCTION_BOX_MESSAGE = 306;    //拍卖宝箱预开启

    public static final short GOODS_USE_AUCTION_BOX_MESSAGE = 307;        //使用拍品宝箱

    public static final short GOODS_REMOVE_AUCTION_BOX_MESSAGE = 308;    //移除拍品宝箱

    public static final short GOODS_LIMIT_LIST_MESSAGE = 316;    //限时物品列表

    public static final short GOODS_LIMIT_ADD_MESSAGE = 317;    //添加限时物品

    public static final short EQUIP_TONGJINGYUDI_MESSAGE = 331;    //升级铜镜玉笛

    public static final short EQUIP_ZUOYOUYAN_MESSAGE = 332;    //升级左右眼

    public static final short DOM_LVUP_MESSAGE = 351;            //主宰升级

    public static final short DOM_RANKUP_MESSAGE = 352;            //主宰升阶

    public static final short DOM_PIECE_MESSAGE = 353;            //主宰分解

    public static final short WING_ACTIVE_MESSAGE = 361;        //激活翅膀

    public static final short WING_SHOW_MESSAGE = 362;            //展示翅膀

    public static final short WEAPON_ACTIVE_MESSAGE = 363;        //激活武器

    public static final short WEAPON_SHOW_MESSAGE = 364;        //展示武器

    public static final short TITLE_INFO_MESSAGE = 365;            //称号信息

    public static final short TITLE_ADORN_MESSAGE = 366;        //佩戴称号

    public static final short ARMOR_ACTIVE_MESSAGE = 367;        //激活装备

    public static final short ARMOR_SHOW_MESSAGE = 368;            //展示装备

    public static final short MOUNT_ACTIVE_MESSAGE = 369;        //激活坐骑

    public static final short MOUNT_SHOW_MESSAGE = 370;            //展示坐骑

    public static final short RED_EXCHANGE_MESSAGE = 371;        //红装碎片兑换

    public static final short ERROR_TIP_MESSAGE = 400;        //错误提示信息

    public static final short SKILL_UP_MESSAGE = 501;        //技能升级

    public static final short SKILL_UP_AUTO_MESSAGE = 502;        //一键技能升级

    public static final short EQUIP_ZHULING_MESSAGE = 550;        //装备注灵、融魂

    public static final short EQUIP_STRENGTH_MESSAGE = 551;        //装备强化

    //public static final short EQUIP_MELTING_HORSE_MESSAGE = 552;		//坐骑装备熔炼

    public static final short EQUIP_ZHUHUN_MESSASGE = 553;        //装备铸魂

    public static final short EQUIP_MELTING_ROLE_MESSAGE = 554;        //人物装备熔炼

    public static final short GONGFA_UPGRADE_MESSAGE = 555;            //功法升级

    public static final short EQUIP_CUILIAN_MESSASGE = 556;        //装备

    public static final short EQUIP_MELTING_ONEKEY_ROLE_MESSAGE = 557;        //人物装备一键熔炼

    public static final short EQUIP_STRENGTH_ONEKEY_MESSAGE = 558;        //人物装备一键强化

    public static final short JEWEL_INLAY_MESSAGE = 560;        //镶嵌宝石

    public static final short JEWEL_MERGE_MESSAGE = 561;        //宝石合成

    public static final short JEWEL_UPGRADE_MESSAGE = 562;        //宝石升级

    public static final short JEWEL_LOTTERY_MESSAGE = 563;        //宝石抽奖

    public static final short JEWEL_LOG_MESSAGE = 564;            //宝石抽奖日志

    public static final short MOUNT_UP_MESSAGE = 601;    //坐骑升阶

    public static final short MAGIC_DETAIL_MESSAGE = 602;    //法宝详细信息

    public static final short MAGIC_LEVEL_UP_MESSAGE = 603;    //法宝升级

    public static final short MAGIC_STAGE_UP_MESSAGE = 604;    //法宝升阶

    public static final short MAGIC_TURNTABLE_MESSAGE = 605; //法宝转盘

    public static final short MOUNT_UP_PILL_MESSAGE = 610;    //坐骑直升丹

    //	public static final short MOUNT_EQUIP_MESSAGE = 620;			//坐骑装备
    public static final short WING_GOD_MESSAGE = 620;    //神羽换装

    public static final short WING_GOD_CRAFT_MESSAGE = 621;        //神羽合成

    public static final short WING_GOD_QUICK_CRAFT_MESSAGE = 622; //神羽快速合成

    public static final short WING_GOD_CONVERSE_MESSAGE = 623;    //神羽转换


    public static final short TRAIN_ITEM_INIT_MESSAGE = 630; //培养项信息

    public static final short TRAIN_ITEM_ACTIVATE_MESSAGE = 631;//培养项激活

    public static final short TRAIN_ITEM_UP = 632; //培养项升级

    public static final short TRAIN_ITEM_QUA_UP = 633; //培养项资质升级

    public static final short PETBABY_PRACTICE = 634; //宠物洗练

    public static final short PET_SPIRIT_MESSAGE = 635;//通灵_兽魂信息

    public static final short PET_SPIRIT_UP = 636;//通灵_兽魂升阶

    public static final short SPIRIT_SKILL_LV_UP = 637;//通灵_兽魂升级

    public static final short SPIRIT_EQUIP = 638;//通灵_兽魂装备

    public static final short SPIRIT_PRO_LEVEL_UP = 639;//通灵_兽魂属性丹升级

    public static final short PET_SHOW_UP = 640;//宠物出站

    public static final short PET_CHANGE_NICKNAME = 641;//宠物修改名称

    public static final short FAIRY_COM_SKILL_UP = 642;//仙侣技能升级

    public static final short QI_YUAN_MESSAGE = 643;//仙侣奇缘信息

//	public static final short TRAIN_ITEM_PILL = 635; //培养项直升丹
//	
//	public static final short TRAIN_ITEM_EQUIP_CRAFT = 636; //坐宠装备合成
//	
//	public static final short TRAIN_ITEM_EQUIP_CONVERSE = 637; //坐宠装备转换


    public static final short GOD_ARTIFACT_INFO_MESSAGE = 650;    //神器列表

    public static final short GOD_ARTIFACT_ACTIVE_MESSAGE = 651;    //神器激活

    public static final short GOD_ARTIFACT_UPGRADE_MESSAGE = 652;    //神器升级

    public static final short HUANHUA_INVOKE_MESSAGE = 653;    //幻化激活

    public static final short HUANHUA_APPEARANCE_CHANGE_MESSAGE = 654;    //幻化外形变更

    public static final short MERIDIAN_UPGRADE_MESSAGE = 660;        //经脉升级

    public static final short REIN_UPGRADE_MESSAGE = 670;            //转生

    public static final short REIN_EXCHANGE_MESSAGE = 671;            //转生兑换

    public static final short SPIRIT_UPGRADE_MESSAGE = 680;            //元神升级

    public static final short SPIRIT_ACTIVE_MESSAGE = 681;            //元神装备

    public static final short SPIRIT_REWARD_MESSAGE = 683;            //元神获取

    public static final short SPIRIT_RES_MESSAGE = 684;                //元神分解

    public static final short SPIRIT_AUTO_ACTIVE = 685;                //元神一键装备

    public static final short LADDER_DETAIL_MESSAGE = 700;        //天梯详情

    public static final short LADDER_MATCH_MESSAGE = 701;        //天梯匹配对手

    public static final short LADDER_RESULT_MESSAGE = 702;        //天梯战斗结果

    public static final short LADDER_BUY_COUNT = 703;            //天梯战斗次数购买

    public static final short LADDER_TOP_LIST = 704;            //天梯排行榜

    public static final short LADDER_HISTORY = 705;            //天梯历史战绩

    public static final short ESCORT_REVENGE_INFO = 708;        //渡劫复仇信息

    public static final short ESCORT_REVENGE_RESULT = 709;        //渡劫复仇结果

    public static final short ESCORT_DETAIL_MESSAGE = 710;        //押镖数据信息

    public static final short ESCORT_DISPATCH_MESSAGE = 711;    //镖车押运

    public static final short ESCORT_ROB_START_MESSAGE = 712;            //镖车劫杀

    public static final short ESCORT_REFRESH_QUALITY_MESSAGE = 713;    //镖车刷星

    public static final short ESCORT_ROBLIST_MESSAGE = 714;                //劫镖车队列表

    public static final short ESCORT_ROB_RESULT_MESSAGE = 715;            //镖车劫杀结果

    public static final short ESCORT_COMPLETE = 716;                //镖车完成

    public static final short ESCORT_LOGS = 717;                //运镖日志

    public static final short ESCORT_LOGS_READ = 718;            //运镖日志是否阅读

    public static final short ESCORT_REWARD_MESSAGE = 719;        //渡劫领取奖励

    public static final short ORANGE_MIX_MESSAGE = 720;            //橙装合成

    public static final short ORANGE_UPGRADE_MESSAGE = 721;        //橙装升级

    public static final short ORANGE_RES_MESSAGE = 722;            //橙装分解

    public static final short RED_MIX_MESSAGE = 723;            //红装合成

    public static final short RED_UPGRADE_MESSAGE = 724;        //红装升级

    public static final short RED_RES_MESSAGE = 725;            //红装分解

    public static final short RED_LOTTERY_MESSAGE = 726;            //寻宝

    public static final short RED_LOG_MESSAGE = 727;            //寻宝日志

    public static final short RED_PICKUP_MESSAGE = 728;            //提取装备

    public static final short RED_BAG_MESSAGE = 729;            //红装仓库

    public static final short BOSS_CITIZEN_INFO_MESSAGE = 730;        //全民BOSS信息

    public static final short BOSS_CITIZEN_START_MESSAGE = 731;        //全民BOSS战斗开始

    public static final short BOSS_CITIZEN_QUIT_MESSAGE = 732;        //全民BOSS战斗退出

    public static final short BOSS_CITIZEN_REWARD_MESSAGE = 733;    //全民BOSS奖励

    public static final short BOSS_CITIZEN_FIGHT_MESSAGE = 734;        //全民BOSS战斗

    public static final short BOSS_CITIZEN_TOP_MESSAGE = 735;        //全民BOSS排行榜

    public static final short BOSS_CITIZEN_REVIVE_MESSAGE = 736;    //全民BOSS复活

    public static final short BOSS_CITIZEN_CUE_MESSAGE = 737;        //全民BOSS提醒设置

    public static final short BOSS_CITIZEN_APPEAR_MESSAGE = 738;    //全民BOSS外形数据

    public static final short BOSS_REIN_INFO_MESSAGE = 740;            //转生BOSS信息

    public static final short BOSS_REIN_START_MESSAGE = 741;        //转生BOSS战斗开始

    public static final short BOSS_REIN_FIGHT_MESSAGE = 742;        //转生BOSS战斗

    public static final short BOSS_REIN_TARGET_MESSAGE = 743;        //转生BOSS当前目标

    public static final short BOSS_REIN_TOP_MESSAGE = 744;            //转生BOSS排行榜

    public static final short BOSS_REIN_APPEAR_MESSAGE = 745;        //转生BOSS外形数据

    public static final short BOSS_REIN_REVIVE_MESSAGE = 746;        //转生BOSS复活

    public static final short BOSS_REIN_HISTORY_MESSAGE = 747;        //转生BOSS上次排行榜

    public static final short BOSS_REIN_NOTICE_MESSAGE = 748;        //转生BOSS开放提醒

    public static final short BOSS_REIN_QUIT_MESSAGE = 749;            //转生BOSS战退出

    public static final short BOSS_LIST_MESSAGE = 750;            //BOSS列表

    public static final short BOSS_TOP_MESSAGE = 751;            //BOSS排行榜

    public static final short BOSS_BAT_START_MESSAGE = 752;        //BOSS参战

    public static final short BOSS_BAT_RESULT_MESSAGE = 753;    //BOSS战斗结果

    public static final short BOSS_CITIZEN_PK_MESSAGE = 755;        //全民BOSS PK第一名

    public static final short BOSS_CITIZEN_PLAYER_REVIVE_MESSAGE = 756;        //全民BOSS玩家复活

    public static final short SHOP_INFO_MESSAGE = 800;        //商城详情

    public static final short SHOP_BUY_MESSAGE = 801;        //商城购买

    public static final short SHOP_REFRESH_MESSAGE = 802;    //商城刷新

    public static final short MAIL_LIST_MESSAGE = 810;        //邮件列表

    public static final short MAIL_READ_MESSAGE = 811;        //读邮件

    public static final short MAIL_REWARD_SINGLE_MESSAGE = 812;        //领取附件--单封邮件

    public static final short MAIL_REWARD_ALL_MESSAGE = 813;        //领取附件--所有邮件

    public static final short MAIL_ADD_MESSAGE = 814;        //增加邮件

    public static final short CHAT_LIST_MESSAGE = 820;        //聊天消息列表

    public static final short CHAT_MESSAGE = 821;                //聊天

    public static final short BROADCAST_MESSAGE = 822;            //系统广播

    public static final short BROADCAST_TZZP_MESSAGE = 823;        //投资转盘广播

    public static final short BROADCAST_PET_MESSAGE = 824;        //宠物抽奖广播

    public static final short TOPRANK_WORSHIP_LIST_MESSAGE = 830;        //排行榜已膜拜列表

    public static final short TOPRANK_WORSHIP_MESSAGE = 831;        //排行榜膜拜

    public static final short NIGHT_FIGHT_JOIN_MESSAGE = 840;        //夜战参加

    public static final short NIGHT_FIGHT_ATTACK_MESSAGE = 841;        //夜战攻击目标

    public static final short NIGHT_FIGHT_TARGET_MESSAGE = 842;        //夜战阵营更新

    public static final short NIGHT_FIGHT_BROADCAST_INTO_MESSAGE = 843;    //夜战进场

    public static final short NIGHT_FIGHT_BROADCAST_EXIT_MESSAGE = 844;    //夜战出场

    public static final short NIGHT_FIGHT_EXIT_MESSAGE = 845;    //夜战退出

    public static final short NIGHT_FIGHT_FIGHT_RESULT_MESSAGE = 846;  //夜战战斗结果

    public static final short NIGHT_FIGHT_EXCHANGE_MESSAGE = 847;  //夜战兑换战绩

    public static final short NIGHT_FIGHT_RANK_MESSAGE = 848;  //夜战排行榜更新

    public static final short NIGHT_FIGHT_MONSTER_MESSAGE = 849;  //夜战攻击怪物

    public static final short NIGHT_FIGHT_REWARD_MESSAGE = 850;  //夜战结束发放奖励

    public static final short NIGHT_FIGHT_REVIVE_MESSAGE = 851;  //夜战复活

    public static final short NIGHT_FIGHT_COUNTDOWN_MESSAGE = 852;  //夜战倒计时

    public static final short HEART_SKILL_DATA_MESSAGE = 860;  //心法数据

    public static final short HEART_SKILL_LEARN_MESSAGE = 861;  //心法学习

    public static final short HEART_SKILL_UP_MESSAGE = 863;  //心法升级

    public static final short HEART_SKILL_RM_MESSAGE = 864;  //心法拆卸

    public static final short HEART_SKILL_COMBINE_MESSAGE = 865;  //心法合成

    public static final short COMBINE_RUNE_BAG_MESSAGE = 870;  //合击符文背包

    public static final short COMBINE_RUNE_BAG_UPDATE_MESSAGE = 871;  //合击符文背包变化

    public static final short COMBINE_RUNE_EQUIP_MESSAGE = 872;  //合击符文装备

    public static final short COMBINE_RUNE_DECOMPOSE_MESSAGE = 873;  //合击符文分解

    public static final short COMBINE_RUNE_COMPOSE_MESSAGE = 874;  //合击符文合成

    public static final short MISSION_CHAIN_UPDATE_MESSAGE = 901; //支线任务更新

    public static final short MISSION_CHAIN_RECEIVE_MESSAGE = 902; //支线任务领取

    public static final short MISSION_DAILY_LIST_MESSAGE = 903; //日常任务列表

    public static final short MISSION_DAILY_UPDATE_MESSAGE = 904; //日常任务更新

    public static final short FIVE_ELEMENTS_ACTIVE_MESSAGE = 910; //五行激活

    public static final short FIVE_ELEMENTS_LIST_MESSAGE = 911; //五行玩法列表

    public static final short FIVE_ELEMENTS_UPGRADE_MESSAGE = 912; //五行玩法升级

    public static final short FIVE_ELEMENTS_ACTIVATE_MESSAGE = 913; //五行激活信息

    public static final short LINGZHEN_LIST_MESSAGE = 914; //灵阵信息

    public static final short LINGZHEN_UPGRADE_MESSAGE = 915; //灵阵升级

    public static final short FIVE_ELEMENTS_FUSE_MESSAGE = 916; //五行融合

    public static final short FIVE_ELEMENTS_COPY_MESSAGE = 917; //五行副本信息

    public static final short FIVE_ELEMENTS_COPY_CHALLENGE_MESSAGE = 918; //五行请求挑战副本

    public static final short FIVE_ELEMENTS_THE_AWARD_MESSAGE = 919; //五行副本领奖

    public static final short FIVE_ELEMENTS_ISFUSE_MESSAGE = 920;  //五行活动是否完成

    public static final short FIVE_ELEMENTS_FIGHT_DUNGEON_RESULT_MESSAGE = 9200; //五行副本战斗结果

    public static final short FIVE_ELEMENTS_ACTIVITY_MESSAGE = 9201; //五行副本活动信息

    public static final short FIVE_ELEMENTS_ACTIVITY_CHALLENGE_MESSAGE = 9202; //五行副本活动请求挑战

    public static final short FIVE_ELEMENTS_ACTIVITY_FIGHT_DUNGEON_RESULT_MESSAGE = 9203; //五行副本活动战斗结果

    public static final short SHARE_INFO_MESSAGE = 921; //分享信息

    public static final short SHARE_COMPLETE_MESSAGE = 922; //分享完成

    public static final short SHARE_REWARD_MESSAGE = 923; //分享奖励

    public static final short DRAGON_BALL_LEVELUP = 930;    // 龙珠升级

    public static final short DRAGON_BALL_RECEIVE_PIECE = 931;    // 龙珠碎片领取

    public static final short DRAGON_BALL_GET_MISSIONS_MESSAGE = 940;    //获取每日任务信息

    public static final short DRAGON_BALL_UPDATE_MISSION_MESSAGE = 941;    //每日任务更新

    public static final short DRAGON_BALL_RECEIVE_BOX_MESSAGE = 942;    // 龙珠宝箱领取

    public static final short MEDAL_LEVEL_UP_MESSAGE = 950; //勋章升级

    public static final short ACHIEVEMENT_GET_MISSIONS_MESSAGE = 960; //获取成就列表

    public static final short ACHIEVEMENT_UPDATE_MISSION_MESSAGE = 961; //更新成就信息

    public static final short ACHIEVEMENT_RECEIVE_MESSAGE = 962; // 成就任务领取

    public static final short ARTIFACT_BOSS_UPDATE_MESSAGE = 970; //神器信息更新

    public static final short ARTIFACT_PIECE_INVOKE_MESSAGE = 971; //神器碎片激活

    public static final short ARTIFACT_BOSS_INVOKE_MESSAGE = 972; //关卡神器激活

    public static final short TLMISSION_UPDATE_MESSAGE = 980; //限时任务刷新

    public static final short TLMISSION_RECEIVE_MESSAGE = 981; //限时任务领取

    public static final short TLMISSION_COMPLETE_MESSAGE = 982; //限时任务完成

    public static final short CARD_MISSION_MESSAGE = 990; //卡牌限时任务列表

    public static final short CARD_MISSION_UPDATE_MESSAGE = 991; //卡牌任务更新

    public static final short CARD_MISSION_RECEIVE_MESSAGE = 992; //卡牌任务领取奖励

    public static final short GAME_ACTIVITY_MESSAGE = 1000;            //活动大厅

    public static final short GAME_ACTIVITY_NEW_MESSAGE = 1001;        //新活动大厅

    public static final short GAME_ACTIVITY_NEW_UPDATE_MESSAGE = 1002;    //新活动大厅数据更新

    public static final short GAME_7DAY_MESSAGE = 1003;                //7日活动数据

    public static final short GAME_TURNTABLE_MESSAGE = 1004; //元宝王者转盘

    public static final short GAME_TURNTABLE_PAY_MESSAGE = 1005; //元宝王者充值

    public static final short GAME_KAM_PO_MESSAGE = 1006; //鉴宝

    public static final short GAME_LUCK_SCORE_MESSAGE = 1007; //鉴宝幸运值领取

    public static final short GAME_INIT_LUCK_SCORE_MESSAGE = 1008; //鉴宝幸运值领取

    public static final short LIMIT_GIFT_DAILY_BUY_MESSAGE = 1009;//节日期间每日限购

    public static final short FEST_PAY_DAILY_FIRST_MESSAGE = 1010; //节日期间每日首冲

    public static final short GAME_PAY_DAILY_FIRST_MESSAGE = 1011; //获取节日期间每日首冲信息

    public static final short GAME_FEST_PAY_CUMULATE_MESSAGE = 1012; //节日期间每日充值奖品领取

    public static final short GAME_FIRECRACKER_MESSAGE = 1013; //幸运鞭炮信息

    public static final short GAME_FEST_PAY_CON_MESSAGE = 1014; //获取节日期间每日充值

    public static final short GAME_FIRECRACKER_LUCK_SCORE_MESSAGE = 1015; //鞭炮值领取

    public static final short GAME_FIRECRACKER_MESSAGE_PROCESS = 1016; //鞭炮抽取

    public static final short GAME_CONSUM_CUMULATE_FIXED_MESSAGE = 1017;// 固定时间累计消费

    public static final short GAME_CONSUM_CUMULATE_FIXED_RECEIVE = 1018;// 固定时间累计消费

    public static final short GAME_MONOPOLY_MESSEAGE = 1019;// 大富翁信息

    public static final short TURN_TABLE_LOG = 1020;// 元宝王者日志

    public static final short GAME_MONOPOLY_RECEIVE = 1021; //大富翁奖励领取

    public static final short GAME_MONOPOLY_DICE = 1022; //大富翁掷骰子

    public static final short GAME_PUZZLE_DICE = 1023; //获取一张拼图

    public static final short GAME_PUZZLE_RECEIVE = 1024; //拼图奖励领取

    public static final short GAME_NEW_YEAR_RECEIVE = 1025; //新年登录奖励领取

    public static final short GAME_KAM_PO2_MESSAGE = 1026; //鉴宝2信息

    public static final short GAME_LUCK_SCORE2_MESSAGE = 1027; //鉴宝幸运值2信息

    public static final short GAME_INIT_LUCK_SCORE2_MESSAGE = 1028; //鉴宝幸运值2初始化

    public static final short GAME_NO_REPEAT_TURNTABLE_DICE = 1029; //消除转盘

    public static final short GAME_NO_REPEAT_TURNTABLE_REFRESH = 1030; //消除转盘免费刷新

    public static final short GAME_KAM_PO3_MESSAGE = 1031; //鉴宝3信息

    public static final short GAME_TREASURES_BUY = 1032; //秘宝_购买

    public static final short GAME_TREASURES_REFRESH = 1033; //秘宝_刷新

    public static final short GAME_TREASURES_REFRESH_TIME = 1034; //秘宝_刷新时间

    public static final short GAME_TREASURES_VOUCHERS_RECEIVE = 1035; //秘宝_代金券领取

    public static final short GAME_TREASURES_BUY_RECORD = 1036; //秘宝购买记录

    public static final short GAME_SET_WORDS_MESSAGE = 1037; //集字兑换

    public static final short GAME_ITEM_CALLBACK_MESSAGE = 1038; //道具回收

    public static final short GAME_MONOPOLY1_DICE = 1039; //探宝2抽取

    public static final short GAME_MONOPOLY1_RECEIVE = 1040; //探宝2领取

    public static final short GAME_MONOPOLY1_EXCHANGE = 1041; //探宝2兑换

    public static final short GAME_TARGET_DAILY_CONSUME_MESSAGE = 1042; //达标累计消费信息

    public static final short GANG_INFO_MESSAGE = 1101; //公会信息

    public static final short GANG_LIST_MESSAGE = 1102; //公会列表

    public static final short GANG_CREATE_MESSAGE = 1103; //公会创建

    public static final short GANG_SEARCH_MESSAGE = 1104; //公会搜索

    public static final short GANG_DECLARATION_MODIFY_MESSAGE = 1105; //公会宣言修改

    public static final short GANG_NOTICE_MODIFY_MESSAGE = 1106; //公会公告修改

    public static final short GANG_LIMIT_LEVEL_MESSAGE = 1107; // 公会限制转生等级

    public static final short GANG_AUTO_ADOPT_MESSAGE = 1108; // 公会自动审核

    public static final short GANG_MEMBER_LIST_MESSAGE = 1109; // 公会会员列表

    public static final short GANG_APPLY_MESSAGE = 1110; //公会申请

    public static final short GANG_APPOINT_MESSAGE = 1111; //公会任命

    public static final short GANG_DISMISS_MESSAGE = 1112; //公会踢人

    public static final short GANG_OVER_MESSAGE = 1113; //公会解散

    public static final short GANG_EXIT_MESSAGE = 1114;  //公会退出

    public static final short GANG_APPLY_LIST_MESSAGE = 1115;  //公会申请列表

    public static final short GANG_ADOPT_MESSAGE = 1116;  //公会审核

    public static final short GANG_DONATE_MESSAGE = 1117;  //公会捐献

    public static final short GANG_SKILL_LEVELUP = 1118; //公会技能学习

    public static final short GANG_DIAL_MESSAGE = 1119; //公会转盘

    public static final short GANG_SKILL_LIST_MESSAGE = 1120; //公会技能列表

    public static final short GANG_LOG_MESSAGE = 1121; //公会日志

    public static final short GANG_SKILL2_LEVELUP = 1122; //公会技能2学习

    public static final short GANG_TURNTABLE_LOG = 1123; //帮会转盘日志

    public static final short GANG_MISSION_LIST = 1124; //帮会任务列表

    public static final short GANG_MISSION_UPDATE = 1125; //帮会任务更新

    public static final short GANG_DUNGEON_PASS_RECEIVE_MESSAGE = 1126; //帮会副本每日通关奖励

    public static final short GANG_DUNGEON_RANK_MESSAGE = 1127; //帮会副本排行榜

    public static final short GANG_DUNGEON_CHEER_MESSAGE = 1128; //帮会副本助威

    public static final short GANG_IMPEACHMENT_MESSAGE = 1129;    //帮会弹劾帮主

    public static final short GANG_BOSS_INFO_MESSAGE = 1131;    //公会BOSS界面信息

    public static final short GANG_BOSS_LIST_MESSAGE = 1132;    //公会BOSS列表

    public static final short GANG_BOSS_START_MESSAGE = 1133;    //公会BOSS战斗开始

    public static final short GANG_BOSS_RESULT_MESSAGE = 1134;    //公会BOSS战斗结束

    public static final short GANG_FIGHT_INFO_MESSAGE = 1140;    //帮会战信息

    public static final short GANG_FIGHT_JOIN_INFO_MESSAGE = 1141;    //帮会战参战信息

    public static final short GANG_FIGHT_REQUEST_MESSAGE = 1142;    //帮会战战斗请求

    public static final short GANG_FIGHT_RESULT_MESSAGE = 1143;    //帮会战战斗结果

    public static final short GANG_FIGHT_RANK_MESSAGE = 1144;    //帮会战排名

    public static final short GANG_FIGHT_REWARD_ASSIGN_MESSAGE = 1145;    //帮会战奖励分配

    public static final short GANG_FIGHT_STORE_MESSAGE = 1146;    //帮会战仓库

    public static final short GANG_FIGHT_MEMBER_RANK_MESSAGE = 1147;    //帮会战个人排名

    public static final short GANG_FIGHT_JOIN_REFRESH_MESSAGE = 1148;    //帮会战参战刷新

    public static final short GANG_FIGHT_JOIN_MESSAGE = 1149;    //帮会战参战

    public static final short GANG_SHOP_BUY_INIT_MESSAGE = 1150; // 帮会商店购买信息

    public static final short GANG_SHOP_BUY_MESSAGE = 1151; // 帮会商店购买信息

    public static final short RELATIONSHIP_GET_LIST_MESSAGE = 1301;    //玩家关系列表

    public static final short RELATIONSHIP_APPLY_MESSAGE = 1302;    //玩家关系申请

    public static final short RELATIONSHIP_PROCESS_APPLICATION_MESSAGE = 1303;//处理管理申请

    public static final short RELATIONSHIP_PROCESS_BLACK_MESSAGE = 1304;//处理拉黑请求

    public static final short RELATIONSHIP_REMOVE_MESSAGE = 1305;    //移除关系请求

    public static final short RELATIONSHIP_NEW_APPLICATION_MESSAGE = 1306;    //好友申请提醒

    public static final short AUCTION_GET_ITEMS = 1401;    //获取拍品数据

    public static final short AUCTION_ADD_PRICE = 1402;    //拍品竞价

    public static final short AUCTION_GET_LOGS = 1403;    //获取拍卖日志

    public static final short AUCTION_UPDATE_SUBSCRIPTIONS = 1404;    //更新关注

    public static final short AUCTION_NEW_SUBSCRIPTION = 1405;    //关注拍品上架提示

    public static final short AUCTION_GET_INCOME = 1406;    //获取收益信息

    public static final short SHENBING_UPSTAR_MESSAGE = 1500; //神兵升星

    public static final short FIGHT_REQUEST_MESSAGE = 2001;    //请求战斗

    public static final short FIGHT_RESULT_MESSAGE = 2002;    //战斗结果

    public static final short FIGHT_DUNGEON_REQUEST_MESSAGE = 2003;    //请求副本战斗

    public static final short FIGHT_DUNGEON_RESULT_MESSAGE = 2004;    //副本战斗结果

    public static final short DUNGEON_STATE_MESSAGE = 2005;    //请求副本状态

    public static final short DUNGEON_BUY_MESSAGE = 2006;    //购买副本次数

    public static final short DUNGEON_SWEEP_MESSAGE = 2007;    //扫荡副本

    public static final short DUNGEON_MATERIAL_ONEKEY_MESSAGE = 2008;    //一键扫荡材料副本

    public static final short DUNGEON_PERSONALBOSS_ONEKEY_MESSAGE = 2009;    //一键扫荡个人boss

    public static final short FIGHT_FIELD_MONSTER_REQUEST_MESSAGE = 2011;    //请求野外小怪战斗

    public static final short FIGHT_FIELD_MONSTER_RESULT_MESSAGE = 2012;    //野外战斗小怪结果

    public static final short FIGHT_FIELD_BOSS_REQUEST_MESSAGE = 2013;    //请求野外boss战斗

    public static final short FIGHT_FIELD_BOSS_RESULT_MESSAGE = 2014;    //野外boss战斗结果

    public static final short DUNGEON_HOLY_STATE_MESSAGE = 2020;            //圣物副本状态

    public static final short DUNGEON_HOLY_FIGHT_REQUEST_MESSAGE = 2021;    //请求圣物副本战斗

    public static final short DUNGEON_HOLY_FIGHT_PASS_MESSAGE = 2022;        //圣物副本通关

    public static final short DUNGEON_HOLY_FIGHT_RESULT_MESSAGE = 2023;        //圣物副本战斗结束

    public static final short DUNGEON_HOLY_RECEIVE_MESSAGE = 2024;            //圣物副本领取宝箱

    public static final short CROSS_ARENA_CHALLENGE_INFO_MESSAGE = 2101;    //竞技场挑战数据

    public static final short CROSS_ARENA_CHALLENGE_REFRESH_MESSAGE = 2102;    //竞技场挑战刷新

    public static final short CROSS_ARENA_BATTLE_MESSAGE = 2103; //竞技场战斗请求

    public static final short CROSS_ARENA_RANK_LIST_MESSAGE = 2104; //竞技场排行

    public static final short CROSS_ARENA_BATTLE_RECORD_MESSAGE = 2105; //竞技场战斗记录

    public static final short CROSS_ARENA_BATTLE_COUNT_BUY = 2106;    //竞技场次数购买

    public static final short GUANJIE_RANK_MESSAGE = 2201;    //官阶等级信息

    public static final short ZHANWEN_ACTIVE_MESSAGE = 2202;    //战纹装备

    public static final short ZHANWEN_UPGRADE_MESSAGE = 2203;    //战纹升级

    public static final short ZHANWEN_RES_MESSAGE = 2204;    //战纹分解

    public static final short ZHANWEN_REWARD_MESSAGE = 2205;    //战纹获取

    public static final short BOSS_MYSTERY_INFO_MESSAGE = 2206;        //秘境BOSS信息

    public static final short BOSS_MYSTERY_START_MESSAGE = 2207;        //秘境BOSS战斗开始

    public static final short BOSS_MYSTERY_QUIT_MESSAGE = 2208;        //秘境BOSS战斗退出

    public static final short BOSS_MYSTERY_REWARD_MESSAGE = 2209;    //秘境BOSS奖励

    public static final short BOSS_MYSTERY_FIGHT_MESSAGE = 2210;        //秘境BOSS战斗

    public static final short BOSS_MYSTERY_TOP_MESSAGE = 2211;        //秘境BOSS排行榜

    public static final short BOSS_MYSTERY_REVIVE_MESSAGE = 2212;    //秘境BOSS复活

    public static final short BOSS_MYSTERY_CUE_MESSAGE = 2213;        //秘境BOSS提醒设置

    public static final short BOSS_MYSTERY_APPEAR_MESSAGE = 2214;    //秘境BOSS外形数据

    public static final short BOSS_MYSTERY_PK_MESSAGE = 2215;       //秘境BOSS PK第一名

    public static final short BOSS_MYSTERY_PLAYER_REVIVE_MESSAGE = 2216; //秘境BOSS玩家复活

    public static final short BOSS_VIP_INFO_MESSAGE = 2217;        //BOSS之家信息

    public static final short BOSS_VIP_START_MESSAGE = 2218;        //BOSS之家战斗开始

    public static final short BOSS_VIP_QUIT_MESSAGE = 2219;        //BOSS之家战斗退出

    public static final short BOSS_VIP_REWARD_MESSAGE = 2220;    //BOSS之家奖励

    public static final short BOSS_VIP_FIGHT_MESSAGE = 2221;        //BOSS之家战斗

    public static final short BOSS_VIP_TOP_MESSAGE = 2222;        //BOSS之家排行榜

    public static final short BOSS_VIP_REVIVE_MESSAGE = 2223;    //BOSS之家复活

    public static final short BOSS_VIP_CUE_MESSAGE = 2224;        //BOSS之家提醒设置

    public static final short BOSS_VIP_APPEAR_MESSAGE = 2225;    //BOSS之家外形数据

    public static final short BOSS_VIP_PK_MESSAGE = 2226;       //BOSS之家 PK第一名

    public static final short BOSS_VIP_PLAYER_REVIVE_MESSAGE = 2227; //BOSS之家玩家复活

    public static final short DUNGEON_FENGMO_DAILY_STATE_MESSAGE = 2301;    //封魔塔每日奖励状态

    public static final short DUNGEON_FENGMO_DAILY_RECEIVE_MESSAGE = 2302;    //封魔塔每日奖励领取

    public static final short TOWN_SOUL_UPGRADE = 2401; //镇魂装备升级

    public static final short TOWN_SOUL_COMPOSE = 2402; //镇魂装备合成

    public static final short TOWN_SOUL_DECOMPOSE = 2403; //镇魂装备分解

    public static final short TOWN_SOUL_TREASURE = 2404; //镇魂宝库

    public static final short TOWN_SOUL_TREASURE_TURNTABLE = 2405; //镇魂宝库转盘

    public static final short TOWN_SOUL_TREASURE_OUT = 2406; //镇魂宝库取出

    public static final short TOWN_SOUL_TREASURE_BOX = 2407; //镇魂宝库领取抽取宝箱

    public static final short TOWN_SOUL_TREASURE_RECORD = 2408; //镇魂宝库新抽取记录

    public static final short FIELD_PVP_INFO_MESSAGE = 2801; //野外PVP信息

    public static final short FIELD_PVP_SEARCH_MESSAGE = 2802; //野外PVP寻找挑战者

    public static final short FIELD_PVP_RANK_MESSAGE = 2803; //野外PVP排行榜

    public static final short FIELD_PVP_RECORD_MESSAGE = 2804; //野外PVP战斗记录

    public static final short FIELD_PVP_REQUEST_MESSAGE = 2805; //野外PVP战斗请求

    public static final short FIELD_PVP_RESULT_MESSAGE = 2806; //野外PVP战斗结果

    public static final short WANBA_DESK_INFO_MESSAGE = 3001; //玩吧发送桌面信息

    public static final short WANBA_DESK_REWARD_MESSAGE = 3002; //玩吧发送桌面领奖

    public static final short LIMIT_TASK_TIP_MESSAGE = 3010; //限时任务小广告状态

    public static final short LIMIT_TASK_TIP_SHOW_MESSAGE = 3011; //限时任务小广告已显示

    public static final short STATE_RECORD_MESSAGE = 3012; //状态记录

    public static final short STATE_SEARCH_MESSAGE = 3013; //状态查询

    public static final short STARCRAFT_INFO_MESSAGE = 3020; //传世争霸信息

    public static final short STARCRAFT_ENTER_MESSAGE = 3021; //传世争霸参战

    public static final short STARCRAFT_ATTACK_DOOR_MESSAGE = 3022; //传世争霸攻击城门boss

    public static final short STARCRAFT_ATTACK_DEAD_MESSAGE = 3023; //传世争霸城门boss死亡

    public static final short STARCRAFT_MOVE_MESSAGE = 3024; //传世争霸 传送

    public static final short STARCRAFT_AREA_PROCESS_MESSAGE = 3025; //传世争霸 进度更新

    public static final short STARCRAFT_BROADCAST_MOVE_MESSAGE = 3026; //传世争霸传送广播

    public static final short STARCRAFT_BROADCAST_RANK_MESSAGE = 3027; //传世争霸排行榜广播

    public static final short STARCRAFT_TARGET_MESSAGE = 3028; //传世争霸目标广播

    public static final short STARCRAFT_ATTACK_TARGET_MESSAGE = 3029; //传世争霸攻击目标

    public static final short STARCRAFT_ATTACK_RESULT_MESSAGE = 3030; //传世争霸攻击结果

    public static final short STARCRAFT_ATTACK_GUARD_MESSAGE = 3031; //传世争霸攻击守卫

    public static final short STARCRAFT_REVIVE_MESSAGE = 3032; //传世争霸复活

    public static final short STARCRAFT_COLLECT_MESSAGE = 3033; //传世争霸采旗

    public static final short STARCRAFT_BALANCE_MESSAGE = 3034; //传世争霸结算

    public static final short STARCRAFT_BROADCAST_COLLECT_MESSAGE = 3035; //传世争霸采旗广播

    public static final short STARCRAFT_COLLECT_STOP_MESSAGE = 3036; //传世争霸采旗中断

    public static final short STARCRAFT_REWARD_MESSAGE = 3037; //传世争霸领取奖励

    public static final short STARCRAFT_COUNTDOWN_MESSAGE = 3038; //传世争霸倒计时

    public static final short MONSTER_SIEGE_GET_INFO_MESSAGE = 3101; //怪物攻城信息

    public static final short MONSTER_SIEGE_START_MESSAGE = 3102; //怪物攻城开始战斗

    public static final short MONSTER_SIEGE_FIGHT_MESSAGE = 3103; //怪物攻城战斗消息

    public static final short MONSTER_SIEGE_QUIT_MESSAGE = 3104; //怪物攻城退出消息

    public static final short MONSTER_BOX_RECEIVE_MESSAGE = 3105; //怪物攻城宝箱领取

    public static final short MONSTER_SIEGE_RECORD_MESSAGE = 3106; //怪物攻城防守记录

    public static final short MONSTER_SIEGE_RANK_MESSAGE = 3107; //怪物攻城排行榜

    public static final short MONSTER_SIEGE_SYNC_MESSAGE = 3108; //怪物攻城玩家同步消息

    public static final short CARD_GET_INFO_MESSAGE = 3201; //获取卡牌信息

    public static final short CARD_LEVEL_UP_MESSAGE = 3202; //卡牌升级

    public static final short GROUP_1_MESSAGE = 4001; //第一组数据

    public static final short GROUP_2_MESSAGE = 4002; //第二组数据

    public static final short GROUP_3_MESSAGE = 4003; //第三组数据

    public static final short SOUL_GET_MESSAGE = 5009; //获取灵髓信息

    public static final short SOUL_REPLACE_MESSAGE = 5002; //灵髓替换

    public static final short SOUL_UP_MESSAGE = 5003; //灵髓升级

    public static final short SOUL_COMPOSE_MESSAGE = 5004; //灵髓合成

    public static final short SOUL_COPY_INFO_MESSAGE = 5005; //灵髓主宰试炼信息

    public static final short SOUL_CHALLENGE_MESSAGE = 5006; //灵髓主宰试炼请求挑战

    public static final short SOUL_FIGHT_DUNGEON_RESULT_MESSAGE = 5007; //灵髓主宰试炼请求战斗结果

    public static final short SOUL_SWEEP_MESSAGE = 5008; //灵髓扫荡

    public static final short FAZHEN_INFO_MESSAGE = 6001; //（圣物）法阵图鉴信息

    public static final short FAZHEN_ACTIVATE_MESSAGE = 6002; //（圣物）法阵激活

    //test use message ----- start
    public static final short TEST_FIGHTING_MESSAGE = 1;    //测试用战斗力校验

    public static final short TEST_LOG_PRINT = 2; //测试用日志打印
    //test use message ----- end
}
