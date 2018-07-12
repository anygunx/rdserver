package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.Escort;
import com.rd.bean.player.EscortLog;
import com.rd.bean.player.EscortPlayer;
import com.rd.bean.player.Player;
import com.rd.common.ChatService;
import com.rd.common.GameCommon;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.dao.EscortDao;
import com.rd.define.*;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.lg.bean.game.Fun;
import com.rd.model.ConstantModel;
import com.rd.model.EscortModel;
import com.rd.model.data.EscortModelData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import com.rd.util.GameUtil;
import com.rd.util.LogUtil;

import java.util.*;

/**
 * 押镖管理器
 *
 * @author Created by U-Demon on 2016年11月19日 下午7:39:00
 * @version 1.0.0
 */
public class EscortManager {

    private GameRole role;

    //押镖数据
    private Escort _escort;

    private EscortDao _dao;

    //车队时间
    private long refreshTime = 0;

    //车队
    private Map<Integer, EscortPlayer> robPlayers = new HashMap<>();

    private byte fightResult = FightDefine.FIGHT_RESULT_FAIL;

    public EscortManager(GameRole role) {
        this.role = role;
    }

    /**
     * 初始化押镖数据
     */
    private void init() {
        this._dao = role.getDbManager().escortDao;
        this._escort = _dao.getPlayerEscort(role.getPlayer().getId());
    }

    private Escort getEscort() {
        if (null == _escort) {
            init();
        }
        return _escort;
    }

    private EscortDao getEscortDao() {
        if (null == _dao) {
            init();
        }
        return _dao;
    }

    /**
     * 获取每日押镖次数上限
     *
     * @return
     */
    public int getDispatchMax() {
        return ConstantModel.ESCORT_DISPATCH_MAX;
    }

    /**
     * 获取押镖数据详情
     *
     * @return
     */
    public Message getEscortDetail() {
        if (this.getEscort() == null)
            init();
        Message msg = new Message(MessageCommand.ESCORT_DETAIL_MESSAGE);
        this.getEscort().getEscortMsg(msg);
        return msg;
    }

    public Message getEscortLogRead() {
        Message msg = new Message(MessageCommand.ESCORT_LOGS_READ);
        msg.setByte(this.getEscort().getReaded());
        return msg;
    }

    /**
     * 发送押镖数据详情
     *
     * @param request
     */
    public void processEscortDetail(Message request) {
        Message msg = getEscortDetail();
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
    }

    public static long getEscortKeepTime(byte quality) {
        EscortModelData model = EscortModel.getEscortModel(quality);
        if (model == null)
            return DateUtil.DAY;
        return model.getKeeptime() * DateUtil.SECOND;
    }

    private List<DropData> balanceItemReward() {
        List<DropData> rewards = new ArrayList<>();
        //物品奖励，还得加上被劫镖的负收益
        EscortModelData model = EscortModel.getEscortModel(getEscort().getQuality());
        for (DropData reward : model.getReward()) {
            int hurtedNum = reward.getN() * ConstantModel.ESCORT_CONSUME / 100 * getEscort().getHurted();
            rewards.add(new DropData(reward.getT(), reward.getG(), reward.getN() - hurtedNum));
        }
        return rewards;
    }

    /**
     * 跨天重置数据
     *
     * @return
     */
    public void dayReset() {
        if (getEscort().getCount() > 0)
            getEscort().setCount(0);
        if (getEscort().getRob() > 0)
            getEscort().setRob(0);
        if (getEscort().getRefresh() > 0)
            getEscort().setRefresh(0);
        if (getEscort().getRobList() != null)
            getEscort().getRobList().clear();
    }

    /**
     * 押运镖车
     *
     * @param request
     */
    public void processDispatch(Message request) {
        //是否有未结算的镖车
        if (getEscort().getCargo() == 1) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ESCORT_UNCOMPLETE);
            return;
        }
        //判断次数
        if (getEscort().getCount() >= getDispatchMax()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ESCORT_DISPATCH_MAX);
            return;
        }
        long curr = System.currentTimeMillis();
        //发车
        getEscort().addCount();
        getEscort().setStartTime(curr);
        getEscort().setCargo(1);
        getEscort().setHurted(0);

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        role.getEventManager().notifyEvent(new GameEvent(EGameEventType.CARRY_ON_MINING, 1, enumSet));
        role.savePlayer(enumSet);

        //发送消息
        Message msg = new Message(MessageCommand.ESCORT_DISPATCH_MESSAGE, request.getChannel());
        msg.setByte(getEscort().getQuality());
        msg.setShort(getEscort().getCount());
        msg.setInt(getEscort().getEscortLeftSec());
        msg.setByte(getEscort().getCargo());
        msg.setByte(getEscort().getHurted());
        role.sendMessage(msg);
        //保存数据
        getEscortDao().updatePlayerEscort(this.getEscort());
        getEscort().addStartLog();
    }

    /**
     * 劫杀镖车开始
     *
     * @param request
     */
    public void processRobStart(Message request) {
        int playerId = request.readInt();
        if (getEscort().getRob() >= ConstantModel.ESCORT_ROB) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ESCORT_ROB_MAX);
            return;
        }
        EscortPlayer ep = robPlayers.get(playerId);
        if (ep == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        Message msg = new Message(MessageCommand.ESCORT_ROB_START_MESSAGE, request.getChannel());
        msg.setByte(ep.getQuality());
        //机器人
        if (playerId < 0) {
            msg.setBool(true);
            msg.setInt(ep.getId());
            //名字
            msg.setString(ep.getName());
            //获取机器人属性
            msg.setLong(ep.getFighting());
        }
        //玩家
        else {
            msg.setBool(false);
            //发送玩家战斗消息
            IGameRole role = GameWorld.getPtr().getGameRole(playerId);
            if (role != null) {
                role.getPlayer().updateFighting();
                role.getPlayer().getBaseSimpleMessage(msg);
                role.getPlayer().getAppearMessage(msg);
                role.getPlayer().getAttrFighting(msg);
            }
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        role.getEventManager().notifyEvent(new GameEvent(EGameEventType.CARRY_ON_MINING_ROB, 1, enumSet));
        role.savePlayer(enumSet);
        role.sendMessage(msg);
    }

    /**
     * 劫杀镖车结果
     *
     * @param request
     */
    public void processRobResult(Message request) {
        int playerId = request.readInt();
        byte result = request.readByte();
        if (getEscort().getRob() >= ConstantModel.ESCORT_ROB) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ESCORT_ROB_MAX);
            return;
        }
        EscortPlayer ep = robPlayers.get(playerId);
        if (ep == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //獎勵
        EscortModelData model = EscortModel.getEscortModel(ep.getQuality());
        if (model == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        List<DropData> rewards = null;

        this.fightResult = result;
        //失败无奖励
        if (FightDefine.FIGHT_RESULT_SUCCESS == this.fightResult) {
            rewards = new ArrayList<>();
            for (DropData reward : model.getReward()) {
                rewards.add(new DropData(reward.getT(), reward.getG(), reward.getN() / ConstantModel.ESCORT_REWARD));
            }
            role.getPackManager().addGoods(rewards, EGoodsChangeType.ESCORT_ROB_ADD, saves);
        }
        getEscort().addRob();
        if (playerId > 0)
            getEscort().getRobList().add(playerId);
        Message msg = new Message(MessageCommand.ESCORT_ROB_RESULT_MESSAGE, request.getChannel());
        msg.setByte(this.fightResult);
        msg.setShort(getEscort().getRob());
        msg.setByte(ep.getQuality());
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        getEscortDao().updatePlayerEscort(this.getEscort());
        //抢劫日志
        getEscort().addRobLog(ep, this.fightResult);
        //TODO 交互数据，先直接修改吧
        if (playerId <= 0)
            return;
        Escort robEscort = null;
        GameRole robRole = GameWorld.getPtr().getOnlineRole(playerId);
        if (robRole != null) {
            robEscort = robRole.getEscortManager().getEscort();
        } else {
            robEscort = getEscortDao().getPlayerEscort(playerId);
        }
        if (robEscort == null)
            return;
        robEscort.addHurted(role.getPlayer(), this.fightResult);
        getEscortDao().updatePlayerEscort(robEscort);

        fightResult = FightDefine.FIGHT_RESULT_FAIL;

        //记录玩家劫镖日志
        LogUtil.log(role.getPlayer(), new Fun(LogFunType.ESCORT_ROB.getId(), 1));
    }

    /**
     * 押镖完成
     *
     * @param request
     */
    public void processComplete(Message request) {
        byte type = request.readByte();
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //立即完成
        if (type == 1) {
            int sec = getEscort().getEscortLeftSec();
            if (sec > 0) {
                int min = (int) Math.ceil(sec / 60.0d);
                DropData cost = new DropData(EGoodsType.DIAMOND, 0, min * 60);
                if (!role.getPackManager().useGoods(cost, EGoodsChangeType.ESCORT_COMPLETE_CONSUME, saves)) {
                    role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                    return;
                }
            }
            //记录完成状态
            getEscort().setArrive(1);
            getEscortDao().updatePlayerEscort(this.getEscort());
        }
        if (getEscort().getCargo() == 0 || !getEscort().isComplete()) {
            Message msgDetail = getEscortDetail();
            msgDetail.setChannel(request.getChannel());
            role.sendMessage(msgDetail);
            return;
        }
        //奖励信息
        List<DropData> rewards = balanceItemReward();
        Message msg = new Message(MessageCommand.ESCORT_COMPLETE, request.getChannel());
        msg.setByte(getEscort().getQuality());
        for (DropData reward : rewards) {
            msg.setInt(reward.getN());
        }
        msg.setByte(getEscort().getHurted());
        int size = 0;
        for (EscortLog log : getEscort().getLogs()) {
            if (size >= getEscort().getHurted())
                break;
            //被劫
            if (log.getT() == 2) {
                size++;
                log.getLogMsg(msg);
            }
        }
        role.sendMessage(msg);
    }

    /**
     * 劫镖车队列表
     *
     * @param request
     */
    public void processRobList(Message request) {
        //车队数量
        int robNum = 20;
        //刷新时间
        long curr = System.currentTimeMillis();
        this.refreshTime = curr;
        robPlayers.clear();
        Message msg = new Message(MessageCommand.ESCORT_ROBLIST_MESSAGE, request.getChannel());
        msg.setShort(robNum);
        //生成车队
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < robNum; i++) {
            Escort rob = getEscortDao().getRobEscortList(this.getEscort(), ids);
            EscortPlayer ep = null;
            //玩家数据
            if (rob != null) {
                ids.add(rob.getPlayerId());
                IGameRole iRole = GameWorld.getPtr().getGameRole(rob.getPlayerId());
                if (iRole != null) {
                    Player player = iRole.getPlayer();
                    if (player != null) {
                        if (player.getFighting() == 0)
                            player.updateFighting();
                        ep = new EscortPlayer(player);
                        ep.setStartTime(rob.getStartTime());
                        ep.setQuality(rob.getQuality());
                        ep.getAppear().init(player, -1);
                    }
                }
            }
            //机器人数据
            if (ep == null) {
                ep = new EscortPlayer();
                ep.setId(-1000 - i);
                ep.setName(GameCommon.getRandomName());
                Player self = role.getPlayer();
                ep.setRein(self.getRein());
                //等级
                int lv = self.getLevel() + GameUtil.getRangedRandom(-10, 5);
                if (lv < 1)
                    lv = 1;
                if (self.getRein() > 0 && lv < GameDefine.REIN_LV)
                    lv = GameDefine.REIN_LV;
                ep.setLevel((short) lv);
                ep.setVip((byte) 0);
                //机器人战斗，根据自身向下浮动25%
                long fighting = self.getFighting() + self.getFighting() * GameUtil.getRangedRandom(-25, -5) / 100;
                ep.setFighting(fighting);
                byte quality = 1;
                if (GameUtil.getRangedRandom(1, 100) <= 30)
                    quality = 2;
                ep.setQuality(quality);
                EscortModelData model = EscortModel.getEscortModel(quality);
                ep.setStartTime(curr - model.getKeeptime() * DateUtil.SECOND * GameUtil.getRangedRandom(70, 5) / 100);
            }
            ep.getEscortMessage(msg);
            robPlayers.put(ep.getId(), ep);
        }
        role.sendMessage(msg);
    }

    /**
     * 镖车刷新品质
     *
     * @param request
     */
    public void processRefreshQuality(Message request) {
        byte type = request.readByte();
        //最高品质的镖车不能再刷新了
        if (getEscort().getQuality() >= ConstantModel.ESCORT_STAR_MAX) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ESCORT_QUALITY_MAX);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //一键刷橙
        if (type == 1) {
            //元宝
            DropData cost = new DropData(EGoodsType.DIAMOND, 0, 3000);
            if (!role.getPackManager().useGoods(cost, EGoodsChangeType.ESCORT_REFRESH_CONSUME, saves)) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                return;
            }
            getEscort().setQuality(ConstantModel.ESCORT_STAR_MAX);
        } else {
//			//首次出橙
//			if (getEscort().getFlag() == 0)
//			{
//				getEscort().setFlag((byte)1);
//				getEscort().setQuality(ConstantModel.ESCORT_STAR_MAX);
//			}
//			else
            {
                //不在免费刷新次数内
                if (getEscort().getRefresh() >= ConstantModel.ESCORT_REFRESH_FREE) {
                    //刷新令
                    DropData cost = new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_REFRESH_FLAG, 1);
                    if (!role.getPackManager().useGoods(cost, EGoodsChangeType.ESCORT_REFRESH_CONSUME, saves)) {
                        //元宝
                        cost = new DropData(EGoodsType.DIAMOND, 0, 200);
                        if (!role.getPackManager().useGoods(cost, EGoodsChangeType.ESCORT_REFRESH_CONSUME, saves)) {
                            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                            return;
                        }
                    }
                }
                //随机品质
                int quality = EscortModel.getRandomModel();
                //品质不会向下掉
                if (quality > getEscort().getQuality())
                    getEscort().setQuality(quality);
                if (quality > getEscort().getQuality() && getEscort().getQuality() >= 4) {
                    ChatService.broadcastPlayerMsg(role.getPlayer(), EBroadcast.ESCORT);
                }
            }
            getEscort().addRefresh();
        }
        //发送消息
        Message msg = new Message(MessageCommand.ESCORT_REFRESH_QUALITY_MESSAGE, request.getChannel());
        msg.setByte(getEscort().getQuality());
        msg.setShort(getEscort().getRefresh());
        role.sendMessage(msg);
        //保存数据
        getEscortDao().updatePlayerEscort(getEscort());
    }

    public void processLogs(Message request) {
        Message msg = getEscortLogs();
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
        getEscort().setReaded((byte) 1);
        getEscortDao().updatePlayerEscortReaded(this.getEscort());
    }

    private Message getEscortLogs() {
        Message msg = new Message(MessageCommand.ESCORT_LOGS);
        msg.setShort(getEscort().getLogs().size());
        for (EscortLog log : getEscort().getLogs()) {
            log.getLogMsg(msg);
        }
        return msg;
    }

    /**
     * 领取渡劫奖励
     *
     * @param request
     */
    public void processReward(Message request) {
        if (!getEscort().isComplete()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (getEscort().getCargo() == 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        //奖励
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        List<DropData> rewards = balanceItemReward();
        role.getPackManager().addGoods(rewards, EGoodsChangeType.ESCORT_COMPLETE_ADD, saves);
        //清空货物
        getEscort().setCargo(0);
        getEscort().setArrive(0);
        //重置品质
        getEscort().setQuality(1);
        if (GameUtil.getRangedRandom(1, 100) <= 30)
            getEscort().setQuality(2);
        //消息
        role.putMessageQueue(getEscortDetail());
        Message msg = new Message(MessageCommand.ESCORT_REWARD_MESSAGE, request.getChannel());
        msg.setByte(1);
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        getEscortDao().updatePlayerEscort(this.getEscort());

        //记录玩家押镖日志
        LogUtil.log(role.getPlayer(), new Fun(LogFunType.ESCORT_COMP.getId(), 1));
    }

    public void processRevengeInfo(Message request) {
        int playerId = request.readInt();
        int time = request.readInt();
        EscortLog revenge = null;
        for (EscortLog log : getEscort().getLogs()) {
            if (log.getId() == playerId && log.getS() / 1000 == time) {
                revenge = log;
                break;
            }
        }
        if (revenge == null || revenge.getT() != 2) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (revenge.getRv() == 1) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ESCORT_REVENGE_REPEAT);
            return;
        }
        Message msg = new Message(MessageCommand.ESCORT_REVENGE_INFO, request.getChannel());
        //发送玩家战斗消息
        msg.setBool(false);
        IGameRole gr = GameWorld.getPtr().getGameRole(playerId);
        if (gr != null) {
            if (gr.getPlayer().getFighting() == 0)
                gr.getPlayer().updateFighting();
            gr.getPlayer().getBaseSimpleMessage(msg);
            gr.getPlayer().getAppearMessage(msg);
            gr.getPlayer().getAttrFighting(msg);
        }
        role.sendMessage(msg);
    }

    public void processRevengeResult(Message request) {
        int playerId = request.readInt();
        int time = request.readInt();
        byte result = request.readByte();
        EscortLog revenge = null;
        for (EscortLog log : getEscort().getLogs()) {
            if (log.getId() == playerId && log.getS() / 1000 == time) {
                revenge = log;
                break;
            }
        }
        if (revenge == null /**|| revenge.getT() != 2*/) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (revenge.getRv() == 1) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ESCORT_REVENGE_REPEAT);
            return;
        }
        revenge.setRv((byte) 1);
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (result == 1) {
            //奖励
            EscortModelData model = EscortModel.getEscortModel(revenge.getQ());
            List<DropData> rewards = new ArrayList<>();
            for (DropData data : model.getReward()) {
                rewards.add(new DropData(data.getT(), data.getG(), data.getN() / ConstantModel.ESCORT_REWARD * 2));
            }
            role.getPackManager().addGoods(rewards, EGoodsChangeType.ESCORT_REVENGE_ADD, saves);
            //跑马灯
            //ChatService.broadcastPlayerMsg(role.getPlayer(), EBroadcast.DUJIE_FUCHOU, revenge.getM());
        }
        //消息
        role.putMessageQueue(getEscortLogs());
        Message msg = new Message(MessageCommand.ESCORT_REVENGE_RESULT, request.getChannel());
        msg.setByte(result);
        msg.setByte(revenge.getQ());
        role.sendMessage(msg);
        //保存数据
        getEscortDao().updatePlayerEscort(this.getEscort());
        if (saves.size() > 0)
            role.savePlayer(saves);
    }

    /**
     * 添加押镖次数
     */
    public void addCountTimes(int addNum) {
        this.getEscort().setCount(this.getEscort().getCount() - addNum);
        getEscortDao().updatePlayerEscort(this.getEscort());
    }

    /**
     * 添加劫镖次数
     */
    public void addRobTimes(int addNum) {
        this.getEscort().setRob(this.getEscort().getRob() - addNum);
        getEscortDao().updatePlayerEscort(this.getEscort());
    }
}
