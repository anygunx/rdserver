package com.rd.action;

import com.rd.common.ChatService;
import com.rd.define.ErrorDefine;
import com.rd.define.GameDefine;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.task.SerialKeyExecutor;
import org.apache.log4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.rd.net.MessageCommand.*;

public class GameAction {

    private static Logger logger = Logger.getLogger(GameAction.class);
    private static final SerialKeyExecutor<Integer> createRoleExec;
    private static final SerialKeyExecutor<Integer> protocolExec;

    static {
        createRoleExec = new SerialKeyExecutor<>(
                new ThreadPoolExecutor(4, 10, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()));
        protocolExec = new SerialKeyExecutor<>(
                new ThreadPoolExecutor(4, 10, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()));
    }

    public static void execute(final int playerId, final short loginCode, final Message request) {
        switch (request.getCmdId()) {
            case MessageCommand.LOGIN_SERVER_MESSAGE:
                process(playerId, loginCode, request);
                break;
            case MessageCommand.CREATE_ROLE_MESSAGE:
                createRoleExec.execute(() -> {
                    try {
                        process(playerId, loginCode, request);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                });
                break;
            default:
                GameRole role = GameWorld.getPtr().getOnlineRole(playerId);
                if (role == null) {
                    //登录超时，踢下线
                    Message response = new Message(MessageCommand.TIMEOUT_MESSAGE, request.getChannel());
                    GameWorld.getPtr().sendMessage(response);
                }
                //作弊
                //else if (role.getBeatManager() != null && role.getBeatManager().isCheat()) {
                //	Message cheatMsg = new Message(MessageCommand.GAME_BEAT_MESSAGE, request.getChannel());
                //	cheatMsg.setBool(false);
                //	GameWorld.getPtr().sendMessage(cheatMsg);
                //}
                else {
                    submit(playerId, () -> {
                        try {
                            process(playerId, loginCode, request);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    });
                }
                break;
        }
    }

    public static void process(int playerId, short loginCode, Message request) {
        GameRole gameRole = GameWorld.getPtr().getOnlineRole(playerId);
        if (gameRole == null && (request.getCmdId() != MessageCommand.LOGIN_SERVER_MESSAGE && request.getCmdId() != MessageCommand.CREATE_ROLE_MESSAGE)) {
            return;
        }
        if (gameRole != null) {
            //判断登录码是否一致，不一致踢下线
            if (gameRole.getPlayer().getLoginCode() != loginCode) {
                Message message = new Message(MessageCommand.REPEAT_LOGIN_MESSAGE, request.getChannel());
                gameRole.sendMessage(message);
                logger.info("PlayerId=" + gameRole.getPlayer().getId() + " is rejected. Last loginCode=" + gameRole.getPlayer().getLoginCode() + ", current loginCode=" + loginCode);
                return;
                //封号玩家，不允许登录
            } else if (gameRole.getPlayer().getState() == GameDefine.PLAYER_STATE_FREEZE) {
                Message message = new Message(MessageCommand.LOGIN_BAN_MESSAGE, request.getChannel());
                gameRole.sendMessage(message);
                return;
            }
            gameRole.setLastTickTime(System.currentTimeMillis());
        }
        //禁止消息
        if (GameWorld.getPtr().containBanCmd(request.getCmdId())) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        EMessage em = EMessage.getCommand(request.getCmdId());
        if (em != null) {
            em.getHandler().accept(gameRole, request);
            return;
        }
        switch (request.getCmdId()) {
            case MessageCommand.GAME_BEAT_MESSAGE:        //99心跳
                gameRole.getBeatManager().gameBeat(request);
                break;
            case MessageCommand.LOGIN_SERVER_MESSAGE: //103 登录游戏服
                GameWorld.getPtr().loginGame(request);
                break;
            case MessageCommand.CREATE_ROLE_MESSAGE:
                GameWorld.getPtr().createRole(request);    //104 创建角色
                break;
//			case MessageCommand.NOVICE_GUIDE_UPDATE:
//				gameRole.getSmallDataManager().processNoviceGuide(request);	//109 新手引导更新
//				break;
            case MessageCommand.REWARD_FIELD_BOSS_REWARD:    //117领取野外BOSS通关奖励
                gameRole.getFunctionManager().processFieldBossReward(request);
                break;
            case MessageCommand.RECEIVE_ONLINE_GIFT:    //118领取在线礼包
                gameRole.getFunctionManager().processReceiveOnlineGift(request);
                break;
//			case MessageCommand.CDKEY_INVOKE_MESSAGE:		//120 CDKEY兑换领奖
//				gameRole.getFunctionManager().processCDKeyInvoke(request);
//				break;
            case MessageCommand.MODIFY_HEAD_ICON_MESSAGE:    //121 修改头像
                gameRole.getFunctionManager().processModifyHeadIcon(request);
                break;
            case MessageCommand.GAME_SEARCH_PLAYER_MESSAGE: //122 按名查询玩家
                gameRole.getFunctionManager().processSearchPlayer(request);
                break;
            case MessageCommand.GAME_RANK_LIST_MESSAGE:        //130 排行榜数据请求
                gameRole.getFunctionManager().processGameRankList(request);
                break;
            case MessageCommand.GAME_FRIEND_DETAIL_MESSAGE:    //131好友详情
                gameRole.getFunctionManager().processFriendDetail(request);
                break;
            case MessageCommand.GAME_RANK_SIMPLE_LIST_MESSAGE:    //132排行榜。地图关卡.诛仙台
                gameRole.getFunctionManager().processGameRankSimpleList(request);
                break;
            case MessageCommand.MONTHLY_CARD_INFO:        //135 月卡信息
                gameRole.getActivityManager().processMonthlyCardInfo(request);
                break;
            case MessageCommand.WELFARE_INFO_MESSAGE:        //141 每日福利信息
                gameRole.getActivityManager().processWelfareInfo(request);
                break;
            case MessageCommand.REBATE_BUY_MESSAGE:            //142百倍返利购买
                gameRole.getActivityManager().processRebateBuy(request);
                break;
            case MessageCommand.INVEST_BUY_MESSAGE:            //143购买投资计划
                gameRole.getActivityManager().processInvestBuy(request);
                break;
            case MessageCommand.INVEST_REWARD_MESSAGE:        //144领取投资计划
                gameRole.getActivityManager().processInvestReward(request);
                break;
            case MessageCommand.DIAL_MESSAGE:                //145转盘
                gameRole.getActivityManager().processDial(request);
                break;
            case MessageCommand.TLSHOP_BUY_MESSAGE:            //146限时商城购买
                gameRole.getActivityManager().processDLShopBuy(request);
                break;
            case MessageCommand.TLGIFT_REWARD_MESSAGE:        //147限时有礼领取
                gameRole.getActivityManager().processDLGiftReward(request);
                break;
            case MessageCommand.RANK_REWARD_INFO_MESSAGE:    //148冲榜奖励消息
                gameRole.getFunctionManager().processRankRewardInfo(request);
                break;
            case MessageCommand.VIPSHOPFL_BUY_MESSAGE:        //149VIP商城购买
                gameRole.getActivityManager().processVipShopFLBuy(request);
                break;
            case MessageCommand.VIPSHOPTL_BUY_MESSAGE:        //150VIP商城购买
                gameRole.getActivityManager().processVipShopTLBuy(request);
                break;
            case MessageCommand.CRASH_COW_MESSAGE:        //151 摇钱树
                gameRole.getActivityManager().processCrashCow(request);
                break;
            case MessageCommand.SPRING_WORD_EXCHANGE_MESSAGE:    //152新春集字
                gameRole.getActivityManager().processSpringWordExchange(request);
                break;
            case MessageCommand.TLHORSE_EXCHANGE_MESSAGE:    //153 限时坐骑
                gameRole.getActivityManager().processTLHorseExchange(request);
                break;
            case MessageCommand.SPRING_SIGN_MESSAGE: //154 春节签到
                gameRole.getActivityManager().processSpringSign(request);
                break;
            case MessageCommand.TLSHOP_SPRING_BUY_MESSAGE:
                gameRole.getActivityManager().processShopSpringBuy(request); //春节限时商城
                break;
            case MessageCommand.FUDAI_MESSAGE:
                gameRole.getActivityManager().processFuDai(request); //福袋
                break;
            case MessageCommand.PAY_CUMULATE_RECEIVE_MESSAGE:
                gameRole.getActivityManager().processPayCumulateReceive(request);
                break;
            case MessageCommand.PAY_COUNT_RECEIVE_MESSAGE:
                gameRole.getActivityManager().processPayCountReceive(request);
                break;
            case MessageCommand.PAY_CONTINUE_RECEIVE_MESSAGE:
                gameRole.getActivityManager().processPayContinueReceive(request);
                break;
            case MessageCommand.PAY_CUMULATE_FIXED_RECEIVE_MESSAGE:
                gameRole.getActivityManager().processPayCumulateFixedReceive(request);
                break;
            case MessageCommand.INVEST_FUND_RECEIVE_MESSAGE:
                gameRole.getActivityManager().processInvestFundReceive(request);
                break;
            case MessageCommand.SLOT_NEW_MACHINE_MESSAGE:
                gameRole.getActivityManager().processSlotNewMachine(request);
                break;
            case MessageCommand.SLOT_MACHINE_MESSAGE:
                gameRole.getActivityManager().processSlotMachine(request);
                break;
            case MessageCommand.LIMIT_LIMIT_LIMIT_MESSAGE:
                gameRole.getActivityManager().processLimitLimitLimt(request);
                break;
            case MessageCommand.SEVEN_DAY_MESSAGE:
                gameRole.getActivityManager().processSevenDay(request);
                break;
            case MessageCommand.BUY_ONE_MESSAGE:
                gameRole.getActivityManager().processBuyOne(request);//一元抢购
                break;
            case MessageCommand.SHENTONG_BUY_MESSAGE:
                gameRole.getActivityManager().processShenTongBuy(request);//一折神通
                break;
            case MessageCommand.SHENTONG_REWARD_MESSAGE:
                gameRole.getActivityManager().processShenTongReward(request);//一折神通
                break;
            case MessageCommand.SHOP_PLAYER_INFO_MESSAGE:
                gameRole.getActivityManager().processShopPlayerInfo(request);//商城信息
                break;
            case MessageCommand.SHOP_PLAYER_BUY_MESSAGE:
                gameRole.getActivityManager().processShopPlayerBuy(request);//商城购买
                break;
            case MessageCommand.SHOP_PLAYER_REFRESH_MESSAGE:
                gameRole.getActivityManager().processShopPlayerRefresh(request);//商城刷新
                break;
            case MessageCommand.LIMITGIFT_LV_BUY_MESSAGE:            //164等级限制礼包购买
                gameRole.getActivityManager().processLvGiftBuy(request);
                break;
            case MessageCommand.LIMITGIFT_VIP_BUY_MESSAGE:            //163VIP限制礼包购买
            case MessageCommand.FESTLIMITGIFT_BUY_MESSAGE:
            case MessageCommand.WEEKENDLIMITGIFT_BUY_MESSAGE:
            case MessageCommand.LIMIT_GIFT_DAILY_BUY_MESSAGE:
                gameRole.getActivityManager().processLimitGiftBuy(request);
                break;
            case MessageCommand.GAME_PAY_DAILY_FIRST_MESSAGE:
                gameRole.getActivityManager().processPayDailyFirstMessage(request);
                break;
            case MessageCommand.FEST_PAY_DAILY_FIRST_MESSAGE:
                gameRole.getActivityManager().processPayDailyFirst(request);
                break;
//			case MessageCommand.TARGET_REWARD_MESSAGE:				//165达标活动领取奖励
//				gameRole.getActivityManager().processTargetReward(request);
//				break;
            case MessageCommand.FESTTARGET_REWARD_MESSAGE:
                gameRole.getActivityManager().processFestTargetReward(request);
                break;
            case MessageCommand.FESTPAYTARGET_REWARD_MESSAGE:
                gameRole.getActivityManager().processFestPayTargetReward(request);
                break;
            case MessageCommand.TARGET_TOP_MESSAGE:                    //166达标活动排行榜
                gameRole.getActivityManager().processTargetTop(request);
                break;
            case MessageCommand.FESTTARGET_RANK_MESSAGE:
                gameRole.getActivityManager().processFestTargetTop(request);
                break;
            case MessageCommand.FESTPAYTARGET_RANK_MESSAGE:
                gameRole.getActivityManager().processFestPayTargetTop(request);
                break;
            case MessageCommand.LOGON_ACT_REWARD_MESSAGE:            //167登录活动领奖
                gameRole.getActivityManager().processLogonReward(request);
                break;
            case MessageCommand.PAY_FEAST_REWARD_MESSAGE:            //169充值盛宴领取
                gameRole.getActivityManager().processPayFeastReward(request);
                break;
            case MessageCommand.GOLDTREE_PRODUCE_MESSAGE:
                gameRole.getActivityManager().processGoldTreeProduce(request);
                break;
            case MessageCommand.GOLDTREE_REWARD_MESSAGE:
                gameRole.getActivityManager().processGoldTreeReward(request);
                break;
            case MessageCommand.XUNBAO_RANK_MESSAGE:
                gameRole.getActivityManager().processXunbaoRank(request);
                break;
            case MessageCommand.WISHING_WELL_MESSAGE:
                gameRole.getActivityManager().processWishingWell(request);
                break;
            case MessageCommand.FESTWISHING_WELL_MESSAGE:
                gameRole.getActivityManager().processFestWishingWell(request);
                break;
            case MessageCommand.WEEKENDWISHING_WELL_MESSAGE:
                gameRole.getActivityManager().processWeekendWishingWell(request);
                break;
            case MessageCommand.FESTLOGON_REWARD_MESSAGE:
                gameRole.getActivityManager().processFestLogonReward(request);
                break;
            case MessageCommand.WEEKENDLOGON_REWARD_MESSAGE:
                gameRole.getActivityManager().processWeekendLogonReward(request);
                break;
            case MessageCommand.WANBALOGON_REWARD_MESSAGE:
                gameRole.getActivityManager().processWanbaLogonReward(request);
                break;
            case MessageCommand.PLAYER_CHARACTER_UNLOCK:            //201角色解锁
                gameRole.getFunctionManager().processCharacterUnlock(request);
                break;
            case MessageCommand.HOLYGOODS_INFO_MESSAGE: //210圣物信息
                gameRole.getFunctionManager().processHolyGoodsInfo(request);
                break;
            case MessageCommand.HOLYGOODS_UP_MESSAGE: //211圣物升级
                gameRole.getFunctionManager().processHolyGoodsUp(request);
                break;
            case MessageCommand.HOLYLINES_EQUIP_MESSAGE: //212装备圣纹
                gameRole.getFunctionManager().processHolyLinesEquip(request);
                break;
            case MessageCommand.HOLYLINES_COMBINE_MESSAGE: //213圣纹合成
                gameRole.getFunctionManager().processHolyLinesCombine(request);
                break;
            case MessageCommand.HOLYLINES_COMBINE_TRANSFORM_MESSAGE: //214圣纹转换
                gameRole.getFunctionManager().processHolyLinesTransform(request);
                break;
            case MessageCommand.AMBIT_INFO_MESSAGE: //220境界信息
                gameRole.getFunctionManager().processAmbitInfo(request);
                break;
            case MessageCommand.AMBIT_UP_MESSAGE: //221境界-进阶
                gameRole.getFunctionManager().processAmbitUp(request);
                break;
            case MessageCommand.AMBIT_HALO_ACTIVE_MESSAGE: //222境界-光环激活
                gameRole.getFunctionManager().processAmbitHaloActive(request);
                break;
            case MessageCommand.AMBIT_HALO_ENDUE_MESSAGE: //223境界-光环幻化
                gameRole.getFunctionManager().processAmbitHaloEndue(request);
                break;
            case MessageCommand.AMBIT_SKILL_MESSAGE: //224境界-技能
                gameRole.getFunctionManager().processAmbitSkill(request);
                break;
            case MessageCommand.GOODS_USE_MESSAGE:            //303 使用物品
                gameRole.getPackManager().processUseGoods(request);
                break;
            case MessageCommand.GOODS_WEAR_EQUIP_MESSAGE: //304 穿装备
                gameRole.getEquipManager().processWearEquip(request);
                break;
            case MessageCommand.GOODS_ARTIFACT_EQUIP_MESSAGE: //305 装备灵器
                gameRole.getEquipManager().processArtifactEquip(request);
                break;
            case MessageCommand.GOODS_PREOPEN_AUCTION_BOX_MESSAGE: //306 宝箱预开启
                gameRole.getPackManager().processPreopenAuctionBox(request);
                break;
            case MessageCommand.GOODS_USE_AUCTION_BOX_MESSAGE: //307 拍品宝箱预开启
                gameRole.getPackManager().processUseAuctionBox(request);
                break;
            case MessageCommand.EQUIP_TONGJINGYUDI_MESSAGE: //331升级铜镜玉笛
                gameRole.getEquipManager().processTongjingYudi(request);
                break;
            case MessageCommand.EQUIP_ZUOYOUYAN_MESSAGE: //332升级左右眼
                gameRole.getEquipManager().processZuoYouYan(request);
                break;
            case MessageCommand.DOM_LVUP_MESSAGE:    //主宰升级
                gameRole.getFunctionManager().processDomLvUp(request);
                break;
            case MessageCommand.DOM_RANKUP_MESSAGE:    //主宰升阶
                gameRole.getFunctionManager().processDomRankUp(request);
                break;
            case MessageCommand.DOM_PIECE_MESSAGE:    //主宰分解
                gameRole.getFunctionManager().processDomPiece(request);
                break;
            case MessageCommand.WING_ACTIVE_MESSAGE:    //361激活翅膀
                gameRole.getEquipManager().processWingActive(request);
                break;
            case MessageCommand.WING_SHOW_MESSAGE:        //362展示翅膀
                gameRole.getEquipManager().processWingShow(request);
                break;
            case MessageCommand.WEAPON_ACTIVE_MESSAGE:    //363激活武器
                gameRole.getEquipManager().processWeaponActive(request);
                break;
            case MessageCommand.WEAPON_SHOW_MESSAGE:    //364展示武器
                gameRole.getEquipManager().processWeaponShow(request);
                break;
            case MessageCommand.TITLE_ADORN_MESSAGE:    //366佩戴称号
                gameRole.getTitleManager().processTitleAdorn(request);
                break;
            case MessageCommand.ARMOR_ACTIVE_MESSAGE:    //367激活装备
                gameRole.getEquipManager().processArmorActive(request);
                break;
            case MessageCommand.ARMOR_SHOW_MESSAGE:        //368展示装备
                gameRole.getEquipManager().processArmorShow(request);
                break;
            case MessageCommand.MOUNT_ACTIVE_MESSAGE:    //369激活坐骑
                gameRole.getEquipManager().processMountActive(request);
                break;
            case MessageCommand.MOUNT_SHOW_MESSAGE:        //370展示坐骑
                gameRole.getEquipManager().processMountShow(request);
                break;
            case MessageCommand.RED_EXCHANGE_MESSAGE:    //371 红装碎片兑换
                gameRole.getFunctionManager().processRedExchange(request);
                break;
            case MessageCommand.SKILL_UP_MESSAGE:            //501 技能升级
                gameRole.getSkillManager().processUpSkill(request);
                break;
            case MessageCommand.SKILL_UP_AUTO_MESSAGE:        //502 一键升级技能
                gameRole.getSkillManager().processUpSkillAuto(request);
                break;
            case MessageCommand.EQUIP_ZHULING_MESSAGE:        //550	装备注灵
                gameRole.getEquipManager().processZhuLing(request);
                break;
            case MessageCommand.EQUIP_STRENGTH_MESSAGE:        //551	装备强化
                gameRole.getEquipManager().processStrength(request);
                break;
//			case MessageCommand.EQUIP_MELTING_HORSE_MESSAGE:		//552	坐骑装备熔炼
//				gameRole.getEquipManager().processMeltingHorse(request);
//				break;
            case MessageCommand.EQUIP_ZHUHUN_MESSASGE:        //553	装备铸魂
                gameRole.getEquipManager().processZhuHun(request);
                break;
            case MessageCommand.EQUIP_MELTING_ROLE_MESSAGE:        //554	人物装备熔炼
                gameRole.getEquipManager().processMeltingRole(request);
                break;
            case MessageCommand.GONGFA_UPGRADE_MESSAGE:            //555	功法升级
                gameRole.getEquipManager().processGongFa(request);
                break;
            case MessageCommand.EQUIP_CUILIAN_MESSASGE:            //556	装备淬炼
                gameRole.getEquipManager().processCuiLian(request);
                break;
            case MessageCommand.EQUIP_MELTING_ONEKEY_ROLE_MESSAGE:        //557	人物装备一键熔炼
                gameRole.getEquipManager().processMeltingOneKeyRole(request);
                break;
            case MessageCommand.EQUIP_STRENGTH_ONEKEY_MESSAGE:        //558	人物装备一键强化
                gameRole.getEquipManager().processEquipStrengthOneKey(request);
                break;
            case MessageCommand.JEWEL_UPGRADE_MESSAGE:        //562宝石升级
                gameRole.getEquipManager().processJewelUpgrade(request);
                break;
            case MessageCommand.MOUNT_UP_MESSAGE: //601 坐骑升阶
                gameRole.getSectionManager().processMountUp(request);
                break;
            case MessageCommand.MAGIC_DETAIL_MESSAGE: //602 法宝相信信息
                gameRole.getSectionManager().processMagicDetail(request);
                break;
            case MessageCommand.MAGIC_LEVEL_UP_MESSAGE: //603 法宝升级
                gameRole.getSectionManager().processMagicLevelUp(request);
                break;
            case MessageCommand.MAGIC_STAGE_UP_MESSAGE: //604 法宝升阶
                gameRole.getSectionManager().processMagicStageUp(request);
                break;
            case MessageCommand.MAGIC_TURNTABLE_MESSAGE: //605 法宝转盘
                gameRole.getSectionManager().processMagicTurntable(request);
                break;
            case MessageCommand.MOUNT_UP_PILL_MESSAGE: //610 坐骑直升丹
                gameRole.getSectionManager().processMountPill(request);
                break;
            case MessageCommand.WING_GOD_MESSAGE: //620 神羽装备
                gameRole.getSectionManager().processWingEquip(request);
                break;
            case MessageCommand.WING_GOD_CRAFT_MESSAGE: //621 神羽装备合成
                gameRole.getSectionManager().processWingGodCraft(request);
                break;
            case MessageCommand.WING_GOD_QUICK_CRAFT_MESSAGE://621 神羽装备快速合成
                gameRole.getSectionManager().processWingGodQuickCraft(request);
                break;
            case MessageCommand.WING_GOD_CONVERSE_MESSAGE://621 神羽装备转换
                gameRole.getSectionManager().processWingGodConverse(request);
                break;

//			case MessageCommand.TRAIN_ITEM_PILL://633培养项直升丹
//				gameRole.getTrainItemManager().processTrainItemPill(request);
//				break;
//			case MessageCommand.TRAIN_ITEM_EQUIP_CRAFT://635培养项装备合成
//				gameRole.getTrainItemManager().processTrainItemEquipCraft(request);
//				break;
//			case MessageCommand.TRAIN_ITEM_EQUIP_CONVERSE://636培养项装备转换
//				gameRole.getTrainItemManager().processTrainItemEquipConverse(request);
//				break;
            case MessageCommand.GOD_ARTIFACT_INFO_MESSAGE: //650神器列表
                gameRole.getEquipManager().processGodArtifactInfo(request);
                break;
            case MessageCommand.GOD_ARTIFACT_ACTIVE_MESSAGE: //651神器激活
                gameRole.getEquipManager().processGodArtifactActive(request);
                break;
            case MessageCommand.GOD_ARTIFACT_UPGRADE_MESSAGE: //652神器升级
                gameRole.getEquipManager().processGodArtifactUpgrade(request);
                break;
//			case MessageCommand.HUANHUA_INVOKE_MESSAGE: //653幻化激活
//				gameRole.getBlessManager().processHuanhuaInvoke(request);
//				break;
//			case MessageCommand.HUANHUA_APPEARANCE_CHANGE_MESSAGE://654幻化外形变更
//				gameRole.getBlessManager().processHuanhuaAppearanceChange(request);
//				break;
            case MessageCommand.MERIDIAN_UPGRADE_MESSAGE:        //660经脉升级
                gameRole.getFunctionManager().processMeridianUpgrade(request);
                break;
            case MessageCommand.REIN_UPGRADE_MESSAGE:
                gameRole.getFunctionManager().processReinUpgrade(request);
                break;
            case MessageCommand.REIN_EXCHANGE_MESSAGE:
                gameRole.getFunctionManager().processReinExchange(request);
                break;
//			case MessageCommand.SPIRIT_UPGRADE_MESSAGE:			//680元神升级
//				gameRole.getSpiritManager().processSpiritUpgrade(request);
//				break;
//			case MessageCommand.SPIRIT_ACTIVE_MESSAGE:			//681元神装备
//				gameRole.getSpiritManager().processSpiritActive(request);
//				break;
//			case MessageCommand.SPIRIT_RES_MESSAGE:				//684元神分解
//				gameRole.getSpiritManager().processSpiritRes(request);
//				break;
            case MessageCommand.LADDER_DETAIL_MESSAGE:    //700天梯详情
                gameRole.getLadderManager().processLadderDetail(request);
                break;
            case MessageCommand.LADDER_MATCH_MESSAGE:    //701天梯匹配
                gameRole.getLadderManager().processLadderMatch(request);
                break;
            case MessageCommand.LADDER_RESULT_MESSAGE:    //702天梯战斗结果
                gameRole.getLadderManager().processLadderResult(request);
                break;
            case MessageCommand.LADDER_BUY_COUNT:        //703天梯战斗次数购买
                gameRole.getLadderManager().processLadderBuy(request);
                break;
            case MessageCommand.LADDER_TOP_LIST:        //704天梯排行榜消息
                gameRole.getLadderManager().processLadderTopList(request);
                break;
            case MessageCommand.LADDER_HISTORY:        //705天梯历史战绩消息
                gameRole.getLadderManager().processLadderHistory(request);
                break;
            case MessageCommand.ESCORT_REVENGE_INFO:            //708渡劫复仇信息
                gameRole.getEscortManager().processRevengeInfo(request);
                break;
            case MessageCommand.ESCORT_REVENGE_RESULT:            //709渡劫复仇结果
                gameRole.getEscortManager().processRevengeResult(request);
                break;
            case MessageCommand.ESCORT_DETAIL_MESSAGE:            //710押镖数据信息
                gameRole.getEscortManager().processEscortDetail(request);
                break;
            case MessageCommand.ESCORT_DISPATCH_MESSAGE:        //711押运镖车
                gameRole.getEscortManager().processDispatch(request);
                break;
            case MessageCommand.ESCORT_ROB_START_MESSAGE:                //712劫杀镖车
                gameRole.getEscortManager().processRobStart(request);
                break;
            case MessageCommand.ESCORT_REFRESH_QUALITY_MESSAGE:    //713镖车刷星
                gameRole.getEscortManager().processRefreshQuality(request);
                break;
            case MessageCommand.ESCORT_ROBLIST_MESSAGE:            //714劫镖车队列表
                gameRole.getEscortManager().processRobList(request);
                break;
            case MessageCommand.ESCORT_ROB_RESULT_MESSAGE:        //715镖车劫杀结果
                gameRole.getEscortManager().processRobResult(request);
                break;
            case MessageCommand.ESCORT_COMPLETE:                //716镖车完成
                gameRole.getEscortManager().processComplete(request);
                break;
            case MessageCommand.ESCORT_LOGS:                    //717运镖日志
                gameRole.getEscortManager().processLogs(request);
                break;
            case MessageCommand.ESCORT_REWARD_MESSAGE:            //719领取奖励
                gameRole.getEscortManager().processReward(request);
                break;
            case MessageCommand.ORANGE_MIX_MESSAGE:            //720橙装合成
                gameRole.getEquipManager().processOrangeMix(request);
                break;
            case MessageCommand.ORANGE_UPGRADE_MESSAGE:            //721橙装升级
                gameRole.getEquipManager().processOrangeUpgrade(request);
                break;
            case MessageCommand.ORANGE_RES_MESSAGE:            //722橙装分解
                gameRole.getEquipManager().processOrangeRes(request);
                break;
            case MessageCommand.RED_MIX_MESSAGE:            //723红装合成
                gameRole.getEquipManager().processRedMix(request);
                break;
            case MessageCommand.RED_UPGRADE_MESSAGE:            //724红装升级
                gameRole.getEquipManager().processRedUpgrade(request);
                break;
            case MessageCommand.RED_RES_MESSAGE:            //725红装分解
                gameRole.getEquipManager().processRedRes(request);
                break;
            case MessageCommand.RED_LOTTERY_MESSAGE:            //726寻宝
                gameRole.getEquipManager().processRedLottery(request);
                break;
            case MessageCommand.RED_PICKUP_MESSAGE:            //728提取装备
                gameRole.getEquipManager().processRedPickUp(request);
                break;
            case MessageCommand.BOSS_CITIZEN_INFO_MESSAGE:    //730
                gameRole.getBossManager().processCitizenInfo(request);
                break;
            case MessageCommand.BOSS_CITIZEN_START_MESSAGE:    //731
                gameRole.getBossManager().processCitizenStart(request);
                break;
            case MessageCommand.BOSS_CITIZEN_QUIT_MESSAGE:    //732
                gameRole.getBossManager().processCitizenQuit(request);
                break;
            case MessageCommand.BOSS_CITIZEN_REWARD_MESSAGE://733
                gameRole.getBossManager().processCitizenReward(request);
                break;
            case MessageCommand.BOSS_CITIZEN_FIGHT_MESSAGE:    //734
                gameRole.getBossManager().processCitizenFight(request);
                break;
            case MessageCommand.BOSS_CITIZEN_CUE_MESSAGE:    //737
                gameRole.getBossManager().processCitizenCue(request);
                break;
            case MessageCommand.BOSS_CITIZEN_PK_MESSAGE:    //755
                gameRole.getBossManager().processCitizenPK(request);
                break;
            case MessageCommand.BOSS_CITIZEN_PLAYER_REVIVE_MESSAGE:
                gameRole.getBossManager().processCitizenRevive(request);
                break;
            case MessageCommand.BOSS_REIN_INFO_MESSAGE:        //740转生BOSS列表信息
                gameRole.getBossManager().processReinInfo(request);
                break;
            case MessageCommand.BOSS_REIN_START_MESSAGE:        //741转生BOSS战斗开始
                gameRole.getBossManager().processReinStart(request);
                break;
            case MessageCommand.BOSS_REIN_FIGHT_MESSAGE:        //742转生BOSS战斗
                gameRole.getBossManager().processReinFight(request);
                break;
            case MessageCommand.BOSS_REIN_REVIVE_MESSAGE:        //746转生BOSS复活
                gameRole.getBossManager().processReinRevive(request);
                break;
            case MessageCommand.BOSS_REIN_HISTORY_MESSAGE:        //747转生BOSS上次排行榜
                gameRole.getBossManager().processReinHistory(request);
                break;
            case MessageCommand.BOSS_LIST_MESSAGE:            //750BOSS列表
                gameRole.getFunctionManager().processBossList(request);
                break;
            case MessageCommand.BOSS_TOP_MESSAGE:            //751BOSS排行榜
                gameRole.getFunctionManager().processBossTop(request);
                break;
            case MessageCommand.BOSS_BAT_START_MESSAGE:        //752BOSS参战
                gameRole.getFunctionManager().processBossBatStart(request);
                break;
            case MessageCommand.BOSS_BAT_RESULT_MESSAGE:    //753战斗结果
                gameRole.getFunctionManager().processBossBatResult(request);
                break;
            case MessageCommand.SHOP_INFO_MESSAGE:        //800商城详情
                gameRole.getShopManager().processShopInfoMsg(request);
                break;
            case MessageCommand.SHOP_BUY_MESSAGE:        //801商城购买
                gameRole.getShopManager().processShopBuyMsg(request);
                break;
            case MessageCommand.SHOP_REFRESH_MESSAGE:    //802 商城刷新
                gameRole.getShopManager().processShopRefreshMsg(request);
                break;
            case MessageCommand.MAIL_LIST_MESSAGE:    //810 邮件列表
                gameRole.getMailManager().processMailList(request);
                break;
            case MessageCommand.MAIL_READ_MESSAGE:    //811读邮件
                gameRole.getMailManager().processMailRead(request);
                break;
            case MessageCommand.MAIL_REWARD_SINGLE_MESSAGE:    //812 领取附件--单封邮件
                gameRole.getMailManager().processMailRewardSingle(request);
                break;
            case MessageCommand.MAIL_REWARD_ALL_MESSAGE:    //813 领取附件--所有邮件
                gameRole.getMailManager().processMailRewardAll(request);
                break;
            case MessageCommand.CHAT_LIST_MESSAGE:                //820 拉取聊天记录
                ChatService.processChatListMsg(request, gameRole);
                break;
            case MessageCommand.CHAT_MESSAGE:                //822 聊天
                gameRole.getChatManager().processChatMsg(request);
                break;
            case MessageCommand.BROADCAST_PET_MESSAGE:
                gameRole.getSectionManager().processPetBroadcast(request);
                break;
            case MessageCommand.TOPRANK_WORSHIP_MESSAGE:        //831 排行榜膜拜
                gameRole.getFunctionManager().processRankWorship(request);
                break;
            case MessageCommand.NIGHT_FIGHT_JOIN_MESSAGE:        //840 加入夜战
                gameRole.getNightFightManager().processJoinFight(request);
                break;
            case MessageCommand.NIGHT_FIGHT_ATTACK_MESSAGE:        //841 夜战攻击
                gameRole.getNightFightManager().processAttack(request);
                break;
            case MessageCommand.NIGHT_FIGHT_TARGET_MESSAGE:        //842 夜战目标更新
                gameRole.getNightFightManager().processTarget(request);
                break;
            case MessageCommand.NIGHT_FIGHT_EXIT_MESSAGE:        //844 夜战退出
                gameRole.getNightFightManager().processExit(request);
                break;
            case MessageCommand.NIGHT_FIGHT_EXCHANGE_MESSAGE:        //847 夜战领取战绩
                gameRole.getNightFightManager().processExchange(request);
            case MessageCommand.NIGHT_FIGHT_MONSTER_MESSAGE:    //849 夜战攻击怪物
                gameRole.getNightFightManager().processMonster(request);
                break;
            case MessageCommand.NIGHT_FIGHT_REVIVE_MESSAGE:    //851 夜战复活
                gameRole.getNightFightManager().processRevive(request);
                break;
            case MessageCommand.NIGHT_FIGHT_COUNTDOWN_MESSAGE:    //852 夜战倒计时
                gameRole.getNightFightManager().processCountDown(request);
                break;
            case MessageCommand.HEART_SKILL_LEARN_MESSAGE:    //861 心法学习
                gameRole.getSkillManager().processHeartSkillLearn(request);
                break;
            case MessageCommand.HEART_SKILL_UP_MESSAGE:    //863 心法升级
                gameRole.getSkillManager().processHeartSkillUp(request);
                break;
            case MessageCommand.HEART_SKILL_RM_MESSAGE:    //864 心法拆卸
                gameRole.getSkillManager().processHeartSkillRm(request);
                break;
            case MessageCommand.HEART_SKILL_COMBINE_MESSAGE:    //865 心法合成
                gameRole.getSkillManager().processHeartSkillCombine(request);
                break;
            case MessageCommand.COMBINE_RUNE_BAG_MESSAGE:    //870 合击符文背包
                gameRole.getEquipManager().processCombineRuneBag(request);
                break;
            case MessageCommand.COMBINE_RUNE_EQUIP_MESSAGE:    //872 合击符文装备
                gameRole.getEquipManager().processCombineRuneEquip(request);
                break;
            case MessageCommand.COMBINE_RUNE_DECOMPOSE_MESSAGE:    //873 合击符文分解
                gameRole.getEquipManager().processCombineRuneDecompose(request);
                break;
            case MessageCommand.COMBINE_RUNE_COMPOSE_MESSAGE:    //874 合击符文合成
                gameRole.getEquipManager().processCombineRuneCompose(request);
                break;
            case MessageCommand.MISSION_CHAIN_RECEIVE_MESSAGE: //902 支线任务领取
                gameRole.getMissionManager().processChainMissionReceive(request);
                break;
            case MessageCommand.FIVE_ELEMENTS_ACTIVE_MESSAGE: //910 五行激活
                gameRole.getFunctionManager().processFiveElementsActive(request);
                break;
            case MessageCommand.FIVE_ELEMENTS_LIST_MESSAGE: //911五行玩法列表
                gameRole.getFunctionManager().processFiveElementsList(request);
                break;
            case MessageCommand.FIVE_ELEMENTS_UPGRADE_MESSAGE: //912五行玩法升级
                gameRole.getFunctionManager().processFiveElementsUpgrade(request);
                break;
            case MessageCommand.FIVE_ELEMENTS_ACTIVATE_MESSAGE: //913五行激活信息列表
                gameRole.getFunctionManager().processFiveElementsActivate(request);
                break;
            case MessageCommand.LINGZHEN_LIST_MESSAGE: //914灵阵信息
                gameRole.getFunctionManager().processLingzhenInfo(request);
                break;
            case MessageCommand.LINGZHEN_UPGRADE_MESSAGE: //915灵阵升级
                gameRole.getFunctionManager().processLingzhenLvUp(request);
                break;
            case MessageCommand.FIVE_ELEMENTS_FUSE_MESSAGE: //916五行融合
                gameRole.getFunctionManager().processFiveElementsFuse(request);
                break;
            case MessageCommand.FIVE_ELEMENTS_COPY_MESSAGE: //917五行副本信息
                gameRole.getFunctionManager().processFiveElementsCopy(request);
                break;
            case MessageCommand.FIVE_ELEMENTS_COPY_CHALLENGE_MESSAGE: //918五行请求挑战副本
                gameRole.getFunctionManager().processFiveElementsChallenge(request);
                break;
            case MessageCommand.FIVE_ELEMENTS_THE_AWARD_MESSAGE: //919五行副本领奖
                gameRole.getFunctionManager().processFiveElementsAward(request);
                break;
            case MessageCommand.FIVE_ELEMENTS_ISFUSE_MESSAGE: //920五行活动是否完成
                gameRole.getFunctionManager().processFiveFuseSuccess(request);
                break;
            case MessageCommand.FIVE_ELEMENTS_FIGHT_DUNGEON_RESULT_MESSAGE: //9200五行副本战斗结果
                gameRole.getFunctionManager().processFiveFightResult(request);
                break;
            case MessageCommand.FIVE_ELEMENTS_ACTIVITY_MESSAGE: //9201五行副本活动信息
                gameRole.getFunctionManager().processFiveElementActivity(request);
                break;
            case MessageCommand.FIVE_ELEMENTS_ACTIVITY_CHALLENGE_MESSAGE: //9202五行副本活动请求挑战
                gameRole.getFunctionManager().processFiveElementsActivityChallenge(request);
                break;
            case MessageCommand.FIVE_ELEMENTS_ACTIVITY_FIGHT_DUNGEON_RESULT_MESSAGE://9203五行副本活动战斗结果
                gameRole.getFunctionManager().processFiveActivityFightResult(request);
                break;
            case MessageCommand.SHARE_INFO_MESSAGE: //921 分享信息
                gameRole.getFunctionManager().processShareInfo(request);
                break;
            case MessageCommand.SHARE_COMPLETE_MESSAGE: //922 分享完成
                gameRole.getFunctionManager().processShareComplete(request);
                break;
            case MessageCommand.SHARE_REWARD_MESSAGE: //923 分享奖励
                gameRole.getFunctionManager().processShareReward(request);
                break;
            case MessageCommand.DRAGON_BALL_LEVELUP: //930 龙珠升级
                gameRole.getFunctionManager().processDragonBallLvup(request);
                break;
            case MessageCommand.DRAGON_BALL_RECEIVE_PIECE: //931 龙珠碎片领取
                gameRole.getFunctionManager().processDragonBallPiecesReceive(request);
                break;
            case MessageCommand.DRAGON_BALL_RECEIVE_BOX_MESSAGE: //942 龙珠宝箱领取
                gameRole.getMissionManager().processReceiveDragonBallBox(request);
                break;
            case MessageCommand.MEDAL_LEVEL_UP_MESSAGE:    //950 勋章升级
                gameRole.getFunctionManager().processMedalLvup(request);
                break;
            case MessageCommand.ACHIEVEMENT_RECEIVE_MESSAGE: //962 成就任务领取
                gameRole.getMissionManager().processReceiveAchievementMission(request);
                break;
            case MessageCommand.ARTIFACT_PIECE_INVOKE_MESSAGE: //971 神器碎片激活
                gameRole.getFunctionManager().processArtifactPieceInvoke(request);
                break;
            case MessageCommand.ARTIFACT_BOSS_INVOKE_MESSAGE: //972 关卡神器激活
                gameRole.getFunctionManager().processArtifactInvoke(request);
                break;
            case MessageCommand.TLMISSION_UPDATE_MESSAGE: //980 限时任务刷新
                gameRole.getMissionManager().processGetTLMission(request);
                break;
            case MessageCommand.TLMISSION_RECEIVE_MESSAGE: //981 限时任务领取
                gameRole.getMissionManager().processReceiveTLMission(request);
                break;
            case MessageCommand.CARD_MISSION_MESSAGE: //990 卡牌限时任务列表
                gameRole.getMissionManager().processGetCardMission(request);
                break;
            case MessageCommand.CARD_MISSION_RECEIVE_MESSAGE: //992 卡牌任务领取奖励
                gameRole.getMissionManager().processReceiveCardMission(request);
                break;
            case MessageCommand.GAME_7DAY_MESSAGE:            //7日活动
                gameRole.getActivityManager().process7DayMessage(request);
                break;
//			case MessageCommand.GAME_TURNTABLE_MESSAGE:    //1004元宝王者
//				gameRole.getActivityManager().processTurntableMessage(request);
//				break;
            case MessageCommand.GAME_TURNTABLE_PAY_MESSAGE:    //1005元宝王者充值
                gameRole.getActivityManager().processTurntableReceive(request);
                break;
            case MessageCommand.GAME_KAM_PO_MESSAGE:
                gameRole.getActivityManager().processKamPoMessage(request); //1006 鉴宝
                break;
            case MessageCommand.GAME_LUCK_SCORE_MESSAGE: //1007鉴宝幸运值领取
                gameRole.getActivityManager().processLuckScore(request);
                break;
            case MessageCommand.GAME_INIT_LUCK_SCORE_MESSAGE:
                gameRole.getActivityManager().processInitLuckScoreMessage(request);
                break;
            case MessageCommand.GAME_FEST_PAY_CUMULATE_MESSAGE:
                gameRole.getActivityManager().processFestPayContinueReceive(request);
                break;
            case MessageCommand.GAME_FEST_PAY_CON_MESSAGE:
                gameRole.getActivityManager().processFestPayConMessage(request);
                break;
            case MessageCommand.GAME_FIRECRACKER_MESSAGE:
                gameRole.getActivityManager().processInitFirecrackerLuckScoreMessage(request);
                break;
            case MessageCommand.GAME_FIRECRACKER_LUCK_SCORE_MESSAGE:
                gameRole.getActivityManager().processFirecrackerLuckScore(request);
                break;
            case MessageCommand.GAME_FIRECRACKER_MESSAGE_PROCESS:
                gameRole.getActivityManager().processLuckFirecrackerMessage(request);
                break;
            case MessageCommand.GAME_CONSUM_CUMULATE_FIXED_MESSAGE:
                gameRole.getActivityManager().processConsumCumulateFixedMessage(request);
                break;
            case MessageCommand.GAME_CONSUM_CUMULATE_FIXED_RECEIVE:
                gameRole.getActivityManager().processConsumCumulateFixedReceive(request);
                break;
            case MessageCommand.GAME_MONOPOLY_RECEIVE:
                gameRole.getActivityManager().processMonopolyReceive(request);
                break;
            case MessageCommand.GAME_MONOPOLY_DICE:
                gameRole.getActivityManager().processMonopolyDice(request);
                break;
            case MessageCommand.GAME_PUZZLE_RECEIVE:
                gameRole.getActivityManager().processPuzzleReceive(request);
                break;
            case MessageCommand.GAME_PUZZLE_DICE:
                gameRole.getActivityManager().processPuzzleDice(request);
                break;
            case MessageCommand.GAME_NEW_YEAR_RECEIVE: //1025 新年登录活动
                gameRole.getActivityManager().processNewYearLogonReward(request);
                break;
//			case MessageCommand.GAME_KAM_PO2_MESSAGE: //1026 鉴宝2信息
//				gameRole.getActivityManager().processKamPo2Message(request);
//				break;
//			case MessageCommand.GAME_LUCK_SCORE2_MESSAGE: //1027 鉴宝幸运值2信息
//				gameRole.getActivityManager().processLuckScore2(request);
//				break;
//			case MessageCommand.GAME_INIT_LUCK_SCORE2_MESSAGE: //1028 鉴宝幸运值2初始化
//				gameRole.getActivityManager().processInitLuckScore2Message(request);
//				break;
            case MessageCommand.GAME_NO_REPEAT_TURNTABLE_DICE: //1029 至尊转盘抽一次
                gameRole.getActivityManager().processNoRepeatTurntableDice(request);
                break;
            case MessageCommand.GAME_NO_REPEAT_TURNTABLE_REFRESH: //1030 至尊转盘刷新时间
                gameRole.getActivityManager().processNoRepeatTurntableRefresh(request);
                break;
            case MessageCommand.GAME_KAM_PO3_MESSAGE: //1031 鉴宝2
                gameRole.getActivityManager().processKamPo2Message(request);
                break;
            case MessageCommand.GAME_TREASURES_BUY: //1032 秘宝购买
                gameRole.getActivityManager().processTreasureBuy(request);
                break;
            case MessageCommand.GAME_TREASURES_REFRESH: //1033 秘宝刷新道具
                gameRole.getActivityManager().processTreasureRefresh(request);
                break;
            case MessageCommand.GAME_TREASURES_REFRESH_TIME: //1034 秘宝免费状态刷新
                gameRole.getActivityManager().processTreasuresRefreshTime(request);
                break;
            case MessageCommand.GAME_TREASURES_VOUCHERS_RECEIVE: //1035 秘宝代金券领取
                gameRole.getActivityManager().processTreasureVouchersReceive(request);
                break;
            case MessageCommand.GAME_TREASURES_BUY_RECORD: //1036 秘宝购买记录
                gameRole.getActivityManager().processTreasureBuyRecord(request);
                break;
            case MessageCommand.GAME_SET_WORDS_MESSAGE: //1037 集字兑换
                gameRole.getActivityManager().processSetWords(request);
                break;
            case MessageCommand.GAME_ITEM_CALLBACK_MESSAGE: //1038 道具回收
                gameRole.getActivityManager().processCallBackItems(request);
                break;
            case MessageCommand.GAME_MONOPOLY1_DICE: //1039 探宝2抽取
                gameRole.getActivityManager().processMonopoly1Dice(request);
                break;
            case MessageCommand.GAME_MONOPOLY1_RECEIVE: //1040 探宝2领取
                gameRole.getActivityManager().processMonopoly1Receive(request);
                break;
            case MessageCommand.GAME_MONOPOLY1_EXCHANGE: //1041 探宝2兑换
                gameRole.getActivityManager().processMonopoly1Exchange(request);
                break;
            case MessageCommand.GAME_TARGET_DAILY_CONSUME_MESSAGE: //1042 每日累计消费达标排行榜
                gameRole.getActivityManager().processTargetConsumeTop(request);
                break;
            case MessageCommand.GANG_INFO_MESSAGE:    //1101 公会信息
                gameRole.getGangManager().processGangInfo(request);
                break;
            case MessageCommand.GANG_LIST_MESSAGE:    //1102 公会列表
                gameRole.getGangManager().processGangList(request);
                break;
            case MessageCommand.GANG_CREATE_MESSAGE:    //1103 公会创建
                gameRole.getGangManager().processGangCreate(request);
                break;
            case MessageCommand.GANG_SEARCH_MESSAGE:    //1104 公会搜索
                gameRole.getGangManager().processGangSearch(request);
                break;
            case MessageCommand.GANG_DECLARATION_MODIFY_MESSAGE:    //1105 公会宣言修改
                gameRole.getGangManager().processGangDeclarationModify(request);
                break;
            case MessageCommand.GANG_NOTICE_MODIFY_MESSAGE:        //1106 公会公告修改
                gameRole.getGangManager().processGangNoticeModify(request);
                break;
            case MessageCommand.GANG_LIMIT_LEVEL_MESSAGE:        //1107 公会限制等级
                gameRole.getGangManager().processGangLimitLevel(request);
                break;
            case MessageCommand.GANG_AUTO_ADOPT_MESSAGE:        //1108 公会自动审核
                gameRole.getGangManager().processGangAutoAdopt(request);
                break;
            case MessageCommand.GANG_MEMBER_LIST_MESSAGE:        //1109 公会成员列表
                gameRole.getGangManager().processGangMemberList(request);
                break;
            case MessageCommand.GANG_APPLY_MESSAGE:                //1110 公会申请
                gameRole.getGangManager().processGangApply(request);
                break;
            case MessageCommand.GANG_APPOINT_MESSAGE:            //1111 公会任命
                gameRole.getGangManager().processGangAppoint(request);
                break;
            case MessageCommand.GANG_DISMISS_MESSAGE:            //1112 公会踢人
                gameRole.getGangManager().processGangDismiss(request);
                break;
            case MessageCommand.GANG_OVER_MESSAGE:                //1113 公会解散
                gameRole.getGangManager().processGangOver(request);
                break;
            case MessageCommand.GANG_EXIT_MESSAGE:                //1114 公会退出
                gameRole.getGangManager().processGangExit(request);
                break;
            case MessageCommand.GANG_APPLY_LIST_MESSAGE:        //1115 公会申请列表
                gameRole.getGangManager().processGangApplyList(request);
                break;
            case MessageCommand.GANG_ADOPT_MESSAGE:                //1116 公会审核
                gameRole.getGangManager().processGangAdopt(request);
                break;
            case MessageCommand.GANG_DONATE_MESSAGE:            //1117 公会捐献
                gameRole.getGangManager().processDonate(request);
                break;
            case MessageCommand.GANG_SKILL_LEVELUP:                //1118 公会技能
                gameRole.getGangManager().processSkill(request);
                break;
            case MessageCommand.GANG_DIAL_MESSAGE:                //1119 公会转盘
                gameRole.getGangManager().processDial(request);
                break;
            case MessageCommand.GANG_LOG_MESSAGE:                //1121 公会日志
                gameRole.getGangManager().processLog(request);
                break;
            case MessageCommand.GANG_SKILL2_LEVELUP:                //1122 公会技能2
                gameRole.getGangManager().processSkill2(request);
                break;
            case MessageCommand.GANG_TURNTABLE_LOG:                //1123 帮会转盘日志
                gameRole.getGangManager().processTurntableLog(request);
                break;
            case MessageCommand.GANG_MISSION_LIST:                //1124 帮会任务列表
                gameRole.getGangManager().processMissionList(request);
                break;
            case MessageCommand.GANG_DUNGEON_PASS_RECEIVE_MESSAGE:    //1126 帮会副本每日通关奖励
                gameRole.getGangManager().processDungeonPassReceive(request);
                break;
            case MessageCommand.GANG_DUNGEON_RANK_MESSAGE:    //1127 帮会副本排行榜
                gameRole.getGangManager().processDungeonRank(request);
                break;
            case MessageCommand.GANG_DUNGEON_CHEER_MESSAGE:    //1128 帮会副本助威
                gameRole.getGangManager().processDungeonCheer(request);
                break;
            case MessageCommand.GANG_IMPEACHMENT_MESSAGE:    //1129 帮会弹劾帮主
                gameRole.getGangManager().processImpeachment(request);
                break;
            case MessageCommand.GANG_BOSS_INFO_MESSAGE:            //公会BOSS界面信息
                gameRole.getGangManager().processBossInfo(request);
                break;
            case MessageCommand.GANG_BOSS_LIST_MESSAGE:            //公会BOSS列表
                break;
            case MessageCommand.GANG_BOSS_START_MESSAGE:        //公会BOSS开始战斗
                gameRole.getGangManager().processBossStart(request);
                break;
            case MessageCommand.GANG_BOSS_RESULT_MESSAGE:        //公会BOSS战斗结果
                gameRole.getGangManager().processBossResult(request);
                break;
            case MessageCommand.GANG_FIGHT_INFO_MESSAGE:        //1140 帮会战信息
                gameRole.getGangManager().processFightInfo(request);
                break;
            case MessageCommand.GANG_FIGHT_JOIN_INFO_MESSAGE:        //1141 帮会战参战信息
                gameRole.getGangManager().processFightJoinInfo(request);
                break;
            case MessageCommand.GANG_FIGHT_REQUEST_MESSAGE:        //1142 帮会战战斗请求
                gameRole.getGangManager().processFightRequest(request);
                break;
            case MessageCommand.GANG_FIGHT_RESULT_MESSAGE:        //1143 帮会战战斗结果
                gameRole.getGangManager().processFightResult(request);
                break;
            case MessageCommand.GANG_FIGHT_RANK_MESSAGE:        //1144 帮会战排名
                gameRole.getGangManager().processFightRank(request);
                break;
            case MessageCommand.GANG_FIGHT_REWARD_ASSIGN_MESSAGE:        //1145 帮会战奖励分配
                gameRole.getGangManager().processFightRewardAssign(request);
                break;
            case MessageCommand.GANG_FIGHT_STORE_MESSAGE:        //1146 帮会战仓库
                gameRole.getGangManager().processFightStore(request);
                break;
            case MessageCommand.GANG_FIGHT_MEMBER_RANK_MESSAGE:        //1147 帮会战个人排名
                gameRole.getGangManager().processFightMemberRank(request);
                break;
            case MessageCommand.GANG_FIGHT_JOIN_REFRESH_MESSAGE:        //1148 帮会战参战刷新
                gameRole.getGangManager().processFightRefresh(request);
                break;
            case MessageCommand.GANG_FIGHT_JOIN_MESSAGE:            //1149 帮会战参战
                gameRole.getGangManager().processFightJoin(request);
                break;
            case MessageCommand.GANG_SHOP_BUY_INIT_MESSAGE:    //1150 获取帮会购买信息
                gameRole.getGangManager().getGangShopBuyMessage(request);
                break;
            case MessageCommand.GANG_SHOP_BUY_MESSAGE: //1151 帮会商店购买
                gameRole.getGangManager().processGangShopBuyMessage(request);
                break;
//			case MessageCommand.RELATIONSHIP_GET_LIST_MESSAGE:	//获取关系列表 1301
//				gameRole.getRelationshipManager().processGetListMessage(request);
//				break;
//			case MessageCommand.RELATIONSHIP_APPLY_MESSAGE: 	 //关系申请 1302
//				gameRole.getRelationshipManager().processApplyMessage(request);
//				break;
//			case MessageCommand.RELATIONSHIP_PROCESS_APPLICATION_MESSAGE: //处理关系申请 1303
//				gameRole.getRelationshipManager().processApplicationMessage(request);
//				break;
//			case MessageCommand.RELATIONSHIP_PROCESS_BLACK_MESSAGE: 	//拉黑 1304
//				gameRole.getRelationshipManager().processBlackMessage(request);
//				break;
//			case MessageCommand.RELATIONSHIP_REMOVE_MESSAGE:
//				gameRole.getRelationshipManager().processRemoveMessage(request); //移除关系 1305
//				break;
//			case MessageCommand.AUCTION_GET_ITEMS:
//				gameRole.getAuctionManager().processGetItems(request); //获取拍品信息 1401
//				break;
//			case MessageCommand.AUCTION_ADD_PRICE:
//				gameRole.getAuctionManager().processAddPrice(request); //竞拍 1402
//				break;
//			case MessageCommand.AUCTION_GET_LOGS:
//				gameRole.getAuctionManager().processGetLogs(request); //获取拍卖日志 1403
//				break;
//			case MessageCommand.AUCTION_UPDATE_SUBSCRIPTIONS:
//				gameRole.getAuctionManager().processUpdateSubscribe(request); //更新拍卖关注 1404
//				break;
//			case MessageCommand.AUCTION_GET_INCOME:
//				gameRole.getAuctionManager().processGetIncome(request); //获取收益信息 1406
//				break;
            case MessageCommand.SHENBING_UPSTAR_MESSAGE:
                gameRole.getLianTiManager().processPromoteShenBing(request);
                break;
            case MessageCommand.FIGHT_REQUEST_MESSAGE: //2001 请求战斗
                gameRole.getFightManager().processFightRequest(request);
                break;
            case MessageCommand.FIGHT_RESULT_MESSAGE:    //2002 战斗结果
                gameRole.getFightManager().processFightResult(request);
                break;
            case MessageCommand.FIGHT_DUNGEON_REQUEST_MESSAGE:    //2003 请求副本战斗
                gameRole.getDungeonManager().processDungeonRquest(request);
                break;
            case MessageCommand.FIGHT_DUNGEON_RESULT_MESSAGE:    //2004 副本战斗结果
                gameRole.getDungeonManager().processDungeonResult(request);
                break;
            case MessageCommand.DUNGEON_STATE_MESSAGE:    //2005 请求副本状态
                gameRole.getDungeonManager().processDungeonState(request);
                break;
            case MessageCommand.DUNGEON_BUY_MESSAGE:    //2006 购买副本次数
                gameRole.getDungeonManager().processDungeonBuy(request);
                break;
            case MessageCommand.DUNGEON_SWEEP_MESSAGE:    //2007 扫荡副本
                gameRole.getDungeonManager().processDungeonSweep(request);
                break;
            case MessageCommand.DUNGEON_MATERIAL_ONEKEY_MESSAGE:    //2008 一键扫荡材料副本
                gameRole.getDungeonManager().processMaterialOneKey(request);
                break;
            case MessageCommand.DUNGEON_PERSONALBOSS_ONEKEY_MESSAGE:    //2009 一键扫荡个人boss
                gameRole.getDungeonManager().processPersonalBossOneKey(request);
                break;
            case MessageCommand.FIGHT_FIELD_MONSTER_REQUEST_MESSAGE: //2011 请求战斗
                gameRole.getFightManager().processFieldMonsterFightRequest(request);
                break;
            case MessageCommand.FIGHT_FIELD_MONSTER_RESULT_MESSAGE:    //2012 战斗结果
                gameRole.getFightManager().processFieldMonsterFightResult(request);
                break;
            case MessageCommand.FIGHT_FIELD_BOSS_REQUEST_MESSAGE: //2013 请求战斗
                gameRole.getFightManager().processFieldBossFightRequest(request);
                break;
            case MessageCommand.FIGHT_FIELD_BOSS_RESULT_MESSAGE:    //2014 战斗结果
                gameRole.getFightManager().processFieldBossFightResult(request);
                break;
            case MessageCommand.DUNGEON_HOLY_STATE_MESSAGE: //2020 圣物副本状态
                gameRole.getDungeonManager().processHolyState(request);
                break;
            case MessageCommand.DUNGEON_HOLY_FIGHT_REQUEST_MESSAGE: //2021 圣物副本请求战斗
                gameRole.getDungeonManager().processHolyFightRequest(request);
                break;
            case MessageCommand.DUNGEON_HOLY_FIGHT_PASS_MESSAGE: //2022 圣物副本通关
                gameRole.getDungeonManager().processHolyFightPass(request);
                break;
            case MessageCommand.DUNGEON_HOLY_FIGHT_RESULT_MESSAGE: //2023 圣物副本战斗结果
                gameRole.getDungeonManager().processHolyFightResult(request);
                break;
            case MessageCommand.DUNGEON_HOLY_RECEIVE_MESSAGE: //2024 圣物副本领取宝箱
                gameRole.getDungeonManager().processHolyReceive(request);
                break;
//			case MessageCommand.CROSS_ARENA_CHALLENGE_INFO_MESSAGE://2101 竞技场挑战数据
//				gameRole.getCrossManager().processArenaChallengeInfo(request);
//				break;
//			case MessageCommand.CROSS_ARENA_CHALLENGE_REFRESH_MESSAGE://2102 竞技场挑战刷新
//				gameRole.getCrossManager().processArenaChallengeRefresh(request);
//				break;
//			case MessageCommand.CROSS_ARENA_BATTLE_MESSAGE://2103 竞技场战斗请求
//				gameRole.getCrossManager().processArenaBattle(request);
//				break;
//			case MessageCommand.CROSS_ARENA_RANK_LIST_MESSAGE://2104 竞技场排行
//				gameRole.getCrossManager().processArenaRankList(request);
//				break;
//			case MessageCommand.CROSS_ARENA_BATTLE_RECORD_MESSAGE://2105 竞技场战斗记录
//				gameRole.getCrossManager().processArenaRecord(request);
//				break;
//			case MessageCommand.CROSS_ARENA_BATTLE_COUNT_BUY://2107 竞技场次数购买
//				gameRole.getCrossManager().processArenaCountBuy(request);
//				break;
            case MessageCommand.ZHANWEN_ACTIVE_MESSAGE://2202 战纹装备
                gameRole.getZhanWenManager().processZhanWenActive(request);
                break;
            case MessageCommand.ZHANWEN_UPGRADE_MESSAGE://2203 战纹升级
                gameRole.getZhanWenManager().processZhanWenUpGrade(request);
                break;
            case MessageCommand.ZHANWEN_RES_MESSAGE://2204 战纹分解
                gameRole.getZhanWenManager().processZhanWenRes(request);
                ;
                break;
            case MessageCommand.BOSS_MYSTERY_INFO_MESSAGE:    //2206 秘境BOSS信息
                gameRole.getBossManager().processMysteryInfo(request);
                break;
            case MessageCommand.BOSS_MYSTERY_START_MESSAGE:    //2207 秘境BOSS战斗开始
                gameRole.getBossManager().processMysteryStart(request);
                break;
            case MessageCommand.BOSS_MYSTERY_QUIT_MESSAGE:    //2208 秘境BOSS战斗退出
                gameRole.getBossManager().processMysteryQuit(request);
                break;
            case MessageCommand.BOSS_MYSTERY_REWARD_MESSAGE://2209 秘境BOSS奖励
                gameRole.getBossManager().processMysteryReward(request);
                break;
            case MessageCommand.BOSS_MYSTERY_FIGHT_MESSAGE:    //2210 秘境BOSS战斗
                gameRole.getBossManager().processMysteryFight(request);
                break;
            case MessageCommand.BOSS_MYSTERY_CUE_MESSAGE:    //2213 秘境BOSS提醒设置
                gameRole.getBossManager().processMysteryCue(request);
                break;
            case MessageCommand.BOSS_MYSTERY_PK_MESSAGE:    //2215 秘境BOSS PK第一名
                gameRole.getBossManager().processMysteryPK(request);
                break;
            case MessageCommand.BOSS_MYSTERY_PLAYER_REVIVE_MESSAGE: //2216 秘境BOSS玩家复活
                gameRole.getBossManager().processMysteryRevive(request);
                break;
            case MessageCommand.BOSS_VIP_INFO_MESSAGE:    //2217 BOSS之家信息
                gameRole.getBossManager().processVipBossInfo(request);
                ;
                break;
            case MessageCommand.BOSS_VIP_START_MESSAGE:    //2218 BOSS之家战斗开始
                gameRole.getBossManager().processVipBossStart(request);
                break;
            case MessageCommand.BOSS_VIP_QUIT_MESSAGE:    //2219 BOSS之家战斗退出
                gameRole.getBossManager().processVipBossQuit(request);
                break;
            case MessageCommand.BOSS_VIP_REWARD_MESSAGE://2220 BOSS之家奖励
                gameRole.getBossManager().processVipBossReward(request);
                break;
            case MessageCommand.BOSS_VIP_FIGHT_MESSAGE:    //2221 BOSS之家战斗
                gameRole.getBossManager().processVipBossFight(request);
                ;
                break;
            case MessageCommand.BOSS_VIP_CUE_MESSAGE:    //2224 BOSS之家提醒设置
                gameRole.getBossManager().processVipBossCue(request);
                break;
            case MessageCommand.BOSS_VIP_PK_MESSAGE:    //2226 BOSS之家 PK第一名
                gameRole.getBossManager().processVipBossPK(request);
                break;
            case MessageCommand.BOSS_VIP_PLAYER_REVIVE_MESSAGE: //2227 BOSS之家玩家复活
                gameRole.getBossManager().processVipBossRevive(request);
                break;
            case MessageCommand.DUNGEON_FENGMO_DAILY_STATE_MESSAGE: //2301 封魔塔领取状态
                gameRole.getDungeonManager().processFengmoDailyState(request);
                break;
            case MessageCommand.DUNGEON_FENGMO_DAILY_RECEIVE_MESSAGE: //2302 封魔塔每日领取
                gameRole.getDungeonManager().processFengmoDailyReceive(request);
                break;
            case MessageCommand.TOWN_SOUL_UPGRADE: //2401 镇魂装备升级
                gameRole.getEquipManager().processTownSoulUpgrade(request);
                break;
            case MessageCommand.TOWN_SOUL_COMPOSE: //2402 镇魂装备合成
                gameRole.getEquipManager().processTownSoulCompose(request);
                break;
            case MessageCommand.TOWN_SOUL_DECOMPOSE: //2403 镇魂装备分解
                gameRole.getEquipManager().processTownSoulDecompose(request);
                break;
            case MessageCommand.TOWN_SOUL_TREASURE: //2404镇魂宝库
                gameRole.getEquipManager().processTownSoulTreasure(request);
                break;
            case MessageCommand.TOWN_SOUL_TREASURE_TURNTABLE: //2405镇魂宝库
                gameRole.getEquipManager().processTownSoulTreasureTurntable(request);
                break;
            case MessageCommand.TOWN_SOUL_TREASURE_OUT: //2406镇魂宝库取出
                gameRole.getEquipManager().processTownSoulTreasureOut(request);
                break;
            case MessageCommand.TOWN_SOUL_TREASURE_BOX: //2407镇魂宝库领取抽取宝箱
                gameRole.getEquipManager().processTownSoulTreasureBox(request);
                break;
            case MessageCommand.FIELD_PVP_INFO_MESSAGE:            //2801 野外PVP信息
                gameRole.getPvpManager().processInfo(request);
                break;
            case MessageCommand.FIELD_PVP_SEARCH_MESSAGE:        //2802 野外PVP寻找挑战者
                gameRole.getPvpManager().processSearch(request);
                break;
            case MessageCommand.FIELD_PVP_RANK_MESSAGE:            //2803 野外PVP排行榜
                gameRole.getPvpManager().processRank(request);
                break;
            case MessageCommand.FIELD_PVP_RECORD_MESSAGE:        //2804 野外PVP战斗记录
                gameRole.getPvpManager().processRecord(request);
                break;
            case MessageCommand.FIELD_PVP_REQUEST_MESSAGE:        //2805 野外PVP战斗请求
                gameRole.getPvpManager().processRequest(request);
                break;
            case MessageCommand.FIELD_PVP_RESULT_MESSAGE:        //2806 野外PVP战斗结果
                gameRole.getPvpManager().processResult(request);
                break;
//			case MessageCommand.WANBA_DESK_INFO_MESSAGE:		//3001玩吧发送桌面信息
//				gameRole.getSmallDataManager().processDeskInfo(request);
//				break;
//			case MessageCommand.WANBA_DESK_REWARD_MESSAGE:		//3002玩吧发送桌面领奖
//				gameRole.getSmallDataManager().processDeskReward(request);
//				break;
            case MessageCommand.LIMIT_TASK_TIP_MESSAGE:        //3010 限时任务小广告状态
                gameRole.getSectionManager().processLimitTaskTip(request);
                break;
            case MessageCommand.LIMIT_TASK_TIP_SHOW_MESSAGE:    //3011限时任务小广告已显示
                gameRole.getSectionManager().processLimitTaskTipShow(request);
                break;
            case MessageCommand.STATE_RECORD_MESSAGE:    //3012状态记录
                gameRole.getSectionManager().processStateRecord(request);
                break;
            case MessageCommand.STATE_SEARCH_MESSAGE:    //3013状态查询
                gameRole.getSectionManager().processStateSearch(request);
                break;
            case MessageCommand.STARCRAFT_INFO_MESSAGE:    //3020 传世争霸信息
                gameRole.getGangManager().processStarcraftInfo(request);
                break;
            case MessageCommand.STARCRAFT_ENTER_MESSAGE:    //3021 传世争霸参与
                gameRole.getGangManager().processStarcraftEnter(request);
                break;
            case MessageCommand.STARCRAFT_ATTACK_DOOR_MESSAGE:    //3022 传世争霸攻击城门boss
                gameRole.getGangManager().processStarcraftAttackDoor(request);
                break;
            case MessageCommand.STARCRAFT_MOVE_MESSAGE:    //3024 传世争霸传送
                gameRole.getGangManager().processStarcraftMove(request);
                break;
            case MessageCommand.STARCRAFT_ATTACK_TARGET_MESSAGE:    //3029 传世争霸攻击目标
                gameRole.getGangManager().processStarcraftAttack(request);
                break;
            case MessageCommand.STARCRAFT_ATTACK_GUARD_MESSAGE:    //3029 传世争霸攻击守卫
                gameRole.getGangManager().processStarcraftAttackGuard(request);
                break;
            case MessageCommand.STARCRAFT_COLLECT_MESSAGE:    //3033 传世争霸采旗
                gameRole.getGangManager().processStarcraftCollect(request);
                break;
            case MONSTER_SIEGE_GET_INFO_MESSAGE:    // 怪物攻城
                gameRole.getMonsterSiegeManager().getInfoMessage(request);
                return;
            case MONSTER_SIEGE_START_MESSAGE:        // 怪物攻城开始战斗
                gameRole.getMonsterSiegeManager().processStartMessage(request);
                return;
            case MONSTER_SIEGE_FIGHT_MESSAGE:        // 怪物攻城手动攻击
                gameRole.getMonsterSiegeManager().processFightMessage(request);
                return;
            case MONSTER_SIEGE_QUIT_MESSAGE:        // 怪物攻城退出战斗
                gameRole.getMonsterSiegeManager().processQuitMessage(request);
                return;
            case MONSTER_BOX_RECEIVE_MESSAGE:        // 怪物攻城宝箱领取
                gameRole.getMonsterSiegeManager().processReceiveBox(request);
                return;
            case MONSTER_SIEGE_RECORD_MESSAGE:        // 怪物攻城守城记录
                gameRole.getMonsterSiegeManager().getRecordList(request);
                return;
            case MONSTER_SIEGE_RANK_MESSAGE:        // 怪物攻城排行
                gameRole.getMonsterSiegeManager().getRankListMessage(request);
                return;
            case MessageCommand.STARCRAFT_REWARD_MESSAGE:    //3037传世争霸领取每日奖励
                gameRole.getGangManager().processStarcraftReward(request);
                break;
            case STARCRAFT_COUNTDOWN_MESSAGE:                //3038传世争霸倒计时
                gameRole.getGangManager().processStarcraftCountDown(request);
                break;
            case CARD_LEVEL_UP_MESSAGE:
                gameRole.getCardManager().processCardLevelUp(request);
                break;
            case GROUP_1_MESSAGE:
                gameRole.getSectionManager().processGroup1(request);
                break;
            case GROUP_2_MESSAGE:
                gameRole.getSectionManager().processGroup2(request);
                break;
            case GROUP_3_MESSAGE:
                gameRole.getSectionManager().processGroup3(request);
                break;
            case MessageCommand.SOUL_GET_MESSAGE:    // 5009灵髓信息
                gameRole.getFunctionManager().processSoulInfoMessage(request);
                break;
            case MessageCommand.SOUL_REPLACE_MESSAGE:    // 5002灵髓替换
                gameRole.getFunctionManager().processSoulReplace(request);
                break;
            case MessageCommand.SOUL_UP_MESSAGE:    // 5003灵髓升级
                gameRole.getFunctionManager().processSoulUpLev(request);
                break;
            case MessageCommand.SOUL_COMPOSE_MESSAGE: // 5004灵髓合成
                gameRole.getFunctionManager().processSoulCompose(request);
                break;
            case MessageCommand.SOUL_COPY_INFO_MESSAGE: // 5005灵髓主宰试炼信息
                gameRole.getFunctionManager().processSoulCopyInfo(request);
                break;
            case MessageCommand.SOUL_CHALLENGE_MESSAGE: // 5006灵髓主宰试炼请求挑战
                gameRole.getFunctionManager().processSoulChallenge(request);
                break;
            case MessageCommand.SOUL_FIGHT_DUNGEON_RESULT_MESSAGE: // 5007灵髓主宰试炼请求战斗结果
                gameRole.getFunctionManager().processSoulFightResult(request);
                break;
            case MessageCommand.SOUL_SWEEP_MESSAGE: //5008灵髓扫荡
                gameRole.getFunctionManager().processSoulSweep(request);
                break;
            case MessageCommand.FAZHEN_INFO_MESSAGE: //6001 法阵图鉴信息
                gameRole.getFunctionManager().processFaZhenInfo(request);
                break;
            case MessageCommand.FAZHEN_ACTIVATE_MESSAGE://6002 法阵激活
                gameRole.getFunctionManager().processFaZhenAct(request);
                break;

            //test message start
//			case MessageCommand.TEST_FIGHTING_MESSAGE: 	//1测试用战斗力校验
//				gameRole.getTestManager().processTestFighting(request);
//				break;
//			case MessageCommand.TEST_LOG_PRINT:
//				gameRole.getTestManager().processLogPrint(request); //2测试用打印
//				break;

            //test message end
        }
    }

    /**
     * 提交任务
     * 为顺序执行
     */
    public static void submit(int playerId, Runnable task) {
        protocolExec.submit(playerId, task);
    }
}
