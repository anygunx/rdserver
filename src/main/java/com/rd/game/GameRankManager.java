package com.rd.game;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.RankLogicData;
import com.rd.activity.data.TargetConsumeDaillyCumulateLogicData;
import com.rd.activity.data.TargetLogicData;
import com.rd.activity.data.XunBaoRankLogicData;
import com.rd.activity.event.TargetDailyConsumeCumulateEvent;
import com.rd.activity.event.TargetEvent;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.AppearPlayer;
import com.rd.bean.player.Player;
import com.rd.bean.player.PlayerActivity;
import com.rd.bean.rank.ActivityRank;
import com.rd.bean.rank.PlayerRank;
import com.rd.common.GameCommon;
import com.rd.common.MailService;
import com.rd.dao.ActivityDao;
import com.rd.dao.GlobalDao;
import com.rd.dao.PlayerDao;
import com.rd.define.EActivityRankType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ERankType;
import com.lg.bean.game.Rank;
import com.rd.model.ConstantModel;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import com.rd.util.GameUtil;
import com.rd.util.LogUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 游戏排行榜
 *
 * @author Created by U-Demon on 2016年12月14日 上午11:33:05
 * @version 1.0.0
 */
public class GameRankManager {

    private static Logger logger = Logger.getLogger(GameRankManager.class);

    private static final GameRankManager _instance = new GameRankManager();

    private GameRankManager() {
    }

    public static GameRankManager getInstance() {
        return _instance;
    }

    //排行榜数据
    private volatile Map<ERankType, List<PlayerRank>> gameRanks;
    //第一名外形数据
    private volatile Map<String, AppearPlayer> gameAppears = new HashMap<>();

    //玩家的相应类型排行
    private volatile Map<Integer, Map<ERankType, Integer>> playerRanks = new HashMap<>();

    //达标活动排行榜
    private volatile Map<Integer, List<ActivityRank>> targetRanks = new HashMap<>();
    private List<ActivityRank> historyTargetRanks;

    //节日达标活动排行榜
    private volatile List<ActivityRank> festRanks = new ArrayList<>();
    private List<ActivityRank> historyFestRanks;
    private int festMin = 0;
    private ReentrantLock festLock = new ReentrantLock();
    //节日榜最大人数
    public static final int FEST_RANK_MAX = 20;
    public static final int FEST_VALUE_MIN = 10000;

    //周末充值达标活动排行榜
    private volatile List<ActivityRank> weekendRanks = new ArrayList<>();
    private List<ActivityRank> historyWeekendRanks;
    private int weekendMin = 0;
    private ReentrantLock weekendLock = new ReentrantLock();
    //节日榜最大人数
    public static final int WEEKEND_RANK_MAX = 20;
    public static final int WEEKEND_VALUE_MIN = 2000;

    //节日充值达标活动排行榜
    private volatile List<ActivityRank> festPayRanks = new ArrayList<>();
    private List<ActivityRank> historyFestPayRanks;
    private int festPayMin = 0;
    private ReentrantLock festPayLock = new ReentrantLock();

    //每日累计消费达标活动排行榜
    private volatile List<ActivityRank> targetConsumeRanks = new ArrayList<>();
    //每日累计消费达标活动历史排行榜
    private List<ActivityRank> historyTargetConsumeRanks;
    private ReentrantLock targetConsumeLock = new ReentrantLock();
    //节日充值达标榜最大人数
    public static final int FESTPAY_RANK_MAX = 20;
    public static final int FESTPAY_VALUE_MIN = 2000;

    //寻宝榜
    private volatile List<ActivityRank> xunbaoRanks = new ArrayList<>();
    private List<ActivityRank> historyXunbaoRanks;
    private int xunbaoMin = 0;
    private ReentrantLock xunbaoLock = new ReentrantLock();
    //寻宝榜最大人数
    public static final int XUNBAO_RANK_MAX = 20;

    //下次刷新时间
    private volatile long nextUpdateTime = -1;

    //排行榜数据
    private Map<Integer, Player> rankData;

    /**
     * 初始化排行榜管理器
     */
    public void init() {
        Map<Integer, Player> all = new PlayerDao().getAllPlayer();
        startRank(all.values());
        this.rankData = new HashMap<>();
        for (int id : this.playerRanks.keySet()) {
            addRank(all.get(id));
        }
        int round = -1;
        try {
            BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.TARGET);
            ActivityRoundConfig currRound = configData.getCurrRound(0, System.currentTimeMillis() - 3600000);
            if (currRound != null) {
                round = currRound.getRound();
                List<ActivityRank> list = targetRanks.get(round);
                for (ActivityRank activityRank : list) {
                    addRank(all.get(activityRank.getId()));
                }
            }
        } catch (NullPointerException e) {
            logger.error("达标排行榜数据为空!");
            throw new NullPointerException("达标排行榜数据为空!");
        }
    }

    public void addRank(Player player) {
        if (player != null) {
            this.rankData.put(player.getId(), player);
        }
    }

    public void startRank() {
        //	startRank(rankData.values());
    }

    /**
     * 排行榜开始排行
     */
    private void startRank(Collection<Player> data) {
        long currTime = System.currentTimeMillis();
        long nextTime = DateUtil.getLastSpaceTime(currTime, DateUtil.MINUTE * 30) + DateUtil.MINUTE * 30;
        if (nextTime - currTime < DateUtil.MINUTE * 15)
            nextTime += DateUtil.MINUTE * 30;
        if (nextTime != this.nextUpdateTime) {
            this.nextUpdateTime = nextTime;
            updateRank(data);
            updateTargetRank(data);
            updateTargetConsumeRank(data);
        }
        try {
            //更新跨服竞技场可参加玩家列表
            //ArenaGameService.onUpdateFighters();
        } catch (Exception e) {
            logger.error("更新可参战玩家发送错误", e);
        }
    }

    /**
     * 更新活动排行榜
     *
     * @param all
     */
    public void updateTargetRank(Collection<Player> all) {
        long curr = System.currentTimeMillis();
        //当前活动数据
        Map<String, TargetLogicData> model = ActivityService.getRoundData(EActivityType.TARGET, 0, curr);
        if (model == null)
            return;
        TargetLogicData logic = null;
        for (TargetLogicData data : model.values())
            logic = data;
        if (logic == null)
            return;
        //记录的排行榜
        Map<Integer, ActivityRank> history = GlobalDao.getInstance().getTargetTop();
        logger.info("活动排行榜开始计算...");
        List<ActivityRank> allRanks = new ArrayList<>();
        for (Player player : all) {
            ActivityRank rank = TargetEvent.getActivityRank(player, logic.getType());
//			if (logic.getType() != TargetEvent.TYPE_REIN &&
//					logic.getType() != TargetEvent.TYPE_FABAO && rank.getV1() <= 0)
//				continue;
            //在历史排行榜中是否有记录
            boolean isHis = false;
            ActivityRank hisRank = history.get(player.getId());
            if (hisRank != null) {
                if (rank.getV1() == hisRank.getV1() && rank.getV2() == hisRank.getV2()) {
                    rank.setM(hisRank.getM());
                    isHis = true;
                }
            }
            if (!isHis) {
                int pass = GameUtil.getRangedRandom(120000, (int) (28 * DateUtil.MINUTE));
                rank.setM(curr - pass);
            }
            allRanks.add(rank);
        }
        history.clear();
        Collections.sort(allRanks);
        List<ActivityRank> acRanks = new ArrayList<>();
        for (int i = 0; i < allRanks.size(); i++) {
            if (i >= 20)
                break;
            ActivityRank rank = allRanks.get(i);
            acRanks.add(rank);
            history.put(rank.getId(), rank);
        }
        targetRanks.put(logic.getRound(), acRanks);
        EActivityRankType rankType = EActivityRankType.getType(logic.getType());
        if (acRanks.size() > 0) {
            IGameRole gr = GameWorld.getPtr().getGameRole(acRanks.get(0).getId());
            Map<String, AppearPlayer> gameActivityAppears = GlobalDao.getInstance().getGameActivityAppears();
            if (gr != null) {
                Player player = gr.getPlayer();
                if (player != null) {
                    AppearPlayer appear = new AppearPlayer();
                    appear.init(player, -1);
                    gameActivityAppears.put(rankType.getId() + "", appear);
                }
            }
            GlobalDao.getInstance().setGameActivityAppears(gameActivityAppears);
            GlobalDao.getInstance().updateTargetTop();
            GlobalDao.getInstance().updateGameAppearPlayer();
            GlobalDao.getInstance().updateTargetRanks();
        }
        logger.info("GameRankManager.updateTargetRank() end currTime=" + DateUtil.formatDateTime(curr));
    }

    /**
     * 更新每日累计消费达标
     *
     * @param all
     */
    public void updateTargetConsumeRank(Collection<Player> all) {
        long curr = System.currentTimeMillis();
        //当前活动数据
        Map<String, TargetConsumeDaillyCumulateLogicData> model = ActivityService.getRoundData(EActivityType.TARGET_DAILY_CONSUME_CUMULATE, 0, curr);
        if (model == null)
            return;
        TargetConsumeDaillyCumulateLogicData logic = null;
        for (TargetConsumeDaillyCumulateLogicData data : model.values())
            logic = data;
        if (logic == null)
            return;
        //记录的排行榜
        Map<Integer, ActivityRank> history = GlobalDao.getInstance().getTargetTop();
        logger.info("每日累计消费活动排行榜开始计算...");
        List<ActivityRank> allRanks = new ArrayList<>();
        for (Player player : all) {
            ActivityRank rank = TargetDailyConsumeCumulateEvent.getActivityRank(player);
//			if (logic.getType() != TargetEvent.TYPE_REIN &&
//					logic.getType() != TargetEvent.TYPE_FABAO && rank.getV1() <= 0)
//				continue;
            //在历史排行榜中是否有记录
            boolean isHis = false;
            ActivityRank hisRank = history.get(player.getId());
            if (hisRank != null) {
                if (rank.getV1() == hisRank.getV1() && rank.getV2() == hisRank.getV2()) {
                    rank.setM(hisRank.getM());
                    isHis = true;
                }
            }
            if (!isHis) {
                int pass = GameUtil.getRangedRandom(120000, (int) (28 * DateUtil.MINUTE));
                rank.setM(curr - pass);
            }
            allRanks.add(rank);
        }
        history.clear();
        Collections.sort(allRanks);
        List<ActivityRank> acRanks = new ArrayList<>();
        for (int i = 0; i < allRanks.size(); i++) {
            if (i >= 20)
                break;
            ActivityRank rank = allRanks.get(i);
            if (rank.getV1() >= logic.getDabiao())
                acRanks.add(rank);
            history.put(rank.getId(), rank);
        }
        targetConsumeRanks = acRanks;
        GlobalDao.getInstance().updateTargetTop();
        logger.info("GameRankManager.updateTargetConsumeRank() end currTime=" + DateUtil.formatDateTime(curr));
    }

    /**
     * 获取活动有关外形
     *
     * @return
     */
    public void getActivityAppearsMessage(Message message, EActivityRankType type) {
        AppearPlayer appear = GlobalDao.getInstance().getGameActivityAppears().get(type.getId() + "");
        if (appear != null) {
            appear.getMessage(message);
        }
    }

    public void updateRank(Collection<Player> all) {
        logger.info("排行榜开始计算...");
        Map<ERankType, List<PlayerRank>> gRanks = new HashMap<>();
        Map<Integer, Map<ERankType, Integer>> pRanks = new HashMap<>();

        for (ERankType rankType : ERankType.values()) {
            gRanks.put(rankType, new ArrayList<PlayerRank>());
        }
        for (Player player : all) {
            player.updateFighting();

            //等级榜
            PlayerRank lvRank = new PlayerRank();
            lvRank.init(player);
            lvRank.setValue(player.getRein());
            lvRank.setValue2(player.getLevel());
            lvRank.setMatch(player.getExp());
            gRanks.get(ERankType.LEVEL).add(lvRank);

            //战力帮
            PlayerRank fightRank = new PlayerRank();
            fightRank.init(player);
            fightRank.setValue(player.getFighting());
            gRanks.get(ERankType.FIGHTING).add(fightRank);

            //仙羽排行榜
            int horseValue = 0;
            int horseStar = 0;
//			for (Character cha : player.getCharacterList()) {
//				horseValue += cha.getHorseFighting();
//				horseStar += cha.getMountStage() * 10 + cha.getMountStar();
//			}
            if (horseValue > 0) {
                PlayerRank horseRank = new PlayerRank();
                horseRank.init(player);
                horseRank.setValue(horseValue);
                horseRank.setValue2(horseStar);
                horseRank.setMatch(player.getSmallData().getHorseTime());
                gRanks.get(ERankType.WING).add(horseRank);
            }

            //官阶威望
            PlayerRank guanJieRank = new PlayerRank();
            guanJieRank.init(player);
            guanJieRank.setValue(player.getWeiWang());
            gRanks.get(ERankType.WEIWANG).add(guanJieRank);
        }
        for (ERankType rankType : ERankType.values()) {
            List<PlayerRank> rankList = gRanks.get(rankType);
            Collections.sort(rankList);
            int i = 1;
            for (PlayerRank rank : rankList) {
                int playerId = rank.getId();
                rank.setRank(i);
                Map<ERankType, Integer> playerRankMap = pRanks.get(playerId);
                if (playerRankMap == null) {
                    playerRankMap = new HashMap<>();
                    pRanks.put(playerId, playerRankMap);
                }
                playerRankMap.put(rankType, rank.getRank());
                if (i > ConstantModel.RANK_CAPACITY) {
                    break;
                }
                if (rankType == ERankType.LEVEL) {            //称号：等级榜
                    if (i == 1)
                        GameCommon.grantTitle(playerId, 1);
                    else if (i <= 10)
                        GameCommon.grantTitle(playerId, 2);
                } else if (rankType == ERankType.FIGHTING) {    //称号：战力榜
                    if (i == 1)
                        GameCommon.grantTitle(playerId, 3);
                    else if (i <= 10)
                        GameCommon.grantTitle(playerId, 4);
                } else if (rankType == ERankType.WING) {        //称号：仙羽榜
                    if (i == 1)
                        GameCommon.grantTitle(playerId, 5);
                    else if (i <= 10)
                        GameCommon.grantTitle(playerId, 6);
                } else if (rankType == ERankType.WEIWANG) {    //称号：官职榜
                    if (i == 1)
                        GameCommon.grantTitle(playerId, 7);
                    else if (i <= 10)
                        GameCommon.grantTitle(playerId, 8);
                }
                i++;
            }
            gRanks.put(rankType, rankList.size() <= ConstantModel.RANK_CAPACITY ?
                    rankList : rankList.subList(0, ConstantModel.RANK_CAPACITY));
            //第一名外形
            if (rankList != null && rankList.size() > 0) {
                if (rankType == ERankType.WEIWANG) {
                    IGameRole gr = GameWorld.getPtr().getGameRole(rankList.get(0).getId());
                    if (gr != null) {
                        Player player = gr.getPlayer();
                        if (player != null) {
                            AppearPlayer appear = new AppearPlayer();
                            appear.init(player, -1);
                            gameAppears.put(rankType.getId() + "_0", appear);
                        }
                    }
                    if (rankList.size() > 1) {
                        gr = GameWorld.getPtr().getGameRole(rankList.get(1).getId());
                        if (gr != null) {
                            Player player = gr.getPlayer();
                            if (player != null) {
                                AppearPlayer appear = new AppearPlayer();
                                appear.init(player, -1);
                                gameAppears.put(rankType.getId() + "_1", appear);
                            }
                        }
                    }
                    if (rankList.size() > 2) {
                        gr = GameWorld.getPtr().getGameRole(rankList.get(2).getId());
                        if (gr != null) {
                            Player player = gr.getPlayer();
                            if (player != null) {
                                AppearPlayer appear = new AppearPlayer();
                                appear.init(player, -1);
                                gameAppears.put(rankType.getId() + "_2", appear);
                            }
                        }
                    }
                } else {
                    IGameRole gr = GameWorld.getPtr().getGameRole(rankList.get(0).getId());
                    if (gr != null) {
                        Player player = gr.getPlayer();
                        if (player != null) {
                            AppearPlayer appear = new AppearPlayer();
                            appear.init(player, -1);
                            gameAppears.put(rankType.getId() + "", appear);
                        }
                    }
                }
            }
            rankList = null;
        }
        this.gameRanks = gRanks;
        this.playerRanks = pRanks;
        long curr = System.currentTimeMillis();
        logger.info("GameRankManager.updateRank() end currTime=" + DateUtil.formatDateTime(curr));
//        try {
//			if (!DateUtil.dayEqual(curr, curr - DateUtil.MINUTE * 30) ||
//					!DateUtil.dayEqual(curr, curr + DateUtil.MINUTE * 30))
//			{
//				printLogRank();
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
    }

    /**
     * 排行榜数据
     */
    public Message getGameRankMsg(GameRole role, ERankType type) {
        Message message = new Message(MessageCommand.GAME_RANK_LIST_MESSAGE);
        message.setByte(type.getId());

        int restTime = (int) ((this.nextUpdateTime - System.currentTimeMillis()) / 1000) + 1;
        if (restTime <= 0)
            restTime = 120;
        message.setInt(restTime);

        //自己的名次
        message.setInt(getRank(role.getPlayer(), type));
        //排行榜
        List<PlayerRank> rankList = gameRanks.get(type);
        if (rankList == null) {
            message.setShort(0);
        } else {
            message.setShort(rankList.size());
            for (PlayerRank rank : rankList) {
                rank.getMessage(message);
            }
        }
        //第一名外形数据
        if (type == ERankType.WEIWANG) {
            AppearPlayer appear = gameAppears.get(type.getId() + "_0");
            if (appear != null) {
                appear.getMessage(message);
            }
            appear = gameAppears.get(type.getId() + "_1");
            if (appear != null) {
                appear.getMessage(message);
            }
            appear = gameAppears.get(type.getId() + "_2");
            if (appear != null) {
                appear.getMessage(message);
            }
        } else {
            AppearPlayer appear = gameAppears.get(type.getId() + "");
            if (appear != null) {
                appear.getMessage(message);
            }
        }
        return message;
    }

    public Message getPlayerAppearMessage(Message message, ERankType type) {
        //第一名外形数据
        if (type == ERankType.WEIWANG) {
            AppearPlayer appear = gameAppears.get(type.getId() + "_0");
            if (appear != null) {
                appear.getMessage(message);
            }
            appear = gameAppears.get(type.getId() + "_1");
            if (appear != null) {
                appear.getMessage(message);
            }
            appear = gameAppears.get(type.getId() + "_2");
            if (appear != null) {
                appear.getMessage(message);
            }
        } else {
            AppearPlayer appear = gameAppears.get(type.getId() + "");
            if (appear != null) {
                appear.getMessage(message);
            }
        }
        return message;
    }

    /**
     * 打印排行榜
     */
    public void printLogRank() {
        try {
            for (Entry<ERankType, List<PlayerRank>> entry : gameRanks.entrySet()) {
                for (PlayerRank rank : entry.getValue()) {
                    Rank log = new Rank(rank.getId(), entry.getKey().getId(), rank.getRank(),
                            rank.getName(), rank.getLevel(), rank.getVip(), (int) rank.getValue(), rank.getValue2());
                    LogUtil.log(log);
                }
            }
        } catch (Exception e) {
            logger.error("打印排行榜数据发生异常.", e);
        }
    }

    public int getRank(Player player, ERankType rankType) {
        int id = player.getId();
        if (!playerRanks.containsKey(id))
            return 0;
        Map<ERankType, Integer> rankMap = playerRanks.get(id);
        if (rankMap == null || !rankMap.containsKey(rankType))
            return 0;
        return rankMap.get(rankType);
    }

    /**
     * 每日排行榜奖励
     */
    public void dailyReward(int day) {
        if (gameRanks == null)
            return;
        //发放每个榜单的奖励
        Map<String, RankLogicData> logicData = ActivityService.getRoundData(EActivityType.RANK, day);
        if (logicData == null)
            return;
        //发放奖励
        for (RankLogicData logic : logicData.values()) {
            ERankType rankType = ERankType.getType(logic.getType());
            if (rankType == null)
                continue;
            List<PlayerRank> ranks = gameRanks.get(rankType);
            if (ranks == null)
                continue;
            for (int i = logic.getLowRank(); i <= logic.getHighRank(); i++) {
                int idx = i - 1;
                if (idx >= ranks.size()) {
                    continue;
                }
                PlayerRank playerRank = ranks.get(idx);
                if (playerRank == null)
                    continue;
                List<String> ls = REWARD_MAILS.get(rankType);
                String content = ls.get(1) + i + ls.get(2);
                Mail mail = MailService.createMail(ls.get(0), content, EGoodsChangeType.GAME_RANK_ADD,
                        logic.getRewards());
                MailService.sendPaymentSystemMail(playerRank.getId(), mail);
            }
        }
    }

    //关卡榜
    private List<ActivityRank> mapStageList = new LinkedList<>();
    private ReentrantLock mapStageLock = new ReentrantLock();
    //诛仙台
    private List<ActivityRank> dekaronList = new LinkedList<>();
    private ReentrantLock dekaronLock = new ReentrantLock();
    //封魔塔
    private List<ActivityRank> fengmotaList = new LinkedList<>();
    private ReentrantLock fengmotaLock = new ReentrantLock();
    //主宰试炼
    private List<ActivityRank> zhuzaiList = new LinkedList<>();
    private ReentrantLock zhuzaiLock = new ReentrantLock();

    //关卡榜、诛仙台榜
    public void loadRankList() {
//		List<ActivityRank> mapStage = new PlayerDao().getPlayerRankMapStage();
//		mapStageList.addAll(mapStage);
//		List<ActivityRank> dekaron = new PlayerDao().getPlayerRankDekaron();
//		dekaronList.addAll(dekaron);
//		List<ActivityRank> fengmota = new PlayerDao().getPlayerRankFengmota();
//		fengmotaList.addAll(fengmota);
//		List<ActivityRank> zhuzai = new PlayerDao().getPlayerRankZhuzai();
//		zhuzaiList.addAll(zhuzai);
//		initXunbaoTop();
//		initFestTop();
//		initWeekendTop();
//		initFestPayTop();
    }

    /**
     * 重新计算排行榜
     *
     * @param player
     */
    public void resetTopMapStage(Player player) {
        ActivityRank myself = new ActivityRank();
        myself.setId(player.getId());
        myself.setN(player.getName());
        myself.setV1(-player.getState());
        myself.setV2(player.getMapStageId());
        myself.setVn(player.getVipLevel());
        myself.setM(player.getFighting());
        try {
            mapStageLock.lock();
            Iterator<ActivityRank> ite = mapStageList.iterator();
            while (ite.hasNext()) {
                ActivityRank delObj = ite.next();
                if (delObj.getId() == myself.getId()) {
                    mapStageList.remove(delObj);
                    break;
                }
            }
            mapStageList.add(myself);
            Collections.sort(mapStageList);
        } catch (Exception e) {
            logger.error("修改地图关卡排行榜时发生异常", e);
        } finally {
            mapStageLock.unlock();
        }
    }

    public void resetTopFengmota(Player player) {
        ActivityRank myself = new ActivityRank();
        myself.setId(player.getId());
        myself.setN(player.getName());
        myself.setV1(-player.getState());
        myself.setV2(player.getDekaron());
        myself.setVn(player.getVipLevel());
        myself.setM(player.getFighting());
        try {
            fengmotaLock.lock();
            Iterator<ActivityRank> ite = fengmotaList.iterator();
            while (ite.hasNext()) {
                ActivityRank delObj = ite.next();
                if (delObj.getId() == myself.getId()) {
                    fengmotaList.remove(delObj);
                    break;
                }
            }
            fengmotaList.add(myself);
            Collections.sort(fengmotaList);
        } catch (Exception e) {
            logger.error("修改封魔塔排行榜时发生异常", e);
        } finally {
            fengmotaLock.unlock();
        }
    }

    public void resetTopZhuzai(Player player) {
        ActivityRank myself = new ActivityRank();
        myself.setId(player.getId());
        myself.setN(player.getName());
        myself.setV1(-player.getState());
        myself.setV2(player.getZhuzai());
        myself.setVn(player.getVipLevel());
        myself.setM(player.getFighting());
        try {
            zhuzaiLock.lock();
            Iterator<ActivityRank> ite = zhuzaiList.iterator();
            while (ite.hasNext()) {
                ActivityRank delObj = ite.next();
                if (delObj.getId() == myself.getId()) {
                    zhuzaiList.remove(delObj);
                    break;
                }
            }
            zhuzaiList.add(myself);
            Collections.sort(zhuzaiList);
        } catch (Exception e) {
            logger.error("修改主宰试炼排行榜时发生异常", e);
        } finally {
            zhuzaiLock.unlock();
        }
    }

    public void resetTopDekaron(Player player) {
        ActivityRank myself = new ActivityRank();
        myself.setId(player.getId());
        myself.setN(player.getName());
        myself.setV1(-player.getState());
        myself.setV2(player.getDekaron());
        myself.setVn(player.getVipLevel());
        myself.setM(player.getFighting());
        try {
            dekaronLock.lock();
            Iterator<ActivityRank> ite = dekaronList.iterator();
            while (ite.hasNext()) {
                ActivityRank delObj = ite.next();
                if (delObj.getId() == myself.getId()) {
                    dekaronList.remove(delObj);
                    break;
                }
            }
            dekaronList.add(myself);
            Collections.sort(dekaronList);
        } catch (Exception e) {
            logger.error("修改诛仙台排行榜时发生异常", e);
        } finally {
            dekaronLock.unlock();
        }
    }

    //寻宝榜
    public void initXunbaoTop() {
        long curr = System.currentTimeMillis();
        Map<String, XunBaoRankLogicData> logicData =
                ActivityService.getRoundData(EActivityType.XUNBAO_RANK, 0, curr);
        if (logicData == null) {
            logicData = ActivityService.getRoundData(EActivityType.XUNBAO_RANK2, 0, curr);
        }
        if (logicData == null)
            return;
        List<PlayerActivity> tops = new ActivityDao().getXunBaoTopList();
        List<ActivityRank> ranks = new ArrayList<>(tops.size());
        int min = 0;
        for (PlayerActivity pa : tops) {
            ActivityRank ar = new ActivityRank();
            ar.setId(pa.getPlayerId());
            ar.setV1(pa.getXunbaoCount());
            ar.setM(pa.getXunbaoTime());
            //排行榜需要展示的数据
            IGameRole igr = GameWorld.getPtr().getGameRole(pa.getPlayerId());
            if (igr != null) {
                Player player = igr.getPlayer();
                ar.setN(player.getName());
                ar.setVn(player.getVipLevel());
                ar.setV2(player.getHead());
            }
            ranks.add(ar);
            if (min <= 0) {
                min = pa.getXunbaoCount();
                continue;
            }
            if (pa.getXunbaoCount() < min)
                min = pa.getXunbaoCount();
        }
        this.xunbaoRanks = ranks;
        this.xunbaoMin = min;
    }

    //节日榜
    public void initFestTop() {
        long curr = System.currentTimeMillis();
        Map<String, TargetLogicData> logicData =
                ActivityService.getRoundData(EActivityType.FEST_TARGET, 0, curr);
        if (logicData == null)
            return;
        List<PlayerActivity> tops = new ActivityDao().getFestTopList();
        List<ActivityRank> ranks = new ArrayList<>(tops.size());
        int min = 0;
        for (PlayerActivity pa : tops) {
            ActivityRank ar = new ActivityRank();
            ar.setId(pa.getPlayerId());
            ar.setV1(pa.getFestConsume());
            ar.setM(pa.getFestTime());
            //排行榜需要展示的数据
            IGameRole igr = GameWorld.getPtr().getGameRole(pa.getPlayerId());
            if (igr != null) {
                Player player = igr.getPlayer();
                ar.setN(player.getName());
                ar.setVn(player.getVipLevel());
                ar.setV2(player.getHead());
            }
            ranks.add(ar);
            if (min <= 0) {
                min = pa.getFestConsume();
                continue;
            }
            if (pa.getFestConsume() < min)
                min = pa.getFestConsume();
        }
        this.festRanks = ranks;
        this.festMin = min;
    }

    //周末榜
    public void initWeekendTop() {
        long curr = System.currentTimeMillis();
        Map<String, TargetLogicData> logicData =
                ActivityService.getRoundData(EActivityType.WEEKEND_TARGET, 0, curr);
        if (logicData == null)
            return;
        List<PlayerActivity> tops = new ActivityDao().getWeekendTopList();
        List<ActivityRank> ranks = new ArrayList<>(tops.size());
        int min = 0;
        for (PlayerActivity pa : tops) {
            ActivityRank ar = new ActivityRank();
            ar.setId(pa.getPlayerId());
            ar.setV1(pa.getWeekendPay());
            ar.setM(pa.getWeekendTime());
            //排行榜需要展示的数据
            IGameRole igr = GameWorld.getPtr().getGameRole(pa.getPlayerId());
            if (igr != null) {
                Player player = igr.getPlayer();
                ar.setN(player.getName());
                ar.setVn(player.getVipLevel());
                ar.setV2(player.getHead());
            }
            ranks.add(ar);
            if (min <= 0) {
                min = pa.getWeekendPay();
                continue;
            }
            if (pa.getWeekendPay() < min)
                min = pa.getWeekendPay();
        }
        this.weekendRanks = ranks;
        this.weekendMin = min;
    }

    //节日充值榜
    public void initFestPayTop() {
        long curr = System.currentTimeMillis();
        Map<String, TargetLogicData> logicData =
                ActivityService.getRoundData(EActivityType.FEST_PAY_TARGET, 0, curr);
        if (logicData == null)
            return;
        List<PlayerActivity> tops = new ActivityDao().getFestPayTopList();
        List<ActivityRank> ranks = new ArrayList<>(tops.size());
        int min = 0;
        for (PlayerActivity pa : tops) {
            ActivityRank ar = new ActivityRank();
            ar.setId(pa.getPlayerId());
            ar.setV1(pa.getFestPay());
            ar.setM(pa.getFestPayTime());
            //排行榜需要展示的数据
            IGameRole igr = GameWorld.getPtr().getGameRole(pa.getPlayerId());
            if (igr != null) {
                Player player = igr.getPlayer();
                ar.setN(player.getName());
                ar.setVn(player.getVipLevel());
                ar.setV2(player.getHead());
            }
            ranks.add(ar);
            if (min <= 0) {
                min = pa.getFestPay();
                continue;
            }
            if (pa.getFestPay() < min)
                min = pa.getFestPay();
        }
        this.festPayRanks = ranks;
        this.festPayMin = min;
    }

    public void clearXunbaoRanks() {
        this.xunbaoRanks.clear();
        this.xunbaoMin = 0;
    }

    public void clearFestRanks() {
        this.festRanks.clear();
        this.festMin = 0;
    }

    public void clearWeekendRanks() {
        this.weekendRanks.clear();
        this.weekendMin = 0;
    }

    public void clearFestPayRanks() {
        this.festPayRanks.clear();
        this.festPayMin = 0;
    }

    //加入寻宝排行榜
    public void addXunbaoTop(GameRole role, int add) {
        //增加玩家寻宝次数数据
        long curr = System.currentTimeMillis();
        Map<String, XunBaoRankLogicData> logicData =
                ActivityService.getRoundData(EActivityType.XUNBAO_RANK, 0, curr);
        if (logicData == null) {
            logicData = ActivityService.getRoundData(EActivityType.XUNBAO_RANK2, 0, curr);
        }
        if (logicData == null)
            return;
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        //同一天
        if (DateUtil.dayEqual(curr, activityData.getXunbaoTime()))
            activityData.addXunbaoCount(add);
            //跨天
        else
            activityData.setXunbaoCount(add);
        activityData.setXunbaoTime(curr);
        new ActivityDao().updateXunbao(activityData);
        //排行榜计算
        if (this.xunbaoRanks.size() >= XUNBAO_RANK_MAX && activityData.getXunbaoCount() <= this.xunbaoMin)
            return;
        try {
            this.xunbaoLock.lock();
            Player player = role.getPlayer();
            //是否已经在榜中
            boolean inTop = false;
            for (ActivityRank ar : this.xunbaoRanks) {
                if (ar.getId() == player.getId()) {
                    ar.setV1(activityData.getXunbaoCount());
                    ar.setM(curr);
                    ar.setVn(player.getVipLevel());
                    ar.setVn2(player.getHead());
                    inTop = true;
                    break;
                }
            }
            if (!inTop) {
                ActivityRank ar = new ActivityRank();
                ar.setId(player.getId());
                ar.setN(player.getName());
                ar.setV1(activityData.getXunbaoCount());
                ar.setM(curr);
                ar.setVn(player.getVipLevel());
                ar.setVn2(player.getHead());
                this.xunbaoRanks.add(ar);
            }
            Collections.sort(this.xunbaoRanks);
            for (int i = this.xunbaoRanks.size() - 1; i >= XUNBAO_RANK_MAX; i--) {
                this.xunbaoRanks.remove(i);
            }
        } catch (Exception e) {
            logger.error("加入寻宝排行榜时发生异常", e);
        } finally {
            this.xunbaoLock.unlock();
        }
    }

    //加入节日排行榜
    public void addFestTop(GameRole role, int value) {
        //增加玩家消耗元宝数据
        long curr = System.currentTimeMillis();
        Map<String, TargetLogicData> logicData = ActivityService.getRoundData(EActivityType.FEST_TARGET, 0, curr);
        if (logicData == null)
            return;
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        //同一天
        if (DateUtil.dayEqual(curr, activityData.getFestTime()))
            activityData.addFestConsume(value);
            //跨天
        else
            activityData.setFestConsume(value);
        activityData.setFestTime(curr);
        new ActivityDao().updateFestConsumeData(activityData);
        //排行榜计算
        if (activityData.getFestConsume() < FEST_VALUE_MIN)
            return;
        if (this.festRanks.size() >= FEST_RANK_MAX && activityData.getFestConsume() <= this.festMin)
            return;
        try {
            this.festLock.lock();
            Player player = role.getPlayer();
            //是否已经在榜中
            boolean inTop = false;
            for (ActivityRank ar : this.festRanks) {
                if (ar.getId() == player.getId()) {
                    ar.setV1(activityData.getFestConsume());
                    ar.setM(curr);
                    ar.setVn(player.getVipLevel());
                    ar.setVn2(player.getHead());
                    inTop = true;
                    break;
                }
            }
            if (!inTop) {
                ActivityRank ar = new ActivityRank();
                ar.setId(player.getId());
                ar.setN(player.getName());
                ar.setV1(activityData.getFestConsume());
                ar.setM(curr);
                ar.setVn(player.getVipLevel());
                ar.setVn2(player.getHead());
                this.festRanks.add(ar);
            }
            Collections.sort(this.festRanks);
            for (int i = this.festRanks.size() - 1; i >= FEST_RANK_MAX; i--) {
                this.festRanks.remove(i);
            }
        } catch (Exception e) {
            logger.error("加入节日消费排行榜时发生异常", e);
        } finally {
            this.festLock.unlock();
        }
    }

    //加入周末排行榜
    public void addWeekendTop(GameRole role, int value) {
        //增加玩家消耗元宝数据
        long curr = System.currentTimeMillis();
        Map<String, TargetLogicData> logicData = ActivityService.getRoundData(EActivityType.WEEKEND_TARGET, 0, curr);
        if (logicData == null)
            return;
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        //同一天
        if (DateUtil.dayEqual(curr, activityData.getWeekendTime()))
            activityData.addWeekendPay(value);
            //跨天
        else
            activityData.setWeekendPay(value);
        activityData.setWeekendTime(curr);
        new ActivityDao().updateWeekendPayData(activityData);
        //排行榜计算
        if (activityData.getWeekendPay() < WEEKEND_VALUE_MIN)
            return;
        if (this.weekendRanks.size() >= WEEKEND_RANK_MAX && activityData.getWeekendPay() <= this.weekendMin)
            return;
        try {
            this.weekendLock.lock();
            Player player = role.getPlayer();
            //是否已经在榜中
            boolean inTop = false;
            for (ActivityRank ar : this.weekendRanks) {
                if (ar.getId() == player.getId()) {
                    ar.setV1(activityData.getWeekendPay());
                    ar.setM(curr);
                    ar.setVn(player.getVipLevel());
                    ar.setVn2(player.getHead());
                    inTop = true;
                    break;
                }
            }
            if (!inTop) {
                ActivityRank ar = new ActivityRank();
                ar.setId(player.getId());
                ar.setN(player.getName());
                ar.setV1(activityData.getWeekendPay());
                ar.setM(curr);
                ar.setVn(player.getVipLevel());
                ar.setVn2(player.getHead());
                this.weekendRanks.add(ar);
            }
            Collections.sort(this.weekendRanks);
            for (int i = this.weekendRanks.size() - 1; i >= WEEKEND_RANK_MAX; i--) {
                this.weekendRanks.remove(i);
            }
        } catch (Exception e) {
            logger.error("加入周末充值排行榜时发生异常", e);
        } finally {
            this.weekendLock.unlock();
        }
    }

    //加入每日累积消费排行榜
    public void addTargetConsumeTop(GameRole role, int value) {
        //增加玩家消耗元宝数据
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.TARGET_DAILY_CONSUME_CUMULATE);
        if (configData == null) return;
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        Map<String, TargetConsumeDaillyCumulateLogicData> logicData = ActivityService.getRoundData(EActivityType.TARGET_DAILY_CONSUME_CUMULATE, 0, curr);
        if (logicData == null)
            return;
        //排行榜计算
        try {
            this.targetConsumeLock.lock();
            Player player = role.getPlayer();
            //是否已经在榜中
            boolean inTop = false;
            for (ActivityRank ar : this.targetConsumeRanks) {
                if (ar.getId() == player.getId()) {
                    ar.setV1(value);
                    inTop = true;
                    break;
                }
            }
            if (!inTop && value > logicData.get("" + currRound.getRound()).getDabiao()) {
                ActivityRank ar = new ActivityRank();
                ar.setId(role.getPlayerId());
                ar.setV1(value);
                ar.setN(role.getPlayer().getName());
                ar.setVn(role.getPlayer().getVipLevel());
                ar.setM(curr);
                this.targetConsumeRanks.add(ar);
            }
            Collections.sort(this.targetConsumeRanks);
            for (int i = this.targetConsumeRanks.size() - 1; i >= 20; i--) {
                this.targetConsumeRanks.remove(i);
            }
        } catch (Exception e) {
            logger.error("加入每日累积消费排行榜时发生异常", e);
        } finally {
            this.targetConsumeLock.unlock();
        }
    }

    //加入节日充值排行榜
    public void addFestPayTop(GameRole role, int value) {
        //增加玩家消耗元宝数据
        long curr = System.currentTimeMillis();
        Map<String, TargetLogicData> logicData = ActivityService.getRoundData(EActivityType.FEST_PAY_TARGET, 0, curr);
        if (logicData == null)
            return;
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        //同一天
        if (DateUtil.dayEqual(curr, activityData.getFestPayTime()))
            activityData.addFestPay(value);
            //跨天
        else
            activityData.setFestPay(value);
        activityData.setFestPayTime(curr);
        new ActivityDao().updateFestPayData(activityData);
        //排行榜计算
        if (activityData.getFestPay() < FESTPAY_VALUE_MIN)
            return;
        if (this.festPayRanks.size() >= FESTPAY_RANK_MAX && activityData.getFestPay() <= this.festPayMin)
            return;
        try {
            this.festPayLock.lock();
            Player player = role.getPlayer();
            //是否已经在榜中
            boolean inTop = false;
            for (ActivityRank ar : this.festPayRanks) {
                if (ar.getId() == player.getId()) {
                    ar.setV1(activityData.getFestPay());
                    ar.setM(curr);
                    ar.setVn(player.getVipLevel());
                    ar.setVn2(player.getHead());
                    inTop = true;
                    break;
                }
            }
            if (!inTop) {
                ActivityRank ar = new ActivityRank();
                ar.setId(player.getId());
                ar.setN(player.getName());
                ar.setV1(activityData.getFestPay());
                ar.setM(curr);
                ar.setVn(player.getVipLevel());
                ar.setVn2(player.getHead());
                this.festPayRanks.add(ar);
            }
            Collections.sort(this.festPayRanks);
            for (int i = this.festPayRanks.size() - 1; i >= FESTPAY_RANK_MAX; i--) {
                this.festPayRanks.remove(i);
            }
        } catch (Exception e) {
            logger.error("加入节日充值排行榜时发生异常", e);
        } finally {
            this.festPayLock.unlock();
        }
    }

    public Message getXunbaoRankMsg(GameRole role, int max) {
        Message msg = new Message(MessageCommand.XUNBAO_RANK_MESSAGE);
        msg.setInt(role.getActivityManager().getActivityData().getXunbaoCount());
        int size = max;
        if (this.xunbaoRanks == null)
            size = 0;
        else {
            if (size > this.xunbaoRanks.size())
                size = this.xunbaoRanks.size();
        }
        msg.setByte(size);
        for (int i = 0; i < size; i++) {
            ActivityRank ar = this.xunbaoRanks.get(i);
            msg.setInt(ar.getId());
            msg.setString(ar.getN());
            msg.setInt(ar.getV1());
            msg.setByte(ar.getVn());
            msg.setByte(ar.getVn2());
        }
        return msg;
    }

    //排行榜邮件奖励数据
    private static final Map<ERankType, List<String>> REWARD_MAILS;

    static {
        REWARD_MAILS = new HashMap<>();
        //等级排行榜
        REWARD_MAILS.put(ERankType.LEVEL, new ArrayList<String>() {
            private static final long serialVersionUID = 1L;

            {
                String title = "等级排行榜奖励";
                String content1 = "恭喜您在今天的等级排行榜中排名第";
                String content2 = ", 希望您再接再厉, 奖励如下：";
                add(title);
                add(content1);
                add(content2);
            }
        });
        //战斗力排行榜
        REWARD_MAILS.put(ERankType.FIGHTING, new ArrayList<String>() {
            private static final long serialVersionUID = 1L;

            {
                String title = "战力排行榜奖励";
                String content1 = "恭喜您在今天的战力排行榜中排名第";
                String content2 = ", 希望您再接再厉, 奖励如下：";
                add(title);
                add(content1);
                add(content2);
            }
        });
        //仙羽排行榜
        REWARD_MAILS.put(ERankType.WING, new ArrayList<String>() {
            private static final long serialVersionUID = 1L;

            {
                String title = "仙羽排行榜奖励";
                String content1 = "恭喜您在今天的仙羽排行榜中排名第";
                String content2 = ", 希望您再接再厉, 奖励如下：";
                add(title);
                add(content1);
                add(content2);
            }
        });
    }

    public long getNextUpdateTime() {
        return nextUpdateTime;
    }

    public List<ActivityRank> getTargetRanks(byte round) {
        return targetRanks.get((int) round);
    }

    public String getTargetRanksJson() {
        return StringUtil.obj2Gson(this.targetRanks);
    }

    public void setTargetRanksStr(String json) {
        this.targetRanks = StringUtil.gson2Map(json, new TypeToken<Map<Integer, List<ActivityRank>>>() {
        });
    }

    public void copyHistoryTargetRank(int round) {
        if (targetRanks.containsKey(round)) {
            historyTargetRanks = ImmutableList.copyOf(targetRanks.get(round));
        }
    }

    public void copyHistoryTargetConsumeRank() {
        this.historyTargetConsumeRanks = ImmutableList.copyOf(this.targetConsumeRanks);
    }

    public List<ActivityRank> getHistoryTargetRanks() {
        return historyTargetRanks;
    }

    public List<ActivityRank> getHistoryTargetConsumeRanks() {
        return this.historyTargetConsumeRanks;
    }

    public List<ActivityRank> getMapStageList() {
        return mapStageList;
    }

    public List<ActivityRank> getDekaronList() {
        return dekaronList;
    }

    public List<ActivityRank> getFengmotaList() {
        return fengmotaList;
    }

    public List<ActivityRank> getZhuzaiList() {
        return zhuzaiList;
    }

    public List<ActivityRank> getXunbaoRanks() {
        return xunbaoRanks;
    }

    public void copyHistoryXunbaoRank() {
        historyXunbaoRanks = ImmutableList.copyOf(xunbaoRanks);
    }

    public void copyHistoryFestRank() {
        historyFestRanks = ImmutableList.copyOf(festRanks);
    }

    public List<ActivityRank> getHistoryFestRanks() {
        return historyFestRanks;
    }

    public void copyHistoryWeekendRank() {
        historyWeekendRanks = ImmutableList.copyOf(weekendRanks);
    }

    public List<ActivityRank> getHistoryWeekendRanks() {
        return historyWeekendRanks;
    }

    public void copyHistoryFestPayRank() {
        historyFestPayRanks = ImmutableList.copyOf(festPayRanks);
    }

    public List<ActivityRank> getHistoryFestPayRanks() {
        return historyFestPayRanks;
    }

    public List<ActivityRank> getHistoryXunbaoRanks() {
        return historyXunbaoRanks;
    }

    public void setXunbaoRanks(List<ActivityRank> xunbaoRanks) {
        this.xunbaoRanks = xunbaoRanks;
    }

    public void setXunbaoMin(int xunbaoMin) {
        this.xunbaoMin = xunbaoMin;
    }

    public List<ActivityRank> getFestRanks() {
        return festRanks;
    }

    public List<ActivityRank> getWeekendRanks() {
        return weekendRanks;
    }

    public List<ActivityRank> getFestPayRanks() {
        return festPayRanks;
    }

    public List<PlayerRank> getGameRanks(ERankType type) {
        if (gameRanks == null)
            return null;
        return gameRanks.get(type);
    }

    /**
     * 获取玩家
     *
     * @param id
     * @return
     */
    public Map<ERankType, Integer> getRanks(int id) {
        return playerRanks.get(id);
    }

    /**
     * 根据玩家id获取玩家
     *
     * @param playerId 玩家id
     * @return
     */
    public Player getPlayerById(int playerId) {
        return rankData.get(playerId);
    }

    public Map<Integer, Player> getRankData() {
        return this.rankData;
    }

    public List<ActivityRank> getTargetConsumeRanks() {
        return targetConsumeRanks;
    }

    public void setTargetConsumeRanks(List<ActivityRank> ranks) {
        this.targetConsumeRanks = ranks;
    }
}
