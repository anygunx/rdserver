package com.rd.common;

import com.google.common.collect.ImmutableList;
import com.rd.bean.boss.Boss;
import com.rd.bean.boss.BossBattlePlayer;
import com.rd.bean.boss.BossBuff;
import com.rd.bean.drop.DropData;
import com.rd.bean.drop.DropGroupData;
import com.rd.bean.fighter.FighterData;
import com.rd.bean.goods.data.EquipData;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.Player;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.dao.GlobalDao;
import com.rd.define.*;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.model.BossModel;
import com.rd.model.DropModel;
import com.rd.model.FighterModel;
import com.rd.model.GoodsModel;
import com.rd.model.data.*;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import com.rd.util.DateUtil;
import com.rd.util.GameUtil;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static com.rd.define.FightDefine.PLAYER_REVIVE_TIME;

/**
 * 管理探索出的所有BOSS数据
 *
 * @author Created by U-Demon on 2016年11月25日 下午12:36:31
 * @version 1.0.0
 */
public class BossService {

    private static Logger logger = Logger.getLogger(BossService.class);

    // 全民BOSS复活锁
    private static ReentrantLock citReviveLock = new ReentrantLock();
    // 全民BOSS奖励锁
    private static ReentrantLock citRewardLock = new ReentrantLock();

    // 秘境BOSS复活锁
    private static ReentrantLock mysteryReviveLock = new ReentrantLock();
    // 秘境BOSS奖励锁
    private static ReentrantLock mysteryRewardLock = new ReentrantLock();

    // BOSS之家奖励锁
    private static ReentrantLock vipRewardLock = new ReentrantLock();

    // 转生BOSS奖励锁
    private static ReentrantLock reinRewardLock = new ReentrantLock();

    ////////////////////////// 常量数据//////////////////////////
    // 每个BOSS伤害排行榜的容量
    public static final int RANK_CAPACITY = 10;
    public static final Map<Integer, String> BOSS_NAMES = new HashMap<Integer, String>() {
        private static final long serialVersionUID = 1L;

        {
            put(16009, "元宝BOSS");
            put(16010, "阅历BOSS");
            put(16011, "真气BOSS");
            put(16012, "强化石BOSS");
            put(16013, "灵气BOSS");
            put(16014, "技能书BOSS");
            put(16015, "荣誉BOSS");
            put(16016, "聚魂签BOSS");
            put(16017, "装备BOSS");
            put(16026, "年兽");
            put(16027, "元宵节BOSS");
        }
    };
    // BOSS成长信息
    public static final float ADD = 1.1f;
    public static final float REDUCE = 0.9f;
    // 转生BOSS多长时间踢出战斗
    public static final long FIGHT_OUTTIME = 10 * DateUtil.SECOND;
    // 全民BOSS复活时间
    public static final long REVIVE_TIME = 60 * DateUtil.SECOND;
    // 全民BOSS奖励 等级和转生对应的装备数量
    public static final int[] CIT_LV_EQUIP = {0, 0, 3, 3, 4, 4, 5, 5, 6};
    public static final int[] CIT_REIN_EQUIP = {0, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 8, 8};
    // 全民BOSS奖励 第一名对应的装备品质几率
    public static final int[] CIT_FIRST_HIG = {5000, 3800, 1000, 200, 0};
    public static final int[] CIT_FIRST_LOW = {4000, 4500, 1250, 250, 0};
    public static final int[] CIT_COMMON_HIG = {4000, 3500, 2000, 500, 0};
    public static final int[] CIT_COMMON_LOW = {4000, 3300, 2150, 550, 0};
    public static final int[] VB_FIRST_HIG = {5000, 3800, 1000, 200, 0};
    public static final int[] VB_FIRST_LOW = {4000, 4500, 1250, 200, 50};
    public static final int[] VB_COMMON_HIG = {4000, 3500, 2000, 500, 0};
    public static final int[] VB_COMMON_LOW = {4000, 3300, 2150, 550, 0};
//	public static final int[] CIT_FIRST_LOW = { 4000, 4500, 1250, 200, 50 };
//	public static final int[] CIT_FIRST_HIG = { 5000, 3800, 1000, 200, 0 };
//	public static final int[] CIT_COMMON_LOW = { 4000, 3300, 2150, 400, 150 };
//	public static final int[] CIT_COMMON_HIG = { 4000, 3500, 2000, 500, 0 };

    // TODO BOSS对应技能的系数valueBase和技能额外伤害valueAdd
    public static final float BOSS_REIN_SKILL_BASE = 1;
    public static final int BOSS_REIN_SKILL_ADD = 0;
    public static long BOSS_REIN_START = 0;
    public static long BOSS_REIN_DURATION = 0;
    public static long BOSS_FIGHT_SPACE = 700;
    // 转生BOSS等级成长
    public static final byte BOSS_REIN_LV_INIT = 3;
    public static final float[][] BOSS_REIN_LV = {{1, 1}, {1, 1}, {2, 1}, {3, 1}, {4, 1}, {5, 1},
            {5.5f, 1.2f}, {6, 1.5f}, {6.3f, 2}, {6.6f, 2.5f}, {8.6f, 3}, {11.1f, 3}, {14.5f, 3},
            {18.9f, 3}, {24.5f, 3}, {31.9f, 3}, {41.5f, 3}, {54, 3}, {70, 3}, {91, 3}, {118, 3},};

    ////////////////////////// 常量数据//////////////////////////

    // BOSS今日出现个数
    private static Map<Integer, Integer> bossNum = new ConcurrentHashMap<>();

    // 所有BOSS列表
    private static Map<Integer, Boss> bossMap = new ConcurrentHashMap<>();

    // 玩家的BUFF
    private static Map<Integer, BossBuff> buffMap = new HashMap<>();

    ////////////////////////// 全民BOSS//////////////////////////
    private static Map<Short, Boss> citizenBoss = new ConcurrentHashMap<>();

    ////////////////////////// 转生BOSS//////////////////////////
    private static Map<Short, Boss> reinBoss = new ConcurrentHashMap<>();

    ////////////////////////// 秘境BOSS//////////////////////////
    private static Map<Short, Boss> mysteryBoss = new ConcurrentHashMap<>();
    //////////////////////////秘境BOSS的默认挑战次数//////////////////////////
    public static final short mysteryBossLeft = 3;

    ////////////////////////// BOSS之家//////////////////////////
    private static Map<Byte, Map<Short, Boss>> vipBoss = new ConcurrentHashMap<>();
    ///////////////////////// BOSS之家初始化时间 ////////////////////
    private static long vipBossTime;

    public static void init() {
        initCitizenBoss();
        initReinBoss();
        initMysteryBoss();
        initVipBoss();
    }

    // 初始化BOSS之家
    private static void initVipBoss() {
        Set<Byte> layers = BossModel.getVipBossMap().keySet();
        long curr = System.currentTimeMillis();
        for (byte layer : layers) {
            Map<Short, Boss> temp = new HashMap<>();
            for (VipBossData data : BossModel.getVipBossMap().get(layer).values()) {
                FighterData fd = FighterModel.getFighterDataById(data.getModelId());
                if (fd == null)
                    continue;
                Boss boss = new Boss();
                boss.setUuid(data.getModelId());
                boss.setId(data.getId());
                boss.initHp(fd.getHp());
                boss.setStartTime(System.currentTimeMillis());
                temp.put(boss.getId(), boss);
            }
            vipBoss.put(layer, temp);
        }
        vipBossTime = curr;
        // BOSS之家俩小时自动刷新
        TaskManager.getInstance().schedulePeriodicTask(ETaskType.COMMON, vipBossRefrshTask, 1000, DateUtil.MINUTE * 30);
    }

    /**
     * 初始化秘境BOSS
     */
    private static void initMysteryBoss() {
        // 根据表进行初始化
        for (BossMysteryData data : BossModel.getMysteryMap().values()) {
            FighterData fd = FighterModel.getFighterDataById(data.getModelId());
            if (fd == null)
                continue;
            Boss boss = new Boss();
            boss.setUuid(data.getModelId());
            boss.setId(data.getId());
            boss.initHp(fd.getHp());
            boss.setStartTime(System.currentTimeMillis());
            mysteryBoss.put(boss.getId(), boss);
        }
    }

    /**
     * 初始化全民BOSS
     */
    private static void initCitizenBoss() {
        // 根据表进行初始化
        for (BossCitData data : BossModel.getCitMap().values()) {
            FighterData fd = FighterModel.getFighterDataById(data.getModelId());
            if (fd == null)
                continue;
            Boss boss = new Boss();
            boss.setUuid(data.getModelId());
            boss.setId(data.getId());
            boss.initHp(fd.getHp());
            boss.setStartTime(System.currentTimeMillis());
            citizenBoss.put(boss.getId(), boss);
        }
        // 20171213 改为触发式updateCaller 不在其他任务里进行。
        // 因为客户端切后台等原因导致战斗5s的检测不合理，去掉了超时战斗的检查。
        // 可能导致ranks里包含已经掉线的玩家，在给客户端下发的数据中使用默认的数据。
        // 介于当前设计中Player的shadow存在服务器10min，极少情况造成影响，最坏情况是掉线玩家数据变成弱鸡。

        // 全民BOSS归属者
//		TaskManager.getInstance().schedulePeriodicTask(ETaskType.COMMON, citBossCallerTask, 1000, 2000);
    }

    /**
     * 初始化转生BOSS
     */
    private static void initReinBoss() {
        try {
            BossReinData data = BossModel.getReinMap().get((short) 1);
            if (data == null) {
                logger.error("转生BOSS启动失败。不存在ID为1的BOSS");
                return;
            }
            BOSS_REIN_START = Integer.valueOf(data.getStartTime().split(":")[0]) * DateUtil.HOUR;
            BOSS_REIN_DURATION = data.getDurationTime() * DateUtil.SECOND;
            TaskManager.getInstance().scheduleDailyTask(ETaskType.COMMON, reinBossInitTask,
                    data.getStartTime() + ":00");
        } catch (ParseException e) {
            logger.error("初始转生BOSS发生异常", e);
        }
    }

    /**
     * 探索体力上限
     *
     * @param level
     * @return
     */
    public static int getPowerMax(int level) {
        for (int i = 0; i < BossModel.EXPLORE_POWER_MAX.length; i++) {
            if (level < BossModel.EXPLORE_POWER_MAX[i][0])
                return BossModel.EXPLORE_POWER_MAX[i][1];
        }
        return 0;
    }

    /**
     * 召唤BOSS
     *
     * @param player
     * @param id     BOSSID
     */
    public static Boss callBoss(Player player, short id) {
        if (bossMap.size() >= BossModel.EXPLORE_BOSS_EXIST)
            return null;
        FighterData fighter = FighterModel.getFighterDataById(id);
        if (fighter == null)
            return null;
        long curr = System.currentTimeMillis();
        BossBattlePlayer battlePlayer = new BossBattlePlayer(player);
        // 通过ID获取BOSSxml中的配置数据
        Boss boss = new Boss();
        boss.setUuid(getUUID());
        boss.setId(id);
        boss.initHp(getBossMaxHp(fighter));
        boss.setStartTime(curr);
        boss.setCaller(battlePlayer);
        bossMap.put(boss.getUuid(), boss);
        // 广播
        // ChatService.broadcastPlayerMsg(player, EBroadcast.BossCall,
        // BOSS_NAMES.get((int)boss.getId()));
        // 召唤奖励
        BossRewardsData reward = BossModel.getReward(boss.getId());
        Mail callReward = MailService.createMail(reward.getCallTitle(), reward.getCallContent(),
                EGoodsChangeType.BOSS_CALL_ADD, reward.getCallReward());
        MailService.sendSystemMail(player.getId(), callReward);
        // 根据BOSS持续时间添加结束的任务
        TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, bossRewardTask, BossModel.EXIST_TIME);
        return boss;
    }

    private static long getBossMaxHp(FighterData fighter) {
        Map<Short, Long> map = GlobalDao.getInstance().getBossHp();
        if (map.containsKey(fighter.getId())) {
            return map.get(fighter.getId());
        } else {
            long maxHp = fighter.getHp();
            map.put(fighter.getId(), maxHp);
            return maxHp;
        }
    }

    // BOSS消失的任务
    private static Task bossTimeOutTask = new Task() {
        @Override
        public void run() {
            try {
                long curr = System.currentTimeMillis();
                for (Entry<Integer, Boss> entry : bossMap.entrySet()) {
                    if (entry.getValue().getDeadTime() > 0
                            && curr - entry.getValue().getDeadTime() >= BossModel.DISAPPEAR_TIME) {
                        // 消失
                        bossMap.remove(entry.getKey());
                    }
                }
            } catch (Exception e) {
                logger.error("BOSS消失时发生异常！", e);
            }
        }

        @Override
        public String name() {
            return "BOSSTIMEOUT";
        }
    };

    // BOSS奖励的任务
    public static Task bossRewardTask = new Task() {
        @Override
        public void run() {
            try {
                TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, bossTimeOutTask,
                        BossModel.DISAPPEAR_TIME);
                long curr = System.currentTimeMillis();
                for (Boss boss : bossMap.values()) {
                    if (boss.isReward())
                        continue;
                    if (curr - boss.getStartTime() >= BossModel.EXIST_TIME) {
                        boss.setDeadTime(curr);
                    }
                    if (boss.getDeadTime() <= 0)
                        continue;
                    boss.setReward(true);
                    BossRewardsData reward = BossModel.getReward(boss.getId());
                    if (reward == null)
                        continue;
                    // 击杀奖励
                    if (boss.getKiller() != null) {
                        Mail killReward = MailService.createMail(reward.getKillTile(), reward.getKillContent(),
                                EGoodsChangeType.BOSS_KILL_ADD, reward.getKillReward());
                        MailService.sendSystemMail(boss.getKiller().getId(), killReward);
                        // BOSS血量*1.2
                        long hp = boss.getHpMax();
                        if (hp < Integer.MAX_VALUE / ADD)
                            hp = (int) (hp * ADD);
                        GlobalDao.getInstance().getBossHp().put(boss.getId(), hp);
                    } else {
                        // BOSS血量*0.9
                        long hp = boss.getHpMax();
                        if (hp >= 100000)
                            hp = (int) (hp * REDUCE);
                        GlobalDao.getInstance().getBossHp().put(boss.getId(), hp);
                    }
                    // 排行榜奖励
                    int i = 0;
                    for (BossBattlePlayer bbp : boss.getRanks().values()) {
                        Mail rankReward = null;
                        // 排名奖励
                        if (i == 0) {
                            rankReward = MailService.createMail(reward.getFirstTitle(), reward.getFirstContent(),
                                    EGoodsChangeType.BOSS_RANK_ADD, reward.getFirstReward());
                        } else if (i == 1) {
                            rankReward = MailService.createMail(reward.getSecondTitle(), reward.getSecondContent(),
                                    EGoodsChangeType.BOSS_RANK_ADD, reward.getSecondReward());
                        } else if (i == 2) {
                            rankReward = MailService.createMail(reward.getThirdTitle(), reward.getThirdContent(),
                                    EGoodsChangeType.BOSS_RANK_ADD, reward.getThirdReward());
                        } else {
                            break;
                        }
                        MailService.sendSystemMail(bbp.getId(), rankReward);
                        i++;
                    }
                    GlobalDao.getInstance().updateBossHp();
                    // 全民奖励
                    // Mail endReward = null;
                    // if (boss.getHp() <= 0)
                    // endReward =
                    // MailService.createMail(reward.getWinnormalTitle(),
                    // reward.getWinnormalContent(),
                    // GameDefine.MAIL_TYPE_SYSTEM,
                    // reward.getWinnormalReward());
                    // else
                    // endReward =
                    // MailService.createMail(reward.getLostnormalTitle(),
                    // reward.getLostnormalContent(),
                    // GameDefine.MAIL_TYPE_SYSTEM,
                    // reward.getLostnormalReward());
                    // for (BossBattlePlayer bbp :
                    // boss.getBattlePlayers().values())
                    // {
                    // MailService.sendSystemMail(bbp.getPlayerId(), endReward);
                    // }
                }
            } catch (Exception e) {
                logger.error("BOSS奖励时发生异常！", e);
            }
        }

        @Override
        public String name() {
            return "BOSSREWARD";
        }
    };

    /**
     * 攻击BOSS
     *
     * @param player
     * @param uuid   UUID
     * @param damage 伤害
     * @return
     */
    public static short attackBoss(Player player, int uuid, int damage) {
        if (player.getBossCount() <= 0) {
            return ErrorDefine.ERROR_BOSS_FIGHT_MAX;
        }
        Boss boss = bossMap.get(uuid);
        // 判断BOSS是否已经死亡
        if (boss == null || boss.getDeadTime() > 0) {
            return ErrorDefine.ERROR_BOSS_DEAD;
        }
        long curr = System.currentTimeMillis();
        // 战斗数据
        BossBattlePlayer battlePlayer = boss.getBattlePlayer(player.getId());
        if (battlePlayer == null) {
            battlePlayer = new BossBattlePlayer(player);
            boss.getBattlePlayers().put(player.getId(), battlePlayer);
        }
        // 攻击CD
        if (curr - battlePlayer.getLastTime() < BossModel.ATK_CD_TIME) {
            return ErrorDefine.ERROR_BOSS_FIGHT_CD;
        }
        // 玩家伤害增加
        battlePlayer.addDamage(damage);
        // BOSS血量减少
        boss.changeHp(battlePlayer, -damage);
        boss.addRankSync(battlePlayer);
        player.changeBossCount(-1);
        return 1;
    }

    /**
     * 攻击全民BOSS
     *
     * @param player
     * @param boss
     * @param damage
     * @return
     */
    public static void atkCitizenBoss(Player player, Boss boss, int damage) {
        // 战斗数据
        BossBattlePlayer battlePlayer = boss.getBattlePlayer(player.getId());
        if (battlePlayer.getDeadTime() + PLAYER_REVIVE_TIME > System.currentTimeMillis()) {
            // 还没复活
            return;
        }
        // 玩家伤害增加
        battlePlayer.addDamage(damage);
        battlePlayer.setLastTime(System.currentTimeMillis());
        // BOSS血量减少
        boss.changeCitHp(battlePlayer, -damage);
        boolean exist = false;
        try {
            exist = boss.isRankExisted(player.getId());
            if (!exist) {
                boss.addRank(battlePlayer);
                // 广播给其他战斗的玩家
                Message appearMsg = new Message(MessageCommand.BOSS_CITIZEN_APPEAR_MESSAGE);
                appearMsg.setByte(1);
                appearMsg.setInt(player.getId());
                appearMsg.setString(player.getName());
                player.getAppearMessage(appearMsg);
                appearMsg.setByte(player.getHead());
                appearMsg.setLong(player.getFighting());
                for (BossBattlePlayer bp : boss.getBattlePlayers().values()) {
                    if (bp.getId() != player.getId()) {
                        GameRole gr = GameWorld.getPtr().getOnlineRole(bp.getId());
                        if (gr != null)
                            gr.putMessageQueue(appearMsg);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static void atkReinBoss(Player player, Boss boss, int damage) {
        if (damage == 0)
            return;
        // 战斗数据
        BossBattlePlayer battlePlayer = boss.getBattlePlayer(player.getId());
        // 玩家伤害增加
        battlePlayer.addDamage(damage);
        battlePlayer.setLastTime(System.currentTimeMillis());
        // BOSS血量减少
        boss.changeReinHp(player, battlePlayer, -damage);
        boss.addRankSync(battlePlayer);
    }

    // 全民BOSS奖励的任务
    public static Task citBossRewardTask = new Task() {
        @Override
        public void run() {
            try {
                citRewardLock.lock();
                for (Boss boss : citizenBoss.values()) {
                    BossCitData model = BossModel.getCitMap().get(boss.getId());
                    if (boss.getHp() <= 0 && !boss.isReward()) {
                        boss.setReward(true);
                        BossBattlePlayer caller = boss.getCaller();
                        for (BossBattlePlayer bbp : boss.getBattlePlayers().values()) {
                            GameRole gr = GameWorld.getPtr().getOnlineRole(bbp.getId());
                            if (gr == null)
                                continue;
                            // 是否伤害第一
                            boolean first = caller == null ? false : caller.getId() == bbp.getId();
                            // 奖励数量
                            int num = 0;
                            if (bbp.getRein() <= 0) {
                                if (bbp.getLevel() / 10 >= BossService.CIT_LV_EQUIP.length)
                                    num = BossService.CIT_LV_EQUIP[BossService.CIT_LV_EQUIP.length - 1];
                                else
                                    num = BossService.CIT_LV_EQUIP[bbp.getLevel() / 10];
                            } else {
                                if (bbp.getRein() >= BossService.CIT_REIN_EQUIP.length)
                                    num = BossService.CIT_REIN_EQUIP[BossService.CIT_REIN_EQUIP.length - 1];
                                else
                                    num = BossService.CIT_REIN_EQUIP[bbp.getRein()];
                            }
                            List<DropData> rewards = new ArrayList<>();
                            // 使用哪个随机
                            int[] rates = null;
                            int redPro = 0;
                            if (first) {
//								if (bbp.getRein() >= 3)
//									rates = BossService.CIT_FIRST_HIG;
//								else
                                rates = BossService.CIT_FIRST_LOW;
                                redPro = model.getOwnredpro();
                            } else {
//								if (bbp.getRein() >= 3)
//									rates = BossService.CIT_COMMON_HIG;
//								else
                                rates = BossService.CIT_COMMON_LOW;
                                redPro = model.getJoinredpro();
                            }
                            if (GameCommon.getRandomIndex(0, redPro) > -1) {
                                EquipData equipData = GoodsModel.getRandomDataByLv(model.getRedLev());
                                if (equipData == null)
                                    continue;
                                DropData drop = new DropData(EGoodsType.EQUIP.getId(), equipData.getGoodsId(),
                                        EquipDefine.QUALITY_RED, 1);
                                rewards.add(drop);
                            }
                            rewards.addAll(model.getRewards());

                            FighterData fd = FighterModel.getFighterDataById(model.getModelId());
                            // 随机装备
                            for (int i = 0; i < num; i++) {
                                int random = GameUtil.getRangedRandom(1, 10000);
                                int quality = GameUtil.getRatesIndex(rates, random);
                                EquipData equipData = GoodsModel.getRandomDataByLv(fd.getLevel());
                                if (equipData == null)
                                    continue;
                                DropData drop = new DropData(EGoodsType.EQUIP.getId(), equipData.getGoodsId(),
                                        (byte) quality, 1);
                                rewards.add(drop);
                                if (quality >= EGoodsQuality.ORANGE.getValue()) {
                                    // 跑马灯
                                    ChatService.broadcastPlayerMsg(gr.getPlayer(), EBroadcast.BOSS_ORANGE, fd.getName(),
                                            equipData.getName(), GameUtil.getLvConvertStr(equipData.getLevel()));
                                }
                            }
                            // 消息
                            Message msg = new Message(MessageCommand.BOSS_CITIZEN_REWARD_MESSAGE);
                            msg.setByte(1);
                            msg.setByte(first);
                            msg.setByte(rewards.size());
                            for (DropData data : rewards) {
                                msg.setByte(data.getT());
                                msg.setShort(data.getG());
                                msg.setByte(data.getQ());
                                msg.setInt(data.getN());
                            }
                            gr.putMessageQueue(msg);
                            // 增加物品
                            EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
                            gr.getPackManager().addGoods(rewards, EGoodsChangeType.CIT_BOSS_ADD, saves);
                            // gr.savePlayer(saves);
                            //发送参加全民boss消息
                            gr.getEventManager().notifyEvent(new GameEvent(EGameEventType.ALL_PEOPLE_BOSS, 1, saves));
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("全民BOSS发放奖励时发生异常", e);
            } finally {
                citRewardLock.unlock();
            }
        }

        @Override
        public String name() {
            return "CITBOSSREWARDTASK";
        }
    };

    // 全民BOSS复活的任务
    public static Task citBossReviveTask = new Task() {
        @Override
        public void run() {
            try {
                // FIXME = =和gm同步？
                citReviveLock.lock();
                long curr = System.currentTimeMillis();
                for (Boss boss : citizenBoss.values()) {
                    if (boss.getHpMax() == 0) {
                        BossCitData model = BossModel.getCitMap().get(boss.getId());
                        // BOSS复活时间
                        if (curr - boss.getDeadTime() >= model.getFuhuoTime() * 1000) {
                            FighterData fd = FighterModel.getFighterDataById(model.getModelId());
                            if (fd != null) {
                                boss.initHp(fd.getHp());
                                boss.setStartTime(curr);
                                boss.setReward(false);
                                boss.getBattlePlayers().clear();
                                boss.clearRanks();
                                boss.setDamageMin(0);
                                boss.setCaller(null);
                                boss.setKiller(null);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("复活全民BOSS时发生异常", e);
            } finally {
                citReviveLock.unlock();
            }
        }

        @Override
        public String name() {
            return "CITBOSSREVIVETASK";
        }
    };
    // 全民BOSS复活的任务
    public static Task citBossReviveNoticeTask = new Task() {
        @Override
        public void run() {
            try {
                List<Short> list = new ArrayList<>();
                long curr = System.currentTimeMillis();
                for (Boss boss : citizenBoss.values()) {
                    if (boss.getHpMax() == 0) {
                        BossCitData model = BossModel.getCitMap().get(boss.getId());
                        long space = curr - boss.getDeadTime();
                        // BOSS复活时间
                        if (space >= (model.getFuhuoTime() - 10) * 1000 && space <= model.getFuhuoTime() * 1000) {
                            list.add(boss.getId());
                        }
                    }
                }
                if (list.size() > 0) {
                    Message msg = new Message(MessageCommand.BOSS_CITIZEN_REVIVE_MESSAGE);
                    msg.setByte(list.size());
                    for (short id : list) {
                        msg.setShort(id);
                    }
                    for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
                        role.putMessageQueue(msg);
                    }
                }
            } catch (Exception e) {
                logger.error("复活全民BOSS提醒时发生异常", e);
            }
        }

        @Override
        public String name() {
            return "CITBOSSREVIVENOTICETASK";
        }
    };

//	// 全民BOSS归属者的任务
//	public static Task citBossCallerTask = new Task() {
//		@Override
//		public void run() {
//			try {
//				long curr = System.currentTimeMillis();
//				for (Boss boss : citizenBoss.values()) {
//					int callerId = 0;
//					if (boss.getCaller() != null)
//						callerId = boss.getCaller().getId();
//					for (BossBattlePlayer bbp : boss.getBattlePlayers().values()) {
//						// 至少5秒的战斗超时
//						if (bbp.getLastTime() > 0 && bbp.getLastTime() + PVP_BATTLE_TIME < curr) {
//							bbp.setLastTime(0);
//							Iterator<BossBattlePlayer> ite = boss.getRanks().iterator();
//							while (ite.hasNext()) {
//								BossBattlePlayer item = ite.next();
//								if (item.getId() == bbp.getId()) {
//									logger.info(bbp.getId() + " 战斗超时移除");
//									ite.remove();
//								}
//							}
//							// 切换归属者
//							if (callerId == bbp.getId()) {
//								if (boss.getRanks().size() > 0)
//									boss.setCaller(boss.getRanks().get(0));
//								else
//									boss.setCaller(null);
//							}
//						}
//					}
//				}
//			} catch (Exception e) {
//				logger.error("全民BOSS归属者发送变化时发生异常", e);
//			}
//		}
//
//		@Override
//		public String name() {
//			return "citBossCallerTask";
//		}
//	};

    // 转生BOSS奖励的任务
    public static Task reinBossRewardTask = new Task() {
        @Override
        public void run() {
            try {
                reinRewardLock.lock();
                long curr = System.currentTimeMillis();
                boolean save = false;
                for (Boss boss : reinBoss.values()) {
                    if (boss.getHp() <= 0 && !boss.isReward()) {
                        boss.setLastKiller(boss.getKiller());
                        boss.setHistory(ImmutableList.copyOf(boss.getRanks().values()));
                        boss.setReward(true);
                        // 奖励
                        onReinReward(boss);
                        // BOSS等级
                        Map<Short, Byte> lvMap = GlobalDao.getInstance().getReinBossLv();
                        if (!lvMap.containsKey(boss.getId()))
                            lvMap.put(boss.getId(), (byte) 1);
                        byte lv = lvMap.get(boss.getId());
                        if (lv < 20) {
                            // BOSS 5分钟内死亡 等级+1
                            if (curr - boss.getStartTime() <= 5 * DateUtil.MINUTE) {
                                lvMap.put(boss.getId(), (byte) (lv + 1));
                                save = true;
                            }
                        }
                    }
                }
                if (save)
                    GlobalDao.getInstance().updateReinBossLv();
            } catch (Exception e) {
                logger.error("奖励转生BOSS时发生异常", e);
            } finally {
                reinRewardLock.unlock();
            }
        }

        @Override
        public String name() {
            return "reinBossRewardTask";
        }
    };

    // 转生BOSS初始化
    public static Task reinBossInitTask = new Task() {
        @Override
        public void run() {
            long curr = System.currentTimeMillis();
            // 根据数据表初始化所有转生BOSS
            for (BossReinData data : BossModel.getReinMap().values()) {
                FighterData fd = FighterModel.getFighterDataById(data.getModelId());
                if (fd == null)
                    continue;
                Boss boss = reinBoss.get(data.getId());
                if (boss == null) {
                    boss = new Boss();
                    boss.setUuid(data.getModelId());
                    boss.setId(data.getId());
                    reinBoss.put(boss.getId(), boss);
                }
                // 转生BOSS的血量
                byte lv = BOSS_REIN_LV_INIT;
                if (GlobalDao.getInstance().getReinBossLv().containsKey(boss.getId()))
                    lv = GlobalDao.getInstance().getReinBossLv().get(boss.getId());
                boss.initHp((long) (fd.getHp() * BOSS_REIN_LV[lv][0]));
                // BOSS攻击力
                boss.setAtk((int) (fd.getAtk() * BOSS_REIN_LV[lv][1]));
                boss.setStartTime(curr);
                boss.setDeadTime(0);
                boss.setReward(false);
                boss.getBattlePlayers().clear();
                boss.clearRanks();
                boss.setDamageMin(0);
                boss.setKiller(null);
            }
            // 转生BOSS结束的任务 时间
            TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, reinBossEndTask, BOSS_REIN_DURATION);
            // 转生BOSS目标追踪
            TaskManager.getInstance().schedulePeriodicTask(ETaskType.COMMON, reinTargetTask, 100, 1000);
            Message msg = new Message(MessageCommand.BOSS_REIN_NOTICE_MESSAGE);
            for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
                role.putMessageQueue(msg);
            }
        }

        @Override
        public String name() {
            return "REINBOSSINITTASK";
        }
    };

    // 转生BOSS结束
    public static Task reinBossEndTask = new Task() {
        @Override
        public void run() {
            try {
                TaskManager.getInstance().cancleTask(ETaskType.COMMON, "REINTARGETTASK");
                reinRewardLock.lock();
                boolean save = false;
                for (Boss boss : reinBoss.values()) {
                    if (boss.getHp() > 0 && !boss.isReward()) {
                        boss.setLastKiller(boss.getKiller());
                        boss.setHistory(ImmutableList.copyOf(boss.getRanks().values()));
                        boss.resetHp();
                        boss.setReward(true);
                        // 奖励
                        onReinReward(boss);
                        // BOSS等级-1
                        Map<Short, Byte> lvMap = GlobalDao.getInstance().getReinBossLv();
                        if (lvMap.containsKey(boss.getId())) {
                            byte lv = lvMap.get(boss.getId());
                            if (lv > 1) {
                                lvMap.put(boss.getId(), (byte) (lv - 1));
                                save = true;
                            }
                        }
                    }
                }
                if (save)
                    GlobalDao.getInstance().updateReinBossLv();
            } catch (Exception e) {
                logger.error("转生BOSS结束任务失败", e);
            } finally {
                reinRewardLock.unlock();
            }
        }

        @Override
        public String name() {
            return "REINBOSSENDTASK";
        }
    };

    // 转生BOSS攻击目标追踪
    public static Task reinTargetTask = new Task() {
        @Override
        public void run() {
            try {
                long curr = System.currentTimeMillis();
                for (Boss boss : reinBoss.values()) {
                    if (boss.getHp() <= 0)
                        continue;
                    // 是否切换目标
                    boolean change = false;
                    // 当前目标为空
                    BossBattlePlayer target = boss.getCaller();
                    if (target == null) {
                        change = true;
                    } else {
                        BossBattlePlayer targetPlayer = boss.getBattlePlayer(target.getId());
                        if (targetPlayer == null) {
                            change = true;
                        }
                        // 目标离线
                        else if (curr - targetPlayer.getLastTime() > FIGHT_OUTTIME) {
                            change = true;
                            targetPlayer.setDeadTime(curr);
                        }
                        // 目标死亡
                        else if (target.getDamage() * BOSS_FIGHT_SPACE + target.getLastTime() <= curr) {
                            change = true;
                            targetPlayer.setDeadTime(curr);
                        }
                    }
                    // 切换目标
                    if (change) {
                        // 找到伤害最高的玩家
                        BossBattlePlayer max = null;
                        for (BossBattlePlayer bbp : boss.getBattlePlayers().values()) {
                            // 没打伤害
                            if (bbp.getDamage() <= 0)
                                continue;
                            // 死亡未复活
                            if (bbp.getDeadTime() > 0 && bbp.getDeadTime() + REVIVE_TIME >= curr)
                                continue;
                            // 离线
                            if (bbp.getLastTime() + FIGHT_OUTTIME < curr) {
                                // 广播给所有人
                                Message quitMsg = new Message(MessageCommand.BOSS_REIN_QUIT_MESSAGE);
                                quitMsg.setInt(bbp.getId());
                                for (BossBattlePlayer fp : boss.getBattlePlayers().values()) {
                                    GameRole gr = GameWorld.getPtr().getOnlineRole(fp.getId());
                                    if (gr == null)
                                        continue;
                                    gr.putMessageQueue(quitMsg);
                                }
                                continue;
                            }
                            if (max == null)
                                max = bbp;
                            else if (bbp.getDamage() > max.getDamage()) {
                                max = bbp;
                            }
                        }
                        if (max != null) {
                            GameRole role = GameWorld.getPtr().getOnlineRole(max.getId());
                            if (role != null) {
                                BossBattlePlayer targetNew = new BossBattlePlayer(role.getPlayer());
                                // 成为目标的时间
                                targetNew.setLastTime(curr);
                                // 计算回合数：伤害=（攻-防）*技能系数+技能额外伤害
                                // 伤害>攻*5%，伤害=伤害；伤害<=攻*5%，伤害=攻*5%
                                int roundTotal = 0, hpTotal = 0;
//								for (Character cha : role.getPlayer().getCharacterList()) {
//									int[] attr = cha.getAttribute();
//									int bossDamage = (int) ((boss.getAtk()
//											- (attr[EAttrType.PHYDEF.getId()] + attr[EAttrType.MAGICDEF.getId()]) / 2)
//											* BOSS_REIN_SKILL_BASE + BOSS_REIN_SKILL_ADD);
//									if (bossDamage <= boss.getAtk() * 0.05f)
//										bossDamage = (int) (boss.getAtk() * 0.05f);
//									if (bossDamage < 1)
//										bossDamage = 1;
//									int round = attr[EAttrType.HP.getId()] / bossDamage;
//									if (round < 1)
//										round = 1;
//									roundTotal += round;
//									hpTotal += attr[EAttrType.HP.getId()];
//								}
                                targetNew.setDamage(roundTotal < 1 ? 1 : roundTotal);
                                // 血量上限
                                targetNew.setRelive(hpTotal);
                                // 设置目标
                                boss.setCaller(targetNew);
                                Message msg = getReinTargetMsg(boss);
                                if (msg == null)
                                    continue;
                                // 发送消息
                                for (BossBattlePlayer bbp : boss.getBattlePlayers().values()) {
                                    GameRole gr = GameWorld.getPtr().getOnlineRole(bbp.getId());
                                    if (gr == null)
                                        continue;
                                    gr.putMessageQueue(msg);
                                }
                            }
                        } else {
                            boss.setCaller(null);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("转生BOSS追踪目标失败", e);
            }
        }

        @Override
        public String name() {
            return "REINTARGETTASK";
        }
    };

    /**
     * 发放转生BOSS奖励
     */
    private static void onReinReward(Boss boss) {
        ReinRewardsData model = BossModel.getReinReward(boss.getId());
        if (model == null)
            return;
        int rank = 0;
        for (BossBattlePlayer bbp : boss.getRanks().values()) {
            try {
                List<DropData> rewards = null;
                String title = "", content = "";
                if (rank == 0) {
                    rewards = model.getFirstReward();
                    title = model.getFirstTitle();
                    content = model.getFirstContent();
                } else if (rank == 1) {
                    rewards = model.getSecondReward();
                    title = model.getSecondTitle();
                    content = model.getSecondContent();
                } else if (rank >= 2 && rank <= 4) {
                    rewards = model.getReward345();
                    title = model.getTitle345();
                    content = model.getContent345();
                } else {
                    rewards = model.getCanyuReward();
                    title = model.getCanyuTitle();
                    content = model.getCanyuContent();
                }
                Mail mail = MailService.createMail(title, content, EGoodsChangeType.BOSS_REIN_RANK_ADD, rewards);
                MailService.sendSystemMail(bbp.getId(), mail);
            } catch (Exception e) {
                logger.error("发放转生BOSS排行奖励异常。rank=" + rank + ",playerId=" + bbp.getId(), e);
            }
            ++rank;
        }
        try {
            if (boss.getKiller() != null) {
                Mail mail = MailService.createMail(model.getKillTitle(), model.getKillContent(),
                        EGoodsChangeType.BOSS_REIN_KILL_ADD, model.getKillReward());
                MailService.sendSystemMail(boss.getKiller().getId(), mail);
            }
        } catch (Exception e) {
            logger.error("发放转生BOSS击杀奖励异常。playerId=" + boss.getKiller().getId(), e);
        }
    }

    /**
     * 转生BOSS目标
     *
     * @return
     */
    public static Message getReinTargetMsg(Boss boss) {
        BossBattlePlayer target = boss.getCaller();
        if (target == null)
            return null;
        long curr = System.currentTimeMillis();
        Message msg = new Message(MessageCommand.BOSS_REIN_TARGET_MESSAGE);
        msg.setInt(target.getId());
        msg.setString(target.getName());
        // 血量上限
        msg.setInt(target.getRelive());
        long total = target.getDamage() * BOSS_FIGHT_SPACE;
        long left = target.getLastTime() + total - curr;
        if (left < 0)
            left = 10000;
        // 当前血量
        msg.setInt((int) (target.getRelive() * left / total));
        // 剩余多少秒死亡
        msg.setInt((int) (left / 1000));
        // 外形数据 AppearPlayer
        IGameRole gr = GameWorld.getPtr().getGameRole(target.getId());
        if (gr != null) {
            gr.getPlayer().getAppearMessage(msg);
        } else {
            msg.setByte(1); // 法宝外形
            msg.setByte(1); // 角色数量
            msg.setByte(1); // 翅膀外形
            msg.setByte(1); // 坐骑外形
            msg.setShort(1); // 武器外形
            msg.setShort(3); // 衣服外形
        }
        return msg;
    }

    /**
     * BOSS数据每日重置
     */
    public static void dailyReset() {
        bossNum.clear();
    }

    /**
     * 不同步该方法了，没必要那么精确
     *
     * @return
     */
    public static boolean addBoss() {
        long curr = System.currentTimeMillis();
        long start = DateUtil.getDayStartTime(curr);
        for (int i = 0; i < BossModel.EXPLORE_BOSS_RULE.length; i++) {
            if (curr >= start + BossModel.EXPLORE_BOSS_RULE[i][0] * DateUtil.HOUR
                    && curr <= start + BossModel.EXPLORE_BOSS_RULE[i][1] * DateUtil.HOUR) {
                if (!bossNum.containsKey(i)) {
                    bossNum.put(i, 1);
                    return true;
                } else if (bossNum.get(i) < BossModel.EXPLORE_BOSS_RULE[i][2]) {
                    bossNum.put(i, bossNum.get(i) + 1);
                    return true;
                } else
                    return false;
            }
        }
        return false;
    }

    public static void lvUpEvent(GameRole role) {
        int lv = role.getPlayer().getLevel();
        if (lv < 15)
            return;
        int preLv = lv - 1;
        int max = getPowerMax(lv);
        int preMax = getPowerMax(preLv);
        if (max > preMax) {

        }
    }

    private static int getUUID() {
        int uuid = 1;
        for (Boss boss : bossMap.values()) {
            if (boss.getUuid() >= uuid) {
                uuid = boss.getUuid();
                uuid++;
            }
        }
        return uuid;
    }

    public static Map<Integer, Boss> getBossMap() {
        return bossMap;
    }

    public static Map<Short, Boss> getCitizenBoss() {
        return citizenBoss;
    }

    public static Map<Short, Boss> getReinBoss() {
        return reinBoss;
    }

    public static Boss getFightReinBoss(int playerId) {
        for (Boss boss : reinBoss.values()) {
            if (boss.getBattlePlayers().containsKey(playerId))
                return boss;
        }
        return null;
    }

    public static BossBattlePlayer getFightReinPlayer(int playerId) {
        for (Boss boss : reinBoss.values()) {
            BossBattlePlayer bbp = boss.getBattlePlayers().get(playerId);
            if (bbp != null)
                return bbp;
        }
        return null;
    }

    // 秘境BOSS奖励的任务
    public static Task mysteryBossRewardTask = new Task() {
        @Override
        public void run() {
            try {
                mysteryRewardLock.lock();
                for (Boss boss : mysteryBoss.values()) {
                    BossMysteryData model = BossModel.getMysteryMap().get(boss.getId());
                    if (boss.getHp() <= 0 && !boss.isReward()) {
                        boss.setReward(true);
                        BossBattlePlayer caller = boss.getCaller();
                        for (BossBattlePlayer bbp : boss.getBattlePlayers().values()) {
                            GameRole gr = GameWorld.getPtr().getOnlineRole(bbp.getId());
                            if (gr == null)
                                continue;
                            // 是否伤害第一
                            boolean first = caller == null ? false : caller.getId() == bbp.getId();
                            // 奖励数量
                            int num = 0;
                            // TODO
                            if (bbp.getRein() <= 0) {
                                if (bbp.getLevel() / 10 >= BossService.CIT_LV_EQUIP.length)
                                    num = BossService.CIT_LV_EQUIP[BossService.CIT_LV_EQUIP.length - 1];
                                else
                                    num = BossService.CIT_LV_EQUIP[bbp.getLevel() / 10];
                            } else {
                                if (bbp.getRein() >= BossService.CIT_REIN_EQUIP.length)
                                    num = BossService.CIT_REIN_EQUIP[BossService.CIT_REIN_EQUIP.length - 1];
                                else
                                    num = BossService.CIT_REIN_EQUIP[bbp.getRein()];
                            }
                            List<DropData> rewards = new ArrayList<>();
                            rewards.addAll(model.getRewards());
                            // 使用哪个随机
                            int[] rates = null;
                            if (first) {
                                if (bbp.getRein() >= 3)
                                    rates = BossService.CIT_FIRST_HIG;
                                else
                                    rates = BossService.CIT_FIRST_LOW;
                            } else {
                                if (bbp.getRein() >= 3)
                                    rates = BossService.CIT_COMMON_HIG;
                                else
                                    rates = BossService.CIT_COMMON_LOW;
                            }
                            FighterData fd = FighterModel.getFighterDataById(model.getModelId());
                            // 随机装备
                            for (int i = 0; i < num; i++) {
                                int random = GameUtil.getRangedRandom(1, 10000);
                                int quality = GameUtil.getRatesIndex(rates, random);
                                EquipData equipData = GoodsModel.getRandomDataByLv(fd.getLevel());
                                if (equipData == null)
                                    continue;
                                DropData drop = new DropData(EGoodsType.EQUIP.getId(), equipData.getGoodsId(),
                                        (byte) quality, 1);
                                rewards.add(drop);
                                if (quality >= EGoodsQuality.ORANGE.getValue()) {
                                    // 跑马灯
                                    ChatService.broadcastPlayerMsg(gr.getPlayer(), EBroadcast.BOSS_ORANGE, fd.getName(),
                                            equipData.getName(), GameUtil.getLvConvertStr(equipData.getLevel()));
                                }
                            }
                            //掉落组掉落物品
                            DropGroupData dropGroupData = DropModel.getDropGroupData(model.getDropid());
                            if (dropGroupData != null) {
                                rewards.addAll(dropGroupData.getRandomDrop());
                            }
                            // 消息
                            Message msg = new Message(MessageCommand.BOSS_MYSTERY_REWARD_MESSAGE);
                            msg.setByte(1);
                            msg.setByte(first);
                            msg.setByte(rewards.size());
                            for (DropData data : rewards) {
                                msg.setByte(data.getT());
                                msg.setShort(data.getG());
                                msg.setByte(data.getQ());
                                msg.setInt(data.getN());
                            }
                            gr.putMessageQueue(msg);
                            // 增加物品
                            EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
                            gr.getPackManager().addGoods(rewards, EGoodsChangeType.MYSTERY_BOSS_ADD, saves);

                            //发送参加秘境boss消息
                            gr.getEventManager().notifyEvent(new GameEvent(EGameEventType.ENTER_MYSTERY_BOSS, 1, saves));
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("秘境BOSS发放奖励时发生异常", e);
            } finally {
                mysteryRewardLock.unlock();
            }
        }

        @Override
        public String name() {
            return "MYSTERYBOSSREWARDTASK";
        }
    };

    // 秘境BOSS复活的任务
    public static Task mysteryBossReviveTask = new Task() {
        @Override
        public void run() {
            try {
                mysteryReviveLock.lock();
                long curr = System.currentTimeMillis();
                for (Boss boss : mysteryBoss.values()) {
                    if (boss.getHpMax() == 0) {
                        BossMysteryData model = BossModel.getMysteryMap().get(boss.getId());
                        // BOSS复活时间
                        if (curr - boss.getDeadTime() >= model.getFuhuoTime() * 1000) {
                            FighterData fd = FighterModel.getFighterDataById(model.getModelId());
                            if (fd != null) {
                                boss.initHp(fd.getHp());
                                boss.setStartTime(curr);
                                boss.setReward(false);
                                boss.getBattlePlayers().clear();
                                boss.clearRanks();
                                boss.setDamageMin(0);
                                boss.setCaller(null);
                                boss.setKiller(null);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("复活秘境BOSS时发生异常", e);
            } finally {
                mysteryReviveLock.unlock();
            }
        }

        @Override
        public String name() {
            return "MYSTERYBOSSREVIVETASK";
        }
    };
    // 秘境BOSS复活的任务
    public static Task mysteryBossReviveNoticeTask = new Task() {
        @Override
        public void run() {
            try {
                List<Short> list = new ArrayList<>();
                long curr = System.currentTimeMillis();
                for (Boss boss : mysteryBoss.values()) {
                    if (boss.getHpMax() == 0) {
                        BossMysteryData model = BossModel.getMysteryMap().get(boss.getId());
                        long space = curr - boss.getDeadTime();
                        // BOSS复活时间
                        if (space >= (model.getFuhuoTime() - 10) * 1000 && space <= model.getFuhuoTime() * 1000) {
                            list.add(boss.getId());
                        }
                    }
                }
                if (list.size() > 0) {
                    Message msg = new Message(MessageCommand.BOSS_MYSTERY_REVIVE_MESSAGE);
                    msg.setByte(list.size());
                    for (short id : list) {
                        msg.setShort(id);
                    }
                    for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
                        role.putMessageQueue(msg);
                    }
                }
            } catch (Exception e) {
                logger.error("复活秘境BOSS提醒时发生异常", e);
            }
        }

        @Override
        public String name() {
            return "MYSTERYBOSSREVIVENOTICETASK";
        }
    };

    /**
     * 攻击秘境BOSS
     *
     * @param player
     * @param boss
     * @param damage
     * @return
     */
    public static void atkMysteryBoss(Player player, Boss boss, int damage) {
        // 战斗数据
        BossBattlePlayer battlePlayer = boss.getBattlePlayer(player.getId());
        if (battlePlayer.getDeadTime() + PLAYER_REVIVE_TIME > System.currentTimeMillis()) {
            return;
        }
        // 玩家伤害增加
        battlePlayer.addDamage(damage);
        battlePlayer.setLastTime(System.currentTimeMillis());
        // BOSS血量减少
        boss.changeMysteryHp(battlePlayer, -damage);
        boolean exist = false;
        try {
            exist = boss.isRankExisted(player.getId());
            if (!exist) {
                boss.addRank(battlePlayer);
                // 广播给其他战斗的玩家
                Message appearMsg = new Message(MessageCommand.BOSS_MYSTERY_APPEAR_MESSAGE);
                appearMsg.setByte(1);
                appearMsg.setInt(player.getId());
                appearMsg.setString(player.getName());
                player.getAppearMessage(appearMsg);
                appearMsg.setByte(player.getHead());
                appearMsg.setLong(player.getFighting());
                for (BossBattlePlayer bp : boss.getBattlePlayers().values()) {
                    if (bp.getId() != player.getId()) {
                        GameRole gr = GameWorld.getPtr().getOnlineRole(bp.getId());
                        if (gr != null)
                            gr.putMessageQueue(appearMsg);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    //////////////////////////////////////////////////////////////////////////// BOSS之家开始/////////////////////////////////////////////////////////////////////////////

    /**
     * 攻击BOSS之家
     *
     * @param player
     * @param boss
     * @param damage
     * @return
     */
    public static void atkVipBoss(Player player, Boss boss, int damage) {
        // 战斗数据
        BossBattlePlayer battlePlayer = boss.getBattlePlayer(player.getId());
        if (battlePlayer.getDeadTime() + PLAYER_REVIVE_TIME > System.currentTimeMillis()) {
            return;
        }
        // 玩家伤害增加
        battlePlayer.addDamage(damage);
        battlePlayer.setLastTime(System.currentTimeMillis());
        // BOSS血量减少
        boss.changeVipHp(battlePlayer, -damage);
        boolean exist = false;
        try {
            exist = boss.isRankExisted(player.getId());
            if (!exist) {
                boss.addRank(battlePlayer);
                // 广播给其他战斗的玩家
                Message appearMsg = new Message(MessageCommand.BOSS_VIP_APPEAR_MESSAGE);
                appearMsg.setByte(1);
                appearMsg.setInt(player.getId());
                appearMsg.setString(player.getName());
                player.getAppearMessage(appearMsg);
                appearMsg.setByte(player.getHead());
                appearMsg.setLong(player.getFighting());
                for (BossBattlePlayer bp : boss.getBattlePlayers().values()) {
                    if (bp.getId() != player.getId()) {
                        GameRole gr = GameWorld.getPtr().getOnlineRole(bp.getId());
                        if (gr != null)
                            gr.putMessageQueue(appearMsg);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    // BOSS之家奖励的任务
    public static Task vipBossRewardTask = new Task() {
        @Override
        public void run() {
            try {
                vipRewardLock.lock();
                for (Byte layer : vipBoss.keySet()) {
                    for (Boss boss : vipBoss.get(layer).values()) {
                        VipBossData model = BossModel.getVipBossMap().get(layer).get(boss.getId());
                        if (boss.getHp() <= 0 && !boss.isReward()) {
                            boss.setReward(true);
                            BossBattlePlayer caller = boss.getCaller();
                            for (BossBattlePlayer bbp : boss.getBattlePlayers().values()) {
                                GameRole gr = GameWorld.getPtr().getOnlineRole(bbp.getId());
                                if (gr == null)
                                    continue;
                                // 是否伤害第一
                                boolean first = caller == null ? false : caller.getId() == bbp.getId();
                                // 奖励数量
                                int num = 0;
                                // TODO
                                if (bbp.getRein() <= 0) {
                                    if (bbp.getLevel() / 10 >= BossService.CIT_LV_EQUIP.length)
                                        num = BossService.CIT_LV_EQUIP[BossService.CIT_LV_EQUIP.length - 1];
                                    else
                                        num = BossService.CIT_LV_EQUIP[bbp.getLevel() / 10];
                                } else {
                                    if (bbp.getRein() >= BossService.CIT_REIN_EQUIP.length)
                                        num = BossService.CIT_REIN_EQUIP[BossService.CIT_REIN_EQUIP.length - 1];
                                    else
                                        num = BossService.CIT_REIN_EQUIP[bbp.getRein()];
                                }
                                List<DropData> rewards = new ArrayList<>();
                                rewards.addAll(model.getRewards());
                                // 使用哪个随机
                                int[] rates = null;
                                if (first) {
                                    if (bbp.getRein() >= 3)
                                        rates = BossService.VB_FIRST_HIG;
                                    else
                                        rates = BossService.VB_FIRST_LOW;
                                } else {
                                    if (bbp.getRein() >= 3)
                                        rates = BossService.VB_COMMON_HIG;
                                    else
                                        rates = BossService.VB_COMMON_LOW;
                                }
                                FighterData fd = FighterModel.getFighterDataById(model.getModelId());
                                // 随机装备
                                for (int i = 0; i < num; i++) {
                                    int random = GameUtil.getRangedRandom(1, 10000);
                                    int quality = GameUtil.getRatesIndex(rates, random);
                                    EquipData equipData = GoodsModel.getRandomDataByLv(fd.getLevel());
                                    if (equipData == null)
                                        continue;
                                    DropData drop = new DropData(EGoodsType.EQUIP.getId(), equipData.getGoodsId(),
                                            (byte) quality, 1);
                                    rewards.add(drop);
                                    if (quality >= EGoodsQuality.ORANGE.getValue()) {
                                        // 跑马灯
                                        ChatService.broadcastPlayerMsg(gr.getPlayer(), EBroadcast.BOSS_ORANGE,
                                                fd.getName(), equipData.getName(),
                                                GameUtil.getLvConvertStr(equipData.getLevel()));
                                    }
                                }
                                // 消息
                                Message msg = new Message(MessageCommand.BOSS_VIP_REWARD_MESSAGE);
                                msg.setByte(1);
                                msg.setByte(first);
                                msg.setByte(rewards.size());
                                for (DropData data : rewards) {
                                    msg.setByte(data.getT());
                                    msg.setShort(data.getG());
                                    msg.setByte(data.getQ());
                                    msg.setInt(data.getN());
                                }
                                gr.putMessageQueue(msg);
                                // 增加物品
                                EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
                                gr.getPackManager().addGoods(rewards, EGoodsChangeType.VIP_BOSS_ADD, saves);
                                //发送参加BOSS之家消息
                                gr.getEventManager().notifyEvent(new GameEvent(EGameEventType.ENTER_VIP_BOSS, 1, saves));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("BOSS之家发放奖励时发生异常", e);
            } finally {
                vipRewardLock.unlock();
            }
        }

        @Override
        public String name() {
            return "VIPBOSSREWARDTASK";
        }
    };

    // BOSS之家定时刷新任务
    public static Task vipBossRefrshTask = new Task() {
        @Override
        public void run() {
            try {
                long curr = System.currentTimeMillis();
                for (Byte layer : vipBoss.keySet()) {
                    for (Boss boss : vipBoss.get(layer).values()) {
                        VipBossData model = BossModel.getVipBossMap().get(layer).get(boss.getId());
                        FighterData fd = FighterModel.getFighterDataById(model.getModelId());
                        if (fd != null) {
                            boss.initHp(fd.getHp());
                            boss.setStartTime(curr);
                            boss.setReward(false);
                            boss.getBattlePlayers().clear();
                            boss.clearRanks();
                            boss.setDamageMin(0);
                            boss.setCaller(null);
                            boss.setKiller(null);
                        }
                    }
                }
                vipBossTime = curr;
            } catch (Exception e) {
                logger.error("BOSS之家刷新时发生异常", e);
            }
        }

        @Override
        public String name() {
            return "vipBossRefrshTask";
        }
    };

    public static Map<Short, Boss> getMysteryBoss() {
        return mysteryBoss;
    }

    public static Map<Byte, Map<Short, Boss>> getVipBoss() {
        return vipBoss;
    }

    public static long getVipBossTime() {
        return vipBossTime;
    }

}
