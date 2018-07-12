package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.ladder.LadderRankInfo;
import com.rd.bean.ladder.PlayerLadder;
import com.rd.bean.player.Player;
import com.rd.bean.player.SimplePlayer;
import com.rd.common.ChatService;
import com.rd.common.GameCommon;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.*;
import com.rd.define.LadderDefine.LadderRGS;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.lg.bean.game.Fun;
import com.rd.model.ConstantModel;
import com.rd.model.LadderModel;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.GameUtil;
import com.rd.util.LogUtil;
import org.apache.log4j.Logger;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 天梯竞技场管理器
 *
 * @author Created by U-Demon on 2016年10月17日 下午7:17:56
 * @version 1.0.0
 */
public class LadderManager {

    private static Logger logger = Logger.getLogger(LadderManager.class);

    //角色
    private GameRole gameRole;

    //天梯数据
    private PlayerLadder _ladder;

    private byte fightResult = FightDefine.FIGHT_RESULT_FAIL;

    public LadderManager(GameRole gameRole) {
        this.gameRole = gameRole;
    }

    //初始化天梯数据
    private void init() {
        this._ladder = gameRole.getDbManager().ladderDao.getPlayerLadder(gameRole);
    }

    private PlayerLadder getLadder() {
        if (null == _ladder) {
            init();
        }
        return _ladder;
    }

    /**
     * 获取战斗次数的上限
     *
     * @return
     */
    public int getFightCountMax() {
        return ConstantModel.LADDER_FIGHT_MAX;
    }

    /**
     * 获取剩余战斗次数
     *
     * @return
     */
    public int getFightCount() {
        long curr = System.currentTimeMillis();
        //次数上限
        int max = getFightCountMax();
        if (getLadder().getCount() >= max) {
            //添加天梯挑战令，可超上限
            getLadder().setRecoverTime(curr);
            return getLadder().getCount();
        }
        //计算恢复
        int recover = (int) ((curr - getLadder().getRecoverTime()) / (ConstantModel.LADDER_FIGHT_RECOVE_TIME * 1000));
        if (recover <= 0) {
            return getLadder().getCount();
        }
        int count = getLadder().getCount() + recover;
        if (count >= max) {
            getLadder().setCount(max);
            getLadder().setRecoverTime(curr);
            return getLadder().getCount();
        } else {
            getLadder().setCount(count);
            getLadder().setRecoverTime(getLadder().getRecoverTime() + recover * ConstantModel.LADDER_FIGHT_RECOVE_TIME * 1000);
            return getLadder().getCount();
        }
    }

    /**
     * 天梯详情
     *
     * @param request
     */
    public void processLadderDetail(Message request) {
        sendLadderDetail();
        gameRole.sendTick(request);
    }

    public void sendLadderDetail() {
        Message message = new Message(MessageCommand.LADDER_DETAIL_MESSAGE);
        message.setInt(getLadder().getStar());
        message.setInt(getLadder().getGoal());
        message.setInt(getFightCount());
        message.setInt(getFightCountMax());
        message.setInt(getLeftRecoverSecond());
        message.setInt(getLadder().getBuyCount());
        long curr = System.currentTimeMillis();
        //赛季结束、开始剩余秒数
        if (curr >= LadderModel.SEASON_OPEN_TIME && curr < LadderModel.SEASON_CLOSE_TIME) {
            message.setByte(0);
            message.setInt((int) ((LadderModel.SEASON_CLOSE_TIME - curr) / 1000));
        } else if (curr < LadderModel.SEASON_OPEN_TIME) {
            message.setByte(1);
            message.setInt((int) ((LadderModel.SEASON_OPEN_TIME - curr) / 1000));
        } else {
            message.setByte(1);
            message.setInt((int) ((LadderModel.SEASON_OPEN_TIME + LadderModel.LAST_TIME + LadderModel.START_TIME
                    - curr) / 1000));
        }
        gameRole.putMessageQueue(message);
    }

    /**
     * 天梯匹配
     *
     * @param request
     */
    public void processLadderMatch(Message request) {
        if (gameRole.getCheatManager().requestFrequent(request)) {
            return;
        }
        if (gameRole.getPlayer().getLevel() < ConstantModel.LADDER_OPEN_LV) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LADDER_LV);
            return;
        }
        //天梯是否在开放时间
        if (LadderModel.SEASON_OPEN_TIME == 0 || LadderModel.SEASON_CLOSE_TIME == 0)
            LadderModel.refreshSeasonTime();
        long curr = System.currentTimeMillis();
        if (curr >= LadderModel.SEASON_CLOSE_TIME || curr < LadderModel.SEASON_OPEN_TIME) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LADDER_CLOSE);
            return;
        }
        //首先处理未结算的战斗
        if (getLadder().getWin() + getLadder().getLose() < getLadder().getTotal()) {
            handlerLadderFight((byte) -9);
//			sendLadderDetail();
        }
        //判断当前地图类型
        if (gameRole.getFightManager().inInstance()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_COPY_ALREADY_IN);
            return;
        }
        //判断次数
        if (getFightCount() <= 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LADDER_COUNT_LESS);
            return;
        }
        //扣除次数
        getLadder().reduceCount();
        //总次数增加
        getLadder().addTotal();
        //gameRole.getPlayer().setMapType(GameDefine.MAP_TYPE_LADDER);
        //第一次匹配机器人
        if (firstLadderRobot(request)) {
            this.fightResult = FightDefine.FIGHT_RESULT_SUCCESS;
            return;
        }
        //通过星级匹配对手
        PlayerLadder pl = getMatchPlayer();
        //星级匹配失败，匹配战斗力
//		int low = gameRole.getPlayer().getFighting() * ConstantModel.LADDER_MATCH_MIN_FIGHT / 100;
//		int high = gameRole.getPlayer().getFighting() * ConstantModel.LADDER_MATCH_MAX_FIGHT / 100;
//		if (pl == null)
//		{
//			ArrayList<Integer> excludes = new ArrayList<>(ladder.getFightIds());
//			excludes.add(gameRole.getPlayer().getId());
//			SimplePlayer sp = gameRole.getDbManager().playerDao.getRandomPlayerByFighting(excludes, low, high);
//			if (sp != null)
//			{
//				pl = gameRole.getDbManager().ladderDao.getPlayerLadder(sp.getPlayerId());
//			}
//		}
        getLadder().setMatchId((int) (System.currentTimeMillis() / 1000));
        //记录匹配过的真人，一定时间内不能再匹配
//		if (pl != null)
//			ladder.getFightIds().add(pl.getPlayerId());
        //发送消息
        Message message = new Message(MessageCommand.LADDER_MATCH_MESSAGE, request.getChannel());
        boolean robot = true;
        //玩家
        if (pl != null) {
            //发送玩家战斗消息
            IGameRole role = GameWorld.getPtr().getGameRole(pl.getPlayerId());
            if (role != null && role.getPlayer().getState() == GameDefine.PLAYER_STATE_NORMAL) {
                robot = false;
                message.setInt(pl.getStar());
                message.setBool(false);
                role.getPlayer().updateFighting();
                role.getPlayer().getBaseSimpleMessage(message);
                role.getPlayer().getAppearMessage(message);
                role.getPlayer().getAttrFighting(message);
            }
        }
        //机器人
        if (robot) {
            //机器人星级
            LadderRGS rgs = LadderModel.getRGS(getLadder().getStar());
            int starMin = getLadder().getStar() - rgs.star - ConstantModel.LADDER_MATCH_MIN_STAR;
            int starMax = starMin + ConstantModel.LADDER_MATCH_MAX_STAR;
            if (starMax > LadderModel.MAX_STAR)
                starMax = LadderModel.MAX_STAR;
            message.setInt(GameUtil.getRangedRandom(starMin, starMax));
            message.setBool(true);
            //机器人ID
            message.setInt(-1000);
            //名字
            message.setString(GameCommon.getRandomName());
            //获取机器人战斗力的百分比
            float rate = getRobotFightingRate();
            long robotFight = (long) (gameRole.getPlayer().getFighting() * rate);
            message.setLong(robotFight);
            //获取机器人属性
//			Map<Byte, Integer> attrs = getRobotAttrs(robotFight);
//			message.setByte(attrs.size());
//			for (Entry<Byte, Integer> attr : attrs.entrySet())
//			{
//				message.setByte(attr.getKey());
//				message.setInt(attr.getValue());
//			}
        }
        gameRole.sendMessage(message);
        //更新数据
        if (gameRole.getDbManager().ladderDao.updatePlayerLadder(getLadder()) == -1) {
            logger.error("更新角色的天梯数据发生异常。角色ID：" + gameRole.getPlayer().getId());
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }

        if (pl != null) {
//			IGameRole role = GameWorld.getPtr().getGameRole(pl.getPlayerId());
            this.fightResult = FightDefine.FIGHT_RESULT_SUCCESS;
        } else {
            this.fightResult = FightDefine.FIGHT_RESULT_SUCCESS;
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        GameEvent event = new GameEvent(EGameEventType.LADDER_MATCH_COUNT, 1, enumSet);
        gameRole.getEventManager().notifyEvent(event);
        gameRole.savePlayer(enumSet);
    }

    private PlayerLadder getMatchPlayer() {
        //要匹配的星级范围
        LadderRGS rgs = LadderModel.getRGS(getLadder().getStar());
        PlayerLadder pl = null;
        //本段位找
        int starMin = LadderModel.getRankMinStar(rgs.rank);
        int starMax = LadderModel.getRankMinStar(rgs.rank + 1) - 1;
        pl = gameRole.getDbManager().ladderDao.getMatchPlayerByStar(getLadder(), starMin, starMax);
        //低一段位找
        if (pl == null) {
            starMin = LadderModel.getRankMinStar(rgs.rank - 1);
            starMax = LadderModel.getRankMinStar(rgs.rank) - 1;
            pl = gameRole.getDbManager().ladderDao.getMatchPlayerByStar(getLadder(), starMin, starMax);
        }
        //高一段位找
        if (pl == null) {
            starMin = LadderModel.getRankMinStar(rgs.rank + 1);
            starMax = LadderModel.getRankMinStar(rgs.rank + 2) - 1;
            pl = gameRole.getDbManager().ladderDao.getMatchPlayerByStar(getLadder(), starMin, starMax);
        }
        //所有段位找
        if (pl == null) {
            pl = gameRole.getDbManager().ladderDao.getMatchPlayerByStar(getLadder(), 0, LadderModel.MAX_STAR);
        }
        return pl;
    }

    private boolean firstLadderRobot(Message request) {
        if (gameRole.getPlayer().getLevel() > 60 || getLadder().getTotal() > 1)
            return false;
        getLadder().setMatchId(1001);

        //发送消息
        Message message = new Message(MessageCommand.LADDER_MATCH_MESSAGE, request.getChannel());
//		message.setInt(ladder.getMatchId());
        //机器人星级
        LadderRGS rgs = LadderModel.getRGS(getLadder().getStar());
        int starMin = getLadder().getStar() - rgs.star - ConstantModel.LADDER_MATCH_MIN_STAR;
        int starMax = starMin + ConstantModel.LADDER_MATCH_MAX_STAR;
        if (starMax > LadderModel.MAX_STAR)
            starMax = LadderModel.MAX_STAR;
        message.setInt(GameUtil.getRangedRandom(starMin, starMax));
        message.setBool(true);
        //机器人ID
        message.setInt(-1000);
        //名字
        message.setString(GameCommon.getRandomName());
        //首次战斗，机器人战斗力的百分比
        float rate = 0.5f;
        long robotFight = (long) (gameRole.getPlayer().getFighting() * rate);
        message.setLong(robotFight);
        gameRole.sendMessage(message);
        //更新数据
        if (gameRole.getDbManager().ladderDao.updatePlayerLadder(getLadder()) == -1) {
            logger.error("更新角色的天梯数据发生异常。角色ID：" + gameRole.getPlayer().getId());
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }
        return true;
    }

    /**
     * 天梯战斗结果
     *
     * @param request
     */
    public void processLadderResult(Message request) {
        //结算奖励到开放时间期间不处理结果数据
        if (LadderModel.SEASON_REWARD_TIME == 0 || LadderModel.SEASON_OPEN_TIME == 0)
            LadderModel.refreshSeasonTime();
        long curr = System.currentTimeMillis();
        if (curr >= LadderModel.SEASON_REWARD_TIME || curr < LadderModel.SEASON_OPEN_TIME) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LADDER_CLOSE);
            return;
        }
        byte result = request.readByte();
        //gameRole.getPlayer().setMapType(GameDefine.MAP_TYPE_NORMAL);
        //战斗场次不能超
        if (getLadder().getWin() + getLadder().getLose() >= getLadder().getTotal()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        //客户端如果失败 服务器也算作失败 平局同样
        if (FightDefine.FIGHT_RESULT_FAIL == result || FightDefine.FIGHT_RESULT_TIE == result) {
            this.fightResult = result;
        }
        //服务器验证失败
        if (FightDefine.FIGHT_RESULT_FAIL == this.fightResult && FightDefine.FIGHT_RESULT_SUCCESS == result) {
            gameRole.putErrorMessage(ErrorDefine.ERROR_SERVER_FIGHT_FAIL);
        }
        int oldHonor = gameRole.getPlayer().getHonor();
        int oldStar = getLadder().getStar();
        handlerLadderFight(this.fightResult);
        int rank = LadderModel.getRGS(getLadder().getStar()).rank;

        sendLadderDetail();
        Message message = new Message(MessageCommand.LADDER_RESULT_MESSAGE, request.getChannel());
        message.setByte(this.fightResult);
        message.setByte(getLadder().getStar() - oldStar);
        message.setInt(getLadder().getConwin());
        message.setInt(gameRole.getPlayer().getHonor() - oldHonor);
        gameRole.sendMessage(message);
        //更新数据
        if (gameRole.getDbManager().ladderDao.updatePlayerLadder(getLadder()) == -1) {
            logger.error("更新角色的天梯数据发生异常。角色ID：" + gameRole.getPlayer().getId());
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }

        //记录玩家天梯日志
        LogUtil.log(gameRole.getPlayer(), new Fun(LogFunType.LADDER.getId(), 1));
        fightResult = FightDefine.FIGHT_RESULT_FAIL;
        //跑马灯
        if (rank == 3 && LadderModel.getRGS(oldStar).rank == 2)
            ChatService.broadcastPlayerMsg(gameRole.getPlayer(), EBroadcast.LADDER_GOLD);
        else if (rank == 4 && LadderModel.getRGS(oldStar).rank == 3)
            ChatService.broadcastPlayerMsg(gameRole.getPlayer(), EBroadcast.LADDER_DIAMOND);

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.LADDER_MATCH, getLadder().getStar(), enumSet));
        gameRole.savePlayer(enumSet);
    }

    @SuppressWarnings("unused")
    private void handlerLadderFight(byte result) {
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        //奖励星数
        int rewardStar = 0;
        LadderRGS oldRgs = LadderModel.getRGS(getLadder().getStar());
        //掉线不计场次
        if (LadderDefine.LADDER_DISCONNECT && result == -9) {
            getLadder().addCount();
            getLadder().reduceTotal();
        }
        //胜利
        else if (result == 1) {
            //增加胜利场次
            getLadder().addWin();
            getLadder().addConWin();
            rewardStar = 1;
            //处理连胜，黄金段位开始没有连胜
            if (getLadder().getStar() < ConstantModel.LADDER_UNCONWIN && getLadder().getConwin() > LadderDefine.LADDER_CONWIN_NUM)
                rewardStar = LadderDefine.LADDER_CONWIN_STAR;
            this.getLadder().addScore(rewardStar * 1000);
            //胜利奖励
            gameRole.getPackManager().addGoods(LadderModel.getFightReward(oldRgs.rank).getWinReward(),
                    EGoodsChangeType.LADDER_FIGHT_REWARD, enumSet);
        }
        //失败
        else {
            getLadder().addLose();
            //请空连胜
            getLadder().setConwin(0);
            this.getLadder().addScore(-1);
            //黄金段位才开始降星
            if (this.getLadder().getStar() >= ConstantModel.LADDER_SUBSTAR)
                rewardStar = -1;
            //失败奖励
            gameRole.getPackManager().addGoods(LadderModel.getFightReward(oldRgs.rank).getLostReward(),
                    EGoodsChangeType.LADDER_FIGHT_REWARD, enumSet);
        }
        //改变星级
        changeLadderStar(rewardStar);
//		LadderRGS newRgs = LadderModel.getRGS(ladder.getStar());
        //跨段奖励
//		if (newRgs.rank > oldRgs.rank)
//		{
//			gameRole.getPackManager().addGoods(LadderModel.getFightReward(newRgs.rank).getFirReward(), 
//					EGoodsChangeType.LADDER_FIGHT_REWARD,enumSet);
//		}
        //重置战斗ID
        getLadder().setMatchId(0);
        //重新计算排行榜
        LadderRankInfo myself = new LadderRankInfo();
        myself.init(gameRole.getPlayer());
        myself.initTop(getLadder());
        LadderModel.resetTopList(myself);

        gameRole.savePlayer(enumSet);
    }

    /**
     * 购买天梯次数
     *
     * @param request
     */
    public void processLadderBuy(Message request) {
        //天梯是否在开放时间
        if (LadderModel.SEASON_OPEN_TIME == 0 || LadderModel.SEASON_CLOSE_TIME == 0)
            LadderModel.refreshSeasonTime();
        long curr = System.currentTimeMillis();
        if (curr >= LadderModel.SEASON_CLOSE_TIME || curr < LadderModel.SEASON_OPEN_TIME) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LADDER_CLOSE);
            return;
        }
        //次数已达上限不能购买
        if (getFightCount() >= getFightCountMax()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LADDER_COUNT_FULL);
            return;
        }
        //购买次数上限
        if (getLadder().getBuyCount() >= ConstantModel.LADDER_FIGHT_BUY_PRICE.size()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LADDER_BUY_COUNT_FULL);
            return;
        }
        //购买消耗
        int cost = ConstantModel.LADDER_FIGHT_BUY_PRICE.get(ConstantModel.LADDER_FIGHT_BUY_PRICE.size() - 1);
        if (getLadder().getBuyCount() < ConstantModel.LADDER_FIGHT_BUY_PRICE.size())
            cost = ConstantModel.LADDER_FIGHT_BUY_PRICE.get(getLadder().getBuyCount());
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(new DropData(EGoodsType.DIAMOND, 0, cost),
                EGoodsChangeType.LADDER_BUY_COUNT_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        //增加次数
        getLadder().addCount();
        getLadder().addBuyCount();
        Message message = new Message(MessageCommand.LADDER_BUY_COUNT, request.getChannel());
        message.setInt(getFightCount());
        message.setInt(getLeftRecoverSecond());
        message.setInt(getLadder().getBuyCount());
        gameRole.sendMessage(message);
        gameRole.savePlayer(saves);
        //更新数据
        if (gameRole.getDbManager().ladderDao.updatePlayerLadder(getLadder()) == -1) {
            logger.error("更新角色的天梯数据发生异常。角色ID：" + gameRole.getPlayer().getId());
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }
    }

    /**
     * 天梯排行榜消息
     *
     * @param request
     */
    public void processLadderTopList(Message request) {
        Message msg = new Message(MessageCommand.LADDER_TOP_LIST, request.getChannel());
        //这里由于请求可能会比较频繁，就不叫锁了，所以可能因为同步问题造成获取榜单失败
        try {
            int size = LadderModel.topList.size();
            if (size > ConstantModel.LADDER_TOP_NUM)
                size = ConstantModel.LADDER_TOP_NUM;
            msg.setByte(size + 1);
            getTopMessage(msg);

            Iterator<LadderRankInfo> ite = LadderModel.topList.iterator();
            int i = 0;
            while (ite.hasNext()) {
                i++;
                LadderRankInfo rankInfo = ite.next();
                if (i > ConstantModel.LADDER_TOP_NUM)
                    break;
                rankInfo.setRank(i);
                rankInfo.getMessage(msg);
            }
            gameRole.sendMessage(msg);
        } catch (Exception e) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LADDER_TOPLIST);
        }
    }

    /**
     * 天梯历史战绩消息
     *
     * @param request
     */
    public void processLadderHistory(Message request) {
//		Message msg = new Message(MessageCommand.LADDER_HISTORY, request.getChannel());
//        msg.setByte(LadderModel.history.size() + 1);
//        getHisMessage(msg);
//        for (LadderRankInfo rankInfo : LadderModel.history){
//        	rankInfo.getMessage(msg);
//        }
//        gameRole.sendMessage(msg);
    }

    public void getTopMessage(Message msg) {
        Player player = gameRole.getPlayer();
        SimplePlayer sp = new SimplePlayer();
        sp.init(player);
        sp.getSimpleMessage(msg);
        boolean has = false;
        int i = 0;
        Iterator<LadderRankInfo> ite = LadderModel.topList.iterator();
        while (ite.hasNext()) {
            i++;
            LadderRankInfo rankInfo = ite.next();
            if (rankInfo.getId() == player.getId()) {
                has = true;
                break;
            }
        }
        if (!has)
            i = gameRole.getDbManager().ladderDao.getCurrLadderRank(getLadder().getScore());
        msg.setInt(i);
        msg.setInt(getLadder().getStar());
        msg.setInt(getLadder().getTotal());
        msg.setInt(getLadder().getGoal());
    }

    public void getHisMessage(Message msg) {
        Player player = gameRole.getPlayer();
        msg.setInt(player.getId());
        msg.setString(player.getName());

        msg.setInt(player.getLevel());
        msg.setInt(player.getVip());
        msg.setInt((int) player.getFighting());
        int lastRank = getLadder().getLastRank();
        if (lastRank <= 0) {
            init();
            lastRank = getLadder().getLastRank();
        }
        msg.setInt(lastRank);
        msg.setInt(getLadder().getLastStar());
        msg.setInt(getLadder().getLastTotal());
        msg.setInt(getLadder().getLastWin());
    }

    /**
     * 改变天梯星级
     *
     * @param star
     * @return
     */
    public void changeLadderStar(int star) {
        //增加
        if (star > 0) {
            int result = this.getLadder().getStar() + star;
            if (result > LadderModel.MAX_STAR)
                result = LadderModel.MAX_STAR;
            this.getLadder().setStar(result);
        }
        //减少
        else if (star < 0) {
            //当前段位
            LadderRGS currRgs = LadderModel.getRGS(this.getLadder().getStar());
            int result = this.getLadder().getStar() + star;
            int score = star * 900;
            //钻石以下不能掉段
            if (currRgs.rank < 4) {
                //变化后的段位
                LadderRGS newRgs = LadderModel.getRGS(this.getLadder().getStar() + star);
                //不能掉段
                if (newRgs == null || newRgs.rank != currRgs.rank) {
                    result = this.getLadder().getStar() - currRgs.star;
                    score = -1;
                }
            }
            this.getLadder().setStar(result);
            this.getLadder().addScore(score);
        }
    }

    /**
     * 获取恢复次数的剩余秒数
     *
     * @return
     */
    public int getLeftRecoverSecond() {
        if (getFightCount() >= getFightCountMax())
            return -1;
        else
            return (int) ((getLadder().getRecoverTime() + ConstantModel.LADDER_FIGHT_RECOVE_TIME * 1000
                    - System.currentTimeMillis()) / 1000);
    }

    /**
     * 每日零点重置数据
     */
    public void resetLadder() {
        getLadder().setRecoverTime(0);
        getLadder().setBuyCount(0);
        getLadder().getFightIds().clear();
    }

    /**
     * 赛季重置
     */
    public void resetSeasonLadder() {
        getLadder().setStar(0);
        getLadder().setTotal(0);
        getLadder().setWin(0);
        getLadder().setLose(0);
        getLadder().setConwin(0);
        getLadder().setScore(0);
        getLadder().getFightIds().clear();
    }

    /**
     * 获取机器人战斗力相对自己的百分比
     *
     * @return
     */
    public static float getRobotFightingRate() {
        //随机机器人战斗力
        //第一位：随机几率百分比，第二位：战斗力区间百分比最小值，第三位：战斗力区间百分比最大值
        int[][] fightRates = {{60, 80, 95}, {40, 95, 150}};
        int random = GameUtil.getRangedRandom(1, 100);
        int total = 0;
        for (int i = 0; i < fightRates.length; i++) {
            total += fightRates[i][0];
            if (random <= total) {
                return GameUtil.getRangedRandom(fightRates[i][1], fightRates[i][2]) / 100.0f;
            }
        }
        return fightRates[fightRates.length - 1][2] / 100.0f;
    }

    /**
     * 根据战斗力构造机器人的属性
     * 2/5的战斗力转换成攻击，2/5转换为生命，1/5的转化为防御，暂定其他6个属性不转，每点战斗力转化属性读战斗力表，算出小数向下取整
     *
     * @param fighting
     * @return
     */
    public static Map<Byte, Integer> getRobotAttrs(int fighting) {
        //生命战斗力
        int hpFight = (int) (fighting * 0.9 * 2 / 5);
        //生命
        int hp = (int) (hpFight / EAttrType.HP.getFactor());
        //攻击战斗力
        int atkFight = (int) (fighting * 0.9 * 2 / 5);
        //攻击
        int atk = (int) (atkFight / EAttrType.ATTACK.getFactor());
        //防御战斗力
        int defFight = (int) (fighting * 0.9 * 1 / 5);
        //防御

        Map<Byte, Integer> attrs = new HashMap<>();
        attrs.put((byte) EAttrType.HP.getId(), hp);
        attrs.put((byte) EAttrType.ATTACK.getId(), atk);

        return attrs;
    }

    /**
     * 添加天梯挑战次数
     */
    public void addCountTimes(int addNum) {
        if (this.getLadder() == null) {
            this.init();
        }
        this.getLadder().setCount(this.getLadder().getCount() + addNum);
        gameRole.getDbManager().ladderDao.updatePlayerLadder(getLadder());
    }

    public int getStar() {
        int star = 0;
        if (this.getLadder() != null) {
            star = this.getLadder().getStar();
        }
        return star;
    }
}
