package com.rd.game.manager;

import com.google.common.base.Preconditions;
import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.*;
import com.rd.activity.data.MonopolyLogicData.NumInfo;
import com.rd.activity.data.MonopolyLogicData1.JubaogeInfo;
import com.rd.activity.data.MonopolyLogicData1.LevelInfo;
import com.rd.activity.data.MonopolyLogicData1.StepInfo;
import com.rd.activity.data.NoRepeatTurntableLogicData.NoRepeatTurntableTargetInfo;
import com.rd.activity.data.TreasuresLogicData.BoughtRecord;
import com.rd.activity.data.TreasuresLogicData.Vouchers;
import com.rd.activity.event.RedPacketEvent;
import com.rd.activity.event.TargetEvent;
import com.rd.activity.group.ActivityGroupData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.activitynew.ActivityNewService;
import com.rd.activitynew.EActivityNewType;
import com.rd.activitynew.data.ActivityNewData;
import com.rd.activitynew.data.ActivityNewGroupData;
import com.rd.activitynew.data.ActivityNewOpenData;
import com.rd.activitynew.info.IActivityInfo;
import com.rd.bean.data.ShopItem;
import com.rd.bean.drop.DropData;
import com.rd.bean.goods.Goods;
import com.rd.bean.goods.data.BoxData;
import com.rd.bean.goods.data.CallBackGoodsData;
import com.rd.bean.goods.data.EquipData;
import com.rd.bean.goods.data.ItemData;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.Activity7Mission;
import com.rd.bean.player.Player;
import com.rd.bean.player.PlayerActivity;
import com.rd.bean.rank.ActivityRank;
import com.rd.common.ChatService;
import com.rd.common.DialService;
import com.rd.common.MailService;
import com.rd.common.goods.EGoodsType;
import com.rd.common.goods.IGoodsCmd;
import com.rd.dao.ActivityDao;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.*;
import com.rd.game.GameRankManager;
import com.rd.game.GameRole;
import com.rd.game.PlayerFirecrackerInfosService;
import com.rd.game.PlayerFirecrackerInfosService.PlayerFirecrackerInfo;
import com.rd.game.PlayerTurntableInfosService;
import com.rd.game.PlayerTurntableInfosService.PlayerTurntableInfo;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.model.*;
import com.rd.model.activity.Activity7Model;
import com.rd.model.data.*;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import com.rd.util.DiceUtil;
import com.rd.util.DiceUtil.Ele;
import com.rd.util.GameUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.*;
import java.util.Map.Entry;

/**
 * 活动管理器，处理个人活动逻辑
 *
 * @author Created by U-Demon on 2016年11月3日 下午8:28:11
 * @version 1.0.0
 */
public class ActivityManager {

    private static Logger logger = Logger.getLogger(ActivityManager.class);

    private GameRole role;

    private Player player;

    //角色活动数据
    private PlayerActivity _activityData;

    private ActivityDao _dao;

    //0--未获得,1--已获得
    private static final byte UNDO = 0;
    private static final byte DO = 1;
    //购买投资计划消耗
    private static final DropData INVEST_COST = new DropData(EGoodsType.DIAMOND, 0, 888);

    public ActivityManager(GameRole gameRole) {
        this.role = gameRole;
        this.player = gameRole.getPlayer();
    }

    private void init() {
        logger.debug("初始化活动管理器");
        _dao = new ActivityDao();
        _activityData = _dao.getPlayerActivity(player.getId());
    }

    public PlayerActivity getActivityData() {
        if (null == _activityData) {
            init();
        }
        return _activityData;
    }

    public ActivityDao getActivityDao() {
        if (null == _dao) {
            init();
        }
        return _dao;
    }

    /**
     * 获取活动大厅消息
     *
     * @return
     */
    public Message getActivityMsg() {
        long curr = System.currentTimeMillis();
        Message msg = new Message(MessageCommand.GAME_ACTIVITY_MESSAGE);
        Set<EActivityType> types = new HashSet<>();
        for (EActivityType type : EActivityType.values()) {
            BaseActivityConfig configData = ActivityService.getActivityConfig(type);
            if (type.isOnlyShow() && configData != null && configData.getCurrRound(player.getId(), curr).getStartTime() < curr
                    && configData.getCurrRound(player.getId(), curr).getEndTime() > curr) {
                types.add(type);
                continue;
            }
            if (type.getId() == 35) {
                if (player.getFiveElements().getFuse() == 1 && (DateUtil.getDayStartTime(player.getSmallData().getFiveFuseTime()) + 5 * 24 * 60 * 60 * 1000) - curr > 0) {
                    types.add(type);
                }
            } else {
                BaseActivityConfig config = ActivityService.getActivityConfig(type);
                //活动是否在本服开放
                if (config == null || !config.containsServer(player.getChannel(), GameDefine.getServerId()))
                    continue;
                //活动开启
                if (ActivityService.getRoundData(type, player.getId(), curr) != null) {
                    if (type.getEvent().isOpen(role))
                        types.add(type);
                }
            }

        }
        //所有开启的活动
        msg.setByte(types.size());
        for (EActivityType type : types) {
            msg.setInt(type.getId());
            msg.setByte(1);
            msg.setByte(ActivityService.getActivityConfig(type).getOrder());
            msg.setInt(getActivityLeftSec(type));
            //具体活动对应的消息
            type.getEvent().getMessage(msg, role);

        }
        return msg;
    }

    public Message getActivityMsg(EActivityType type) {
        long curr = System.currentTimeMillis();
        Message msg = new Message(MessageCommand.GAME_ACTIVITY_MESSAGE);
        msg.setByte(1);
        msg.setInt(type.getId());
        boolean open = false;
        if (ActivityService.getRoundData(type, player.getId(), curr) != null
                && type.getEvent().isOpen(role)) {
            open = true;
        }
        //投资计划
        if (type == EActivityType.INVEST) {
            if (getActivityData().getInvests().size() > 0 && EActivityType.INVEST.getEvent().isOpen(role))
                open = true;
        }
        if (open) {
            msg.setByte(1);
            msg.setByte(ActivityService.getActivityConfig(type).getOrder());
            msg.setInt(getActivityLeftSec(type));
            type.getEvent().getMessage(msg, role);
        } else {
            msg.setByte(0);
        }
        return msg;
    }

    public Message getActivityMsg(EnumSet<EActivityType> actEnum) {
        long curr = System.currentTimeMillis();

        Message msg = new Message(MessageCommand.GAME_ACTIVITY_MESSAGE);
        msg.setByte(actEnum.size());
        for (EActivityType type : actEnum) {

            msg.setInt(type.getId());
            boolean open = false;
            BaseActivityConfig configData = ActivityService.getActivityConfig(type);
            if ((type.isOnlyShow() && configData != null
                    && configData.getCurrRound(player.getId(), curr).getStartTime() < curr
                    && configData.getCurrRound(player.getId(), curr).getEndTime() > curr)
                    || (ActivityService.getRoundData(type, player.getId(), curr) != null
                    && type.getEvent().isOpen(role))) {
                open = true;
            }
            //投资计划
            if (type == EActivityType.INVEST) {
                if (getActivityData().getInvests().size() > 0 && EActivityType.INVEST.getEvent().isOpen(role))
                    open = true;
            }
            if (open) {
                msg.setByte(1);
                msg.setByte(ActivityService.getActivityConfig(type).getOrder());
                msg.setInt(getActivityLeftSec(type));
                type.getEvent().getMessage(msg, role);
            } else {
                msg.setByte(0);
            }
        }
        return msg;
    }

    /**
     * 刷新充值活动
     */
    public void sendActivityPayUpdateMessage() {
        for (EActivityType activityType : EActivityType.payActivities) {
            Message message = getActivityMsg(activityType);
            role.putMessageQueue(message);
        }
    }

    public int getActivityLeftSec(EActivityType type) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(type);
        ActivityRoundConfig currRound = configData.getCurrRound(role.getPlayerId(), curr);
        long leftSec = 0;
        if (type.getId() != 35) {
            leftSec = (currRound.getEndTime() - curr) / 1000;
        } else {
            leftSec = ((DateUtil.getDayStartTime(player.getSmallData().getFiveFuseTime()) + 5 * 24 * 60 * 60 * 1000) - curr) / 1000;
        }
        if (leftSec > Integer.MAX_VALUE)
            leftSec = Integer.MAX_VALUE;
        return (int) leftSec;
    }

    /**
     * 处理登录相关的活动
     */
    public void handleLogin() {
        long curr = System.currentTimeMillis();
        long lastLogin = player.getLastLoginTime();
        long createTime = player.getCreateTime();
        boolean saveLogin = false;
        //跨天登录
        if (!DateUtil.dayEqual(lastLogin, curr)) {
            getActivityData().addLoginDay(player, curr);
            saveLogin = true;
        }
        //连续登陆信息
        int theDay = DateUtil.getDistanceDay(createTime, curr);
        if (theDay < Activity7Model.ACTIVE_DAY + 200) {
            for (int i = getActivityData().getLoginInfos().size(); i <= theDay; ++i) {
                if (i < Activity7Model.ACTIVE_DAY)
                    getActivityData().getLoginInfos().add((byte) 1);
                else
                    getActivityData().getLoginInfos().add((byte) 0);
            }
            if (getActivityData().getLoginInfos().get(theDay) == 0) {
                getActivityData().getLoginInfos().set(theDay, (byte) 1);
                saveLogin = true;
            }
        }
        player.setLastLoginTime(curr);
        //保存数据
        if (saveLogin) {
            getActivityDao().updateLoginInfo(getActivityData());
            role.savePlayer(EnumSet.of(EPlayerSaveType.LOGINTIME));
        }
        //商城数据
        if (getActivityData().getPlayerShopItems().size() == 0) {
            getActivityData().setPlayerShopRefresh(curr);
            refreshShopPlayer();
        }
    }

    /**
     * 不在登录时更新
     */
    public void handleLogin2NewYear() {
        long curr = System.currentTimeMillis();
        long lastLogin = player.getLastLoginTime2Fest();
        boolean saveLogin = false;
        //跨天登录
        if (!DateUtil.dayEqual(lastLogin, curr) || getActivityData().getNewYearLoginDay() == 0) {
            getActivityData().addNewYearLoginDay(player, curr);
            saveLogin = true;
        }
        //连续登陆信息
//		int theDay = DateUtil.getDistanceDay(createTime, curr);
//		if (theDay < Activity7Model.ACTIVE_DAY + 200)
//		{
//			for (int i = getActivityData().getLoginInfos().size(); i <= theDay; ++i)
//			{
//				if (i < Activity7Model.ACTIVE_DAY)
//					getActivityData().getLoginInfos().add((byte) 1);
//				else
//					getActivityData().getLoginInfos().add((byte) 0);
//			}
//			if (getActivityData().getLoginInfos().get(theDay) == 0)
//			{
//				getActivityData().getLoginInfos().set(theDay, (byte) 1);
//				saveLogin = true;
//			}
//		}
        player.setLastLoginTime2Fest(curr);
        //保存数据
        if (saveLogin) {
            getActivityDao().updateLoginInfo2Fest(getActivityData());
            role.savePlayer(EnumSet.of(EPlayerSaveType.LOGONTIME_FEST));
        }
        //商城数据
        if (getActivityData().getPlayerShopItems().size() == 0) {
            getActivityData().setPlayerShopRefresh(curr);
            refreshShopPlayer();
        }
    }

    /**
     * 处理充值相关
     */
    public void handlePay(int rmb, int diamond) {
        try {
            //今日充值总和
//			int sum = paygetActivityDao().getTodayRMBPay(player);
            //int totalRmb = role.getPayManager().getTodayRmbInPay();
//			int totalDiamond = role.getPayManager().getTodayDiamondInPay();
            //每日福利
//			handlerWelfare(totalRmb);
            //累计充值
            //handlerPayCumulate(diamond);
            //充值盛宴
            handlerPayFeast(diamond);
            //累充好礼
            //handlerLeiChong(rmb, totalRmb);
            //连续充值
            //handlerPayContinue(rmb);
            // 端午连续充值
//			handlerPayContinue2(totalRmb);
            //周末转盘
//			handlerZmzp(diamond);
            //一元抢购
//			handlerBuyOne(rmb);
        } catch (Exception e) {
            logger.error("处理充值相关数据时发生异常！", e);
        }
    }

    /**
     * 购买百倍返利
     *
     * @param request
     */
    public void processRebateBuy(Message request) {
        byte id = request.readByte();
        int type = request.readInt();
        //KEY
        int key = getRebateKey(type, id);
        long curr = System.currentTimeMillis();
        Map<String, RebateLogicData> logicData = ActivityService.getRoundData(
                EActivityType.getType(type), player.getId(), curr);
        if (logicData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        RebateLogicData model = logicData.get(id + "");
        if (model == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //已经购买
        if (getActivityData().getRebates().contains(key)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REBATE_BUY);
            return;
        }
        //购买条件
        if (model.getPre() > 0 && !getActivityData().getRebates().contains(getRebateKey(type, model.getPre()))) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REBATE_PRE);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //消耗元宝
        DropData cost = new DropData(EGoodsType.DIAMOND, 0, model.getPrice());
        if (!role.getPackManager().useGoods(cost, EGoodsChangeType.REBATE_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        getActivityData().getRebates().add(key);
        role.getPackManager().addGoods(model.getRewards(), EGoodsChangeType.REBATE_ADD, saves);
        //发送消息
        Message msg = new Message(MessageCommand.REBATE_BUY_MESSAGE, request.getChannel());
        msg.setInt(type);
        msg.setByte(id);
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        getActivityDao().updateRebateJson(getActivityData());

        ChatService.broadcastPlayerMsg(role.getPlayer(), EBroadcast.REBATE, String.valueOf(model.getPrice()));
    }

    public void processLimitGiftBuy(Message request) {
        byte id = request.readByte();
        short num = request.readShort();
        EActivityType type = EActivityType.LIMIT_GIFT_VIP;
        short cmdId = 0;
        //活动
        if (request.getCmdId() == MessageCommand.LIMITGIFT_LV_BUY_MESSAGE) {
            type = EActivityType.LIMIT_GIFT_LV;
            cmdId = MessageCommand.LIMITGIFT_LV_BUY_MESSAGE;
        } else if (request.getCmdId() == MessageCommand.LIMITGIFT_VIP_BUY_MESSAGE) {
            type = EActivityType.LIMIT_GIFT_VIP;
            cmdId = MessageCommand.LIMITGIFT_VIP_BUY_MESSAGE;
        } else if (request.getCmdId() == MessageCommand.FESTLIMITGIFT_BUY_MESSAGE) {
            type = EActivityType.FEST_LIMIT_GIFT;
            cmdId = MessageCommand.FESTLIMITGIFT_BUY_MESSAGE;
        } else if (request.getCmdId() == MessageCommand.WEEKENDLIMITGIFT_BUY_MESSAGE) {
            type = EActivityType.WEEKEND_LIMIT_GIFT;
            cmdId = MessageCommand.WEEKENDLIMITGIFT_BUY_MESSAGE;
        } else if (request.getCmdId() == MessageCommand.LIMIT_GIFT_DAILY_BUY_MESSAGE) {
            type = EActivityType.FEST_LIMIT_DAILY_GIFT;
            cmdId = MessageCommand.LIMIT_GIFT_DAILY_BUY_MESSAGE;
        }

        //KEY
        int key = getRebateKey(type.getId(), id);
        long curr = System.currentTimeMillis();
        Map<String, LimitGiftLogicData> logicData = ActivityService.getRoundData(
                type, player.getId(), curr);
        if (logicData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        LimitGiftLogicData model = logicData.get(id + "");
        if (model == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //购买条件
        if (type == EActivityType.LIMIT_GIFT_LV) {
            if (player.getLevelWithRein() < model.getLimit()) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
                return;
            }
        } else if (type == EActivityType.LIMIT_GIFT_VIP || type == EActivityType.FEST_LIMIT_GIFT) {
            if (player.getVip() < model.getLimit()) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
                return;
            }
        } else if (type == EActivityType.FEST_LIMIT_DAILY_GIFT) {
            if (player.getVipLevel() < model.getLimit()) {//vip等级不足
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
                return;
            }
        }
        //购买次数
        int count = 0;
        if (getActivityData().getGift().containsKey(key))
            count = getActivityData().getGift().get(key);
        if (count + num > model.getMax()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_NUM_MAX);
            return;
        }

        //判断背包容量
        if (!role.getPackManager().capacityEnough(model.getRewards())) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BAG_FULL_MELT);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //消耗元宝
        if (model.getPrice() != null &&
                !role.getPackManager().useGoods(model.getPrice(), EGoodsChangeType.LIMITGIFT_CONSUME, saves)) {
            if (model.getPrice().getT() == EGoodsType.DIAMOND.getId())
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            else
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOLD_LESS);
            return;
        }
        getActivityData().getGift().put(key, count + num);
        role.getPackManager().addGoods(model.getRewards(), EGoodsChangeType.LIMITGIFT_ADD, saves);
        //发送消息
        Message msg = new Message(cmdId, request.getChannel());
        msg.setByte(id);
        msg.setShort(num);
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        getActivityDao().updateGiftJson(getActivityData());
    }

    public void processLvGiftBuy(Message request) {
        byte id = request.readByte();
        short num = request.readShort();

        long curr = System.currentTimeMillis();
        Map<String, LimitGiftLogicData> logicData = ActivityService.getRoundData(
                EActivityType.LIMIT_GIFT_LV, player.getId(), curr);
        if (logicData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        LimitGiftLogicData model = logicData.get(id + "");
        if (model == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //购买条件
        if (player.getLevelWithRein() < model.getLimit()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            return;
        }
        //购买次数
        if (getActivityData().getLvGift().contains(id)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_NUM_MAX);
            return;
        }

        //判断背包容量
        if (!role.getPackManager().capacityEnough(model.getRewards())) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BAG_FULL_MELT);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //消耗元宝
        if (model.getPrice() != null &&
                !role.getPackManager().useGoods(model.getPrice(), EGoodsChangeType.LIMITGIFT_CONSUME, saves)) {
            if (model.getPrice().getT() == EGoodsType.DIAMOND.getId())
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            else
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOLD_LESS);
            return;
        }
        getActivityData().getLvGift().add(id);
        role.getPackManager().addGoods(model.getRewards(), EGoodsChangeType.LIMITGIFT_ADD, saves);
        //发送消息
        Message msg = new Message(MessageCommand.LIMITGIFT_LV_BUY_MESSAGE, request.getChannel());
        msg.setByte(id);
        msg.setShort(num);
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        getActivityDao().updateLvGiftJson(getActivityData());
    }

    private int getRebateKey(int type, byte id) {
        return type * 10000 + id;
    }

    /**
     * 获取活动消息
     *
     * @return
     */
    public Message get7DayMessage() {
        handleLogin();
        Message msg = new Message(MessageCommand.GAME_7DAY_MESSAGE);
        long create = DateUtil.getDayStartTime(player.getCreateTime());
        //7日活动总时间
        long endTime = create + Activity7Model.ACTIVE_DAY * DateUtil.DAY;
        msg.setString(DateUtil.formatDateTime(endTime - 1));
        //七日登录
        msg.setShort(getActivityData().getLoginInfos().size());
        for (byte state : getActivityData().getLoginInfos()) {
            msg.setByte(state);
        }
        //七日活动
        getActivity7Msg(msg);
        return msg;
    }

    public void processWelfareInfo(Message request) {
        Message msg = getWelfareMsg();
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
    }

    /**
     * 获取每日首冲信息
     *
     * @param request
     */
    public void processPayDailyFirstMessage(Message request) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.FEST_PAY_DAILY_FIRST);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData group = ActivityService.getGroupData(EActivityType.FEST_PAY_DAILY_FIRST);
        int firstOrder = role.getPayManager().getTodayDiamondInPay();
        boolean payFirstDailyPayStatus = getActivityData().isPayDailyFirstStatus();
        //活动数据轮次
        Message msg = new Message(MessageCommand.GAME_PAY_DAILY_FIRST_MESSAGE, request.getChannel());
        msg.setByte(group.getDataRound(currRound.getRound()));
        msg.setInt(firstOrder);
        msg.setBool(payFirstDailyPayStatus);
        role.sendMessage(msg);
    }

    /**
     * 获取每日首冲信息
     *
     * @param request
     */
    public void putPayDailyFirstMessage() {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.FEST_PAY_DAILY_FIRST);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData group = ActivityService.getGroupData(EActivityType.FEST_PAY_DAILY_FIRST);
        int firstOrder = role.getPayManager().getTodayDiamondInPay();
        boolean payFirstDailyPayStatus = getActivityData().isPayDailyFirstStatus();
        //活动数据轮次
        Message msg = new Message(MessageCommand.GAME_PAY_DAILY_FIRST_MESSAGE);
        msg.setByte(group.getDataRound(currRound.getRound()));
        msg.setInt(firstOrder);
        msg.setBool(payFirstDailyPayStatus);
        role.putMessageQueue(msg);
    }

    /**
     * 每日首冲奖品领取
     *
     * @param request
     */
    public void processPayDailyFirst(Message request) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.FEST_PAY_DAILY_FIRST);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        int id = currRound.getRound() + 1;
        int firstOrder = role.getPayManager().getTodayDiamondInPay();
        Map<String, PayDailyFirstLogicData> logicData = ActivityService.getRoundData(
                EActivityType.FEST_PAY_DAILY_FIRST, player.getId(), curr);
        PayDailyFirstLogicData model = logicData.get(id + "");
        if (firstOrder < model.getCost()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PAY_DAILY_FIRST_RECEIVE);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(model.getGoods(), EGoodsChangeType.LIMITGIFT_ADD, saves);
        if (getActivityData().isPayDailyFirstStatus()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PAY_DAILY_FIRST_RECEIVE);
        }
        getActivityData().setPayDailyFirstStatus(1);
        Message message = new Message(MessageCommand.FEST_PAY_DAILY_FIRST_MESSAGE, request.getChannel());
//		message.setBool(getActivityData().isPayDailyFirstStatus());
        role.sendMessage(message);
        role.savePlayer(saves);
        getActivityDao().updatePayDailyFirstStatus(getActivityData());
    }

    /**
     * 每日福利消息
     *
     * @return
     */
    public Message getWelfareMsg() {
        //福利的状态
        List<Entry<Byte, Byte>> states = getWelfareState();
        Message msg = new Message(MessageCommand.WELFARE_INFO_MESSAGE);
        msg.setByte(states.size());
        for (Entry<Byte, Byte> state : states) {
            msg.setByte(state.getKey());
            msg.setByte(state.getValue());
        }
        return msg;
    }

    /**
     * 处理每日福利
     */
    public void handlerWelfare(int sum) {
        List<Entry<Byte, Byte>> states = getWelfareState();
        boolean save = false;
        for (Entry<Byte, Byte> state : states) {
            //发放每日福利
            if (state.getValue() == UNDO) {
                WelfareModelData model = WelfareModel.getModel(state.getKey());
                if (model != null && model.getPrice() <= sum) {
                    getActivityData().getWelfare().put(state.getKey(), System.currentTimeMillis());
                    save = true;
                    Mail mail = MailService.createMail(model.getTitle(), model.getContent(),
                            EGoodsChangeType.FEWALE_ADD, model.getRewards());
                    MailService.sendSystemMail(player.getId(), mail);
                }
            }
        }
        if (save) {
            role.putMessageQueue(getWelfareMsg());
            getActivityDao().updateWelfareJson(getActivityData());
        }
    }

    /**
     * 充值累积奖励领取
     *
     * @param request
     */
    public void processPayCumulateReceive(Message request) {
        int cost = request.readInt();
        long curr = System.currentTimeMillis();
        if (getActivityData().isPayCumulateReceived(cost)) {
            // 已领取
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        Map<String, PayCumulateLogicData> logicDatas = ActivityService.getRoundData(
                EActivityType.PAY_CUMULATE, role.getPlayerId(), curr);
        if (logicDatas == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_ACTIVITY);
            return;
        }
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.PAY_CUMULATE);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        //充值金额
        int paySum = role.getPayManager().getDiamondInPay(currRound.getStartTimeStr(), currRound.getEndTimeStr());
        PayCumulateLogicData logicData = logicDatas.get(String.valueOf(cost));
        if (logicData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        if (paySum < logicData.getId()) {
            // 没达到
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        getActivityData().receivePayCumulate(cost);
        getActivityDao().updatePayCumulate(getActivityData());

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(logicData.getGoods(), EGoodsChangeType.PAY_CUL_ADD, saves);
        role.savePlayer(saves);

        Message message = getActivityMsg(EActivityType.PAY_CUMULATE);
        message.setChannel(request.getChannel());
        role.sendMessage(message);

        ChatService.broadcastPlayerMsg(role.getPlayer(), EBroadcast.PAY_CUMULATE);
    }

    /**
     * 固定时间充值累积奖励领取
     *
     * @param request
     */
    public void processPayCumulateFixedReceive(Message request) {
        int id = request.readInt();
        int cost = request.readInt();
        long curr = System.currentTimeMillis();
        String key = id + "_" + cost;
        if (getActivityData().isPayCumulateFixedReceived(key)) {
            // 已领取
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        EActivityType activityType = EActivityType.getType(id);
        Map<String, PayCumulateLogicData> logicDatas = ActivityService.getRoundData(
                activityType, role.getPlayerId(), curr);
        if (logicDatas == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_ACTIVITY);
            return;
        }
        BaseActivityConfig configData = ActivityService.getActivityConfig(activityType);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        //充值金额
        int paySum = role.getPayManager().getDiamondInPay(currRound.getStartTimeStr(), currRound.getEndTimeStr());
        PayCumulateLogicData logicData = logicDatas.get(String.valueOf(cost));
        if (logicData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        if (paySum < logicData.getId()) {
            // 没达到
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        getActivityData().receivePayCumulateFixed(key);
        getActivityDao().updatePayCumulateFixed(getActivityData());

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(logicData.getGoods(), EGoodsChangeType.PAY_CUL_ADD, saves);
        role.savePlayer(saves);

        Message message = getActivityMsg(activityType);
        message.setChannel(request.getChannel());
        role.sendMessage(message);

        ChatService.broadcastPlayerMsg(role.getPlayer(), EBroadcast.PAY_CUMULATE);
    }

    /**
     * 投资基金领取
     *
     * @param request
     */
    public void processInvestFundReceive(Message request) {
        if (role.getPlayer().getForever() < 1) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        byte receive = request.readByte();
        PlayerActivity data = getActivityData();
        if (receive != (data.getInvestFund() + 1)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        Map<String, InvestFundData> model = ActivityService.getRoundData(EActivityType.INVEST_FUND, 0);
        if (receive > model.size()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        InvestFundData investFundData = model.get(Byte.toString(receive));
        if (investFundData.getLevel() != 0 && investFundData.getLevel() > role.getPlayer().getLevelWithRein()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            return;
        }
        if (investFundData.getPower() != 0) {
            role.getPlayer().updateFighting();
            if (investFundData.getPower() > role.getPlayer().getFighting()) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_FIGHTING_LESS);
                return;
            }
        }

        data.setInvestFund(receive);
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(investFundData.getReward(), EGoodsChangeType.INVEST_FUND_RECEIVE_ADD, enumSet);

        Message message = new Message(MessageCommand.INVEST_FUND_RECEIVE_MESSAGE, request.getChannel());
        message.setByte(receive);
        role.sendMessage(message);

        this.getActivityDao().updateInvestFund(data);
        role.savePlayer(enumSet);
    }

    /**
     * 192 新拉霸
     *
     * @param request
     */
    public void processSlotNewMachine(Message request) {
        byte id = request.readByte();

        PlayerActivity data = getActivityData();

        Map<String, SlotMachineData> model = ActivityService.getRoundData(EActivityType.SLOT_NEW_MACHINE, 0);
        if (id > model.size() || data.getSlotNewMachine() >= id) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        SlotMachineData slotMachineData = model.get(Byte.toString(id));

        int offset = slotMachineData.getMaxrewards() - slotMachineData.getMinrewards();
        if (offset < 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (!EGoodsType.DIAMOND.getCmd().consume(role, new DropData(EGoodsType.DIAMOND.getId(), 0, slotMachineData.getPrice()), EGoodsChangeType.SLOT_MACHINE_CONSUME, enumSet)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }

        data.setSlotNewMachine(id);

        int reward = (int) (slotMachineData.getMinrewards() + Math.random() * offset);

        EGoodsType.DIAMOND.getCmd().reward(role, new DropData(EGoodsType.DIAMOND.getId(), 0, reward), EGoodsChangeType.SLOT_MACHINE_ADD, enumSet);

        Message message = new Message(MessageCommand.SLOT_NEW_MACHINE_MESSAGE, request.getChannel());
        message.setByte(id);
        message.setInt(reward);
        role.sendMessage(message);

        this.getActivityDao().updateSlotNewMachine(data);
        role.savePlayer(enumSet);
    }

    /**
     * 193 拉霸
     *
     * @param request
     */
    public void processSlotMachine(Message request) {
        byte id = request.readByte();

        PlayerActivity data = getActivityData();

        Map<String, SlotMachineData> model = ActivityService.getRoundData(EActivityType.SLOT_MACHINE, 0);
        if (id > model.size() || data.getSlotMachine() >= id) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        SlotMachineData slotMachineData = model.get(Byte.toString(id));

        int offset = slotMachineData.getMaxrewards() - slotMachineData.getMinrewards();
        if (offset < 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (!EGoodsType.DIAMOND.getCmd().consume(role, new DropData(EGoodsType.DIAMOND.getId(), 0, slotMachineData.getPrice()), EGoodsChangeType.SLOT_MACHINE_CONSUME, enumSet)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }

        data.setSlotMachine(id);

        int reward = (int) (slotMachineData.getMinrewards() + Math.random() * offset);

        EGoodsType.DIAMOND.getCmd().reward(role, new DropData(EGoodsType.DIAMOND.getId(), 0, reward), EGoodsChangeType.SLOT_MACHINE_ADD, enumSet);

        Message message = new Message(MessageCommand.SLOT_MACHINE_MESSAGE, request.getChannel());
        message.setByte(id);
        message.setInt(reward);
        role.sendMessage(message);

        this.getActivityDao().updateSlotMachine(data);
        role.savePlayer(enumSet);
    }

    /**
     * 194 限时限级限购
     *
     * @param request
     */
    public void processLimitLimitLimt(Message request) {
        byte d = request.readByte();

        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.LIMIT_LIMIT_LIMIT);
        ActivityRoundConfig currRound = configData.getCurrRound(role.getPlayerId(), System.currentTimeMillis());

        int id = 0;
        int round = currRound.getRound();

        Map<String, LimitLimitLimitData> model = ActivityService.getRoundData(EActivityType.LIMIT_LIMIT_LIMIT, round);

        PlayerActivity activityData = role.getActivityManager().getActivityData();
        if (round == activityData.getLimitLimitLimit() / 1000) {
            id = activityData.getLimitLimitLimit() % 1000;
        }
        ++id;

        if (d != id) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        LimitLimitLimitData data = model.get(Integer.toString(id + round * 1000));
        if (null == data) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (player.getVipLevel() < data.getVip()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
            return;
        }
        if (player.getLevelWithRein() < data.getLevel()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            return;
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().useGoods(data.getPrice(), EGoodsChangeType.LIMIT_LIMIT_LIMIT_CONSUME, enumSet)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        role.getPackManager().addGoods(data.getRewards(), EGoodsChangeType.LIMIT_LIMIT_LIMIT_ADD, enumSet);

        Message message = new Message(MessageCommand.LIMIT_LIMIT_LIMIT_MESSAGE, request.getChannel());
        message.setByte(round);
        message.setByte(id);
        role.sendMessage(message);

        activityData.setLimitLimitLimit((short) (id + round * 1000));
        role.getActivityManager().getActivityDao().updataLimitLimitLimit(activityData);
        role.savePlayer(enumSet);
        ChatService.broadcastPlayerMsg(player, EBroadcast.LIMIT_LIMIT_LIMIT, data.getPrice().getN() + "");
    }

    /**
     * 195 七日开服活动
     *
     * @param request
     */
    public void processSevenDay(Message request) {
        byte round = request.readByte();
        byte type = request.readByte();
        byte id = request.readByte();

        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.SEVEN_DAY);
        ActivityRoundConfig roundData = configData.getCurrRound(player.getId(), System.currentTimeMillis());

        if (roundData.getRound() < round) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        String key = round + "_" + type;

        Map<String, Byte> sd = role.getActivityManager().getActivityData().getSevenDay();
        Byte value = sd.get(key);
        if (value != null && value != (id - 1)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        } else if (value == null && id != 1) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        Map<String, SevenDayLogicData> logicMap = ActivityService.getRoundData(EActivityType.SEVEN_DAY, round);
        SevenDayLogicData logicData = logicMap.get(key + "_" + id);
        if (logicData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);

        if (EReach.SUPER_SALES.getType() == type) {
            if (!EGoodsType.DIAMOND.getCmd().consume(role, new DropData(EGoodsType.DIAMOND.getId(), 0, logicData.getTarget()), EGoodsChangeType.SEVEN_DAY_CONSUME, enumSet)) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                return;
            }
            ChatService.broadcastPlayerMsg(player, EBroadcast.SEVEN_DAY);
        } else if (EReach.EVERY_DAY_WELFARE.getType() == type) {

        } else {
            if (!EReach.getType(type).getHandler().apply(role, logicData.getTarget())) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
                return;
            }
        }

        role.getPackManager().addGoods(logicData.getRewards(), EGoodsChangeType.SEVEN_DAY_ADD, enumSet);
        sd.put(key, id);

        Message message = new Message(MessageCommand.SEVEN_DAY_MESSAGE, request.getChannel());
        message.setByte(round);
        message.setByte(type);
        message.setByte(id);
        role.sendMessage(message);

        role.getActivityManager().getActivityDao().updataSevenDay(role.getActivityManager().getActivityData());
        role.savePlayer(enumSet);

    }

    /**
     * 获取累计消费信息
     *
     * @param request
     */
    public void processConsumCumulateFixedMessage(Message request) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.CONSUM_CUMULATE);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;

        ActivityGroupData group = ActivityService.getGroupData(EActivityType.CONSUM_CUMULATE);

        int consume = this.dealConsumeDailyStr(DateUtil.getDayStartTime(curr), player.getConsumeDaily());
        Message msg = new Message(MessageCommand.GAME_CONSUM_CUMULATE_FIXED_MESSAGE, request.getChannel());
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        msg.setByte(getActivityData().getConsumeCumulateFixedData().size());
        for (Integer key : getActivityData().getConsumeCumulateFixedData()) {
            msg.setInt(key);
        }
        msg.setInt(consume);
        msg.setShort(group.getDataRound(currRound.getRound()));
        role.sendMessage(msg);
        getActivityDao().updateConsumCumulateReceived(getActivityData());
    }

    /**
     * 累计消费领取
     *
     * @param request
     */
    public void processConsumCumulateFixedReceive(Message request) {
        int cost = request.readInt();
        long curr = System.currentTimeMillis();
        //是否已领取
        boolean received = getActivityData().isConsumeCumulateFixedReceived(cost);
        if (received) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_CONSUM_CUMULATE_RECEIVED);
            return;
        }
        Map<String, ConsumCumulateLogicData> logicDatas = ActivityService.getRoundData(
                EActivityType.CONSUM_CUMULATE, role.getPlayerId(), curr);
        ConsumCumulateLogicData ccld = logicDatas.get(cost + "");
        int consume = this.dealConsumeDailyStr(DateUtil.getDayStartTime(curr), player.getConsumeDaily());
        //是否满足领取条件
        if (consume < ccld.getId()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_CONSUM_CUMULATE_NO_COST);
            return;
        }
        //领取
        getActivityData().receiveConsumeCumulateFixed(cost);
        List<DropData> dropDatas = ccld.getRewards();
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        for (DropData dd : dropDatas) {
            EGoodsType.getGoodsType(dd.getT()).getCmd().reward(role, dd, EGoodsChangeType.CONSUM_CUMULATE_RECEIVE, saves);
        }
        Set<Integer> sets = getActivityData().getConsumeCumulateFixedData();
        Message msg = new Message(MessageCommand.GAME_CONSUM_CUMULATE_FIXED_RECEIVE, request.getChannel());
        msg.setByte(sets.size());
        for (Integer s : sets) {
            msg.setInt(s);
        }
        role.sendMessage(msg);
        role.savePlayer(saves);
        getActivityDao().updateConsumCumulateReceived(getActivityData());
    }

    /**
     * 处理每日消耗
     *
     * @param currTime 当天凌晨时间戳
     * @param str      数据库
     * @return
     */
    private int dealConsumeDailyStr(long currTime, String str) {
        try {
            if (str == null || "".equals(str) || "null".equals(str) || "{}".equals(str)) return 0;
            if (str.indexOf(":") > -1) {
                String s = str.substring(str.lastIndexOf(":") + 1, str.length() - 1);
                return Integer.parseInt(s);
            } else {
                String[] strs = str.split(",");
                String str1 = strs[0];
                long oldTime = Long.parseLong(str1);
                if (oldTime != currTime) return 0;
                return Integer.parseInt(strs[1]);
            }
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 元宝王者信息
     *
     * @param request
     */
    public void processTurntableMessage(Message request) {
        //是否首次参加
        int turntableRound = getActivityData().getTurntableRound();
        int round = -1;
        if (turntableRound <= 0) {
            round = (int) (Math.random() * 7);
            getActivityData().setTurntableRound(round);
            getActivityDao().updateTurntableRound(getActivityData());
        } else {
            round = turntableRound;
        }
        Map<String, TurntableLogicData> logics = ActivityService.getRoundData(EActivityType.TURN_TABLE, round);
        if (logics == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_ACTIVITY);
            return;
        }
        Map<String, Integer> orders = role.getPayManager().getDailyOrderByAmount();

        List<TurntableLogicData> tlds = new ArrayList<>(logics.values());
        Collections.sort(tlds, new Comparator<TurntableLogicData>() {

            @Override
            public int compare(TurntableLogicData o1, TurntableLogicData o2) {
                if (o1.getId() > o2.getId()) return 1;
                if (o1.getId() < o2.getId()) return -1;
                return 0;
            }
        });
        //int paySum = 10000;//role.getPayManager().getTodayDiamondInPay();
        Map<String, Integer> usedOrder = getActivityData().setTodayUsedOrderJson(getActivityDao().getTodayUsedOrder(getActivityData()));
        for (String uid : orders.keySet()) {
            for (String usedUID : usedOrder.keySet()) {
                if (uid.equals(usedUID)) {
                    orders.put(uid, 0);
                }
            }
        }

        int turntableData = getActivityData().getTurntableData();
        int highest = 0;
        for (TurntableLogicData data : tlds) {
            if (data.getId() > highest) {
                highest = data.getId();
            }
        }
        if (turntableData < highest) {
            for (String uid : orders.keySet()) {
                int o = orders.get(uid);
                for (TurntableLogicData data : tlds) {
                    if (o >= data.getId() && getActivityData().getTurntableData() < data.getId() &&
                            !getActivityData().getTurntableReceiveNum().contains(data.getId())) {

                        getActivityData().addTurntableReceivceNum(data.getId());
                        break;
                    }
                }
                if (o != 0) {
                    getActivityData().getTodayUsedOrder().put(uid, o);
                }
            }
        }
        //档位下坐标
        int coordi = 0;
        for (int i = 0; i < tlds.size(); i++) {
            if (turntableData == 0) {
                break;
            }
            if (tlds.get(i).getId() == turntableData) {
                coordi = i + 1;
                break;
            }
        }
        Message message = new Message(MessageCommand.GAME_TURNTABLE_MESSAGE);
        message.setChannel(request.getChannel());
        int len = getActivityData().getTurntableReceiveNum().size();
        message.setByte(len);
        ArrayList<Integer> list = new ArrayList<>(getActivityData().getTurntableReceiveNum());
        Collections.sort(list);
        for (int i = 0; i < len; i++) {
            message.setInt(list.get(i));
        }
        List<PlayerTurntableInfo> ptis = PlayerTurntableInfosService.getPlayerTurntableInfos();
        int size = ptis.size() > 5 ? 5 : ptis.size();
        message.setByte(size);
        for (int i = 0; i < size; i++) {
            message.setString(ptis.get(i).getPlayerName());
            message.setInt(ptis.get(i).getReward());
        }
        message.setInt(round);
        message.setByte(coordi);
        role.sendMessage(message);
        getActivityDao().updateTurntableReceiveNums(getActivityData());
        getActivityDao().updateTodayUsedOrder(getActivityData());
    }

    /**
     * 元宝王者
     *
     * @param request
     */
    public void processTurntableReceive(Message request) {
        //领取id
        int cost = request.readInt();
        int round = getActivityData().getTurntableRound();
        Map<String, TurntableLogicData> logics = ActivityService.getRoundData(EActivityType.TURN_TABLE, round);

        /**
         * 处理随机
         */
        //倍数
        TurntableLogicData tld = logics.get(cost + "");

        List<Integer> gailvs = tld.getGailvs();
        List<Ele> eles = new ArrayList<>();
        for (int i = 0; i < gailvs.size(); i++) {
            Ele ele = new Ele(i, gailvs.get(i));
            eles.add(ele);
        }
        //随机数
        Ele ele = DiceUtil.dice(eles);
        int grade = ele.getId();

        //判断是否已领取
        if (getActivityData().isTurnTableReceived(cost)) {
            // 已领取
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        getActivityData().receiveTurntableData(cost);
        //消耗
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        DropData data = new DropData(EGoodsType.DIAMOND, 0, cost);
        EGoodsType.getGoodsType(data.getT()).getCmd().consume(role, data, EGoodsChangeType.TURN_TABLE_ADD, saves);
        //发邮件
        DropData dd = tld.getReward();
        List<DropData> datas = new ArrayList<>();
        datas.add(dd);
        Mail mail = MailService.createMail(new MailRewardModelData((short) 1, "元宝王者奖励", "恭喜！在元宝王者活动中获得高倍元宝奖励，请注意查收！", datas), EGoodsChangeType.TURN_TABLE_ADD);
        MailService.sendSystemMail(getActivityData().getPlayerId(), mail);

        List<TurntableLogicData> tlds = new ArrayList<>(logics.values());
        Collections.sort(tlds, new Comparator<TurntableLogicData>() {

            @Override
            public int compare(TurntableLogicData o1, TurntableLogicData o2) {
                if (o1.getId() > o2.getId()) return 1;
                if (o1.getId() < o2.getId()) return -1;
                return 0;
            }
        });
        //档位下坐标
        int coordi = 0;
        int turntableData = getActivityData().getTurntableData();
        for (int i = 0; i < tlds.size(); i++) {
            if (turntableData == 0) {
                break;
            }
            if (tlds.get(i).getId() == turntableData) {
                coordi = i + 1;
                break;
            }
        }
        //添加元宝王者玩家数据信息
        PlayerTurntableInfo pti = new PlayerTurntableInfo();
        pti.setPlayerName(role.getPlayer().getName());
        pti.setReward(dd.getN());
        PlayerTurntableInfosService.addPlayerInfo(pti);


        Message message = new Message(MessageCommand.GAME_TURNTABLE_PAY_MESSAGE);
        message.setChannel(request.getChannel());
        message.setInt(dd.getN());
        int len = getActivityData().getTurntableReceiveNum().size();
        message.setByte(len);
        ArrayList<Integer> list = new ArrayList<>(getActivityData().getTurntableReceiveNum());
        Collections.sort(list);
        for (int i = 0; i < len; i++) {
            message.setInt(list.get(i));
        }
        message.setByte(grade);
        message.setInt(round);
        message.setByte(coordi);
        role.sendMessage(message);
        role.savePlayer(saves);
        getActivityDao().updateTurntable(getActivityData());
        getActivityDao().updateTurntableReceiveNums(getActivityData());
        ChatService.broadcastPlayerMsg(player, EBroadcast.TURN_TABLE, "" + dd.getN());
    }

    /**
     * 鉴宝幸运初始化
     *
     * @param request
     */
    public void processInitLuckScoreMessage(Message request) {
        int luckScore = getActivityData().getKamPoLuckScore();
        List<Integer> scoreList = getActivityData().getReceivedLuckScore();
        Message msg = new Message(MessageCommand.GAME_INIT_LUCK_SCORE_MESSAGE);
        msg.setChannel(request.getChannel());
        msg.setInt(luckScore);
        int size = scoreList == null ? 0 : scoreList.size();
        msg.setByte(size);
        for (Integer score : scoreList) {
            msg.setInt(score);
        }
        role.sendMessage(msg);
    }

    /**
     * 鉴宝幸运初始化2
     *
     * @param request
     */
    public void processInitLuckScore2Message(Message request) {
        int luckScore = getActivityData().getKamPo2LuckScore();
        List<Integer> scoreList = getActivityData().getReceivedLuckScore2();
        Message msg = new Message(MessageCommand.GAME_INIT_LUCK_SCORE2_MESSAGE);
        msg.setChannel(request.getChannel());
        msg.setInt(luckScore);
        int size = scoreList == null ? 0 : scoreList.size();
        msg.setByte(size);
        for (Integer score : scoreList) {
            msg.setInt(score);
        }
        role.sendMessage(msg);
    }

    /**
     * 消除转盘
     *
     * @param request
     */
    public void processNoRepeatTurntableDice(Message request) {
        Map<String, NoRepeatTurntableLogicData> model = ActivityService.getRoundData(EActivityType.NOREPEATTURNTABLE, 0);
        if (model == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        NoRepeatTurntableLogicData logic = model.get("1");
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        int luck = getActivityData().getNoRepeatTurntableLuck();
        boolean isFullLuck = false;
        int free = getActivityData().getFree();
        List<Integer> receiveds = getActivityData().getNoRepeatTurntableReceived();
        if (free == 1) {
            int num = getActivityData().getNoRepeatTurntableNum();
            if (num <= 0) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_TIMES_LESS);
                return;
            }
            getActivityData().setNoRepeatTurntableNum(num - 1);
            //消耗元宝
            DropData price = logic.getPrice();
            if (!EGoodsType.getGoodsType(price.getT()).getCmd().consume(role, price, EGoodsChangeType.NOREPEATTURNTABLE_CONSUME, saves)) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                return;
            }
        } else {
            free = 1;
            getActivityData().setFree((byte) free);
            long curr = System.currentTimeMillis();
            getActivityData().setNoRepeatTurntableRefreshTime(curr);
            Message message = this.getNoRepeatTurntbaleRefreshMsg();

            role.putMessageQueue(message);
        }
        //获取转盘奖励
        List<Ele> all = this.getEleById(model, getActivityData().getNoRepeatTurntableAll());
        //随机
        Ele ele = DiceUtil.noRepeatDice(all, receiveds);
        //发奖励
        NoRepeatTurntableLogicData data = model.get("" + ele.getId());
        DropData rewards = data.getRewards();
        List<DropData> dd_rewards = new ArrayList<>();
        if (rewards.getT() == EGoodsType.EQUIP.getId()) {
            int grid = role.getPlayer().getEquipBagFreeGrid();
            if (grid <= 0) {
                dd_rewards.add(rewards);
                Mail mail = MailService.createMail(new MailRewardModelData((short) 1, "至尊转盘轮盘未领取奖励", "亲爱的玩家您好，由于您的装备背包已满，您于至尊转盘获得的盛世装备通过邮件下发到您的角色，请查收\r\n" +
                        "", dd_rewards), EGoodsChangeType.NOREPEATTURNTABLE_ADD);
                MailService.sendSystemMail(getActivityData().getPlayerId(), mail);
            } else {
                EGoodsType.getGoodsType(rewards.getT()).getCmd().reward(role, rewards, EGoodsChangeType.NOREPEATTURNTABLE_ADD, saves);
            }
        } else {
            EGoodsType.getGoodsType(rewards.getT()).getCmd().reward(role, rewards, EGoodsChangeType.NOREPEATTURNTABLE_ADD, saves);
        }
        if (data.getType() == 1) {
            String name = "";
            if (rewards.getT() == EGoodsType.EQUIP.getId()) {
                EquipData equipData = GoodsModel.getEquipDataById(rewards.getG());
                name = equipData.getName();
            } else if (rewards.getT() == EGoodsType.ITEM.getId()) {
                ItemData itemData = GoodsModel.getItemDataById(rewards.getG());
                name = itemData.getName();
            } else if (rewards.getT() == EGoodsType.WING_GODS.getId()) {
                WingGodModelData wgmd = SectionModel.getWingGod(rewards.getG());
                name = wgmd.getName();
            } else if (rewards.getT() == EGoodsType.BOX.getId()) {
                BoxData boxData = GoodsModel.getBoxDataById(rewards.getG());
                name = boxData.getName();
            } else if (EGoodsType.SUUL_PIECE.getId() == rewards.getT()) {
                LingSuiModelData lingSui = FunctionModel.getLingSuiModelData((byte) rewards.getG());
                name = lingSui.getName();
            }
            ChatService.broadcastPlayerMsg(player, EBroadcast.NOREPEAT_TURNTABLE1, name);
        } else if (data.getType() == 2) {
            String name = "";
            if (rewards.getT() == EGoodsType.EQUIP.getId()) {
                RedModelData equipData = OrangeModel.getRed(rewards.getG());
                name = equipData.getName();
            } else if (rewards.getT() == EGoodsType.ITEM.getId()) {
                ItemData itemData = GoodsModel.getItemDataById(rewards.getG());
                name = itemData.getName();
            } else if (rewards.getT() == EGoodsType.WING_GODS.getId()) {
                WingGodModelData wgmd = SectionModel.getWingGod(rewards.getG());
                name = wgmd.getName();
            } else if (rewards.getT() == EGoodsType.BOX.getId()) {
                BoxData boxData = GoodsModel.getBoxDataById(rewards.getG());
                name = boxData.getName();
            } else if (EGoodsType.SUUL_PIECE.getId() == rewards.getT()) {
                LingSuiModelData lingSui = FunctionModel.getLingSuiModelData((byte) rewards.getG());
                name = lingSui.getName();
            }
            ChatService.broadcastPlayerMsg(player, EBroadcast.NOREPEAT_TURNTABLE2, name);
        }
        EGoodsType.getGoodsType(rewards.getT()).getCmd().reward(role, rewards, EGoodsChangeType.NOREPEATTURNTABLE_ADD, saves);
        //随机id
        getActivityData().setNoRepeatTurntableRandomId(ele.getId());
        //添加已领取
        getActivityData().getNoRepeatTurntableReceived().add(ele.getId());
        //幸运值+1
        luck += DiceUtil.dice(logic.getEles()).getId();
        getActivityData().setNoRepeatTurntableLuck(luck);
        int maxTargetedLuck = this.getNoRepeatTurntableMaxTargeted();
        NoRepeatTurntableTargetInfo nexttargetedLuck = this.getNoRepeatTurntableSegment(model, maxTargetedLuck);
        int segmentId = this.getIdBySegment(model, maxTargetedLuck);
        if (receiveds.size() == 6) {
            if (luck >= nexttargetedLuck.getSegment()) {
                segmentId = this.getIdBySegment(model, nexttargetedLuck.getSegment());
                isFullLuck = true;
                if (luck >= this.getNoRepeatTurntableMaxSegment(model)) {
                    luck = 0;
                    getActivityData().clearNoRepeatTurntableTargeted();
                } else {
                    getActivityData().getNoRepeatTurntableTargeted().add(nexttargetedLuck.getSegment());
                }
                getActivityData().setNoRepeatTurntableLuck(luck);
            }
            getActivityData().clearNoRepeatTurntableReceived();
            List<Ele> eles = this.getNoRepeatTurntableEle(model, isFullLuck, segmentId);
            getActivityData().setNoRepeatTurntableAll(this.parse2IntList(eles));
        }
        getActivityData().setNoRepeatTurnSegmentId(segmentId);
        Message msg = this.getActivityMsg(EActivityType.NOREPEATTURNTABLE);
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
        role.savePlayer(saves);
        getActivityDao().updateNoRepeatTurntable(getActivityData());
        getActivityDao().updateNoRepeatTurntableRefreshTime(getActivityData());
    }

    /**
     * 清除转盘免费时间刷新
     *
     * @param request
     */
    public void processNoRepeatTurntableRefresh(Message request) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig activityConfig = ActivityService.getActivityConfig(EActivityType.NOREPEATTURNTABLE);
        long start = activityConfig.getStartTime(0);
        long end = activityConfig.getEndTime();
        if (curr < start || curr > end) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_ACTIVITY);
            return;
        }
        //当前时间和上次刷新不在同一个间隔中
        if (getActivityData().getFree() != 0 && (curr - getActivityData().getNoRepeatTurntableRefreshTime()) >= NoRepeatTurntableLogicData.REFRESH_SPACE) {
            getActivityData().setFree((byte) 0);
        }
        Message msg = this.getNoRepeatTurntbaleRefreshMsg();
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
        getActivityDao().updateNoRepeatTurntableRefreshTime(getActivityData());
    }

    private Message getNoRepeatTurntbaleRefreshMsg() {
        long curr = System.currentTimeMillis();
        long left = getActivityData().getNoRepeatTurntableRefreshTime() + NoRepeatTurntableLogicData.REFRESH_SPACE - curr;
        left = left < 0 ? 0 : left;
        if (getActivityData().getFree() == 0) {
            left = 0;
        }
        Message msg = new Message(MessageCommand.GAME_NO_REPEAT_TURNTABLE_REFRESH);
        msg.setByte(getActivityData().getFree());
        msg.setLong(left);
        return msg;
    }

    /**
     * 带权值id集合转换成id集合
     *
     * @param eles
     * @return
     */
    public List<Integer> parse2IntList(List<Ele> eles) {
        if (eles == null || eles.isEmpty()) return new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        for (Ele ele : eles) {
            list.add(ele.getId());
        }
        return list;
    }

    /**
     * 根据id获取权值
     *
     * @param logics
     * @param ids
     * @return
     */
    private List<Ele> getEleById(Map<String, NoRepeatTurntableLogicData> logics, List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();
        List<Ele> eles = new ArrayList<>();
        for (String id : logics.keySet()) {
            for (Integer i : ids) {
                if (Integer.valueOf(id) == i) {
                    NoRepeatTurntableLogicData nrtld = logics.get(id);
                    Ele ele = new Ele(nrtld.getId(), nrtld.getChance());
                    eles.add(ele);
                    break;
                }
            }
        }
        return eles;
    }

    /**
     * 获取奖品id
     *
     * @param logics
     * @param isFirstRound
     * @param isFullLuck
     * @return
     */
    public List<Ele> getNoRepeatTurntableEle(Map<String, NoRepeatTurntableLogicData> logics, boolean isFullLuck, int segmentId) {
        List<Ele> list = new ArrayList<>();
        List<Ele> allType0 = new ArrayList<>();
        List<Ele> allType1 = new ArrayList<>();
        List<Ele> allType2 = new ArrayList<>();
        NoRepeatTurntableLogicData data = logics.get("1");
        for (String id : logics.keySet()) {
            NoRepeatTurntableLogicData nrtld = logics.get(id);
            if (nrtld.getType() == 0) {
                Ele ele = new Ele(nrtld.getId(), nrtld.getChance());
                allType0.add(ele);
            } else if (nrtld.getType() == 1) {
                Ele ele = new Ele(nrtld.getId(), nrtld.getChance());
                allType1.add(ele);
            } else if (nrtld.getType() == 2) {
                Ele ele = new Ele(nrtld.getId(), nrtld.getChance());
                allType2.add(ele);
            }
        }
        for (int i = 0; i < 5; i++) {
            list.add(DiceUtil.noRepeatDice(allType0, parse2IntList(list)));
        }
        if (isFullLuck) {
            int rewardId = this.getRewardId(data, segmentId);
            int chance = logics.get(rewardId + "").getChance();
            Ele ele = new Ele(rewardId, chance);
            list.add(ele);
        } else {
            list.add(DiceUtil.noRepeatDice(allType1, parse2IntList(list)));
        }
        return list;
    }

    private int getRewardId(NoRepeatTurntableLogicData data, int segmentId) {
        List<NoRepeatTurntableTargetInfo> list = data.getTargetInfos();
        for (NoRepeatTurntableTargetInfo nrtti : list) {
            if (nrtti.getId() == segmentId) {
                return nrtti.getRewardId();
            }
        }
        return -1;
    }

    /**
     * 获取奖品id
     *
     * @param logics
     * @param isFirstRound
     * @param isFullLuck
     * @return
     */
    public List<Ele> getNoRepeatTurntableEle(Map<String, NoRepeatTurntableLogicData> logics) {
        List<Ele> list = new ArrayList<>();
        List<Ele> allType0 = new ArrayList<>();
        List<Ele> allType1 = new ArrayList<>();
        List<Ele> allType2 = new ArrayList<>();
        for (String id : logics.keySet()) {
            NoRepeatTurntableLogicData nrtld = logics.get(id);
            if (nrtld.getType() == 0) {
                Ele ele = new Ele(nrtld.getId(), nrtld.getChance());
                allType0.add(ele);
            } else if (nrtld.getType() == 1) {
                Ele ele = new Ele(nrtld.getId(), nrtld.getChance());
                allType1.add(ele);
            } else if (nrtld.getType() == 2) {
                Ele ele = new Ele(nrtld.getId(), nrtld.getChance());
                allType2.add(ele);
            }
        }
        for (int i = 0; i < 5; i++) {
            list.add(DiceUtil.noRepeatDice(allType0, parse2IntList(list)));
        }
        list.add(allType2.get(1));
        return list;
    }

    /**
     * 获取清除转盘最大分段值
     *
     * @param logics
     * @return
     */
    private int getNoRepeatTurntableMaxSegment(Map<String, NoRepeatTurntableLogicData> logics) {
        NoRepeatTurntableLogicData logic = logics.get("1");
        List<NoRepeatTurntableTargetInfo> targetInfos = logic.getTargetInfos();
        int max = Integer.MIN_VALUE;
        for (NoRepeatTurntableTargetInfo nrtti : targetInfos) {
            if (max < nrtti.getSegment()) {
                max = nrtti.getSegment();
            }
        }
        return max;
    }

    public NoRepeatTurntableTargetInfo getNoRepeatTurntableSegment(Map<String, NoRepeatTurntableLogicData> logics, int targetMaxLuck) {
        NoRepeatTurntableLogicData logic = logics.get("1");
        List<NoRepeatTurntableTargetInfo> targetInfos = logic.getTargetInfos();
        //至尊值从小到大
        Collections.sort(targetInfos, new Comparator<NoRepeatTurntableTargetInfo>() {

            @Override
            public int compare(NoRepeatTurntableTargetInfo o1, NoRepeatTurntableTargetInfo o2) {
                if (o1.getSegment() > o2.getSegment()) return 1;
                if (o1.getSegment() < o2.getSegment()) return -1;
                return 0;
            }
        });
        for (NoRepeatTurntableTargetInfo nrtti : targetInfos) {
            if (nrtti.getSegment() > targetMaxLuck) return nrtti;
        }
        return null;
    }

    public int getNoRepeatTurntableMaxTargeted() {
        List<Integer> list = getActivityData().getNoRepeatTurntableTargeted();
        int max = Integer.MIN_VALUE;
        for (Integer i : list) {
            if (max < i) {
                max = i;
            }
        }
        return max;
    }

    public int getIdBySegment(Map<String, NoRepeatTurntableLogicData> logics, int segment) {
        NoRepeatTurntableLogicData logic = logics.get("1");
        List<NoRepeatTurntableTargetInfo> targetInfos = logic.getTargetInfos();
        if (segment <= 0) return 1;
        for (NoRepeatTurntableTargetInfo nrtti : targetInfos) {
            if (nrtti.getSegment() > segment) return nrtti.getId();
        }
        return -1;
    }

//	private List<Ele> getTypeEle_12(Map<String, NoRepeatTurntableLogicData> logics){
//		List<Ele> eles = logics.get("1").getEles();
//		List<Ele> allType0 = new ArrayList<>();
//		for(String id : logics.keySet()) {
//			NoRepeatTurntableLogicData nrtld = logics.get(id);
//			for(Ele ele : eles) {
//				if(nrtld.getType() == ele.getId()) {
//					nrtld
//				}
//			}
//			if(nrtld.getType() == ) {
//				Ele ele = new Ele(nrtld.getId(), nrtld.getChance());
//				allType0.add(ele);
//			}else if(nrtld.getType() == 2) {
//				Ele ele = new Ele(nrtld.getId(), nrtld.getChance());
//				allType0.add(ele);
//			}
//		}
//	}


    /**
     * 玩家获得奖励信息
     *
     * @param request
     */
    public void processKamPoMessage(Message request) {
        int day = 0;//getActivityDao().getKamPoDay(getActivityData());
        Map<String, KamPoLogicData> logics = ActivityService.getRoundData(EActivityType.KAM_PO, day);
        List<Ele> eles = new ArrayList<>();
        for (KamPoLogicData kpd : logics.values()) {
            Ele ele = new Ele(kpd.getId(), kpd.getDiaoluo());
            eles.add(ele);
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        Ele getEle = DiceUtil.dice(eles);
        //随机到的id
        int id = getEle.getId();
        KamPoLogicData kpd = logics.get(id + "");
        DropData dd_consume = kpd.getPrices();
        EGoodsType.getGoodsType(dd_consume.getT()).getCmd().consume(role, dd_consume, EGoodsChangeType.KAM_PO_SONSUME, saves);
        DropData dd_reward = kpd.getReward();
        EGoodsType.getGoodsType(dd_reward.getT()).getCmd().reward(role, dd_reward, EGoodsChangeType.KAM_PO_ADD, saves);

        //更新幸运值
        int scoreSum = getActivityData().addKamPoLuckScore(kpd.getLuckScore());
        int highest = 0;
        List<Integer> allLuckRewards = new ArrayList<>();
        for (Integer s : kpd.getLuck_reward().keySet()) {
            allLuckRewards.add(s);
            if (s > highest) highest = s;
        }
        allLuckRewards.removeAll(getActivityData().getReceivedLuckScore());
        if (scoreSum > highest) {
            getActivityData().setKamPoLuckScore(scoreSum - highest);
            if (allLuckRewards != null && !allLuckRewards.isEmpty()) {
                List<DropData> dropDatas = new ArrayList<>();
                for (Integer i : allLuckRewards) {
                    DropData dd = kpd.getLuck_reward().get(i);
                    dropDatas.add(dd);
                }
                Mail mail = MailService.createMail(new MailRewardModelData((short) 1, "幸运轮盘未领取奖励", "亲爱的玩家，您在参加幸运轮盘时未即时领取专属奖励，特以邮件形式为您发放", dropDatas), EGoodsChangeType.LUCK_SCORE_ADD);
                MailService.sendSystemMail(getActivityData().getPlayerId(), mail);
            }
            getActivityData().resetReceivedLuckScore();
        }
        Message message = new Message(MessageCommand.GAME_KAM_PO_MESSAGE);
        message.setChannel(request.getChannel());
        message.setInt(getActivityData().getKamPoLuckScore());
        message.setByte(id);
        List<Integer> receivedScores = getActivityData().getReceivedLuckScore();
        message.setByte(receivedScores.size());
        for (Integer i : receivedScores) {
            message.setInt(i);
        }
        role.sendMessage(message);
        role.savePlayer(saves);
        getActivityDao().updateKamPoLuckScore(getActivityData());
        getActivityDao().updateReceiveLuckScore(getActivityData());
    }

    /**
     * 玩家获得奖励信息
     * @param request
     */
//	public void processKamPo2Message(Message request) {
//		int day = 0;//getActivityDao().getKamPoDay(getActivityData());
//		Map<String, KamPoLogicData2> logics = ActivityService.getRoundData(EActivityType.KAM_PO2, day);
//		List<Ele> eles = new ArrayList<>();
//		for(KamPoLogicData2 kpd : logics.values()) {
//			Ele ele = new Ele(kpd.getId(), kpd.getDiaoluo());
//			eles.add(ele);
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		Ele getEle = DiceUtil.dice(eles);
//		//随机到的id
//		int id = getEle.getId();
//		KamPoLogicData2 kpd = logics.get(id+"");
//		DropData dd_consume = kpd.getPrices();
//		EGoodsType.getGoodsType(dd_consume.getT()).getCmd().consume(role, dd_consume, EGoodsChangeType.KAM_PO_SONSUME, saves);
//		DropData dd_reward = kpd.getReward();
//		List<DropData> dd_rewards = new ArrayList<>();
//		if(dd_reward.getT() == EGoodsType.EQUIP.getId()) {
//			int grid = role.getPlayer().getEquipBagFreeGrid();
//			if(grid <= 0) {
//				dd_rewards.add(dd_reward);
//				Mail mail = MailService.createMail(new MailRewardModelData((short)1, "幸运轮盘未领取奖励", "亲爱的玩家，您在参加幸运轮盘时未即时领取专属奖励，特以邮件形式为您发放", dd_rewards), EGoodsChangeType.LUCK_SCORE_ADD);
//				MailService.sendSystemMail(getActivityData().getPlayerId(), mail);
//			}else {
//				EGoodsType.getGoodsType(dd_reward.getT()).getCmd().reward(role, dd_reward, EGoodsChangeType.KAM_PO_ADD, saves);
//			}
//		}else {
//			EGoodsType.getGoodsType(dd_reward.getT()).getCmd().reward(role, dd_reward, EGoodsChangeType.KAM_PO_ADD, saves);
//		}
//
//		//更新幸运值
//		int scoreSum = getActivityData().addKamPo2LuckScore(kpd.getLuckScore());
//		int highest = 0;
//		List<Integer> allLuckRewards = new ArrayList<>();
//		for(Integer s : kpd.getLuck_reward().keySet()) {
//			allLuckRewards.add(s);
//			if(s > highest) highest = s;
//		}
//		allLuckRewards.removeAll(getActivityData().getReceivedLuckScore2());
//		if(scoreSum > highest) {
//			getActivityData().setKamPo2LuckScore(scoreSum - highest);
//			if(allLuckRewards != null && !allLuckRewards.isEmpty()) {
//				List<DropData> dropDatas = new ArrayList<>();
//				for(Integer i : allLuckRewards) {
//					DropData dd = kpd.getLuck_reward().get(i);
//					dropDatas.add(dd);
//				}
//				Mail mail = MailService.createMail(new MailRewardModelData((short)1, "幸运轮盘未领取奖励", "亲爱的玩家，您在参加幸运轮盘时未即时领取专属奖励，特以邮件形式为您发放", dropDatas), EGoodsChangeType.LUCK_SCORE_ADD);
//				MailService.sendSystemMail(getActivityData().getPlayerId(), mail);
//			}
//			getActivityData().resetReceivedLuckScore2();
//		}
//		getActivityDao().updateKamPo2LuckScore(getActivityData());
//		getActivityDao().updateReceiveLuckScore2(getActivityData());
//		Message message = new Message(MessageCommand.GAME_KAM_PO2_MESSAGE);
//		message.setChannel(request.getChannel());
//		message.setInt(getActivityData().getKamPo2LuckScore());
//		message.setByte(id);
//		List<Integer> receivedScores = getActivityData().getReceivedLuckScore2();
//		message.setByte(receivedScores.size());
//		for(Integer i : receivedScores) {
//			message.setInt(i);
//		}
//		role.sendMessage(message);
//		role.savePlayer(saves);
//	}

    /**
     * 根据当前次数获取组
     *
     * @param cost     次数
     * @param logics
     * @param isReturn
     * @return
     */
    private int handleCost2KamPo(int cost, List<KamPoLogicData3> logics) {
        if (logics == null || logics.isEmpty()) return -1;
        int costRound = 1;
        int len = logics.size();
        List<Integer> costList = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            if (!costList.contains(logics.get(i).getCost())) {
                costList.add(logics.get(i).getCost());
            }
        }
        int size = costList.size();
        Collections.sort(costList, new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 > o2 ? -1 : o1 < o2 ? 1 : 0;
            }
        });
        List<Integer> list = getActivityData().getKamPo2Costs();
        if (list == null || list.isEmpty()) {
            getActivityData().getKamPo2Costs().add(1);
        }
        Collections.sort(list, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 > o2 ? -1 : o1 < o2 ? 1 : 0;
            }
        });
        for (int i = 0; i < size; i++) {
            int curr = costList.get(i);
            if (cost >= curr /*&& curr > list.get(0)*/) {
                costRound = curr;
                break;
            }
        }
        return costRound;
    }

    private List<Ele> getEles4KamPo(List<KamPoLogicData3> logics, int cost) {
        if (logics == null || logics.isEmpty()) {
            return null;
        }
        List<Ele> eles = new ArrayList<>();
        int len = logics.size();
        for (int i = 0; i < len; i++) {
            KamPoLogicData3 kpld = logics.get(i);
            if (kpld.getCost() == cost) {
                Ele ele = new Ele(kpld.getId(), kpld.getDiaoluo());
                eles.add(ele);
            }
        }
        return eles;
    }

    /**
     * 玩家获得奖励信息
     *
     * @param request
     */
    public void processKamPo2Message(Message request) {
        byte type = request.readByte();
        short activityId = request.readShort();
        int day = 0;//getActivityDao().getKamPoDay(getActivityData());
        Map<String, KamPoLogicData3> logics = ActivityService.getRoundData(EActivityType.getType(activityId), day);

        KamPoLogicData3 kpdConsume = logics.get("1");
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        DropData dd_consume = null;
        if (type == 0) {
            getActivityData().setKamPo2RandomNum(1);
            dd_consume = kpdConsume.getPrices();
        } else if (type == 1) {
            getActivityData().setKamPo2RandomNum(10);
            dd_consume = kpdConsume.getPrices10();
        }

        EGoodsType.getGoodsType(dd_consume.getT()).getCmd().consume(role, dd_consume, EGoodsChangeType.KAM_PO2_SONSUME, saves);
        while (getActivityData().getKamPo2RandomNum() > 0) {
            int count = getActivityData().getKamPo2Count();
            int cost = this.handleCost2KamPo(count, new ArrayList<>(logics.values()));
            getActivityData().setKamPo2Cost(cost);
            this.handleRandom(getActivityData().getKamPo2Cost(), new ArrayList<KamPoLogicData3>(logics.values()), EActivityType.getType(activityId));
        }
        List<Integer> ids = getActivityData().getKamPo2Ids();
        for (Integer id : ids) {
            KamPoLogicData3 kpd = logics.get(id + "");
            DropData dd_reward = kpd.getReward();
            List<DropData> dd_rewards = new ArrayList<>();
            if (dd_reward.getT() == EGoodsType.EQUIP.getId()) {
                int grid = role.getPlayer().getEquipBagFreeGrid();
                if (grid <= 0) {
                    dd_rewards.add(dd_reward);
                    Mail mail = MailService.createMail(new MailRewardModelData((short) 1, "幸运轮盘未领取奖励", "亲爱的玩家，您在参加幸运轮盘时未即时领取专属奖励，特以邮件形式为您发放", dd_rewards), EGoodsChangeType.KAM_PO2_ADD);
                    MailService.sendSystemMail(getActivityData().getPlayerId(), mail);
                } else {
                    EGoodsType.getGoodsType(dd_reward.getT()).getCmd().reward(role, dd_reward, EGoodsChangeType.KAM_PO2_ADD, saves);
                }
            } else {
                EGoodsType.getGoodsType(dd_reward.getT()).getCmd().reward(role, dd_reward, EGoodsChangeType.KAM_PO2_ADD, saves);
            }
            //走马灯
            if (EGoodsType.BOX.getId() == dd_reward.getT()) {
                if (dd_reward.getG() == 207 || dd_reward.getG() == 208 || dd_reward.getG() == 209 || dd_reward.getG() == 210) {
                    BoxData boxData = GoodsModel.getBoxDataById(dd_reward.getG());
                    ChatService.broadcastPlayerMsg(player, EBroadcast.KAM_PO2, boxData.getName());
                }
            }
        }
        Message message = new Message(MessageCommand.GAME_KAM_PO3_MESSAGE);
        message.setChannel(request.getChannel());
        message.setByte(ids.size());
        for (Integer id : ids) {
            message.setInt(id);
        }
        getActivityData().clearKamPo2Ids();
        role.sendMessage(message);
        role.savePlayer(saves);
        getActivityDao().updateKamPo2(getActivityData());

    }

    private void handleRandom(int cost, List<KamPoLogicData3> logics, EActivityType type) {
        if (getActivityData().getKamPo2RandomNum() <= 0) return;
        getActivityData().reduceAutoKamPo2RandomNum();
        Ele getEle = DiceUtil.dice(this.getEles4KamPo(logics, getActivityData().getKamPo2Cost()));
        //随机到的id
        int id = getEle.getId();
        getActivityData().getKamPo2Ids().add(id);
        Map<String, KamPoLogicData3> map = ActivityService.getRoundData(type, 0);
        KamPoLogicData3 kpd = map.get(id + "");
        getActivityData().kamPo2AutoCount();
        if (kpd.getType() == 1) {
            getActivityData().getKamPo2Costs().add(cost);
            //cost返回到1
            int cost1 = 1;
            getActivityData().setKamPo2Cost(cost1);
            this.handleRandom(cost1, logics, type);
        } else if (kpd.getType() == 2) {
            getActivityData().getKamPo2Costs().add(cost);
            //抽取次数归零
            getActivityData().resetKamPo2Count();
            //清空已抽取过的组
            this.clearKamPo2Costs();
            //cost返回到1
            int cost2 = this.handleCost2KamPo(getActivityData().getKamPo2Count(), logics);
            getActivityData().setKamPo2Cost(cost2);
            this.handleRandom(cost2, logics, type);
        }
    }

    private void clearKamPo2Costs() {
        getActivityData().clearKamPo2Costs();
        getActivityData().getKamPo2Costs().add(1);
    }

    private List<FirecrackerLogicData> handleCost4Firecracker(int cost, List<FirecrackerLogicData> list) {
        if (list == null || list.isEmpty()) return new ArrayList<>();
        int len = list.size();
        List<FirecrackerLogicData> flds = new ArrayList<>();
        List<Integer> costList = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            if (!costList.contains(list.get(i).getCost())) {
                costList.add(list.get(i).getCost());
            }
        }
        int costRound = 1;
        int size = costList.size();
        Collections.sort(costList, new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 > o2 ? -1 : o1 < o2 ? 1 : 0;
            }
        });
        for (int i = 0; i < size; i++) {
            int curr = costList.get(i);
            if (cost >= curr) {
                costRound = curr;
                break;
            }
        }
        for (int i = 0; i < len; i++) {
            FirecrackerLogicData fld = list.get(i);
            if (fld.getCost() == costRound) {
                flds.add(fld);
            }
        }
        return flds;
    }

    /**
     * 处理幸运鞭炮
     *
     * @param request
     */
    public void processLuckFirecrackerMessage(Message request) {
        int type = request.readByte();//0 - 抽一次， 1 - 抽5次
        int round = 0;//getActivityDao().getKamPoDay(getActivityData());
        Map<String, FirecrackerLogicData> logics = ActivityService.getRoundData(EActivityType.LUCK_FIRECRACKER, round);
        List<Ele> eles = new ArrayList<>();
        int cost = getActivityData().getFirecrackerCount();
        int currCost = 0;
        for (FirecrackerLogicData kpd : this.handleCost4Firecracker(cost, new ArrayList<>(logics.values()))) {
            currCost = kpd.getCost();
            Ele ele = new Ele(kpd.getId(), kpd.getDiaoluo());
            eles.add(ele);
        }
        //抽取次数
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //随机到的id
        List<Integer> ids = new ArrayList<>();
        int randomNum = 0;
        int addScore = 0;
        if (type == 1) {
            randomNum = 5;
            addScore = 5;
        } else if (type == 0) {
            randomNum = 1;
            addScore = 1;
        } else {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //消耗元宝
        FirecrackerLogicData fld = logics.get("" + 1);
        DropData dd_consume = null;
        if (type == 0) {
            dd_consume = fld.getPrices0();
        } else {
            dd_consume = fld.getPrices1();
        }
        boolean consume = EGoodsType.getGoodsType(dd_consume.getT()).getCmd().consume(role, dd_consume, EGoodsChangeType.FIRECARCKER_SONSUME, saves);
        if (!consume) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        //开始随机
        while (randomNum > 0) {
            Ele getEle = DiceUtil.dice(eles);
            int id = getEle.getId();
            ids.add(id);
            randomNum--;

            FirecrackerLogicData kpd = logics.get(id + "");
            //添加鞭炮全服信息
            if (kpd.getType() > 0) {
                PlayerFirecrackerInfo firecrackerInfo = new PlayerFirecrackerInfo();
                firecrackerInfo.setPlayerName(role.getPlayer().getName());
                firecrackerInfo.setReward(kpd.getReward());
                PlayerFirecrackerInfosService.addPlayerInfo(firecrackerInfo);
                getActivityData().resetFirecrackerCount();
                DropData dd_reward = kpd.getReward();
                boolean reward = EGoodsType.getGoodsType(dd_reward.getT()).getCmd().reward(role, dd_reward, EGoodsChangeType.FIRECARCKER_TURN, saves);
                if (!reward) {
                    role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
                    return;
                }
                break;
            }
            DropData dd_reward = kpd.getReward();
            boolean reward = EGoodsType.getGoodsType(dd_reward.getT()).getCmd().reward(role, dd_reward, EGoodsChangeType.FIRECARCKER_TURN, saves);
            if (!reward) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
                return;
            }

            //抽取次数+1
            getActivityData().firecrackerCountAutoIncrement();
        }
        //如果第一次拿到type>0的奖品，则后面的4次重新随机
        while (randomNum > 0) {
            Map<String, FirecrackerLogicData> logics2 = ActivityService.getRoundData(EActivityType.LUCK_FIRECRACKER, round);
            List<Ele> eles2 = new ArrayList<>();
            int cost2 = getActivityData().getFirecrackerCount();
            for (FirecrackerLogicData kpd : this.handleCost4Firecracker(cost2, new ArrayList<>(logics2.values()))) {
                Ele ele = new Ele(kpd.getId(), kpd.getDiaoluo());
                eles2.add(ele);
            }
            Ele getEle = DiceUtil.dice(eles2);
            int id = getEle.getId();
            ids.add(id);
            randomNum--;

            FirecrackerLogicData kpd = logics.get(id + "");
            //添加鞭炮全服信息
            if (kpd.getType() > 0) {
                PlayerFirecrackerInfo firecrackerInfo = new PlayerFirecrackerInfo();
                firecrackerInfo.setPlayerName(role.getPlayer().getAccount());
                firecrackerInfo.setReward(kpd.getReward());
                PlayerFirecrackerInfosService.addPlayerInfo(firecrackerInfo);
                getActivityData().resetFirecrackerCount();
            }
            DropData dd_reward = kpd.getReward();
            boolean reward = EGoodsType.getGoodsType(dd_reward.getT()).getCmd().reward(role, dd_reward, EGoodsChangeType.KAM_PO_ADD, saves);
            if (!reward) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
                return;
            }
//			role.savePlayer(saves);
            //抽取次数+1
            getActivityData().firecrackerCountAutoIncrement();
        }
        FirecrackerLogicData kpd = logics.get(ids.get(0) + "");
        //更新幸运值
        int scoreSum = getActivityData().addFirecrackerLuckScore(addScore);
        int highest = 0;
        List<Integer> allLuckRewards = new ArrayList<>();
        Map<Integer, Integer> allLuckScores = kpd.getLuck_score();
        for (Integer s : kpd.getLuck_reward().keySet()) {
            allLuckRewards.add(s);
            if (allLuckScores.get(s) > highest) highest = allLuckScores.get(s);
        }
        allLuckRewards.removeAll(getActivityData().getReceivedFirecrackerLuckScore());
        if (scoreSum > highest) {
            scoreSum = highest;
        }
        Message message = new Message(MessageCommand.GAME_FIRECRACKER_MESSAGE_PROCESS);
        message.setChannel(request.getChannel());
        message.setInt(getActivityData().getFirecrackerLuckScore());
        message.setByte(ids.size());
        for (Integer id : ids) {
            message.setInt(id);
        }
        List<Integer> receivedScores = getActivityData().getReceivedFirecrackerLuckScore();
        message.setByte(receivedScores.size());
        for (Integer i : receivedScores) {
            message.setInt(i);
        }
        List<PlayerFirecrackerInfo> infos = PlayerFirecrackerInfosService.getPlayerTurntableInfos();
        message.setByte(infos.size());
        for (PlayerFirecrackerInfo pfi : infos) {
            message.setString(pfi.getPlayerName());
            String str = "" + pfi.getReward().getT() + "," + pfi.getReward().getG() + "," + pfi.getReward().getN();
            message.setString(str);
        }
        message.setInt(currCost);
        role.sendMessage(message);
        role.savePlayer(saves);
        getActivityDao().updateFirecrackerLuckScore(getActivityData());
        getActivityDao().updateReceiveFirescrackerLuckScore(getActivityData());
        getActivityDao().updateFirecrackerCost(getActivityData());
    }

    /**
     * 鞭炮幸运值领取
     *
     * @param request
     */
    public void processFirecrackerLuckScore(Message request) {
        int score = request.readInt();
        Map<String, FirecrackerLogicData> logics = ActivityService.getRoundData(EActivityType.LUCK_FIRECRACKER, 0);
        FirecrackerLogicData kpd = logics.get(10 + "");
        Map<Integer, DropData> datas = kpd.getLuck_reward();
        DropData dd = datas.get(score);
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        boolean flag2 = getActivityData().addReceivedFirecrackerLuckScore(score);
        boolean flag1 = true;
        if (flag2) {
            byte t = dd.getT();
            EGoodsType type = EGoodsType.getGoodsType(t);
            IGoodsCmd cmd = type.getCmd();
            flag1 = cmd.reward(role, dd, EGoodsChangeType.FIRECARCKER_RECEIVE, saves);
        }
        if (!flag1 || !flag2) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_FIRECRACKER_LUCK_RECEIVE);
            return;
        }
        Message message = new Message(MessageCommand.GAME_FIRECRACKER_LUCK_SCORE_MESSAGE);
        message.setChannel(request.getChannel());
        List<Integer> receivedScores = getActivityData().getReceivedFirecrackerLuckScore();
        message.setByte(receivedScores.size());
        for (Integer i : receivedScores) {
            message.setInt(i);
        }
        role.sendMessage(message);
        role.savePlayer(saves);
        getActivityDao().updateReceiveFirescrackerLuckScore(getActivityData());
    }

    /**
     * 鞭炮幸运初始化
     *
     * @param request
     */
    public void processInitFirecrackerLuckScoreMessage(Message request) {
        int luckScore = getActivityData().getFirecrackerLuckScore();
        List<Integer> scoreList = getActivityData().getReceivedFirecrackerLuckScore();
        Message msg = new Message(MessageCommand.GAME_FIRECRACKER_MESSAGE);
        msg.setChannel(request.getChannel());
        msg.setInt(luckScore);
        int size = scoreList == null ? 0 : scoreList.size();
        msg.setByte(size);
        for (Integer score : scoreList) {
            msg.setInt(score);
        }
        List<PlayerFirecrackerInfo> infos = PlayerFirecrackerInfosService.getPlayerTurntableInfos();
        msg.setByte(infos.size());
        for (PlayerFirecrackerInfo pfi : infos) {
            msg.setString(pfi.getPlayerName());
            String str = "" + pfi.getReward().getT() + "," + pfi.getReward().getG() + "," + pfi.getReward().getN();
            msg.setString(str);
        }
        role.sendMessage(msg);
    }

    /**
     * 获取未领取幸运奖励
     *
     * @return
     */
    public List<DropData> getReceiveLuckScore() {
        Map<String, KamPoLogicData> logics = ActivityService.getRoundData(EActivityType.KAM_PO, 0);
        KamPoLogicData kpd = logics.get("1");
        Map<Integer, DropData> map = kpd.getLuck_reward();
        List<Integer> all = new ArrayList<>(map.keySet());
        List<Integer> received = getActivityData().getReceivedLuckScore();
        all.removeAll(received);
        List<DropData> dropDatas = new ArrayList<>();
        int currLuckScore = getActivityData().getKamPoLuckScore();
        for (int i = 0; i < all.size(); i++) {
            int score = all.get(i);
            if (score <= currLuckScore) {
                DropData dd = kpd.getLuck_reward().get(score);
                dropDatas.add(dd);
            }
        }
        return dropDatas;
    }

    /**
     * 发送未领取幸运奖励
     */
    public void sendReceiveLuckScore() {
        List<DropData> dropDatas = this.getReceiveLuckScore();
        Mail mail = MailService.createMail(new MailRewardModelData((short) 1, "幸运轮盘未领取奖励", "亲爱的玩家，您在参加幸运轮盘时未即时领取专属奖励，特以邮件形式为您发放", dropDatas), EGoodsChangeType.LUCK_SCORE_ADD);
        int id = MailService.sendSystemMail(getActivityData().getPlayerId(), mail);
        if (id != -1) {
            getActivityData().resetReceivedLuckScore();
            getActivityData().setKamPoLuckScore(0);
            getActivityDao().updateKamPoLuckScore(getActivityData());
            getActivityDao().updateReceiveLuckScore(getActivityData());
        }
    }

    /**
     * 鉴宝幸运值领取
     *
     * @param request
     */
    public void processLuckScore(Message request) {
        int score = request.readInt();
        Map<String, KamPoLogicData> logics = ActivityService.getRoundData(EActivityType.KAM_PO, 0);
        KamPoLogicData kpd = logics.get(10 + "");
        Map<Integer, DropData> datas = kpd.getLuck_reward();
        DropData dd = datas.get(score);
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        boolean flag2 = getActivityData().addReceivedLuckScore(score);
        boolean flag1 = true;
        if (flag2) {
            byte t = dd.getT();
            EGoodsType type = EGoodsType.getGoodsType(t);
            IGoodsCmd cmd = type.getCmd();
            flag1 = cmd.reward(role, dd, EGoodsChangeType.LUCK_SCORE_ADD, saves);
        }
        if (!flag1 || !flag2) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_LUCK_SORCE_RECEIVE);
            return;
        }
        Message message = new Message(MessageCommand.GAME_LUCK_SCORE_MESSAGE);
        message.setChannel(request.getChannel());
        List<Integer> receivedScores = getActivityData().getReceivedLuckScore();
        message.setByte(receivedScores.size());
        for (Integer i : receivedScores) {
            message.setInt(i);
        }
        role.sendMessage(message);
        getActivityDao().updateReceiveLuckScore(getActivityData());
    }

    /**
     * 鉴宝幸运值领取
     *
     * @param request
     */
    public void processLuckScore2(Message request) {
        int score = request.readInt();
        Map<String, KamPoLogicData2> logics = ActivityService.getRoundData(EActivityType.KAM_PO2, 0);
        KamPoLogicData2 kpd = logics.get(10 + "");
        Map<Integer, DropData> datas = kpd.getLuck_reward();
        DropData dd = datas.get(score);
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        boolean flag2 = getActivityData().addReceivedLuckScore2(score);
        boolean flag1 = true;
        if (flag2) {
            byte t = dd.getT();
            EGoodsType type = EGoodsType.getGoodsType(t);
            IGoodsCmd cmd = type.getCmd();
            flag1 = cmd.reward(role, dd, EGoodsChangeType.LUCK_SCORE_ADD, saves);
        }
        if (!flag1 || !flag2) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_LUCK_SORCE_RECEIVE);
            return;
        }
        getActivityDao().updateReceiveLuckScore2(getActivityData());
        Message message = new Message(MessageCommand.GAME_LUCK_SCORE2_MESSAGE);
        message.setChannel(request.getChannel());
        List<Integer> receivedScores = getActivityData().getReceivedLuckScore2();
        message.setByte(receivedScores.size());
        for (Integer i : receivedScores) {
            message.setInt(i);
        }
        role.sendMessage(message);
    }

    /**
     * 充值累计奖励领取
     *
     * @param request
     */
    public void processPayCountReceive(Message request) {
        byte day = request.readByte();
        String key = String.valueOf(day);
        Map<String, LeiChongLogicData> logics = ActivityService.getRoundData(EActivityType.LEICHONG, 0);
        LeiChongLogicData logic = logics.get(key);
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        if (getActivityData().isPayCountReceived(day)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (role.getPayManager().getPayDays() < day) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        getActivityData().receivePayCount(day);
        getActivityDao().updateLeichongReward(getActivityData());

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(logic.getReward(), EGoodsChangeType.LEICHONG_ADD, saves);
        role.savePlayer(saves);

        Message message = getActivityMsg(EActivityType.LEICHONG);
        message.setChannel(request.getChannel());
        role.sendMessage(message);
    }

    private void sendPayContinueMessage(GameRole role, Channel channel) {
        Message message = getActivityMsg(EActivityType.PAY_CONTINUE);
        message.setChannel(channel);
        role.sendMessage(message);
    }

    /**
     * 连续充值奖励领取
     *
     * @param request
     */
    public void processPayContinueReceive(Message request) {
        byte dayTarget = request.readByte();
        long curr = System.currentTimeMillis();
        BaseActivityConfig config = ActivityService.getActivityConfig(EActivityType.PAY_CONTINUE);
        if (config == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_ACTIVITY);
            return;
        }
        long startTime = config.getStartTime(0);
        long endTime = config.getEndTime();
        if (curr < startTime || curr > endTime) {
            //dead
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_ACTIVITY);
            return;
        }
        Map<String, PayContinueLogicData> logics = ActivityService.getRoundData(EActivityType.PAY_CONTINUE, 0);
        if (logics == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_ACTIVITY);
            return;
        }

        if (logics.isEmpty()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        // 注：此算法中只支持统一价格 否则深度增加
        int rmb = logics.values().iterator().next().getRmb();
        byte dayCount = role.getPayManager().getPayGreaterDays(rmb, startTime, curr);
        if (dayCount < dayTarget) {
            sendPayContinueMessage(role, request.getChannel());
            return;
        }

        PayContinueLogicData logic = logics.get(String.valueOf(dayTarget));
        if (logic == null) {
            sendPayContinueMessage(role, request.getChannel());
            return;
        }

        //已经领取过该奖励
        if (getActivityData().getPayConReward().contains(dayTarget)) {
            sendPayContinueMessage(role, request.getChannel());
            return;
        }
        getActivityData().getPayConReward().add(dayTarget);
        getActivityDao().updatePayConReward(getActivityData());
        // 发奖励
        if (logic.getRewards().isEmpty()) {
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(logic.getRewards(), EGoodsChangeType.PAYCON_ADD, saves);
        role.savePlayer(saves);

        Message message = getActivityMsg(EActivityType.PAY_CONTINUE);
        message.setChannel(request.getChannel());
        role.sendMessage(message);
    }

    public void processFestPayConMessage(Message request) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig config = ActivityService.getActivityConfig(EActivityType.FEST_PAY_CONTINUE);
        Map<String, FestPayContinueLogicData> logics = ActivityService.getRoundData(EActivityType.FEST_PAY_CONTINUE, 0);
        // 注：此算法中只支持统一价格 否则深度增加
        int rmb = logics.values().iterator().next().getRmb();
        long startTime = config.getStartTime(0);
        long endTime = config.getEndTime();
        int day = DateUtil.getDistanceDay(startTime, curr) + 1;
        int dayCount = role.getPayManager().getPayConGreaterDays(rmb, startTime, endTime);
        // 注：此算法中只支持统一价格 否则深度增加
        getActivityData().setFestPayConDayCount(dayCount);
        Message msg = new Message(MessageCommand.GAME_FEST_PAY_CON_MESSAGE, request.getChannel());
        //第几天
        msg.setByte(day);

        msg.setByte(dayCount);
//		int sum = new PayDao().getTodayRMBPay(role.getPlayer());
        int sum = role.getPayManager().getTodayRmbInPay();
        //今日充值金额
        msg.setInt(sum);
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        //连续充值领取
        msg.setByte(getActivityData().getFestPayConReward().size());
        for (byte reward : getActivityData().getFestPayConReward()) {
            msg.setByte(reward);
        }
        //今天剩余多少秒
        msg.setInt((int) ((DateUtil.getDayStartTime(curr) + DateUtil.DAY - curr) / DateUtil.SECOND));
        role.sendMessage(msg);
        getActivityDao().updatePayConDayCount(getActivityData());
    }

    /**
     * 连续充值奖励领取
     *
     * @param request
     */
    public void processFestPayContinueReceive(Message request) {
        byte dayTarget = request.readByte();
        short activityId = request.readShort();
        long curr = System.currentTimeMillis();
        BaseActivityConfig config = ActivityService.getActivityConfig(EActivityType.getType(activityId));
        if (config == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_ACTIVITY);
            return;
        }
        long startTime = config.getStartTime(0);
        long endTime = config.getEndTime();
        if (curr < startTime || curr > endTime) {
            //dead
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_ACTIVITY);
            return;
        }
        Map<String, FestPayContinueLogicData> logics = ActivityService.getRoundData(EActivityType.getType(activityId), dayTarget - 1);
        if (logics == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_ACTIVITY);
            return;
        }

        if (logics.isEmpty()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        int dayCount = getActivityData().getFestPayConDayCount();

        if (dayCount < dayTarget) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        FestPayContinueLogicData logic = logics.get(String.valueOf(dayTarget));
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        //已经领取过该奖励
        if (getActivityData().getFestPayConReward().contains(dayTarget)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        getActivityData().getFestPayConReward().add(dayTarget);
        // 发奖励
        if (logic.getRewards().isEmpty()) {
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(logic.getRewards(), EGoodsChangeType.FEST_PAY_DAILY_CONTINUE, saves);

        Message message = getActivityMsg(EActivityType.getType(activityId));
        message.setChannel(request.getChannel());
        role.sendMessage(message);
        role.savePlayer(saves);
        getActivityDao().updateFestPayConReward(getActivityData());
    }

    /**
     * 大富翁奖励领取
     *
     * @param request
     */
    public void processMonopolyReceive(Message request) {
        byte type = request.readByte();//类型
        int num = request.readInt();//领取的数

        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.MONOPOLY);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;

        ActivityGroupData group = ActivityService.getGroupData(EActivityType.MONOPOLY);
        Map<String, MonopolyLogicData> mapRound = ActivityService.getRoundData(EActivityType.MONOPOLY, role.getPlayerId(), curr);
        MonopolyLogicData mld = mapRound.get(getActivityData().getMonopolyCurrLevel() + "");
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (type == 1) {//层数
            int receiveLevel = getActivityData().getMonopolyCurrLevel();
            if (!mld.getLevelInfos().keySet().contains(num) || (num > receiveLevel || getActivityData().getMonopolyLevelReceived().contains(num))) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_MONOPOLY_NO_COST);
                return;
            }
            getActivityData().addMonopolyLevelReceived(num);
            getActivityDao().updateMonopolyLevelReceived(getActivityData());
            List<DropData> dds = mld.getLevelInfos().get(num);
            role.getPackManager().addGoods(dds, EGoodsChangeType.MONOPOLY_TYPE_RECEIVE, saves);
        } else if (type == 2) {//次数
            int todayNum = getActivityData().getMonopolyTodayNum();
            if (!mld.getNumInfos().keySet().contains(num) || (num > todayNum || getActivityData().getMonopolyTodayNumReceive().contains(num))) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_MONOPOLY_NO_COST);
                return;
            }
            getActivityData().addMonopolyTodayNumReceive(num);
            MonopolyLogicData.NumInfo numInfo = null;
            for (NumInfo ni : mld.getNumInfos().values()) {
                if (ni.getNum() == num) {
                    numInfo = ni;
                    break;
                }
            }
            List<DropData> dd = numInfo.getDd();
            role.getPackManager().addGoods(dd, EGoodsChangeType.MONOPOLY_TYPE_RECEIVE, saves);
            getActivityDao().updateMonopolyTodayNumReceive(getActivityData());
        }
        List<Integer> todayNumReceives = getActivityData().getMonopolyTodayNumReceive();
        Map<Integer, NumInfo> numInfos = mld.getNumInfos();
        int numReceive = 1;
        int highestNum = 0;
        for (Integer n : todayNumReceives) {
            if (highestNum < n) {
                highestNum = n;
            }
        }
        for (NumInfo ni : numInfos.values()) {
            if (ni.getNum() == highestNum) {
                numReceive = ni.getId() + 1;
                break;
            }
        }
        List<Integer> levelReceived = getActivityData().getMonopolyLevelReceived();
        Message message = new Message(MessageCommand.GAME_MONOPOLY_RECEIVE, request.getChannel());
        message.setByte(levelReceived.size());
        //层数已领取id
        for (int i : levelReceived) {
            message.setInt(i);
        }
        message.setInt(numReceive);
        role.sendMessage(message);
        role.savePlayer(saves);
    }

    public void processMonopolyDice(Message request) {
        byte type = request.readByte();

        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.MONOPOLY);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData group = ActivityService.getGroupData(EActivityType.MONOPOLY);
        Map<String, MonopolyLogicData> mapRound = ActivityService.getRoundData(EActivityType.MONOPOLY, role.getPlayerId(), curr);
        MonopolyLogicData mld = mapRound.get(getActivityData().getMonopolyCurrLevel() + "");
        MonopolyLogicData nextMld = mapRound.get(getActivityData().getMonopolyNextLevel() + "");
        int playerTime = getActivityData().getMonopolyPlayerTime();
        //随机组
        Set<Integer> set = new HashSet<>();
        for (String str : mapRound.keySet()) {
            set.add(Integer.valueOf(str));
        }

        List<DropData> consume = null;
        if (type == 1) {
            playerTime -= 1;
            consume = mld.getPrices0();
        } else {
            playerTime -= 2;
            consume = mld.getPrices1();
        }
        if (playerTime < 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MONOPOLY_PLAYER_TIME_LESS);
            return;
        }
        getActivityData().setMonopolyPlayerTime(playerTime);
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		boolean flag = EGoodsType.getGoodsType(consume.get(1).getT()).getCmd().consume(role, consume.get(1), EGoodsChangeType.MONOPOLY_CONSUME, saves);
//		if(!flag) {
//			if(!EGoodsType.getGoodsType(consume.get(0).getT()).getCmd().consume(role, consume.get(0), EGoodsChangeType.MONOPOLY_CONSUME, saves)) {
//				role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
//				return;
//			}
//		}
        if (!role.getPackManager().addGoods(consume, EGoodsChangeType.MONOPOLY_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        int currLevel = getActivityData().getMonopolyCurrLevel();
        int currSteps = getActivityData().getMonopolyCurrSteps();
        int highestSteps = mld.getStepInfos().size() / set.size();
        //所有奖励层数
        List<Integer> allReceives = new ArrayList<>(mld.getLevelInfos().keySet());
        Ele ele = DiceUtil.dice(mld.getEleMap().get((int) type));
        int dice = ele.getId();
        int allCurrSteps = currSteps + dice;
//		int currReceiveLevel = currLevel;
//		boolean currReceiceFlag = false;

        //发奖励
        int step = currSteps == 0 ? 1 : currSteps;
        int stepId = 0;
        for (MonopolyLogicData.StepInfo si : mld.getStepInfos().values()) {
            if (si.getGroup() == currLevel && si.getStep() == step) {
                stepId = si.getId();
                break;
            }
        }

        int highestStepRewardId = this.getLevelStepHighest(mld);
        int endReward1 = stepId + dice;
        int endReward2 = 0;
        if (endReward1 > highestStepRewardId) {
            endReward2 = endReward1 - highestStepRewardId;
            endReward1 = highestStepRewardId;
        }
        if (currSteps != 0) {
            stepId = stepId + 1;
        }
        for (Integer i = stepId; i <= endReward1; i++) {
            getActivityData().addStepId(i);
            DropData dd = mld.getStepInfos().get(i).getReward();
            EGoodsType.getGoodsType(dd.getT()).getCmd().reward(role, dd, EGoodsChangeType.MONOPOLY_RECEIVE, saves);
        }
        int lowestStepId = this.getLevelStepLowest(nextMld);
        for (Integer i = 0; i < endReward2; i++) {
            getActivityData().addStepId(lowestStepId + i);
            DropData dd = mld.getStepInfos().get(lowestStepId + i).getReward();
            EGoodsType.getGoodsType(dd.getT()).getCmd().reward(role, dd, EGoodsChangeType.MONOPOLY_RECEIVE, saves);
        }

        int highestLevelReceive = 0;
        for (int i : allReceives) {
            if (i > highestLevelReceive) {
                highestLevelReceive = i;
            }
        }

        if (highestSteps < allCurrSteps) {
            allCurrSteps = allCurrSteps - highestSteps;
            //层数+1
            int todayPlayLevel = getActivityData().getMonopolyTodayPlayLevel() + 1 > highestLevelReceive ? highestLevelReceive : getActivityData().getMonopolyTodayPlayLevel() + 1;
            getActivityData().setMonopolyTodayPlayLevel(todayPlayLevel);
            getActivityDao().updateMonopolyTodayPlayLevel(getActivityData());
            //重置当前层数和下次层数
            currLevel = getActivityData().getMonopolyNextLevel();
            getActivityData().setMonopolyCurrLevel(currLevel);
            int nextLevel = DiceUtil.getIntByOrder(currLevel, set);
            getActivityData().setMonopolyNextLevel(nextLevel);
            getActivityDao().updateMonopolyNextLevel(getActivityData());
            getActivityDao().updateMonopolyCurrLevel(getActivityData());
//			currReceiceFlag = true;
        }
        //保存当前步数
        getActivityData().setMonopolyCurrSteps(allCurrSteps);

        if (getActivityData().getMonopolyTodayPlayLevel() >= highestLevelReceive) {
            getActivityData().setMonopolyTodayPlayLevel(0);
            getActivityDao().updateMonopolyTodayPlayLevel(getActivityData());
            allReceives.removeAll(getActivityData().getMonopolyLevelReceived());
            List<DropData> dds = new ArrayList<>();
            boolean flag1 = true;
            for (int i : allReceives) {
                for (DropData dd : mld.getLevelInfos().get(i)) {
                    if (dds.isEmpty()) {
                        dds.add(dd);
                    } else {
                        int size = dds.size();
                        for (int j = 0; j < size; j++) {
                            DropData dropData = dds.get(j);
                            if (dropData.getT() == dd.getT() && dropData.getG() == dd.getG()) {
                                int sum = dd.getN() + dropData.getN();
                                DropData data = new DropData(dd.getT(), dd.getG(), sum);
                                dds.set(j, data);
                                flag1 = false;
                                break;
                            }
                        }
                        if (flag1) {
                            dds.add(dd);
                        }
                    }
                }
            }
            Mail mail = MailService.createMail("探宝层数未领取奖励", "探宝层数未领取奖励", EGoodsChangeType.MONOPOLY_RECEIVE, dds);
            MailService.sendSystemMail(role.getPlayerId(), mail);
            getActivityData().resetMonopolyLevelReceived();
            getActivityDao().updateMonopolyLevelReceived(getActivityData());
        }
        //更新重置次数
        //抽取次数+1
        int todayNum = getActivityData().getMonopolyTodayNum();
        if (type == 1) {
            todayNum += 1;
            getActivityData().setDiceOne(dice);
        } else if (type == 2) {
            todayNum += 2;
            int[] two = DiceUtil.getSumNum(mld.getAllDices(), dice);
            getActivityData().setDiceOne(two[0]);
            getActivityData().setDiceTwo(two[1]);
        }
        //添加次数领奖
//		for(Integer i : mld.getNumInfos().keySet()) {
//			if(todayNum >= i &&!getActivityData().getMonopolyTodayNumReceive().contains(i)) {
//				getActivityData().addMonopolyTodayNumReceive(i);
//			}
//		}
        getActivityData().setMonopolyTodayNum(todayNum);
        Message message = getActivityMsg(EActivityType.MONOPOLY);
        message.setChannel(request.getChannel());
        role.sendMessage(message);
        role.savePlayer(saves);
        getActivityDao().updateMonopolyTodayNumReceive(getActivityData());
        getActivityDao().updateMonopolyTodayNum(getActivityData());
        getActivityDao().updateMonopolyCurrSteps(getActivityData());
        getActivityDao().updateMonopolyPlayerTime(getActivityData());
    }

    private int getLevelStepLowest(MonopolyLogicData nextMld) {
        List<MonopolyLogicData.StepInfo> sis = new ArrayList<>();
        for (MonopolyLogicData.StepInfo si : nextMld.getStepInfos().values()) {
            if (si.getGroup() == getActivityData().getMonopolyNextLevel()) {
                sis.add(si);
            }
        }
        int lowestStepId = Integer.MAX_VALUE;
        for (MonopolyLogicData.StepInfo si : sis) {
            if (si.getId() < lowestStepId) {
                lowestStepId = si.getId();
            }
        }
        return lowestStepId;
    }

    private int getLevelStepHighest(MonopolyLogicData mld) {
        List<MonopolyLogicData.StepInfo> sis = new ArrayList<>();
        for (MonopolyLogicData.StepInfo si : mld.getStepInfos().values()) {
            if (si.getGroup() == getActivityData().getMonopolyCurrLevel()) {
                sis.add(si);
            }
        }
        int highestStepId = Integer.MIN_VALUE;
        for (MonopolyLogicData.StepInfo si : sis) {
            if (si.getId() > highestStepId) {
                highestStepId = si.getId();
            }
        }
        return highestStepId;
    }

    /**
     * 拼图(verb)
     *
     * @param request
     */
    public void processPuzzleDice(Message request) {
        int id = request.readInt();
        long curr = System.currentTimeMillis();
        Map<String, PuzzleLogicData> mapRound = ActivityService.getRoundData(EActivityType.PUZZLE, role.getPlayerId(), curr);
        List<Integer> puzzleReceiveds = getActivityData().getPuzzleReceived();
        if (puzzleReceiveds.contains(id)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PUZZLE_RECEIVED);
            return;
        }
        getActivityData().addPuzzleReceived(id);
        getActivityDao().updatePuzzleReceived(getActivityData());

        PuzzleLogicData pld = mapRound.get(id + "");
        DropData cost = pld.getCost();
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        boolean flag = EGoodsType.getGoodsType(cost.getT()).getCmd().consume(role, cost, EGoodsChangeType.PUZZLE_CONSUME, saves);
        if (!flag) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        Message msg = getActivityMsg(EActivityType.PUZZLE);
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
        role.savePlayer(saves);
    }

    /**
     * 拼图奖励领取
     *
     * @param request
     */
    public void processPuzzleReceive(Message request) {
        long curr = System.currentTimeMillis();
        Map<String, PuzzleLogicData> mapRound = ActivityService.getRoundData(EActivityType.PUZZLE, role.getPlayerId(), curr);
        PuzzleLogicData pld = mapRound.get("" + 75);
        int id = getActivityData().getPuzzleRestTime();
        int allRestTime = 200;
        int receivedSize = getActivityData().getPuzzleReceived().size();
        if (receivedSize < mapRound.size()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PUZZLE_NOT_FINISH);
            return;
        }
        if (allRestTime < id) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PUZZLE_RECEIVED);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        List<DropData> rewards = pld.getRewards().get(1);
        boolean flag = false;
        for (DropData reward : rewards) {
            flag = EGoodsType.getGoodsType(reward.getT()).getCmd().reward(role, reward, EGoodsChangeType.PUZZLE_RECEIVE, saves);
        }
        if (!flag) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PUZZLE_RECEIVED);
        }

        List<Integer> puzzleReceiveds = getActivityData().getPuzzleReceived();
        if (puzzleReceiveds.size() == mapRound.size()) {
            int restTime = getActivityData().getPuzzleRestTime() + 1;
            getActivityData().setPuzzleRestTime(restTime);
            getActivityDao().updatePuzzleRestTime(getActivityData());
        }
        getActivityData().resetPuzzleReceived();
        getActivityDao().updatePuzzleReceived(getActivityData());
        int restTime = getActivityData().getPuzzleRestTime();
        Message msg = new Message(MessageCommand.GAME_PUZZLE_RECEIVE, request.getChannel());
        msg.setByte(restTime);
        msg.setByte(puzzleReceiveds.size());
        for (Integer pr : puzzleReceiveds) {
            msg.setInt(pr);
        }
        role.sendMessage(msg);
        role.savePlayer(saves);
    }

    private void handlerPayContinue2(int sum) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig config = ActivityService.getActivityConfig(EActivityType.DUANWU_CONTINUE);
        if (config == null)
            return;
        byte day = (byte) (DateUtil.getDistanceDay(config.getStartTime(0), curr) + 1);
        Map<String, PayContinueLogicData> logics = ActivityService.getRoundData(EActivityType.DUANWU_CONTINUE, 0);
        if (day >= logics.size())
            return;
        PayContinueLogicData logic = logics.get(day + "");
        if (logic == null || logic.getRmb() > sum) {
            role.putMessageQueue(getActivityMsg(EActivityType.DUANWU_CONTINUE));
            return;
        }
        //已经领取过该奖励
        if (getActivityData().getPayConReward2().contains(day)) {
            role.putMessageQueue(getActivityMsg(EActivityType.DUANWU_CONTINUE));
            return;
        }
        getActivityData().getPayConReward2().add(day);
        getActivityDao().updatePayConReward2(getActivityData());
        //奖励
        Mail mail = MailService.createMail(logic.getTitle(), logic.getContent(),
                EGoodsChangeType.PAYCON_ADD, logic.getRewards());
        MailService.sendSystemMail(player.getId(), mail);
        //所有连续都满足
        if (getActivityData().getPayConReward2().size() == logics.size() - 1) {
            PayContinueLogicData last = logics.get(logics.size() + "");
            Mail lastMail = MailService.createMail(last.getTitle(), last.getContent(),
                    EGoodsChangeType.PAYCON_ADD, last.getRewards());
            MailService.sendSystemMail(player.getId(), lastMail);
        }
        role.putMessageQueue(getActivityMsg(EActivityType.DUANWU_CONTINUE));
    }

    private void handlerZmzp(int yuanbao) {
        Map<String, WishingWellLogicData> logicData = ActivityService.getRoundData(
                EActivityType.WEEKEND_WISHING_WELL, player.getId(), System.currentTimeMillis());
        if (logicData != null) {
            role.putMessageQueue(getActivityMsg(EActivityType.WEEKEND_WISHING_WELL));
        }
    }

    private void handlerPayFeast(int yuanbao) {
        long curr = System.currentTimeMillis();
        Map<String, PayFeastLogicData> logicData = ActivityService.getRoundData(
                EActivityType.PAY_FEAST, player.getId(), curr);
        if (logicData != null) {
            role.putMessageQueue(getActivityMsg(EActivityType.PAY_FEAST));
        }
        Map<String, TargetLogicData> festLogic = ActivityService.getRoundData(
                EActivityType.FEST_PAY_TARGET, role.getPlayerId(), curr);
        if (festLogic != null) {
            //消费金额
            GameRankManager.getInstance().addFestPayTop(role, yuanbao);
            role.putMessageQueue(getActivityMsg(EActivityType.FEST_PAY_TARGET));
            getActivityDao().updateFestPayData(getActivityData());
        }
    }

    private void handlerBuyOne(int rmb) {
        long curr = System.currentTimeMillis();
        Map<String, BuyOneLogicData> logicData = ActivityService.getRoundData(
                EActivityType.BUY_ONE, player.getId(), curr);
        if (logicData != null) {
            role.putMessageQueue(getActivityMsg(EActivityType.BUY_ONE));
        }
    }

    /**
     * 获取与loop0Id对应的ID及状态
     *
     * @return
     */
    private List<Entry<Byte, Byte>> getWelfareState() {
        long curr = System.currentTimeMillis();
        List<Entry<Byte, Byte>> entrys = new ArrayList<>();
        //Loop为0的福利
        List<Byte> baseIds = WelfareModel.getIdsByLoop(0);
        for (byte baseId : baseIds) {
            Entry<Byte, Byte> entry = null;
            //没领取过
            if (!getActivityData().getWelfare().containsKey(baseId)) {
                entry = new AbstractMap.SimpleEntry<>(baseId, UNDO);
            }
            //今天领取
            else if (DateUtil.dayEqual(getActivityData().getWelfare().get(baseId), curr)) {
                entry = new AbstractMap.SimpleEntry<>(baseId, DO);
            } else {
                //对应服务器今天的ID
                byte todayId = WelfareModel.getIdByLoop(ActivityService.getWelfareLoop(), baseId);
                //没领取过
                if (!getActivityData().getWelfare().containsKey(todayId)) {
                    entry = new AbstractMap.SimpleEntry<>(todayId, UNDO);
                }
                //今天领取
                else if (DateUtil.dayEqual(getActivityData().getWelfare().get(todayId), curr)) {
                    entry = new AbstractMap.SimpleEntry<>(todayId, DO);
                } else {
                    entry = new AbstractMap.SimpleEntry<>(todayId, UNDO);
                }
            }
            entrys.add(entry);
        }
        return entrys;
    }

    public void processMonthlyCardInfo(Message request) {
        Message msg = getMonthlyCardMsg();
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
    }

    /**
     * 月卡消息
     *
     * @return
     */
    public Message getMonthlyCardMsg() {
        long curr = System.currentTimeMillis();
        Message msg = new Message(MessageCommand.MONTHLY_CARD_INFO);
        //长度
        msg.setByte(2);
        //月卡ID
        msg.setByte(1);
        //月卡剩余时间
        if (!isMonthlyCard()) {
            msg.setInt(0);
        } else {
            int left = (int) ((getActivityData().getMonthlyCardEnd().getTime() - curr) / 1000);
            msg.setInt(left);
        }
        //终生卡ID
        msg.setByte(2);
        //是否购买终生卡
        msg.setInt(player.getForever());
        return msg;
    }

    public boolean isMonthlyCard() {
        if (getActivityData().getMonthlyCardEnd() == null ||
                getActivityData().getMonthlyCardEnd().getTime() <= System.currentTimeMillis())
            return false;
        return true;
    }

    public void process7DayMessage(Message request) {
        Message msg = get7DayMessage();
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
    }

    /**
     * 7日活动消息
     *
     * @param msg
     */
    public void getActivity7Msg(Message msg) {
        msg.setShort(getActivityData().getDay7Mission().size());
        for (Activity7Mission miss : getActivityData().getDay7Mission().values()) {
            miss.getMessage(msg, player);
        }
    }

    /**
     * 购买月卡
     *
     * @param id
     * @return
     */
    public boolean buyMonthlyCard(byte id) {
        MonthlyCardModelData model = MonthlyCardModel.getModel(id);
        if (model == null)
            return false;
        long curr = System.currentTimeMillis();
        long curr0 = DateUtil.getDayStartTime(curr);
        //月卡
        if (model.getId() == 1) {
            //月卡已到期
            if (getActivityData().getMonthlyCardEnd() == null || getActivityData().getMonthlyCardEnd().getTime() <= curr0) {
                getActivityData().setMonthlyCardEnd(new Date(curr0 + model.getKeepDay() * DateUtil.DAY));
            }
            //月卡未到期
            else {
                getActivityData().setMonthlyCardEnd(new Date(getActivityData().getMonthlyCardEnd().getTime() +
                        model.getKeepDay() * DateUtil.DAY));
            }
            //当天未领取的话发放邮件
            if (!DateUtil.dayEqual(curr, getActivityData().getMonthlyCardReward())) {
                getActivityData().setMonthlyCardReward(curr);
                //发送邮件
                Mail mail = MailService.createMail(model.getTitle(), model.getContent(), EGoodsChangeType.MONTHLY_CARD_ADD,
                        model.getReward());
                MailService.sendSystemMail(player.getId(), mail);
            }
            getActivityDao().updateMonthlyCard(getActivityData());

            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
            role.getEventManager().notifyEvent(new GameEvent(EGameEventType.ACTIVE_MONTH_CARD, 1, enumSet));
            role.savePlayer(enumSet);
        }
        //终生卡
        else if (model.getId() == 2) {
            player.addForever();
            //当天未领取的话发放邮件
//			if (!DateUtil.dayEqual(curr, player.getForeverReward()))
//			{
//				player.setForeverReward(curr);
//				//发送邮件
//				Mail mail = MailService.createMail(model.getTitle(), model.getContent(), EGoodsChangeType.MONTHLY_CARD_ADD,
//						model.getReward());
//				MailService.sendSystemMail(player.getId(), mail);
//			}
            getActivityDao().updateForever(player);
        }
        return true;
    }

    /**
     * 重置月卡领奖时间
     */
    public void resetMonthlyCardReward() {
        long curr = System.currentTimeMillis();
        //月卡未到期
        if (getActivityData().getMonthlyCardEnd() != null && getActivityData().getMonthlyCardEnd().getTime() > curr) {
            getActivityData().setMonthlyCardReward(curr);
        }
    }

    /**
     * 登录时计算月卡奖励
     */
    @Deprecated
    public void initMonthlyCardReward() {
        if (getActivityData().getMonthlyCardEnd() == null)
            return;
        MonthlyCardModelData model = MonthlyCardModel.getModel((byte) 1);
        if (model == null)
            return;
        long curr = System.currentTimeMillis();
        if (getActivityData().getMonthlyCardReward() == 0)
            getActivityData().setMonthlyCardReward(curr - DateUtil.DAY);
        long lastTime = DateUtil.getDayStartTime(getActivityData().getMonthlyCardReward());
        //补发差的月卡
        int day = DateUtil.getDistanceDay(lastTime, curr);
        boolean save = false;
        for (int i = 1; i <= day; i++) {
            long time = lastTime + i * DateUtil.DAY;
            if (time >= getActivityData().getMonthlyCardEnd().getTime())
                break;
            //发送邮件
            Mail mail = MailService.createMail(model.getTitle(), model.getContent(), EGoodsChangeType.MONTHLY_CARD_ADD,
                    model.getReward());
            mail.setSendTime(time);
            MailService.sendSystemMail(player.getId(), mail);
            getActivityData().setMonthlyCardReward(time);
            save = true;
        }
        if (save) {
            getActivityDao().updateMonthlyCard(getActivityData());
        }
    }

    /**
     * 购买投资计划
     *
     * @param request
     */
    public void processInvestBuy(Message request) {
        //已购买
        if (getActivityData().getInvests().size() > 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVEST_BUY);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().useGoods(INVEST_COST, EGoodsChangeType.INVEST_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        getActivityData().getInvests().add(System.currentTimeMillis());
        Message msg = new Message(MessageCommand.INVEST_BUY_MESSAGE, request.getChannel());
        msg.setByte(1);
        role.sendMessage(msg);
        role.savePlayer(saves);
        getActivityDao().updateInvestJson(getActivityData());
    }

    /**
     * 领取投资计划
     *
     * @param request
     */
    public void processInvestReward(Message request) {
        long id = request.readByte();
        //是否已购买
        if (getActivityData().getInvests().size() <= 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVEST_UNBUY);
            return;
        }
        Map<String, InvestLogicData> model = ActivityService.getRoundData(EActivityType.INVEST, 0);
        InvestLogicData logic = model.get(id + "");
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //是否已领取
        if (getActivityData().getInvests().contains(id)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVEST_REWARD);
            return;
        }
        //领取了所有的投资计划
        if (getActivityData().getInvests().size() > model.size()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVEST_REWARD);
            return;
        }
        //是否达到天数
        int day = DateUtil.getDistanceDay(getActivityData().getInvests().get(0), System.currentTimeMillis());
        if (id > day + 1) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVEST_UNFIT);
            return;
        }
        getActivityData().getInvests().add(id);
        //领取物品
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(logic.getRewards(), EGoodsChangeType.INVEST_ADD, saves);
        //消息
        Message msg = new Message(MessageCommand.INVEST_REWARD_MESSAGE, request.getChannel());
        msg.setByte((byte) id);
        role.sendMessage(msg);
        role.savePlayer(saves);
        getActivityDao().updateInvestJson(getActivityData());
    }

    /**
     * 转盘
     *
     * @param request
     */
    public void processDial(Message request) {
        boolean oneKey = (request.readByte() == 1);
        long curr = System.currentTimeMillis();
        Map<String, DialLogicData> logicData = ActivityService.getRoundData(EActivityType.TURNPLATE, role.getPlayerId(), curr);
        if (logicData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        DialLogicData logic = logicData.get("1");
        List<DropData> rewardList = DialService.roll(role, logic.getModelData(), oneKey, EGoodsChangeType.DIAL_CONSUME, EGoodsChangeType.DIAL_ADD);
        if (rewardList != null) {
            //发送消息
            Message msg = new Message(MessageCommand.DIAL_MESSAGE, request.getChannel());
            msg.setByte(rewardList.size());
            for (DropData data : rewardList) {
                data.getMessage(msg);
            }
            role.sendMessage(msg);
        } else {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
        }
    }

    /**
     * 限时商城购买
     *
     * @param request
     */
    public void processDLShopBuy(Message request) {
        int id = request.readInt();
        int num = request.readInt();
        long curr = System.currentTimeMillis();
        Map<String, TLShopLogicData> logicData = ActivityService.getRoundData(
                EActivityType.TLSHOP, role.getPlayerId(), curr);
        if (logicData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        TLShopLogicData logic = logicData.get(id + "");
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //数量是否达上限
        int currNum = 0;
        int shopKey = ActivityService.getShopKey(EActivityType.TLSHOP.getId(), id);
        if (getActivityData().getShop().containsKey(shopKey))
            currNum = getActivityData().getShop().get(shopKey);
        if (currNum + num > logic.getMax()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_BUY);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //消耗
        DropData cost = new DropData(logic.getPrice().getT(), logic.getPrice().getG(), logic.getPrice().getN() * num);
        if (!role.getPackManager().useGoods(cost, EGoodsChangeType.TLSHOP_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        getActivityData().getShop().put(shopKey, currNum + num);
        DropData reward = new DropData(logic.getGoods().getT(), logic.getGoods().getG(), logic.getGoods().getN() * num);
        role.getPackManager().addGoods(reward, EGoodsChangeType.TLSHOP_ADD, saves);
        Message msg = new Message(MessageCommand.TLSHOP_BUY_MESSAGE, request.getChannel());
        msg.setInt(id);
        msg.setInt(num);
        role.sendMessage(msg);
        //保存数据
        getActivityDao().updateShopJson(getActivityData());
        role.savePlayer(saves);
    }

    public void processVipShopFLBuy(Message request) {
        int id = request.readInt();
        int num = request.readInt();
        int numNew = processVipShopBuy(request, EActivityType.VIPSHOPFULI, id, num);
        if (numNew < 0)
            return;
        Message msg = new Message(MessageCommand.VIPSHOPFL_BUY_MESSAGE, request.getChannel());
        msg.setInt(id);
        msg.setInt(numNew);
        role.sendMessage(msg);
    }

    public void processVipShopTLBuy(Message request) {
        int id = request.readInt();
        int num = request.readInt();
        int numNew = processVipShopBuy(request, EActivityType.VIPSHOPTL, id, num);
        if (numNew < 0)
            return;
        Message msg = new Message(MessageCommand.VIPSHOPTL_BUY_MESSAGE, request.getChannel());
        msg.setInt(id);
        msg.setInt(numNew);
        role.sendMessage(msg);
    }

    /**
     * VIP商城购买
     *
     * @param request
     */
    public int processVipShopBuy(Message request, EActivityType activityType, int id, int num) {
        long curr = System.currentTimeMillis();
        Map<String, VipShopLogicData> logicData = ActivityService.getRoundData(
                activityType, role.getPlayerId(), curr);
        if (logicData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return -1;
        }
        VipShopLogicData logic = logicData.get(id + "");
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return -1;
        }
        //数量是否达上限
        int currNum = 0;
        int shopKey = ActivityService.getShopKey(activityType.getId(), id);
        if (getActivityData().getShop().containsKey(shopKey))
            currNum = getActivityData().getShop().get(shopKey);
        int max = 0;
        //购买上限
        int vipLv = player.getVipLevel();
        if (activityType == EActivityType.VIPSHOPFULI) {
            if (logic.getVip().containsKey(vipLv))
                max = logic.getVip().get(vipLv);
        } else if (activityType == EActivityType.VIPSHOPTL) {
            int min = 10000;
            int maxNum = 0;
            for (Entry<Integer, Integer> entry : logic.getVip().entrySet()) {
                min = entry.getKey();
                maxNum = entry.getValue();
            }
            if (vipLv >= min)
                max = maxNum;
        }
        if (currNum + num > max) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_BUY);
            return -1;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().capacityEnough(logic.getGoods())) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BAG_FULL);
            return -1;
        }

        //消耗
        DropData cost = new DropData(logic.getPrice().getT(), logic.getPrice().getG(), logic.getPrice().getN() * num);
        if (!role.getPackManager().useGoods(cost, EGoodsChangeType.VIPSHOP_CONSUME, saves)) {
            if (logic.getPrice().getT() == EGoodsType.GOLD.getId())
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOLD_LESS);
            else
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return -1;
        }
        getActivityData().getShop().put(shopKey, currNum + num);
        List<DropData> rewardList = new ArrayList<>();
        for (DropData dropData : logic.getGoods()) {
            DropData reward = new DropData(dropData.getT(), dropData.getG(), dropData.getQ(), dropData.getN() * num);
            rewardList.add(reward);
        }
        role.getPackManager().addGoods(rewardList, EGoodsChangeType.VIPSHOP_ADD, saves);
        //保存数据
        getActivityDao().updateShopJson(getActivityData());
        role.savePlayer(saves);
        return currNum + num;
    }

    /**
     * 秘宝购买道具
     *
     * @param request
     */
    public void processTreasureBuy(Message request) {
        int id = request.readInt();
        Map<String, TreasuresLogicData> logics = ActivityService.getRoundData(EActivityType.TREASURES, 0);
        TreasuresLogicData tld = logics.get("" + id);
        if (tld == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        int vouchers = player.getVouchers();
        DropData price = tld.getPrice();
        int priceNum = price.getN();
        int halfPriceNum = priceNum / 2;
        //积分
        int integral = 0;
        //本次购买消耗的代金券
        int consumeVouchers = 0;
        //本次购买消耗的元宝数
        int consumeDiamond = 0;
        DropData data = null;
        if (vouchers >= halfPriceNum) {
            integral = priceNum - halfPriceNum;
            consumeDiamond = priceNum - halfPriceNum;
            data = new DropData(price.getT(), price.getG(), consumeDiamond);
            consumeVouchers = halfPriceNum;
            vouchers = halfPriceNum;
        } else {
            integral = priceNum - vouchers;
            consumeDiamond = priceNum - vouchers;
            consumeVouchers = vouchers;
            data = new DropData(price.getT(), price.getG(), consumeDiamond);
        }
        if (!EGoodsType.getGoodsType(price.getT()).getCmd().consume(role, data, EGoodsChangeType.TREASURE_ADD, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        //消耗代金券
        if (consumeVouchers != 0) {
            DropData vouchersConsume = new DropData(EGoodsType.VOUCHERS, 0, consumeVouchers);
            if (!EGoodsType.getGoodsType(vouchersConsume.getT()).getCmd().consume(role, vouchersConsume, EGoodsChangeType.TREASURE_CONSUME, saves)) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_TREASURE_VOUCHERS_NOT_ENOUGH);
                return;
            }
        }
        getActivityData().setTreasureVouchers(player.getVouchers());
        //添加积分
        getActivityData().addTreasureIntegral(integral);

        DropData reward = tld.getRewards();
        if (!EGoodsType.getGoodsType(reward.getT()).getCmd().reward(role, reward, EGoodsChangeType.TREASURE_ADD, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        //添加已购买道具
        BoughtRecord br = new BoughtRecord(id, consumeVouchers, consumeDiamond);
        getActivityData().getBuiedItems().add(br);
        //添加本次刷新购买过的道具
        getActivityData().getReBuiedItems().add(id);


        Message message = new Message(MessageCommand.GAME_TREASURES_BUY);
        message.setChannel(request.getChannel());
        message.setInt(getActivityData().getTreasureIntegral());
        message.setInt(player.getVouchers());
//		message.setByte(getActivityData().getBuiedItems().size());
//		for(BoughtRecord i : getActivityData().getBuiedItems()) {
//			message.setInt(i.getId());
//			message.setInt(i.getVouchers());
//			message.setInt(i.getDiamond());
//		}
        message.setByte(getActivityData().getReBuiedItems().size());
        for (Integer i : getActivityData().getReBuiedItems()) {
            message.setInt(i);
        }
        role.sendMessage(message);
        role.savePlayer(saves);
        //保存数据库 getActivityDao().updateTreasures()
        getActivityDao().updateTreasureBuy(getActivityData());

    }

    /**
     * 秘宝刷新道具
     *
     * @param request
     */
    public void processTreasureRefresh(Message request) {
        byte free = getActivityData().getTreasureFree();
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (free == 0) {
            getActivityData().setTreasureFree((byte) 1);
            long curr = System.currentTimeMillis();
            getActivityData().setTreasuresRefreshTime(curr);
            Message message = this.getTreasuresRefreshMsg();
            role.putMessageQueue(message);
        } else if (free == 1) {
            DropData consume = new DropData(EGoodsType.ITEM.getId(), 98, 1);
            if (!EGoodsType.getGoodsType(consume.getT()).getCmd().consume(role, consume, EGoodsChangeType.TREASURE_CONSUME, saves)) {
                DropData consume1 = new DropData(EGoodsType.DIAMOND.getId(), 0, 200);
                if (!EGoodsType.getGoodsType(consume1.getT()).getCmd().consume(role, consume1, EGoodsChangeType.TREASURE_CONSUME, saves)) {
                    role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                    return;
                } else {
                    getActivityData().addTreasureIntegral(consume1.getN());
                }
            }
        }
        Map<String, TreasuresLogicData> logics = ActivityService.getRoundData(EActivityType.TREASURES, 0);
        List<Integer> items = this.getRandomIds4Treasures(logics);
        getActivityData().clearFourItems();
        getActivityData().setFourItems(items);
        getActivityData().clearReBuiedItems();
        Message msg = new Message(MessageCommand.GAME_TREASURES_REFRESH);
        msg.setChannel(request.getChannel());
        msg.setInt(getActivityData().getTreasureIntegral());
        msg.setByte(items.size());
        for (Integer item : items) {
            msg.setInt(item);
        }
        msg.setByte(getActivityData().getReBuiedItems().size());
        for (Integer id : getActivityData().getReBuiedItems()) {
            msg.setInt(id);
        }
        role.sendMessage(msg);
        role.savePlayer(saves);
        //保存数据库
        getActivityDao().updateTreasureRefresh(getActivityData());
    }

    /**
     * 秘宝时间刷新
     *
     * @param request
     */
    public void processTreasuresRefreshTime(Message request) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig activityConfig = ActivityService.getActivityConfig(EActivityType.TREASURES);
        long start = activityConfig.getStartTime(0);
        long end = activityConfig.getEndTime();
        if (curr < start || curr > end) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_ACTIVITY);
            return;
        }
        //当前时间和上次刷新不在同一个间隔中
        if (getActivityData().getTreasureFree() != 0 && (curr - getActivityData().getTreasuresRefreshTime()) >= TreasuresLogicData.REFRESH_SPACE) {
            getActivityData().setTreasureFree((byte) 0);
        }
        Message msg = this.getTreasuresRefreshMsg();
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
        //更新数据库
        getActivityDao().updateTreasureFree(getActivityData());
    }

    private Message getTreasuresRefreshMsg() {
        long curr = System.currentTimeMillis();
        long left = getActivityData().getTreasuresRefreshTime() + TreasuresLogicData.REFRESH_SPACE - curr;
        left = left < 0 ? 0 : left;
        if (getActivityData().getTreasureFree() == 0) {
            left = 0;
        }
        Message msg = new Message(MessageCommand.GAME_TREASURES_REFRESH_TIME);
        msg.setByte(getActivityData().getTreasureFree());
        msg.setLong(left);
        return msg;
    }

    /**
     * 获取随机秘宝道具
     *
     * @param logics
     * @return
     */
    public List<Integer> getRandomIds4Treasures(Map<String, TreasuresLogicData> logics) {
        List<Ele> type1List = new ArrayList<>();
        List<Ele> type2List = new ArrayList<>();
        List<Ele> type3List = new ArrayList<>();
        List<Ele> type4List = new ArrayList<>();
        List<Integer> randomAll = new ArrayList<>();
        for (String id : logics.keySet()) {
            TreasuresLogicData tld = logics.get(id);
            if (tld.getType() == 1) {
                Ele ele = new Ele(Integer.valueOf(tld.getId()), tld.getChance());
                type1List.add(ele);
            } else if (tld.getType() == 2) {
                Ele ele = new Ele(Integer.valueOf(tld.getId()), tld.getChance());
                type2List.add(ele);
            } else if (tld.getType() == 3) {
                Ele ele = new Ele(Integer.valueOf(tld.getId()), tld.getChance());
                type3List.add(ele);
            } else if (tld.getType() == 4) {
                Ele ele = new Ele(Integer.valueOf(tld.getId()), tld.getChance());
                type4List.add(ele);
            }
        }
        randomAll.add(DiceUtil.dice(type4List).getId());
        randomAll.add(DiceUtil.dice(type3List).getId());
        randomAll.add(DiceUtil.dice(type2List).getId());
        randomAll.add(DiceUtil.dice(type1List).getId());
        return randomAll;
    }

    /**
     * 秘宝代金券领取
     *
     * @param request
     */
    public void processTreasureVouchersReceive(Message request) {
        int vouchersId = request.readInt();
        Map<String, TreasuresLogicData> logics = ActivityService.getRoundData(EActivityType.TREASURES, 0);
        TreasuresLogicData tld = logics.get("1");
        Vouchers vou = tld.getVos().get(vouchersId);
        if (vou == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //积分
        int integral = getActivityData().getTreasureIntegral();
        //当前积分不足
        if (integral < vou.getCost()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_TREASURE_INTEGRAL_NOT_ENOUGH);
            return;
        }
        List<Integer> vous = getActivityData().getVouchersList();
        if (vous.contains(vouchersId)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //添加到已得代金券集合
        getActivityData().getVouchersList().add(vouchersId);
        getActivityData().addTreasureVouchers(vou.getReward().getN());
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        DropData reward = vou.getReward();
        EGoodsType.getGoodsType(reward.getT()).getCmd().reward(role, reward, EGoodsChangeType.VOUCHERS_RECEIVE, saves);
        Message message = new Message(MessageCommand.GAME_TREASURES_VOUCHERS_RECEIVE);
        message.setChannel(request.getChannel());
        message.setInt(getActivityData().getTreasureVouchers());
        message.setByte(getActivityData().getVouchersList().size());
        for (int i : getActivityData().getVouchersList()) {
            message.setInt(i);
        }
        role.sendMessage(message);
        //更新数据库
        role.savePlayer(saves);
        getActivityDao().updateTreasureVouchers(getActivityData());
    }

    /**
     * 获取秘宝购买记录
     *
     * @param request
     */
    public void processTreasureBuyRecord(Message request) {
        List<BoughtRecord> items = getActivityData().getBuiedItems();
        Message message = new Message(MessageCommand.GAME_TREASURES_BUY_RECORD);
        message.setChannel(request.getChannel());
        message.setByte(items.size());
        for (BoughtRecord i : items) {
            message.setInt(i.getId());
            message.setInt(i.getVouchers());
            message.setInt(i.getDiamond());
        }
        role.sendMessage(message);
    }

    /**
     * 集字活动
     *
     * @param request
     */
    public void processSetWords(Message request) {
        int id = request.readInt();
        Map<String, SetWordsLogicData> logics = ActivityService.getRoundData(EActivityType.SET_WORDS, 0);
        SetWordsLogicData swld = logics.get("" + id);
        if (swld == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        getActivityData().addSetWordsNums(id);
        List<DropData> consumes = swld.getCost();
        if (!role.getPackManager().useGoods(consumes, EGoodsChangeType.SET_WORDS_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        DropData reward = swld.getRewards();
        if (!role.getPackManager().addGoods(reward, EGoodsChangeType.SET_WORDS_ADD, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        Message message = new Message(MessageCommand.GAME_SET_WORDS_MESSAGE);
        message.setChannel(request.getChannel());
        Map<Integer, Integer> map = getActivityData().getSetWordsNums();
        message.setInt(id);
        message.setInt(map.get(id));
        role.sendMessage(message);
        role.savePlayer(saves);
        getActivityDao().updateSetWordsNum(getActivityData());
    }

    /**
     * 回收道具
     *
     * @param request
     */
    public void processCallBackItems(Message request) {
        String req = request.readString();
        List<Short> ids = StringUtil.getIds(req);
        List<Short> ids1 = new ArrayList<>();
        List<DropData> consumes = new ArrayList<>();
        List<DropData> rewards = new ArrayList<>();
        List<CallBackGoodsData> list = GoodsModel.getCallBackGoods();
        for (CallBackGoodsData cbgd : list) {
            Goods item = role.getPackManager().getItemById(cbgd.getItem().getG());
            if (item == null || item.getN() <= 0 || !ids.contains(cbgd.getId())) continue;
            DropData consume = new DropData(cbgd.getItem().getT(), cbgd.getItem().getG(), item.getN());
            DropData reward = cbgd.getReward();
            DropData callBackReward = new DropData(reward.getT(), reward.getG(), item.getN() * reward.getN());
            consumes.add(consume);
            rewards.add(callBackReward);
            ids1.add(cbgd.getId());
        }
        ids.removeAll(ids1);
        if (!ids.isEmpty()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().useGoods(consumes, EGoodsChangeType.CALL_BACK_CONSUME, saves);
        role.getPackManager().addGoods(rewards, EGoodsChangeType.CALL_BACK_ADD, saves);
        Message msg = new Message(MessageCommand.GAME_ITEM_CALLBACK_MESSAGE, request.getChannel());
        role.sendMessage(msg);
        role.savePlayer(saves);
    }

    /**
     * 限时有礼领取
     *
     * @param request
     */
    public void processDLGiftReward(Message request) {
        byte id = request.readByte();
        long curr = System.currentTimeMillis();
        Map<String, TLGiftLogicData> logicData = ActivityService.getRoundData(EActivityType.TLGIFT,
                role.getPlayerId(), curr);
        if (logicData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        TLGiftLogicData logic = logicData.get(id + "");
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        if (getActivityData().getTlGift().contains(id)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_TLGIFT_REWARD);
            return;
        }
        //判断值是否达到可领取
//		if (getBlessLevel(ERankType.getType(logic.getType()+2)) < logic.getMubiao())
//		{
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_TLGIFT_UNFIT);
//			return;
//		}
        getActivityData().getTlGift().add(id);
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(logic.getReward(), EGoodsChangeType.TLGIFT_ADD, saves);
        Message msg = new Message(MessageCommand.TLGIFT_REWARD_MESSAGE, request.getChannel());
        msg.setByte(id);
        role.sendMessage(msg);
        role.savePlayer(saves);
        getActivityDao().updateTlGiftJson(getActivityData());
    }

//	public void updatePayRecord() {
//		getActivityDao().updatePayRecord(getActivityData());
//	}

    /**
     * 摇钱树
     **/
    public void processCrashCow(Message request) {
        int id = request.readInt();
        boolean oneKey = (request.readByte() == 1);
        long curr = System.currentTimeMillis();

        EActivityType type = ActivityService.CRASH_COW_TYPE_MAPPING.get(id);
        if (type == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        Map<String, DialLogicData> logicData = ActivityService.getRoundData(type, role.getPlayerId(), curr);
        if (logicData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        DialLogicData logic = logicData.get(String.valueOf(id));
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }

        PlayerActivity activity = getActivityData();
        boolean free = !oneKey && activity.getCrashcowTimes() < ActivityService.CRASH_COW_DAILY_FREE_TIMES;

        List<DropData> rewardList = DialService.roll(role, logic.getModelData(), oneKey, EGoodsChangeType.CRASH_COW_CONSUME, EGoodsChangeType.CRASH_COW_ADD, free);
        if (free) {
            activity.setCrashcowTimes((byte) (activity.getCrashcowTimes() + 1));
            getActivityDao().updateCrashcowTimes(activity);
        }

        if (rewardList != null) {
            //发送消息
            Message msg = new Message(MessageCommand.CRASH_COW_MESSAGE, request.getChannel());
            msg.setByte(rewardList.size());
            for (DropData data : rewardList) {
                data.getMessage(msg);
            }
            int freeTimes = ActivityService.CRASH_COW_DAILY_FREE_TIMES - getActivityData().getCrashcowTimes();
            msg.setByte(freeTimes < 0 ? 0 : freeTimes);
            role.sendMessage(msg);
        } else {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
        }
    }

    /**
     * 春节集字
     **/
    public void processSpringWordExchange(Message request) {
        byte id = request.readByte();
        long curr = System.currentTimeMillis();
        // get data
        Map<String, SpringWordCollectionLogicData> logicData = ActivityService.getRoundData(EActivityType.SPRING_WORD_COLLECTION,
                role.getPlayerId(), curr);
        if (logicData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        SpringWordCollectionLogicData logic = logicData.get(String.valueOf(id));
        Preconditions.checkNotNull(logic, "ActivityManager.processSpringWordExchange(). Unexpected id=" + id);
        ExchangeModelData modelData = logic.getModelData();
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        // check
        if (!role.getPackManager().useGoods(modelData.getConsume(), EGoodsChangeType.SPRING_WORD_COLLECTION_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        // do
        role.getPackManager().addGoods(modelData.getReward(), EGoodsChangeType.SPRING_WORD_COLLECTION_ADD, saves);
        // save
        role.savePlayer(saves);
        // msg
        Message msg = new Message(MessageCommand.SPRING_WORD_EXCHANGE_MESSAGE, request.getChannel());
        role.sendMessage(msg);
    }

    /**
     * 限时坐骑
     **/
    public void processTLHorseExchange(Message request) {
        byte id = request.readByte();
        long curr = System.currentTimeMillis();
        // get data
        Map<String, TLHorseLogicData> logicData = ActivityService.getRoundData(EActivityType.TLHORSE,
                role.getPlayerId(), curr);
        if (logicData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        TLHorseLogicData logic = logicData.get(String.valueOf(id));
        Preconditions.checkNotNull(logic, "ActivityManager.processTLHorseExchange(). Unexpected id=" + id);
        PlayerActivity playerActivity = getActivityData();
        if (playerActivity.getTLHorseList().contains(id)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_NONE);
            return;
        }
        ExchangeModelData modelData = logic.getModelData();
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        // check
        if (!role.getPackManager().useGoods(modelData.getConsume(), EGoodsChangeType.TLHORSE_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        // do
        getActivityData().getTLHorseList().add(id);
        role.getPackManager().addGoods(modelData.getReward(), EGoodsChangeType.TLHORSE_ADD, saves);
        // save
        getActivityDao().updateTLHorseJson(getActivityData());
        role.savePlayer(saves);
        // msg
        Message msg = new Message(MessageCommand.TLHORSE_EXCHANGE_MESSAGE, request.getChannel());
        msg.setByte(id);
        role.sendMessage(msg);
    }

    /**
     * 春节签到
     *
     * @param request
     */
    public void processSpringSign(Message request) {
        long currentTime = System.currentTimeMillis();
        // get data
        Map<String, SignLogicData> logicData = ActivityService.getRoundData(EActivityType.SIGN,
                role.getPlayerId(), currentTime);
        // check
        if (logicData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        long lastSign = getActivityData().getSignTime();
        if (DateUtil.dayEqual(lastSign, currentTime)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        int nextSign = getActivityData().getSignNum() + 1;
        SignLogicData signData = logicData.get(String.valueOf(nextSign));
        if (signData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        // do
        getActivityData().setSignTime(currentTime);
        getActivityData().setSignNum((short) (nextSign));
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(signData.getRewardList(), EGoodsChangeType.SPRING_SIGN_ADD, saves);
        //save
        getActivityDao().updateSign(getActivityData());
        role.savePlayer(saves);

        // msg
        Message msg = new Message(MessageCommand.SPRING_SIGN_MESSAGE, request.getChannel());
        msg.setShort(getActivityData().getSignNum());
        role.sendMessage(msg);
    }

    /**
     * 春节限时商城购买
     *
     * @param request
     */
    public void processShopSpringBuy(Message request) {
        EActivityType activityType = EActivityType.TLSHOP_SPRING;
        int id = request.readInt();
        int num = request.readInt();
        long curr = System.currentTimeMillis();
        Map<String, TLShopLogicData> logicData = ActivityService.getRoundData(
                activityType, role.getPlayerId(), curr);
        if (logicData == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        TLShopLogicData logic = logicData.get(String.valueOf(id));
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //数量是否达上限
        int currNum = 0;
        int shopKey = ActivityService.getShopKey(activityType.getId(), id);
        if (getActivityData().getShopSpring().containsKey(shopKey))
            currNum = getActivityData().getShopSpring().get(shopKey);
        if (logic.getMax() >= 0 && currNum + num > logic.getMax()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_BUY);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //消耗
        DropData cost = new DropData(logic.getPrice().getT(), logic.getPrice().getG(), logic.getPrice().getN() * num);
        if (!role.getPackManager().useGoods(cost, EGoodsChangeType.TLSHOP_SPRING_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        getActivityData().getShopSpring().put(shopKey, currNum + num);
        DropData reward = new DropData(logic.getGoods().getT(), logic.getGoods().getG(), logic.getGoods().getN() * num);
        role.getPackManager().addGoods(reward, EGoodsChangeType.TLSHOP_SPRING_ADD, saves);
        Message msg = new Message(MessageCommand.TLSHOP_SPRING_BUY_MESSAGE, request.getChannel());
        msg.setInt(id);
        msg.setInt(num);
        role.sendMessage(msg);
        //保存数据
        getActivityDao().updateShopSpringJson(getActivityData());
        role.savePlayer(saves);
    }

    /**
     * 福袋
     *
     * @param request
     */
    public void processFuDai(Message request) {
        byte costType = request.readByte();
        Map<String, FuDaiLogicData> model = ActivityService.getRoundData(EActivityType.FUDAI, 0);
        if (model == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        FuDaiLogicData logic = model.get("0");
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (role.getPackManager().getBagFreeCapacity() < 1) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BAG_FULL_MELT);
            return;
        }
        //消耗
        DropData cost = logic.getCostDiamond();
        if (costType == 1)
            cost = logic.getCostItem();
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().useGoods(cost, EGoodsChangeType.FUDAI_CONSUME, saves)) {
            if (costType == 1)
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            else
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        int count = getActivityData().getTzzp() / 10000;
        count++;
        int spec = getActivityData().getTzzp() % 10000;
        //奖励
        List<DropData> rewards = null;
        //特殊掉落
        int currCount = count % 100;
        int currRound = count / 100;
        for (int i = 0; i < logic.getSpec().length; i++) {
            int[] specDrop = logic.getSpec()[i];
            if (spec <= i + currRound * logic.getSpec().length && currCount >= specDrop[0] && currCount <= specDrop[1]) {
                int random = GameUtil.getRangedRandom(currCount, specDrop[1]);
                boolean drop = false;
                //等概率掉
                if (random == specDrop[1])
                    drop = true;
                    //到最后一次还没掉的话 必掉
                else if (currCount == specDrop[1])
                    drop = true;
                if (drop) {
                    spec++;
                    rewards = new ArrayList<>();
                    rewards.add(new DropData((byte) specDrop[2], specDrop[3], specDrop[4]));
                    break;
                }
            }
        }
        //正常掉落
        if (rewards == null) {
            rewards = DropModel.getDropGroupData(logic.getDropId()).getRandomDrop();
        }
        role.getPackManager().addGoods(rewards, EGoodsChangeType.FUDAI_ADD, saves);
        //消息
        Message msg = new Message(MessageCommand.FUDAI_MESSAGE, request.getChannel());
        DropData reward = rewards.get(0);
        msg.setByte(reward.getT());
        msg.setShort(reward.getG());
        msg.setInt(reward.getN());
        role.sendMessage(msg);
        //保存数据
        getActivityData().setTzzp(count * 10000 + spec);
        getActivityDao().updateTzzp(getActivityData());
        role.savePlayer(saves);
    }

    /**
     * 累计消费活动
     *
     * @param num
     */
    public void handlerConsumeDiamond(int num) {
        long curr = System.currentTimeMillis();
        Map<String, ConsumeLogicData> logicData = ActivityService.getRoundData(
                EActivityType.CONSUME, role.getPlayerId(), curr);
        if (logicData != null) {
            //消费金额
            getActivityData().addConsume(num);
            int sum = getActivityData().getConsume();
            for (ConsumeLogicData consumeData : logicData.values()) {
                if (consumeData.getId() > sum - num && consumeData.getId() <= sum) {
                    Mail mail = MailService.createMail(consumeData.getTitle(), consumeData.getContent(),
                            EGoodsChangeType.CONSUME_CUL_ADD, consumeData.getRewards());
                    MailService.sendSystemMail(player.getId(), mail);
                }
            }
            role.putMessageQueue(getActivityMsg(EActivityType.CONSUME));
            getActivityDao().updateConsumeData(getActivityData());
        }
        Map<String, TargetLogicData> festLogic = ActivityService.getRoundData(
                EActivityType.FEST_TARGET, role.getPlayerId(), curr);
        if (festLogic != null) {
            //消费金额
            GameRankManager.getInstance().addFestTop(role, num);
            role.putMessageQueue(getActivityMsg(EActivityType.FEST_TARGET));
            getActivityDao().updateFestConsumeData(getActivityData());
        }
        Map<String, TargetLogicData> weekendLogic = ActivityService.getRoundData(
                EActivityType.WEEKEND_TARGET, role.getPlayerId(), curr);
        if (weekendLogic != null) {
            //消费金额
            GameRankManager.getInstance().addWeekendTop(role, num);
            role.putMessageQueue(getActivityMsg(EActivityType.WEEKEND_TARGET));
            getActivityDao().updateWeekendPayData(getActivityData());
        }
        //红包
        int redValue = num / 5;
        if (redValue > 0) {
            BaseActivityConfig redConfig = ActivityService.getActivityConfig(EActivityType.RED_PACKET);
            if (redConfig != null) {
                if (curr >= redConfig.getStartTime(0) && curr <= redConfig.getEndTime()) {
                    int pass = DateUtil.getDistanceDay(redConfig.getStartTime(0), curr) + 1;
                    if (pass <= RedPacketEvent.LAST_DAY) {
                        for (int i = getActivityData().getRedpacket().size(); i <= pass; i++) {
                            getActivityData().getRedpacket().add(0);
                        }
                        getActivityData().getRedpacket().set(pass, getActivityData().getRedpacket().get(pass) + redValue);
                        role.putMessageQueue(getActivityMsg(EActivityType.RED_PACKET));
                        getActivityDao().updateRedpacketJson(getActivityData());
                    }
                }
            }
        }
    }

    public void processBuyOne(Message request) {
        int id = request.readInt();
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.BUY_ONE);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        Map<String, BuyOneLogicData> model = ActivityService.getRoundData(EActivityType.BUY_ONE, currRound.getRound());
        BuyOneLogicData logic = model.get(String.valueOf(id));
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //已购买次数
        int buy = 0;
        if (getActivityData().getBuyOne().containsKey(id))
            buy = getActivityData().getBuyOne().get(id);
        if (buy >= logic.getMax()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BUY_MAX);
            return;
        }
        //充值金额
        int sum = role.getPayManager().getRmbInPay(currRound.getStartTimeStr(), currRound.getEndTimeStr());
        //可购买次数
        int count = sum / logic.getChongzhi();
        if (count <= buy) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BUY_COUNT);
            return;
        }
        //消耗
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().useGoods(logic.getCost(), EGoodsChangeType.BUY_ONE_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        //次数
        getActivityData().getBuyOne().put(id, ++buy);
        role.getPackManager().addGoods(logic.getReward(), EGoodsChangeType.BUY_ONE_ADD, saves);
        //发送消息
        Message msg = new Message(MessageCommand.BUY_ONE_MESSAGE, request.getChannel());
        msg.setInt(id);
        msg.setInt(buy);
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        getActivityDao().updateBuyOneJson(getActivityData());
    }

    public void processShenTongBuy(Message request) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.SHENTONG);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        Map<String, ShenTongLogicData> model = ActivityService.getRoundData(EActivityType.SHENTONG, currRound.getRound());
        ShenTongLogicData logic = null;
        for (ShenTongLogicData data : model.values())
            logic = data;
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //消耗
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().useGoods(logic.getCost(), EGoodsChangeType.SHENTONG_BUY_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        //次数
        if (getActivityData().getShenTong().size() == 0)
            getActivityData().getShenTong().add(0);
        int buy = getActivityData().getShenTong().get(0);
        getActivityData().getShenTong().set(0, ++buy);
        role.getPackManager().addGoods(logic.getItem(), EGoodsChangeType.SHENTONG_BUY_ADD, saves);
        //消息
        Message msg = new Message(MessageCommand.SHENTONG_BUY_MESSAGE, request.getChannel());
        msg.setInt(buy);
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        getActivityDao().updateShenTong(getActivityData());
    }

    public void processShenTongReward(Message request) {
        int index = request.readInt();
        index++;
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.SHENTONG);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        Map<String, ShenTongLogicData> model = ActivityService.getRoundData(EActivityType.SHENTONG, currRound.getRound());
        ShenTongLogicData logic = null;
        for (ShenTongLogicData data : model.values())
            logic = data;
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //超出范围
        if (index <= 0 || index > logic.getTimeMax()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //购买次数
        if (getActivityData().getShenTong().size() == 0)
            getActivityData().getShenTong().add(0);
        int buy = getActivityData().getShenTong().get(0);
        //最大可领取的索引
        int max = buy / logic.getRewardTime();
        if (index > max) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BUY_COUNT);
            return;
        }
        //是否已经领取过
        for (int i = 1; i < getActivityData().getShenTong().size(); i++) {
            if (getActivityData().getShenTong().get(i) == index) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_REPEAT);
                return;
            }
        }
        getActivityData().getShenTong().add(index);
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(logic.getReward(), EGoodsChangeType.SHENTONG_REWARD_ADD, saves);
        //消息
        Message msg = new Message(MessageCommand.SHENTONG_REWARD_MESSAGE, request.getChannel());
        msg.setInt(--index);
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
    }

    /**
     * 商城信息
     *
     * @param request
     */
    public void processShopPlayerInfo(Message request) {
        long curr = System.currentTimeMillis();
        //当前时间和上次刷新不在同一个间隔中
        if ((int) (curr / ShopModel.REFRESH_SPACE) !=
                (int) (getActivityData().getPlayerShopRefresh() / ShopModel.REFRESH_SPACE)) {
            getActivityData().setPlayerShopRefresh(curr);
            refreshShopPlayer();
        }
        if (getActivityData().getPlayerShopItems().size() == 0) {
            getActivityData().setPlayerShopRefresh(curr);
            refreshShopPlayer();
        }
        Message msg = getShopPlayerMsg();
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
    }

    public Message getShopPlayerMsg() {
        long curr = System.currentTimeMillis();
        Message msg = new Message(MessageCommand.SHOP_PLAYER_INFO_MESSAGE);
        long left = (int) (getActivityData().getPlayerShopRefresh() / ShopModel.REFRESH_SPACE + 1) *
                ShopModel.REFRESH_SPACE - curr;
        msg.setInt((int) (left / DateUtil.SECOND));
        msg.setByte(getActivityData().getPlayerShopItems().size());
        for (ShopItem item : getActivityData().getPlayerShopItems()) {
            msg.setByte(item.getG().getT());
            msg.setShort(item.getG().getG());
            msg.setByte(item.getG().getQ());
            msg.setInt(item.getG().getN());
            msg.setInt(ShopModel.BUY_MAX - item.getN());
            msg.setByte(item.getD());
            msg.setByte(item.getPt());
            msg.setInt(item.getPn());
        }
        return msg;
    }

    /**
     * 刷新商城商品
     */
    private void refreshShopPlayer() {
        List<ShopItem> items = new ArrayList<>();
        //装备数量
        int equipNum = GameUtil.getRatesIndex(ShopModel.SELL_EQUIP_NUM, GameUtil.getRangedRandom(1, 100));
        //随机装备
        for (int i = 0; i < equipNum; i++) {
            ShopItem item = new ShopItem();
            DropData equip = new DropData();
            equip.setT(EGoodsType.EQUIP.getId());
            equip.setQ(ShopModel.SELL_EQUIP_QUALITY);
            equip.setN(1);
            //装备ID
            EquipData data = GoodsModel.getRandomDataByLv(player);
            if (data == null)
                continue;
            ShopShenMi model = ShopModel.getShopEquip(data.getLevel());
            if (model == null)
                continue;
            equip.setG(data.getGoodsId());
            item.setG(equip);
            item.setN(0);
            //折扣
            int discount = model.getDiscount();
            if (model.getDiscount() <= 0) {
                discount = GameUtil.getRatesValue(ShopModel.DISCOUNT_RATES, GameUtil.getRangedRandom(1, 100));
            }
            item.setD(discount);
            //价格
            if (GameUtil.getRangedRandom(1, 100) <= 20) {
                item.setPt(EGoodsType.DIAMOND.getId());
                int pn = model.getDiamond() * discount / 100;
                if (pn < 1)
                    pn = 1;
                item.setPn(pn);
            } else {
                item.setPt(EGoodsType.GOLD.getId());
                float d = discount / 100.0f;
                int pn = (int) (model.getGold() * d);
                if (pn < 1)
                    pn = 1;
                item.setPn(pn);
            }
            items.add(item);
        }
        //从数据表中随机物品
        for (int i = items.size(); i < ShopModel.SELL_MAX; i++) {
            ShopItem item = new ShopItem();
            //随机物品
            int modelId = GameUtil.getRatesValue(ShopModel.getGoodsRates(), GameUtil.getRangedRandom(1, 10000));
            ShopShenMi model = ShopModel.getShenMiData(modelId);
            if (model == null)
                continue;
            DropData g = new DropData(model.getItemType(), model.getItemId(), model.getItemNum());
            item.setG(g);
            item.setN(0);
            //折扣
            int discount = model.getDiscount();
            if (model.getDiscount() <= 0) {
                discount = GameUtil.getRatesValue(ShopModel.DISCOUNT_RATES, GameUtil.getRangedRandom(1, 100));
            }
            item.setD(discount);
            item.setPt(EGoodsType.DIAMOND.getId());
            int pn = model.getDiamond() * discount / 100;
            if (pn < 1)
                pn = 1;
            item.setPn(pn);
            items.add(item);
        }
        getActivityData().setPlayerShopItems(items);
        //保存数据
        getActivityDao().updatePlayerShop(getActivityData());
    }

    /**
     * 购买
     *
     * @param request
     */
    public void processShopPlayerBuy(Message request) {
        byte index = request.readByte();
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        int rsPoints = 0;
        //消息
        Message msg = new Message(MessageCommand.SHOP_PLAYER_BUY_MESSAGE, request.getChannel());
        //全部购买
        if (index == -1) {
            for (ShopItem item : getActivityData().getPlayerShopItems()) {
                if (item.getN() >= ShopModel.BUY_MAX)
                    continue;
                DropData cost = new DropData(item.getPt(), 0, item.getPn());
                //扣除消耗
                if (!role.getPackManager().useGoods(cost, EGoodsChangeType.SHOP_BUY_CONSUME, saves))
                    continue;
                if (cost.getT() == EGoodsType.DIAMOND.getId())
                    rsPoints += cost.getN();
                //增加
                item.addN(1);
                role.getPackManager().addGoods(item.getG(), EGoodsChangeType.SHOP_BUY_ADD, saves);
            }
            msg.setByte(6);
            for (int i = 0; i < getActivityData().getPlayerShopItems().size(); i++) {
                msg.setByte(i);
                msg.setInt(ShopModel.BUY_MAX - getActivityData().getPlayerShopItems().get(i).getN());
            }
        } else {
            if (index < 0 || index >= getActivityData().getPlayerShopItems().size()) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
                return;
            }
            ShopItem item = getActivityData().getPlayerShopItems().get(index);
            if (item.getN() >= ShopModel.BUY_MAX) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_BUY);
                return;
            }
            DropData cost = new DropData(item.getPt(), 0, item.getPn());
            //扣除消耗
            if (!role.getPackManager().useGoods(cost, EGoodsChangeType.SHOP_BUY_CONSUME, saves)) {
                if (item.getPt() == EGoodsType.GOLD.getId())
                    role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOLD_LESS);
                else
                    role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                return;
            }
            if (cost.getT() == EGoodsType.DIAMOND.getId())
                rsPoints += cost.getN();
            //增加
            item.addN(1);
            role.getPackManager().addGoods(item.getG(), EGoodsChangeType.SHOP_BUY_ADD, saves);
            msg.setByte(1);
            msg.setByte(index);
            msg.setInt(ShopModel.BUY_MAX - item.getN());
        }
        if (rsPoints > 0) {
            DropData points = new DropData(EGoodsType.RSPOINTS, 0, rsPoints);
            role.getPackManager().addGoods(points, EGoodsChangeType.SHOP_BUY_ADD, saves);
        }
        //商城全部购买完刷新
        boolean refresh = true;
        for (ShopItem item : getActivityData().getPlayerShopItems()) {
            if (item.getN() < ShopModel.BUY_MAX) {
                refresh = false;
                break;
            }
        }
        if (refresh) {
            refreshShopPlayer();
            role.putMessageQueue(getShopPlayerMsg());
        }
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        getActivityDao().updatePlayerShop(getActivityData());
    }

    public void processShopPlayerRefresh(Message request) {
        //消耗20元宝
        DropData cost = new DropData(EGoodsType.DIAMOND, 0, 200);
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().useGoods(cost, EGoodsChangeType.SHOP_REFRESH_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        DropData points = new DropData(EGoodsType.RSPOINTS, 0, cost.getN());
        role.getPackManager().addGoods(points, EGoodsChangeType.SHOP_BUY_ADD, saves);
        refreshShopPlayer();
        getActivityData().setPlayerShopRefresh(System.currentTimeMillis());
        Message msg = getShopPlayerMsg();
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
        role.savePlayer(saves);
    }

    public void processTargetReward(Message request) {
//		long curr = System.currentTimeMillis();
//		Map<String, TargetLogicData> model = ActivityService.getRoundData(EActivityType.TARGET, 0, curr);
//		if (model == null) {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		TargetLogicData logic = null;
//		for (TargetLogicData data : model.values()) {
//			// 最后一个？
//			logic = data;
//		}
//		if (getActivityData().getTarget().size() > logic.getMuBiaoCount()) {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		int value = logic.getMuBiao(getActivityData().getTarget().size() + 1);
//		List<DropData> rewards = logic.getReward(value);
//		ActivityRank rank = TargetEvent.getActivityRank(role.getPlayer(), logic.getType());
//		if (logic.getType() == TargetEvent.TYPE_REIN)
//			value = value/10-8;
//		if (rank.getV1() < value) {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
//			return;
//		}
//		getActivityData().getTarget().add((byte) (getActivityData().getTarget().size()+1));
//		//增加物品
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		role.getPackManager().addGoods(rewards, EGoodsChangeType.TARGET_REWARD_ADD, saves);
//		//消息
//		Message msg = new Message(MessageCommand.TARGET_REWARD_MESSAGE, request.getChannel());
//		int result = getTargetRank(logic);
//		msg.setByte(result);
//		role.sendMessage(msg);
//		//保存数据
//		role.savePlayer(saves);
//		getActivityDao().updateTarget(getActivityData());
    }

    public void processFestTargetReward(Message request) {
//		long curr = System.currentTimeMillis();
//		Map<String, TargetLogicData> model = ActivityService.getRoundData(EActivityType.FEST_TARGET, 0, curr);
//		if (model == null) {
//			processWeekendTargetReward(request);
//			return;
//		}
//		TargetLogicData logic = null;
//		for (TargetLogicData data : model.values()) {
//			logic = data;
//		}
//		if (getActivityData().getFestReward() >= logic.getMuBiaoCount()){
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
//			return;
//		}
//		int value = logic.getMuBiao(getActivityData().getFestReward() + 1);
//		List<DropData> rewards = logic.getReward(value);

//		int value = 0;
//		List<DropData> rewards = null;
//		if (getActivityData().getFestReward() == 0) {
//			value = logic.getMubiao1();
//			rewards = logic.getReward1();
//		} else if (getActivityData().getFestReward() == 1) {
//			value = logic.getMubiao2();
//			rewards = logic.getReward2();
//		} else if (getActivityData().getFestReward() == 2) {
//			value = logic.getMubiao3();
//			rewards = logic.getReward3();
//		} else if (getActivityData().getFestReward() == 3) {
//			value = logic.getMubiao4();
//			rewards = logic.getReward4();
//		} else {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
//			return;
//		}
//		if (getActivityData().getFestConsume() < value) {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
//			return;
//		}
//		getActivityData().addFestReward();
//		//增加物品
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		role.getPackManager().addGoods(rewards, EGoodsChangeType.FEST_TARGET_ADD, saves);
//		//消息
//		Message msg = new Message(MessageCommand.FESTTARGET_REWARD_MESSAGE, request.getChannel());
//		msg.setByte(getActivityData().getFestReward()+1);
//		role.sendMessage(msg);
//		//保存数据
//		role.savePlayer(saves);
//		getActivityDao().updateFestConsumeData(getActivityData());
    }

    public void processWeekendTargetReward(Message request) {
//		long curr = System.currentTimeMillis();
//		Map<String, TargetLogicData> model = ActivityService.getRoundData(EActivityType.WEEKEND_TARGET, 0, curr);
//		if (model == null) {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		TargetLogicData logic = null;
//		for (TargetLogicData data : model.values()) {
//			logic = data;
//		}
//
//		if (getActivityData().getWeekendReward() >= logic.getMuBiaoCount()){
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
//			return;
//		}
//		int value = logic.getMuBiao(getActivityData().getWeekendReward() + 1);
//		List<DropData> rewards = logic.getReward(value);

//		int value = 0;
//		List<DropData> rewards = null;
//		if (getActivityData().getWeekendReward() == 0) {
//			value = logic.getMubiao1();
//			rewards = logic.getReward1();
//		} else if (getActivityData().getWeekendReward() == 1) {
//			value = logic.getMubiao2();
//			rewards = logic.getReward2();
//		} else if (getActivityData().getWeekendReward() == 2) {
//			value = logic.getMubiao3();
//			rewards = logic.getReward3();
//		} else if (getActivityData().getWeekendReward() == 3) {
//			value = logic.getMubiao4();
//			rewards = logic.getReward4();
//		} else {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
//			return;
//		}
//		if (getActivityData().getWeekendPay() < value) {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
//			return;
//		}
//		getActivityData().addWeekendReward();
//		//增加物品
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		role.getPackManager().addGoods(rewards, EGoodsChangeType.WEEKEND_TARGET_ADD, saves);
//		//消息
//		Message msg = new Message(request.getCmdId(), request.getChannel());
//		msg.setByte(getActivityData().getWeekendReward()+1);
//		role.sendMessage(msg);
//		//保存数据
//		role.savePlayer(saves);
//		getActivityDao().updateWeekendPayData(getActivityData());
    }

    public void processFestPayTargetReward(Message request) {
//		long curr = System.currentTimeMillis();
//		Map<String, TargetLogicData> model = ActivityService.getRoundData(EActivityType.FEST_PAY_TARGET, 0, curr);
//		if (model == null) {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		TargetLogicData logic = null;
//		for (TargetLogicData data : model.values()) {
//			logic = data;
//		}
//		if (getActivityData().getFestPayReward() >= logic.getMuBiaoCount()){
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
//			return;
//		}
//		int value = logic.getMuBiao(getActivityData().getFestPayReward() + 1);
//		List<DropData> rewards = logic.getReward(value);

//		int value = 0;
//		List<DropData> rewards = null;
//		if (getActivityData().getFestPayReward() == 0) {
//			value = logic.getMubiao1();
//			rewards = logic.getReward1();
//		} else if (getActivityData().getFestPayReward() == 1) {
//			value = logic.getMubiao2();
//			rewards = logic.getReward2();
//		} else if (getActivityData().getFestPayReward() == 2) {
//			value = logic.getMubiao3();
//			rewards = logic.getReward3();
//		} else if (getActivityData().getFestPayReward() == 3) {
//			value = logic.getMubiao4();
//			rewards = logic.getReward4();
//		} else {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
//			return;
//		}
//		if (getActivityData().getFestPay() < value) {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
//			return;
//		}
//		getActivityData().addFestPayReward();
//		//增加物品
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		role.getPackManager().addGoods(rewards, EGoodsChangeType.WEEKEND_TARGET_ADD, saves);
//		//消息
//		Message msg = new Message(request.getCmdId(), request.getChannel());
//		msg.setByte(getActivityData().getFestPayReward()+1);
//		role.sendMessage(msg);
//		//保存数据
//		role.savePlayer(saves);
//		getActivityDao().updateFestPayData(getActivityData());
    }

    public void processTargetTop(Message request) {
        byte round = request.readByte();
        Message msg = getTargetTop(round);
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
    }

    /**
     * 每日累计消费达标排行
     *
     * @param request
     */
    public void processTargetConsumeTop(Message request) {
        Message msg = getTargetConsumeTop();
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
    }

    /**
     * 获取每日累计消费
     *
     * @return
     */
    private Message getTargetConsumeTop() {
        Message msg = new Message(MessageCommand.GAME_TARGET_DAILY_CONSUME_MESSAGE);
//		long curr = System.currentTimeMillis();
//		int left = (int) ((GameRankManager.getInstance().getNextUpdateTime() - curr)/DateUtil.SECOND);
//		msg.setInt(left);
        //排行榜信息
        List<ActivityRank> ranks = GameRankManager.getInstance().getTargetConsumeRanks();
        int size = 0;
        if (ranks == null || ranks.isEmpty()) {
            size = 0;
        } else {
            size = ranks.size();
        }
        msg.setByte(size);
        for (int i = 0; i < size; i++) {
            ActivityRank rank = ranks.get(i);
            msg.setByte(i + 1);
            msg.setString(rank.getN());
            msg.setByte(rank.getVn());
            msg.setInt(rank.getV1());
            this.getPlayerSimpleMessage2TargetConsumeTop(msg, rank.getId());
        }
        return msg;
    }

    public void processFestTargetTop(Message request) {
        Message msg = getFestTargetTop();
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
    }

    public void processFestPayTargetTop(Message request) {
        Message msg = getFestPayTargetTop();
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
    }

    public void processLogonReward(Message request) {
        byte rewardDay = request.readByte();
        //已经领取过
        if (getActivityData().getLogonRewards().contains(new Byte(rewardDay))) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_REPEAT);
            return;
        }
        long curr = System.currentTimeMillis();
        if (!DateUtil.dayEqual(curr, role.getPlayer().getLastLoginTime()))
            handleLogin();
        int today = getActivityData().getLoginDay();
        if (today < rewardDay) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        if (today > 100)
            today = 100;
        Map<String, LogonLogicData> model = ActivityService.getRoundData(EActivityType.LOGON, player.getId(), curr);
        if (model == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        LogonLogicData logic = model.get(rewardDay + "");
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        //领奖
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(logic.getRewards(), EGoodsChangeType.LOGON_REWARD_ADD, saves);
        getActivityData().getLogonRewards().add(rewardDay);
        Message msg = new Message(MessageCommand.LOGON_ACT_REWARD_MESSAGE, request.getChannel());
        msg.setByte(today);
        msg.setByte(getActivityData().getLogonRewards().size());
        for (byte reward : getActivityData().getLogonRewards()) {
            msg.setByte(reward);
        }
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        getActivityDao().updateLogonRewards(getActivityData());
    }

    public void processNewYearLogonReward(Message request) {
        byte rewardDay = request.readByte();
        //已经领取过
        if (getActivityData().getNewYearLogonRewards().contains(new Byte(rewardDay))) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_REPEAT);
            return;
        }
        long curr = System.currentTimeMillis();
        if (!DateUtil.dayEqual(curr, role.getPlayer().getLastLoginTime2Fest()))
            this.handleLogin2NewYear();
        int today = getActivityData().getNewYearLoginDay();
        if (today < rewardDay) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        if (today > 9)
            today = 9;
        Map<String, LogonLogicData> model = ActivityService.getRoundData(EActivityType.NEW_YEAR_LOGON, player.getId(), curr);
        if (model == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        LogonLogicData logic = model.get(rewardDay + "");
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        //领奖
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(logic.getRewards(), EGoodsChangeType.NEW_YEAR_LOGON_RECEIVE, saves);
        getActivityData().getNewYearLogonRewards().add(rewardDay);
        Message msg = new Message(MessageCommand.GAME_NEW_YEAR_RECEIVE, request.getChannel());
        msg.setByte(today);
        msg.setByte(getActivityData().getNewYearLogonRewards().size());
        for (byte reward : getActivityData().getNewYearLogonRewards()) {
            msg.setByte(reward);
        }
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        getActivityDao().updateNewYearLogonRewards(getActivityData());
    }

    /**
     * 节日活动
     *
     * @param request
     */
    public void processFestLogonReward(Message request) {
        byte rewardDay = request.readByte();
        long curr = System.currentTimeMillis();
        if (ActivityService.getRoundData(EActivityType.FEST_LOGON, 0, curr) == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        //活动开始时间
        BaseActivityConfig config = ActivityService.getActivityConfig(EActivityType.FEST_LOGON);
        long startTime = config.getStartTime(role.getPlayerId());
        //距离活动开始第几天
        int today = DateUtil.getDistanceDay(startTime, curr) + 1;
        if (today < rewardDay) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        //节日登录活动记录
        if (getActivityData().getFestLogon().size() < today) {
            for (int i = getActivityData().getFestLogon().size(); i < today - 1; i++) {
                getActivityData().getFestLogon().add((byte) 0);
            }
            getActivityData().getFestLogon().add((byte) 1);
        }
        byte state = getActivityData().getFestLogon().get(rewardDay - 1);
        //已经领取过
        if (state == 2) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_REPEAT);
            return;
        }
        if (state == 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        Map<String, LogonLogicData> model = ActivityService.getRoundData(EActivityType.FEST_LOGON, 0);
        if (model == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        LogonLogicData logic = model.get(rewardDay + "");
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        //领奖
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(logic.getRewards(), EGoodsChangeType.LOGON_REWARD_ADD, saves);
        getActivityData().getFestLogon().set(rewardDay - 1, (byte) 2);
        Message msg = new Message(MessageCommand.FESTLOGON_REWARD_MESSAGE, request.getChannel());
        msg.setByte(rewardDay);
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        getActivityDao().updateFestLogon(getActivityData());
    }

    public void processWeekendLogonReward(Message request) {
        byte rewardDay = request.readByte();
        long curr = System.currentTimeMillis();
        if (ActivityService.getRoundData(EActivityType.WEEKEND_LOGON, 0, curr) == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        //活动开始时间
        BaseActivityConfig config = ActivityService.getActivityConfig(EActivityType.WEEKEND_LOGON);
        long startTime = config.getStartTime(role.getPlayerId());
        //距离活动开始第几天
        int today = DateUtil.getDistanceDay(startTime, curr) + 1;
        if (today < rewardDay) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        //节日登录活动记录
        if (getActivityData().getWeekendLogon().size() < today) {
            for (int i = getActivityData().getWeekendLogon().size(); i < today - 1; i++) {
                getActivityData().getWeekendLogon().add((byte) 0);
            }
            getActivityData().getWeekendLogon().add((byte) 1);
        }
        byte state = getActivityData().getWeekendLogon().get(rewardDay - 1);
        //已经领取过
        if (state == 2) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_REPEAT);
            return;
        }
        if (state == 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        Map<String, LogonLogicData> model = ActivityService.getRoundData(EActivityType.WEEKEND_LOGON, 0);
        if (model == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        LogonLogicData logic = model.get(rewardDay + "");
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        //领奖
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(logic.getRewards(), EGoodsChangeType.LOGON_REWARD_ADD, saves);
        getActivityData().getWeekendLogon().set(rewardDay - 1, (byte) 2);
        Message msg = new Message(MessageCommand.WEEKENDLOGON_REWARD_MESSAGE, request.getChannel());
        msg.setByte(rewardDay);
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        getActivityDao().updateWeekendLogon(getActivityData());
    }

    public void processWanbaLogonReward(Message request) {
        byte rewardDay = request.readByte();
        //活动开始时间
        BaseActivityConfig config = ActivityService.getActivityConfig(EActivityType.WANBA_LOGON);
        long startTime = config.getStartTime(role.getPlayerId());
        //距离活动开始第几天
        int today = DateUtil.getDistanceDay(startTime, System.currentTimeMillis()) + 1;
        if (today < rewardDay) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        //节日登录活动记录
        if (getActivityData().getWanbaLogon().size() < today) {
            for (int i = getActivityData().getWanbaLogon().size(); i < today - 1; i++) {
                getActivityData().getWanbaLogon().add((byte) 0);
            }
            getActivityData().getWanbaLogon().add((byte) 1);
        }
        byte state = getActivityData().getWanbaLogon().get(rewardDay - 1);
        //已经领取过
        if (state == 2) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_REPEAT);
            return;
        }
        if (state == 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        Map<String, LogonLogicData> model = ActivityService.getRoundData(EActivityType.WANBA_LOGON, 0);
        if (model == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        LogonLogicData logic = model.get(rewardDay + "");
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        //领奖
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(logic.getRewards(), EGoodsChangeType.LOGON_REWARD_ADD, saves);
        getActivityData().getWanbaLogon().set(rewardDay - 1, (byte) 2);
        Message msg = new Message(MessageCommand.WANBALOGON_REWARD_MESSAGE, request.getChannel());
        msg.setByte(rewardDay);
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        getActivityDao().updateWanbaLogon(getActivityData());
    }

    public void processRedpacketReward(Message request) {
        BaseActivityConfig config = ActivityService.getActivityConfig(EActivityType.RED_PACKET);
        long curr = System.currentTimeMillis();
        int pass = DateUtil.getDistanceDay(config.getStartTime(0), curr) + 1;
        if (pass <= RedPacketEvent.LAST_DAY || getActivityData().getRedpacket().size() == 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        //已经领取
        if (getActivityData().getRedpacket().get(0) == 1) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_REPEAT);
            return;
        }
        int total = getActivityData().getRedpacketTotal();
        if (total == 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        getActivityData().getRedpacket().set(0, 1);
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        DropData reward = new DropData(EGoodsType.DIAMOND, 0, total);
        role.getPackManager().addGoods(reward, EGoodsChangeType.REDPACKET_REWARD_ADD, saves);
        //发送消息
        role.sendMessage(getActivityMsg(EActivityType.RED_PACKET));
        role.savePlayer(saves);
        getActivityDao().updateRedpacketJson(getActivityData());
    }

    public void processPayFeastReward(Message request) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig config = ActivityService.getActivityConfig(EActivityType.PAY_FEAST);
        ActivityRoundConfig currRound = config.getCurrRound(0, curr);
        if (currRound == null) {
            role.putErrorMessage(ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        Map<String, PayFeastLogicData> logicData = ActivityService.getRoundData(
                EActivityType.PAY_FEAST, currRound.getRound());
        if (logicData == null) {
            role.putErrorMessage(ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        //已经领取过
        if (getActivityData().getPayFeast() == 1) {
            role.putErrorMessage(ErrorDefine.ERROR_REWARD_REPEAT);
            return;
        }
        //充值金额
        int sum = role.getPayManager().getDiamondInPay(currRound.getStartTimeStr(), currRound.getEndTimeStr());
        PayFeastLogicData logic = logicData.get("0");
        if (sum < logic.getCost()) {
            role.putErrorMessage(ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        getActivityData().setPayFeast((byte) 1);
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(logic.getRewards(), EGoodsChangeType.PAY_FEAST_REWARD_ADD, saves);
        //消息
        Message msg = new Message(MessageCommand.PAY_FEAST_REWARD_MESSAGE, request.getChannel());
        msg.setByte(getActivityData().getPayFeast() + 1);
        role.sendMessage(msg);
        //保存
        role.savePlayer(saves);
        getActivityDao().updatePayFeast(getActivityData());
    }

    public Message getTargetTop(byte round) {
        Message msg = new Message(MessageCommand.TARGET_TOP_MESSAGE);
        long curr = System.currentTimeMillis();
        int left = (int) ((GameRankManager.getInstance().getNextUpdateTime() - curr) / DateUtil.SECOND);
        msg.setInt(left);
        //排行榜信息
        List<ActivityRank> ranks = GameRankManager.getInstance().getTargetRanks(round);
        int size = 0;
        if (ranks == null || ranks.isEmpty()) {
            size = 0;
        } else {
            size = ranks.size();
        }
        msg.setByte(size);
        for (int i = 0; i < size; i++) {
            ActivityRank rank = ranks.get(i);
            msg.setByte(i + 1);
            msg.setString(rank.getN());
            msg.setByte(rank.getVn());
            msg.setInt(rank.getV1());
            msg.setInt(rank.getV2());
            this.getPlayerSimpleMessage2TargetTop(msg, round, rank.getId());
        }

        this.getPlayerAppear2Fighting(msg);
        return msg;
    }

    public Message getPlayerAppear2Fighting(Message message) {
        long curr = System.currentTimeMillis();
        Map<String, TargetLogicData> model = ActivityService.getRoundData(EActivityType.TARGET, 0, curr);
        TargetLogicData logic = null;
        if (model == null) {
            model = ActivityService.getRoundData(EActivityType.TARGET, 0, curr - DateUtil.DAY);
        }
        for (TargetLogicData data : model.values()) {
            // 最后一个？
            logic = data;
        }
        EActivityRankType type = EActivityRankType.getType(logic.getType());
        message.setByte(type.getId());
        for (EActivityRankType eart : EActivityRankType.values()) {
            if (eart.getId() <= type.getId()) {
                GameRankManager.getInstance().getActivityAppearsMessage(message, eart);
            }
        }
        return message;
    }

    public Message getPlayerSimpleMessage2TargetConsumeTop(Message msg, int playerId) {
        Player player = GameRankManager.getInstance().getPlayerById(playerId);
        msg.setByte(player.getHead());
//		msg.setShort(player.getLevel());
//		int consume = 0;
//		ActivityRank rank = TargetDailyConsumeCumulateEvent.getActivityRank(player);
//		consume = rank.getV1();
//		msg.setInt(consume);
        return msg;
    }

    public Message getPlayerSimpleMessage2TargetTop(Message msg, byte round, int playerId) {
        Player player = GameRankManager.getInstance().getPlayerById(playerId);
        msg.setByte(player.getHead());
        msg.setShort(player.getLevel());
        int fighting = 0;
        ActivityRank rank = TargetEvent.getActivityRank(player, (byte) (round + 1));
        fighting = rank.getV1();
        msg.setInt(fighting);
        return msg;
    }

    public Message getFestTargetTop() {
        Message msg = new Message(MessageCommand.FESTTARGET_RANK_MESSAGE);
        //排行榜信息
        List<ActivityRank> ranks = GameRankManager.getInstance().getFestRanks();
        if (ranks.size() > 0) {
            msg.setByte(ranks.size());
            for (int i = 0; i < ranks.size(); i++) {
                ActivityRank rank = ranks.get(i);
                msg.setByte(i + 1);
                msg.setString(rank.getN());
                msg.setByte(rank.getVn());
                msg.setInt(rank.getV1());
                msg.setInt(rank.getV2());
            }
        } else {
            List<ActivityRank> weekRanks = GameRankManager.getInstance().getWeekendRanks();
            msg.setByte(weekRanks.size());
            for (int i = 0; i < weekRanks.size(); i++) {
                ActivityRank rank = weekRanks.get(i);
                msg.setByte(i + 1);
                msg.setString(rank.getN());
                msg.setByte(rank.getVn());
                msg.setInt(rank.getV1());
                msg.setInt(rank.getV2());
            }
        }
        return msg;
    }

    public Message getFestPayTargetTop() {
        Message msg = new Message(MessageCommand.FESTPAYTARGET_RANK_MESSAGE);
        //排行榜信息
        List<ActivityRank> ranks = GameRankManager.getInstance().getFestPayRanks();
        msg.setByte(ranks.size());
        for (int i = 0; i < ranks.size(); i++) {
            ActivityRank rank = ranks.get(i);
            msg.setByte(i + 1);
            msg.setString(rank.getN());
            msg.setByte(rank.getVn());
            msg.setInt(rank.getV1());
            msg.setInt(rank.getV2());
        }
        return msg;
    }

    /**
     * 获取当前领取第几个奖励
     *
     * @return
     */
    public void getTargetRank(TargetLogicData logic) {
        //领取记录
//		List<Byte> rewards = getActivityData().getTarget();
//		int rank = rewards.size() < logic.getMuBiaoCount() ? rewards.size() +1 : 0;
//		return rank;
//		int[] result = {1, 0};
//		if (rewards.size() == 0) {
//			result[0] = 1;
//			result[1] = logic.getMubiao1();
//		} else if (rewards.size() == 1) {
//			result[0] = 2;
//			result[1] = logic.getMubiao2();
//		} else if (rewards.size() == 2) {
//			result[0] = 3;
//			result[1] = logic.getMubiao3();
//		} else if (rewards.size() > 2) {
//			result[0] = 0;
//			result[1] = Integer.MAX_VALUE;
//		}
//		return result;
    }

    public void processGoldTreeReward(Message request) {
        byte num = request.readByte();
        if (num < 0 || num >= GameDefine.GOLD_TREE_REWARD.length) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        int[] goldTree = GameDefine.GOLD_TREE_REWARD[num];
        //该奖励还不可领取
        if (getActivityData().getGoldTreeNum() < goldTree[0]) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_NO);
            return;
        }
        //该奖励已领取
        if (getActivityData().getGoldTreeReward().contains(num)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_REWARD_REPEAT);
            return;
        }
        getActivityData().getGoldTreeReward().add(num);
        //奖励
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        DropData reward = new DropData((byte) goldTree[1], goldTree[2], goldTree[3]);
        role.getPackManager().addGoods(reward, EGoodsChangeType.GOLDTREE_REWARD_ADD, saves);
        //消息
        Message msg = new Message(MessageCommand.GOLDTREE_REWARD_MESSAGE, request.getChannel());
        msg.setByte(num);
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        getActivityDao().updateGoldTree(getActivityData());
    }

    public void processGoldTreeProduce(Message request) {
        Map<String, GoldTreeLogicData> model = ActivityService.getRoundData(
                EActivityType.GOLD_TREE, 0, System.currentTimeMillis());
        if (model == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOLDTREE_CLOSED);
            return;
        }
        int num = getActivityData().getGoldTreeNum() + 1;
        //次数
        int max = GameDefine.GOLD_TREE_NUM;
        int vipNum = VipModel.getVipWeal(player.getVipLevel(), EVipType.GOLDTREE);
        if (vipNum > max)
            max = vipNum;
        //次数上限
        if (num > max) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BUY_MAX);
            return;
        }
        GoldTreeLogicData logic = model.get(String.valueOf(num));
        if (logic == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BUY_MAX);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //消耗
        if (!role.getPackManager().useGoods(logic.getCost(), EGoodsChangeType.GOLDTREE_GET_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        getActivityData().addGoldTreeNum();
        //产出
        float addRate = logic.getAddRate() / 10000f;
        int gold = (int) (logic.getGold() * addRate);
        int ran = GameUtil.getRangedRandom(1, 10000);
        int doub = 0;
        if (ran <= logic.getDoubleRate()) {
            gold *= 2;
            doub = 1;
        }
        role.getPackManager().addGoods(new DropData(EGoodsType.GOLD, 0, gold),
                EGoodsChangeType.GOLDTREE_GET_ADD, saves);
        //消息
        Message msg = new Message(MessageCommand.GOLDTREE_PRODUCE_MESSAGE, request.getChannel());
        msg.setShort(num);
        msg.setInt(gold);
        msg.setByte(doub);
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        getActivityDao().updateGoldTree(getActivityData());
    }

    public void processXunbaoRank(Message request) {
        Message msg = GameRankManager.getInstance().getXunbaoRankMsg(role, GameRankManager.XUNBAO_RANK_MAX);
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
    }

    public void processWishingWell(Message request) {
        long curr = System.currentTimeMillis();
        Map<String, WishingWellLogicData> model = ActivityService.getRoundData(EActivityType.WISHING_WELL, 0, curr);
        if (model == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        WishingWellLogicData logic = null;
        for (WishingWellLogicData data : model.values()) {
            logic = data;
        }
        //许愿次数
        if (getActivityData().getWishing() == null)
            getActivityData().setWishing(new ArrayList<Byte>());
        if (getActivityData().getWishing().size() == 0)
            getActivityData().getWishing().add((byte) 0);
        int count = getActivityData().getWishing().get(0);
        //次数上限
        if (count >= logic.getPrices().size()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_NUM_MAX);
            return;
        }
        //消耗
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        int cost = logic.getPrices().get(count);
        if (cost > 0) {
            if (!role.getPackManager().useGoods(new DropData(EGoodsType.DIAMOND, 0, cost),
                    EGoodsChangeType.WISHING_WELL_CONSUME, saves)) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                return;
            }
        }
        //许愿池
        List<Integer> wishingWell = this.getWishingWell(count, logic, getActivityData().getWishing());
        int index = wishingWell.get(GameUtil.getRangedRandom(0, wishingWell.size() - 1));
        //奖励
        DropData reward = logic.getRewards().get(index);
        role.getPackManager().addGoods(reward, EGoodsChangeType.WISHING_WELL_ADD, saves);
        getActivityData().getWishing().add((byte) index);
        //次数增加
        getActivityData().getWishing().set(0, (byte) (++count));
        //消息
        Message msg = new Message(MessageCommand.WISHING_WELL_MESSAGE, request.getChannel());
        msg.setByte(count);
        msg.setByte(index);
        role.sendMessage(msg);
        //保存数据
        getActivityDao().updateWishingInfo(getActivityData());
        role.savePlayer(saves);
        //广播
        if (index >= 7) {
            String name = "稀世珍宝";
            if (reward.getT() == EGoodsType.ITEM.getId())
                name = GoodsModel.getItemDataById(reward.getG()).getName();
            else if (reward.getT() == EGoodsType.BOX.getId())
                name = GoodsModel.getBoxDataById(reward.getG()).getName();
//			ChatService.broadcastPlayerMsg(player, EBroadcast.WISHING_WELL, name);
        }
    }

    public void processFestWishingWell(Message request) {
        long curr = System.currentTimeMillis();
        Map<String, WishingWellLogicData> model = ActivityService.getRoundData(
                EActivityType.FEST_WISHING_WELL, 0, curr);
        if (model == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        WishingWellLogicData logic = null;
        for (WishingWellLogicData data : model.values()) {
            logic = data;
        }
        //许愿次数
        if (getActivityData().getFestWishing() == null)
            getActivityData().setFestWishing(new ArrayList<Byte>());
        if (getActivityData().getFestWishing().size() == 0)
            getActivityData().getFestWishing().add((byte) 0);
        int count = getActivityData().getFestWishing().get(0);
        //次数上限
        if (count >= logic.getPrices().size()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_NUM_MAX);
            return;
        }
        //消耗
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        int cost = logic.getPrices().get(count);
        if (cost > 0) {
            if (!role.getPackManager().useGoods(new DropData(EGoodsType.DIAMOND, 0, cost),
                    EGoodsChangeType.WISHING_WELL_CONSUME, saves)) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                return;
            }
        }
        //许愿池
        List<Integer> wishingWell = this.getWishingWell(count, logic, getActivityData().getFestWishing());
        int index = wishingWell.get(GameUtil.getRangedRandom(0, wishingWell.size() - 1));
        //奖励
        DropData reward = logic.getRewards().get(index);
        role.getPackManager().addGoods(reward, EGoodsChangeType.WISHING_WELL_ADD, saves);
        getActivityData().getFestWishing().add((byte) index);
        //次数增加
        getActivityData().getFestWishing().set(0, (byte) (++count));
        //消息
        Message msg = new Message(MessageCommand.FESTWISHING_WELL_MESSAGE, request.getChannel());
        msg.setByte(count);
        msg.setByte(index);
        role.sendMessage(msg);
        //保存数据
        getActivityDao().updateFestWishingInfo(getActivityData());
        role.savePlayer(saves);
        //广播
        if (index >= 7) {
            String name = "稀世珍宝";
            if (reward.getT() == EGoodsType.ITEM.getId())
                name = GoodsModel.getItemDataById(reward.getG()).getName();
            else if (reward.getT() == EGoodsType.BOX.getId())
                name = GoodsModel.getBoxDataById(reward.getG()).getName();
//			ChatService.broadcastPlayerMsg(player, EBroadcast.WISHING_WELL, name);
        }
    }

    public void processWeekendWishingWell(Message request) {
        long curr = System.currentTimeMillis();
        Map<String, WishingWellLogicData> model = ActivityService.getRoundData(
                EActivityType.WEEKEND_WISHING_WELL, 0, curr);
        if (model == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        WishingWellLogicData logic = null;
        for (WishingWellLogicData data : model.values()) {
            logic = data;
        }
        //许愿次数
        if (getActivityData().getFestWishing() == null)
            getActivityData().setFestWishing(new ArrayList<Byte>());
        if (getActivityData().getFestWishing().size() == 0)
            getActivityData().getFestWishing().add((byte) 0);
//		int sum = new PayDao().getTodayRMBPay(role.getPlayer())*10;
        int sum = role.getPayManager().getTodayRmbInPay() * 10;
        int next = 0;
        for (; next < logic.getPrices().size(); next++) {
            if (sum < logic.getPrices().get(next))
                break;
        }
        int left = next - (getActivityData().getFestWishing().size() - 1);
        //次数上限
        if (getActivityData().getFestWishing().size() - 1 == 10) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_NUM_MAX);
            return;
        }
        if (left < 1) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_CHARGE_NO_ARRIVE);
            return;
        }
        //消耗
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //许愿池
        List<Integer> wishingWell = this.getWishingWell(getActivityData().getFestWishing().size() - 1, logic, getActivityData().getFestWishing());
        int index = wishingWell.get(GameUtil.getRangedRandom(0, wishingWell.size() - 1));
        //奖励
        DropData reward = logic.getRewards().get(index);
        role.getPackManager().addGoods(reward, EGoodsChangeType.WISHING_WELL_ADD, saves);
        getActivityData().getFestWishing().add((byte) index);
        //消息
        Message msg = new Message(MessageCommand.WEEKENDWISHING_WELL_MESSAGE, request.getChannel());
        msg.setByte(left - 1);
        msg.setByte(index);
        role.sendMessage(msg);
        //保存数据
        getActivityDao().updateFestWishingInfo(getActivityData());
        role.savePlayer(saves);
        //广播
        if (index >= 7) {
            String name = "稀世珍宝";
            if (reward.getT() == EGoodsType.ITEM.getId())
                name = GoodsModel.getItemDataById(reward.getG()).getName();
            else if (reward.getT() == EGoodsType.BOX.getId())
                name = GoodsModel.getBoxDataById(reward.getG()).getName();
//			ChatService.broadcastPlayerMsg(player, EBroadcast.FEST_ZHUANPAN, name);
        }
    }

    /**
     * 根据当前层数和当前步数获取大富翁数据
     *
     * @param currLevel 当前层数
     * @param currSteps 当前层数所对应的步数
     * @param logics    所有数据
     * @return
     */
    public MonopolyLogicData1 getMonopolyDataIdByCurrLevel(int currLevel, int currSteps, Map<String, MonopolyLogicData1> logics) {
        if (currSteps == 0) currSteps = 1;
        for (MonopolyLogicData1 mld : logics.values()) {
            if (mld.getChance() == currLevel && currSteps == mld.getStepNum()) {
                return mld;
            }
        }
        return logics.get("1");
    }

    /**
     * 获取所有层数
     *
     * @param logics
     * @return
     */
    private Set<Integer> getAllLevels(Map<String, MonopolyLogicData1> logics) {
        Set<Integer> set = new HashSet<>();
        for (MonopolyLogicData1 mld : logics.values()) {
            set.add(mld.getChance());
        }
        return set;
    }

    /**
     * 获取下一层
     *
     * @param currLevel 当前层
     * @param logics
     * @return
     */
    public int getNextLevel(int playedAllLevel, Map<String, MonopolyLogicData1> logics) {
        List<Integer> list = new ArrayList<>(this.getAllLevels(logics));
        int min = this.getMinInt(list);
        if (playedAllLevel <= 0) return min;
        Collections.sort(list);
        int max = this.getMaxInt(list);
        int level = playedAllLevel % max;
        for (Integer i : list) {
            if (i > level) {
                return i;
            }
        }
        return min;
    }

    /**
     * 获取最大值
     *
     * @param cols
     * @return
     */
    public int getMaxInt(Collection<Integer> cols) {
        int max = Integer.MIN_VALUE;
        for (Integer i : cols) {
            if (i > max) max = i;
        }
        return max;
    }

    /**
     * 获取最小值
     *
     * @param cols
     * @return
     */
    public int getMinInt(Collection<Integer> cols) {
        int min = Integer.MAX_VALUE;
        for (Integer i : cols) {
            if (i < min) min = i;
        }
        return min;
    }

    private int getLevelStepHighestId() {
        long curr = System.currentTimeMillis();
        Map<String, MonopolyLogicData1> mapRound = ActivityService.getRoundData(EActivityType.MONOPOLY1, role.getPlayerId(), curr);
        int highestStepId = Integer.MIN_VALUE;
        for (MonopolyLogicData1 mld1 : mapRound.values()) {
            if (mld1.getId() > highestStepId) {
                highestStepId = mld1.getId();
            }
        }
        return highestStepId;
    }

    private int getLevelStepLowestId() {
        long curr = System.currentTimeMillis();
        Map<String, MonopolyLogicData1> mapRound = ActivityService.getRoundData(EActivityType.MONOPOLY1, role.getPlayerId(), curr);
        int lowestStepId = Integer.MAX_VALUE;
        for (MonopolyLogicData1 mld1 : mapRound.values()) {
            if (mld1.getChance() == getActivityData().getMonopolyCurrLevel() && mld1.getId() < lowestStepId) {
                lowestStepId = mld1.getId();
            }
        }
        return lowestStepId;
    }

    public int getMaxRewardStep() {
        long curr = System.currentTimeMillis();
        Map<String, MonopolyLogicData1> mapRound = ActivityService.getRoundData(EActivityType.MONOPOLY1, role.getPlayerId(), curr);
        MonopolyLogicData1 mld = mapRound.get("1");
        Map<Integer, StepInfo> mapStep = mld.getStepInfos();
        int maxStep = Integer.MIN_VALUE;
        for (StepInfo stepInfo : mapStep.values()) {
            if (stepInfo.getStep() > maxStep) {
                maxStep = stepInfo.getStep();
            }
        }
        return maxStep;
    }

    public int getMaxRewardStepId() {
        long curr = System.currentTimeMillis();
        Map<String, MonopolyLogicData1> mapRound = ActivityService.getRoundData(EActivityType.MONOPOLY1, role.getPlayerId(), curr);
        MonopolyLogicData1 mld = mapRound.get("1");
        Map<Integer, StepInfo> mapStep = mld.getStepInfos();
        int maxStepId = Integer.MIN_VALUE;
        for (StepInfo stepInfo : mapStep.values()) {
            if (stepInfo.getId() > maxStepId) {
                maxStepId = stepInfo.getId();
            }
        }
        return maxStepId;
    }

    private int getMaxLevel() {
        long curr = System.currentTimeMillis();
        Map<String, MonopolyLogicData1> mapRound = ActivityService.getRoundData(EActivityType.MONOPOLY1, role.getPlayerId(), curr);
        MonopolyLogicData1 mld = mapRound.get("1");
        Map<Integer, LevelInfo> mapLevel = mld.getLevelInfos();
        int max = Integer.MIN_VALUE;
        for (LevelInfo levelInfo : mapLevel.values()) {
            if (levelInfo.getLevel() > max) {
                max = levelInfo.getLevel();
            }
        }
        return max;
    }

    /**
     * 大富翁1抽取
     *
     * @param request
     */
    public void processMonopoly1Dice(Message request) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.MONOPOLY1);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData group = ActivityService.getGroupData(EActivityType.MONOPOLY1);
        Map<String, MonopolyLogicData1> mapRound = ActivityService.getRoundData(EActivityType.MONOPOLY1, role.getPlayerId(), curr);
        int currLevel = getActivityData().getMonopoly1CurrLevel();
        int currSteps = getActivityData().getMonopoly1CurrSteps();
        MonopolyLogicData1 mld = this.getMonopolyDataIdByCurrLevel(currLevel, currSteps, mapRound);
        //所有层数档位(对应chance)
        Set<Integer> set = new HashSet<>();
        for (MonopolyLogicData1 data : mapRound.values()) {
            set.add(data.getChance());
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (getActivityData().getMonopoly1FreeNum() >= 10) {
            List<DropData> consume = mld.getPrices0();
            boolean flag = EGoodsType.getGoodsType(consume.get(1).getT()).getCmd().consume(role, consume.get(1), EGoodsChangeType.MONOPOLY1_CONSUME, saves);
            if (!flag) {
                if (!EGoodsType.getGoodsType(consume.get(0).getT()).getCmd().consume(role, consume.get(0), EGoodsChangeType.MONOPOLY1_CONSUME, saves)) {
                    role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                    return;
                }
            }
        } else {
            getActivityData().addMonopoly1FreeNum();
        }
        int highestSteps = mapRound.size() / set.size();
        //所有奖励层数
        List<Integer> allReceives = new ArrayList<>(mld.getLevelInfos().keySet());
        Ele ele = DiceUtil.dice(mld.getEles1());
        int dice = ele.getId();
        int playedStep = getActivityData().getMonopoly1PlayedStep();
        if ((playedStep + dice) >= this.getMaxRewardStep()) {
            getActivityData().setMonopoly1PlayedStep(this.getMaxRewardStep());
        } else {
            getActivityData().addMonopoly1PlayedStep(dice);
        }
        getActivityData().setMonopoly1DiceOne(dice);
        int allCurrSteps = currSteps + dice;
        //最高奖励层数
        int highestLevelReceive = 0;
        for (int i : allReceives) {
            LevelInfo levelInfo = mld.getLevelInfos().get(i);
            if (levelInfo.getLevel() > highestLevelReceive) {
                highestLevelReceive = levelInfo.getLevel();
            }
        }

        if (highestSteps < allCurrSteps) {
            allCurrSteps = allCurrSteps - highestSteps;
            //层数+1
            int todayPlayLevel = getActivityData().getMonopoly1TodayPlayLevel() + 1 /*> highestLevelReceive ? highestLevelReceive : getActivityData().getMonopoly1TodayPlayLevel() + 1*/;
            getActivityData().setMonopoly1TodayPlayLevel(todayPlayLevel);
            //重置当前层数和下次层数
            currLevel = getActivityData().getMonopoly1NextLevel();
            getActivityData().setMonopoly1CurrLevel(currLevel);
            int allPlayedLevel = (getActivityData().getMonopoly1ResetNum()) * highestLevelReceive + todayPlayLevel;
            int nextLevel = this.getNextLevel(allPlayedLevel + 1, mapRound);
            getActivityData().setMonopoly1NextLevel(nextLevel);
        }
        //发奖励
        int highestStepRewardId = highestSteps;
        int endReward1 = currSteps + dice;
        int endReward2 = 0;
        int step = 0;
        if (endReward1 > highestStepRewardId) {
            endReward2 = endReward1 - highestStepRewardId;
            endReward1 = highestStepRewardId;
            step = endReward2;
        } else {
            step = endReward1;
        }
        int stepId = 0;
        for (MonopolyLogicData1 data : mapRound.values()) {
            if (data.getChance() == currLevel && data.getStepNum() == step) {
                stepId = data.getId();
                break;
            }
        }
        getActivityData().setMonopoly1RewardStepId(stepId);
        DropData ddReward = mapRound.get(getActivityData().getMonopoly1RewardStepId() + "").getReward();
        EGoodsType.getGoodsType(ddReward.getT()).getCmd().reward(role, ddReward, EGoodsChangeType.MONOPOLY1_ADD, saves);
        //保存当前步数
        getActivityData().setMonopoly1CurrSteps(allCurrSteps);
        if (getActivityData().getMonopoly1TodayPlayLevel() >= highestLevelReceive && getActivityData().getMonopoly1ResetNum() < 3) {
            getActivityData().setMonopoly1TodayPlayLevel(0);
            getActivityData().addMonopoly1ResetNum();
            getActivityData().setMonopoly1Status((byte) 1);
            //TODO 保存数据库
            allReceives.removeAll(getActivityData().getMonopoly1LevelReceived());
            List<DropData> dds = new ArrayList<>();
            boolean flag = true;
            for (int i : allReceives) {
                for (DropData dd : mld.getLevelInfos().get(i).getReward()) {
                    if (dds.isEmpty()) {
                        dds.add(dd);
                    } else {
                        int size = dds.size();
                        for (int j = 0; j < size; j++) {
                            DropData dropData = dds.get(j);
                            if (dropData.getT() == dd.getT() && dropData.getG() == dd.getG()) {
                                int sum = dd.getN() + dropData.getN();
                                DropData data = new DropData(dd.getT(), dd.getG(), sum);
                                dds.set(j, data);
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            dds.add(dd);
                        }
                    }
                }
            }
            Mail mail = MailService.createMail("探宝层数未领取奖励", "探宝层数未领取奖励", EGoodsChangeType.MONOPOLY1_ADD, dds);
            MailService.sendSystemMail(role.getPlayerId(), mail);
            getActivityData().resetMonopoly1LevelReceived();
            //TODO 保存数据库
        }
        int todayPlayLevel = getActivityData().getMonopoly1TodayPlayLevel();
        if ((getActivityData().getMonopoly1ResetNum() * highestLevelReceive + todayPlayLevel) == highestSteps + 1) {
            getActivityData().setMonopoly1Status((byte) 1);
        }
        Message message = getActivityMsg(EActivityType.MONOPOLY1);
        message.setChannel(request.getChannel());
        role.sendMessage(message);
        role.savePlayer(saves);
        getActivityDao().updateMonopoly4Dice(getActivityData());
    }

    /**
     * 大富翁1奖励领取
     *
     * @param request
     */
    public void processMonopoly1Receive(Message request) {
        byte type = request.readByte();//类型
        int num = request.readInt();//领取的数

        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.MONOPOLY1);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;

        ActivityGroupData group = ActivityService.getGroupData(EActivityType.MONOPOLY1);
        Map<String, MonopolyLogicData1> mapRound = ActivityService.getRoundData(EActivityType.MONOPOLY1, role.getPlayerId(), curr);
        int currLevel = getActivityData().getMonopoly1CurrLevel();
        int currSteps = getActivityData().getMonopoly1CurrSteps();
        MonopolyLogicData1 mld = this.getMonopolyDataIdByCurrLevel(currLevel, currSteps, mapRound);
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (type == 1) {//层数
            int receiveLevel = getActivityData().getMonopoly1TodayPlayLevel();
            int currReceiveLevel = mld.getLevelInfos().get(num).getLevel();
            if (!mld.getLevelInfos().keySet().contains(num) || (currReceiveLevel > receiveLevel || getActivityData().getMonopoly1LevelReceived().contains(num))) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_MONOPOLY_NO_COST);
                return;
            }
            getActivityData().addMonopoly1LevelReceived(num);
            //TODO 保存数据库
            List<DropData> dds = mld.getLevelInfos().get(num).getReward();
            role.getPackManager().addGoods(dds, EGoodsChangeType.MONOPOLY1_ADD, saves);
        } else if (type == 2) {//步数
            int allStep = getActivityData().getMonopoly1PlayedStep();
            int currReceiveStep = mld.getStepInfos().get(num).getStep();
            if (!mld.getStepInfos().keySet().contains(num) || (currReceiveStep > allStep || getActivityData().getMonopoly1TodayStepReceive().contains(num))) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_MONOPOLY_NO_COST);
                return;
            }
            getActivityData().addMonopoly1TodayStepReceive(num);
            //TODO 保存数据库
            MonopolyLogicData1.StepInfo stepInfo = null;
            for (StepInfo si : mld.getStepInfos().values()) {
                if (si.getId() == num) {
                    stepInfo = si;
                    break;
                }
            }
            List<DropData> dd = stepInfo.getReward();
            role.getPackManager().addGoods(dd, EGoodsChangeType.MONOPOLY1_ADD, saves);
        }
        List<Integer> todayStepReceives = getActivityData().getMonopoly1TodayStepReceive();
        Map<Integer, StepInfo> stepInfos = mld.getStepInfos();
        int stepReceive = 1;
        int highestStep = 0;
        for (Integer n : todayStepReceives) {
            if (highestStep < n) {
                highestStep = n;
            }
        }
        for (StepInfo si : stepInfos.values()) {
            if (si.getId() == highestStep) {
                stepReceive = si.getId() + 1;
                break;
            }
        }
        if (stepReceive > this.getMaxRewardStepId()) {
            stepReceive = 0;
        }
        List<Integer> levelReceived = getActivityData().getMonopoly1LevelReceived();
        Message message = new Message(MessageCommand.GAME_MONOPOLY1_RECEIVE, request.getChannel());
        message.setByte(levelReceived.size());
        //层数已领取id
        for (int i : levelReceived) {
            message.setInt(i);
        }
        message.setInt(stepReceive);
        role.sendMessage(message);
        role.savePlayer(saves);
        getActivityDao().updateMonopoly4Receive(getActivityData());
    }

    /**
     * 大富翁1聚宝阁兑换
     *
     * @param request
     */
    public void processMonopoly1Exchange(Message request) {
        int id = request.readInt();
        long curr = System.currentTimeMillis();
        Map<String, MonopolyLogicData1> mapRound = ActivityService.getRoundData(EActivityType.MONOPOLY1, role.getPlayerId(), curr);
        MonopolyLogicData1 mld = mapRound.get("1");
        Map<Integer, JubaogeInfo> jubaogeInfos = mld.getJubaogeInfos();
        JubaogeInfo jubaoge = jubaogeInfos.get(id);
        if (jubaoge == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        List<DropData> consumes = jubaoge.getConsume();
        if (!role.getPackManager().useGoods(consumes, EGoodsChangeType.MONOPOLY1_EXCHANGE_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        List<DropData> rewards = jubaoge.getReward();
        if (!role.getPackManager().addGoods(rewards, EGoodsChangeType.MONOPOLY1_EXCHANGE_ADD, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }

        Message message = new Message(MessageCommand.GAME_MONOPOLY1_EXCHANGE, request.getChannel());
        role.sendMessage(message);
    }

    //计算许愿池
    private List<Integer> getWishingWell(int count, WishingWellLogicData logic, List<Byte> wishing) {
        int group1 = 3, group2 = 8;
        List<Integer> list = new ArrayList<>();
        //第1-3天
        if (count < group1) {
            for (int index : logic.getFirst()) {
                if (!this.containWishingIndex(wishing, index - 1)) {
                    list.add(index - 1);
                }
            }
        }
        //第4-8天
        else if (count >= group1 && count < group2) {
            for (int index : logic.getFirst()) {
                if (!this.containWishingIndex(wishing, index - 1)) {
                    list.add(index - 1);
                }
            }
            for (int index : logic.getSecond()) {
                if (!this.containWishingIndex(wishing, index - 1)) {
                    list.add(index - 1);
                }
            }
        } else {
            for (int index : logic.getFirst()) {
                if (!this.containWishingIndex(wishing, index - 1)) {
                    list.add(index - 1);
                }
            }
            for (int index : logic.getSecond()) {
                if (!this.containWishingIndex(wishing, index - 1)) {
                    list.add(index - 1);
                }
            }
            for (int index : logic.getThird()) {
                if (!this.containWishingIndex(wishing, index - 1)) {
                    list.add(index - 1);
                }
            }
        }
        return list;
    }

    private boolean containWishingIndex(List<Byte> wishing, int index) {
        for (int i = 1; i < wishing.size(); i++) {
            if (wishing.get(i) == index)
                return true;
        }
        return false;
    }

    /**
     * 新活动大厅消息
     *
     * @return
     */
    public Message getActivityNewMessage(short cmdId) {
        Map<Byte, List<Byte>> activityMap = new HashMap<>();
        long currTime = System.currentTimeMillis();
        for (ActivityNewOpenData openData : ActivityNewService.getActivityOpenMap().values()) {
            if (openData.getStartTime() < currTime && currTime < openData.getEndTime()) {
                ActivityNewGroupData activityNewGroupData = ActivityNewService.getActivityGroupMap().get(openData.getId());
                for (ActivityNewData activityNewData : activityNewGroupData.getActivityMap().values()) {
                    if (currTime < activityNewData.getEndTime()) {
                        if (activityMap.containsKey(openData.getId())) {
                            activityMap.get(openData.getId()).add(activityNewData.getId());
                        } else {
                            activityMap.put(openData.getId(), new ArrayList<Byte>(Arrays.asList(activityNewData.getId())));
                        }
                    }
                }
            }
        }

        Message message = new Message(cmdId);
        message.setByte(activityMap.size());
        for (Entry<Byte, List<Byte>> entry : activityMap.entrySet()) {
            message.setByte(entry.getKey());
            message.setByte(entry.getValue().size());
            for (byte id : entry.getValue()) {
                message.setByte(id);
                IActivityInfo info = EActivityNewType.getType(id).getActivityInfo();
                info.getMessage(message, entry.getKey(), role);
            }
        }
        return message;
    }

    /**
     * 1001 新活动大厅
     *
     * @return
     */
    public Message getActivityNewMessage() {
        return getActivityNewMessage(MessageCommand.GAME_ACTIVITY_NEW_MESSAGE);
    }

    /**
     * 1002 新活动大厅数据更新
     *
     * @return
     */
    public Message getActivityNewUpdateMessage() {
        return getActivityNewMessage(MessageCommand.GAME_ACTIVITY_NEW_UPDATE_MESSAGE);
    }

    /**
     * 1002 新活动大厅数据更新
     *
     * @return
     */
    public Message getActivityNewUpdateMessage(byte groupId, byte activityId) {
        Message message = new Message(MessageCommand.GAME_ACTIVITY_NEW_UPDATE_MESSAGE);
        message.setByte(1);
        message.setByte(groupId);
        message.setByte(1);
        message.setByte(activityId);
        IActivityInfo info = EActivityNewType.getType(activityId).getActivityInfo();
        info.getMessage(message, groupId, role);
        return message;
    }

    /**
     * 刷新充值活动
     */
    public void sendActivityNewPayUpdateMessage() {
        role.putMessageQueue(getActivityNewUpdateMessage());
    }
}
