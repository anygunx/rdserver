package com.rd.game.pvp;

import com.google.common.collect.ImmutableList;
import com.rd.bean.boss.Boss;
import com.rd.bean.boss.BossBattlePlayer;
import com.rd.bean.fighter.FighterData;
import com.rd.bean.player.AttrCharacter;
import com.rd.bean.player.BattlePlayer;
import com.rd.common.BossService;
import com.rd.dao.jedis.JedisManager;
import com.rd.define.EAttrType;
import com.rd.game.local.GameHttpManager;
import com.rd.model.BossModel;
import com.rd.model.FighterModel;
import com.rd.model.data.BossReinData;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 跨服战BOSS管理器
 *
 * @author U-Demon Created on 2017年5月31日 下午2:17:09
 * @version 1.0.0
 */
public class BossPvPManager extends BasePvPManager {

    private static final Logger logger = Logger.getLogger(BossPvPManager.class);

    //在REDIS中存储的KEY
    private static final String REDIS_BOSS_REIN_LV_KEY = "BOSS_REIN_LV";

    //////////////////////////转生BOSS//////////////////////////
    private Map<Short, Boss> reinBoss = new ConcurrentHashMap<>();
    //////////////////////////转生BOSS的锁//////////////////////////
    private ReentrantLock reinInitLock = new ReentrantLock();
    private ReentrantLock reinRewardLock = new ReentrantLock();

    @Override
    public void init() {
        super.init();
    }

    /**
     * 初始化转生BOSS
     */
    public void initReinBoss() {
        long curr = System.currentTimeMillis();
        try {
            reinInitLock.lock();
            //根据数据表初始化所有转生BOSS
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
                //转生BOSS的血量
                byte lv = BossService.BOSS_REIN_LV_INIT;
                String lvStr = JedisManager.gi().hget(getBossReinLvKey(), boss.getId() + "");
                if (lvStr != null)
                    lv = Byte.valueOf(lvStr);
                boss.initHp((long) (fd.getHp() * BossService.BOSS_REIN_LV[lv][0]));
                //BOSS攻击力
                boss.setAtk((int) (fd.getAtk() * BossService.BOSS_REIN_LV[lv][1]));
                boss.setStartTime(curr);
                boss.setDeadTime(0);
                boss.setReward(false);
                boss.getBattlePlayers().clear();
                boss.clearRanks();
                boss.setDamageMin(0);
                boss.setKiller(null);
            }
            //转生BOSS结束的任务  时间
            //TODO 转生BOSS时长
            TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, reinPvPEndTask, BossService.BOSS_REIN_DURATION);
            //转生BOSS目标追踪
            TaskManager.getInstance().schedulePeriodicTask(ETaskType.COMMON, reinPvPTargetTask,
                    100, 1000);
        } catch (Exception e) {
            logger.error("初始化跨服转生BOSS时发生异常", e);
        } finally {
            reinInitLock.unlock();
        }
    }

    /**
     * 攻击转生BOSS
     *
     * @param playerId
     * @param bossId
     * @param damage
     * @return
     */
    public int atkReinBoss(int playerId, short bossId, int damage) {
        if (damage == 0)
            return -1;
        Boss boss = reinBoss.get(bossId);
        if (boss == null)
            return -2;
        BattlePlayer player = PvPWorld.gi().getBattlePlayer(playerId);
        if (player == null)
            return -3;
        //战斗数据
        BossBattlePlayer battlePlayer = boss.getBattlePlayer(playerId);
        //玩家伤害增加
        battlePlayer.addDamage(damage);
        battlePlayer.setLastTime(System.currentTimeMillis());
        //BOSS血量减少
        boss.changeReinPvPHp(battlePlayer, -damage, reinPvPDeadTask);
        boss.addRankSync(battlePlayer);
        return 1;
    }

    /**
     * 转生BOSS死亡的任务
     */
    private Task reinPvPDeadTask = new Task() {
        @Override
        public void run() {
            try {
                reinRewardLock.lock();
                long curr = System.currentTimeMillis();
                for (Boss boss : reinBoss.values()) {
                    if (boss.getHp() <= 0 && !boss.isReward()) {
                        boss.setLastKiller(boss.getKiller());
                        boss.setHistory(ImmutableList.copyOf(boss.getRanks().values()));
                        boss.setReward(true);
                        //TODO 通知游戏服奖励
                        //BOSS等级
                        byte lv = BossService.BOSS_REIN_LV_INIT;
                        String lvStr = JedisManager.gi().hget(getBossReinLvKey(), boss.getId() + "");
                        if (lvStr != null)
                            lv = Byte.valueOf(lvStr);
                        if (lv < 20) {
                            //BOSS 5分钟内死亡 等级+1
                            if (curr - boss.getStartTime() <= 5 * DateUtil.MINUTE) {
                                JedisManager.gi().hset(getBossReinLvKey(), boss.getId() + "", (lv + 1) + "");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("跨服转生BOSS死亡时发生异常", e);
            } finally {
                reinRewardLock.unlock();
            }
        }

        @Override
        public String name() {
            return "reinPvPDeadTask";
        }
    };

    //转生BOSS结束
    private Task reinPvPEndTask = new Task() {
        @Override
        public void run() {
            try {
                TaskManager.getInstance().cancleTask(ETaskType.COMMON, "reinPvPTargetTask");
                reinRewardLock.lock();
                for (Boss boss : reinBoss.values()) {
                    if (boss.getHp() > 0 && !boss.isReward()) {
                        boss.setLastKiller(boss.getKiller());
                        boss.setHistory(ImmutableList.copyOf(boss.getRanks().values()));
                        boss.resetHp();
                        boss.setReward(true);
                        //TODO 通知游戏服奖励
                        //BOSS等级-1
                        byte lv = BossService.BOSS_REIN_LV_INIT;
                        String lvStr = JedisManager.gi().hget(getBossReinLvKey(), boss.getId() + "");
                        if (lvStr != null)
                            lv = Byte.valueOf(lvStr);
                        if (lv > 1) {
                            JedisManager.gi().hset(getBossReinLvKey(), boss.getId() + "", (lv - 1) + "");
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("跨服转生BOSS结束时发生异常", e);
            } finally {
                reinRewardLock.unlock();
            }
        }

        @Override
        public String name() {
            return "reinPvPEndTask";
        }
    };

    /**
     * 转生BOSS攻击目标追踪
     */
    private Task reinPvPTargetTask = new Task() {
        @Override
        public void run() {
            try {
                long curr = System.currentTimeMillis();
                for (Boss boss : reinBoss.values()) {
                    if (boss.getHp() <= 0)
                        continue;
                    //是否切换目标
                    boolean change = false;
                    //当前目标为空
                    BossBattlePlayer target = boss.getCaller();
                    if (target == null) {
                        change = true;
                    } else {
                        BossBattlePlayer targetPlayer = boss.getBattlePlayer(target.getId());
                        if (targetPlayer == null) {
                            change = true;
                        }
                        //目标离线
                        else if (curr - targetPlayer.getLastTime() > BossService.FIGHT_OUTTIME) {
                            change = true;
                            targetPlayer.setDeadTime(curr);
                        }
                        //目标死亡
                        else if (target.getDamage() * BossService.BOSS_FIGHT_SPACE + target.getLastTime() <= curr) {
                            change = true;
                            targetPlayer.setDeadTime(curr);
                        }
                    }
                    //切换目标
                    if (change) {
                        //找到伤害最高的玩家
                        BossBattlePlayer max = null;
                        for (BossBattlePlayer bbp : boss.getBattlePlayers().values()) {
                            //没打伤害
                            if (bbp.getDamage() <= 0)
                                continue;
                            //死亡未复活
                            if (bbp.getDeadTime() > 0 && bbp.getDeadTime() + BossService.REVIVE_TIME >= curr)
                                continue;
                            //离线
                            if (bbp.getLastTime() + BossService.FIGHT_OUTTIME < curr) {
                                //TODO 广播给游戏服所有人
                                continue;
                            }
                            if (max == null)
                                max = bbp;
                            else if (bbp.getDamage() > max.getDamage()) {
                                max = bbp;
                            }
                        }
                        if (max != null) {
                            BattlePlayer player = PvPWorld.gi().getBattlePlayer(max.getId());
                            if (player != null) {
                                BossBattlePlayer targetNew = new BossBattlePlayer(player);
                                //成为目标的时间
                                targetNew.setLastTime(curr);
                                //计算回合数：伤害=（攻-防）*技能系数+技能额外伤害
                                //伤害>攻*5%，伤害=伤害；伤害<=攻*5%，伤害=攻*5%
                                int roundTotal = 0, hpTotal = 0;
                                for (AttrCharacter cha : player.getAcs()) {
                                    int[] attr = cha.getAttribute();
                                    int bossDamage = (int) ((boss.getAtk() -
                                            (attr[EAttrType.PHYDEF.getId()] + attr[EAttrType.MAGICDEF.getId()]) / 2)
                                            * BossService.BOSS_REIN_SKILL_BASE + BossService.BOSS_REIN_SKILL_ADD);
                                    if (bossDamage <= boss.getAtk() * 0.05f)
                                        bossDamage = (int) (boss.getAtk() * 0.05f);
                                    if (bossDamage < 1)
                                        bossDamage = 1;
                                    int round = attr[EAttrType.HP.getId()] / bossDamage;
                                    if (round < 1)
                                        round = 1;
                                    roundTotal += round;
                                    hpTotal += attr[EAttrType.HP.getId()];
                                }
                                targetNew.setDamage(roundTotal < 1 ? 1 : roundTotal);
                                //血量上限
                                targetNew.setRelive(hpTotal);
                                //设置目标
                                boss.setCaller(targetNew);
                                //TODO 向游戏服发送新目标的消息
                            }
                        } else {
                            boss.setCaller(null);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("跨服转生BOSS追踪目标失败", e);
            }
        }

        @Override
        public String name() {
            return "reinPvPTargetTask";
        }
    };

    /**
     * 转生BOSS等级在Redis中的HashKey
     *
     * @return
     */
    private String getBossReinLvKey() {
        return REDIS_BOSS_REIN_LV_KEY + GameHttpManager.SPLIT + this.pvpId;
    }

}
