package com.rd.bean.boss;

import com.google.common.collect.Ordering;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.rd.bean.player.Player;
import com.rd.common.BossService;
import com.rd.common.FightCommon;
import com.rd.common.GameCommon;
import com.rd.define.ErrorDefine;
import com.rd.define.FightDefine;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.model.BossModel;
import com.rd.model.NBossModel;
import com.rd.model.data.BossMysteryData;
import com.rd.model.data.copy.quanmin.QianMinBossData;
import com.rd.net.message.Message;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 探索出的BOSS
 *
 * @author Created by U-Demon on 2016年11月25日 下午12:37:24
 * @version 1.0.0
 *          TODO fsm
 */
public class Boss {

    private static Logger logger = Logger.getLogger(Boss.class);

    //唯一ID
    private int uuid;

    //模型ID
    private short id;

    //最大血量
    private long hpMax = 0;
    //当前血量
    private volatile long hp = 0;

    //攻击
    private int atk = 0;

    //开始时间
    private long startTime;

    //死亡时间
    private long deadTime = 0;

    //是否发放了奖励
    private volatile boolean reward = false;

    //参战的所有玩家
    private Map<Integer, BossBattlePlayer> battlePlayers = new ConcurrentHashMap<>();

    /**
     * 不清楚其他boss逻辑没有修改，考虑从battlePlayers获取ranks避免数据不一致
     * 注:使用getRanks()方法的ranks.ascendingMap()遍历才可以保证顺序
     * FIXME 这个库不稳定 换掉
     */
    private ConcurrentLinkedHashMap<Integer, BossBattlePlayer> ranks =
            new ConcurrentLinkedHashMap.Builder<Integer, BossBattlePlayer>().maximumWeightedCapacity(BossService.RANK_CAPACITY).build();
    //榜单最后一名的伤害
    private volatile long damageMin = 0;
    //上次排行
    private List<BossBattlePlayer> history = null;
    private BossBattlePlayer lastKiller = null;

    //召唤玩家
    private volatile BossBattlePlayer caller = null;

    //击杀玩家
    private BossBattlePlayer killer = null;

    private volatile long pkTime = 0;

    public void getBossMsg(Message msg, int playerId) {
        msg.setInt(uuid);
        msg.setShort(id);
        msg.setLong(hp);
        msg.setLong(hpMax);
        if (hp <= 0 || deadTime != 0)
            msg.setInt(0);
        else {
            long curr = System.currentTimeMillis();
            msg.setInt((int) ((startTime + BossModel.EXIST_TIME - curr) / 1000));
        }
        if (caller == null) {
            msg.setByte(0);
        } else {
            msg.setByte(1);
            caller.getSimpleMessage(msg);
        }
        if (killer == null) {
            msg.setByte(0);
        } else {
            msg.setByte(1);
            killer.getSimpleMessage(msg);
        }
        msg.setInt(getReliveCount(playerId));
    }

    public int getReliveCount(int playerId) {
        BossBattlePlayer bbp = battlePlayers.get(playerId);
        if (bbp != null) {
            return bbp.getRelive();
        } else {
            return 0;
        }
    }

    /**
     * 改变BOSS血量
     * 如果返回true，表示击杀
     *
     * @param hp
     */
    public synchronized boolean changeHp(BossBattlePlayer bbp, int hp) {
        if (this.hp <= 0)
            return false;
        this.hp += hp;
        //击杀BOSS
        if (this.hp <= 0) {
            this.hp = 0;
            this.deadTime = System.currentTimeMillis();
            this.killer = bbp;
            //BOSS奖励的任务
            TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, BossService.bossRewardTask, 100);
            //广播
//			ChatService.broadcastPlayerMsg(player, EBroadcast.BossKill, BossService.BOSS_NAMES.get((int)this.id));
            return true;
        }
        return false;
    }

    public synchronized boolean changeCitHp(BossBattlePlayer bbp, int hp) {
        if (this.hp <= 0)
            return false;
        this.hp += hp;
        //击杀BOSS
        if (this.hp <= 0) {
            this.hp = 0;
            this.hpMax = 0;
            this.deadTime = System.currentTimeMillis();
            this.killer = bbp;
            //BOSS发奖励的任务
            TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, BossService.citBossRewardTask, 100);
            //BOSS复活的任务 时间
            QianMinBossData data = NBossModel.getQianMinBossDataMap().get(id);
//			TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, BossService.citBossReviveNoticeTask, 
//					(data.getRebirthtime()-6)*1000);
            TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, BossService.citBossReviveTask,
                    data.getRebirthtime() * 1000);
            return true;
        }
        return false;
    }

    /**
     * 减少秘境BOSS血量
     *
     * @param bbp
     * @param hp
     * @return
     */
    public synchronized boolean changeMysteryHp(BossBattlePlayer bbp, int hp) {
        if (this.hp <= 0)
            return false;
        this.hp += hp;
        //击杀BOSS
        if (this.hp <= 0) {
            this.hp = 0;
            this.hpMax = 0;
            this.deadTime = System.currentTimeMillis();
            this.killer = bbp;
            //BOSS发奖励的任务
            TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, BossService.mysteryBossRewardTask, 100);
            //BOSS复活的任务 时间
            BossMysteryData data = BossModel.getMysteryMap().get(id);

            TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, BossService.mysteryBossReviveNoticeTask,
                    (data.getFuhuoTime() - 6) * 1000);
            TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, BossService.mysteryBossReviveTask,
                    data.getFuhuoTime() * 1000);
            return true;
        }
        return false;
    }

    /**
     * 减少BOSS之家血量
     *
     * @param bbp
     * @param hp
     * @return
     */
    public synchronized boolean changeVipHp(BossBattlePlayer bbp, int hp) {
        if (this.hp <= 0)
            return false;
        this.hp += hp;
        //击杀BOSS
        if (this.hp <= 0) {
            this.hp = 0;
            this.hpMax = 0;
            this.deadTime = System.currentTimeMillis();
            this.killer = bbp;
            //BOSS发奖励的任务
            TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, BossService.vipBossRewardTask, 100);
//			//BOSS复活的任务 时间
//			BossMysteryData data = BossModel.getMysteryMap().get(id);
//			
//			TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, BossService.mysteryBossReviveNoticeTask, 
//					(data.getFuhuoTime()-6)*1000);
//			TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, BossService.mysteryBossReviveTask, 
//					data.getFuhuoTime()*1000);
            return true;
        }
        return false;
    }

    public synchronized boolean changeReinHp(Player player, BossBattlePlayer bbp, int hp) {
        if (this.hp <= 0)
            return false;
        this.hp += hp;
        //击杀BOSS
        if (this.hp <= 0) {
            this.hp = 0;
            this.hpMax = 0;
            this.deadTime = System.currentTimeMillis();
            this.killer = bbp;
            //BOSS发奖励的任务
            TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, BossService.reinBossRewardTask, 100);
            return true;
        }
        return false;
    }

    public synchronized boolean changeReinPvPHp(BossBattlePlayer bbp, int hp, Task deadTask) {
        if (this.hp <= 0)
            return false;
        this.hp += hp;
        //击杀BOSS
        if (this.hp <= 0) {
            this.hp = 0;
            this.hpMax = 0;
            this.deadTime = System.currentTimeMillis();
            this.killer = bbp;
            //BOSS死亡的任务
            TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, deadTask, 100);
            return true;
        }
        return false;
    }

    /**
     * 加入排行榜
     *
     * @param player
     */
    private ReentrantLock topLock = new ReentrantLock();

    public void addRankSync(BossBattlePlayer battlePlayer) {
        if (battlePlayer.getDamage() <= damageMin)
            return;
        try {
            topLock.lock();

            // linkedMap对于排序不是最优解，此处copyOnWrite效率差。
            // 但所有boss共用一个ranks集合。适用于对于只包含查找和更新指定元素的ranks。
            //加入元素重排
            ConcurrentLinkedHashMap<Integer, BossBattlePlayer> tmp = new ConcurrentLinkedHashMap.Builder<Integer, BossBattlePlayer>().maximumWeightedCapacity(BossService.RANK_CAPACITY).build();
            tmp.putAll(ranks);
            tmp.put(battlePlayer.getId(), battlePlayer);
            Ordering<BossBattlePlayer> ordering = Ordering.natural();
            List<BossBattlePlayer> tmpList = ordering.greatestOf(tmp.values().iterator(), BossService.RANK_CAPACITY);

            //重新赋值
            tmp.clear();
            for (BossBattlePlayer bbp : tmpList) {
                tmp.put(bbp.getId(), bbp);
            }
            ranks = tmp;
            damageMin = tmpList.size() > 0 ? tmpList.listIterator().next().getDamage() : 0;
        } catch (Exception e) {
            logger.error("将玩家加入BOSS伤害排行榜时发生异常!", e);
            // FIXME 这种补救会导致另外的问题 逻辑不清楚暂时保留
            removeRank(battlePlayer.getId());
        } finally {
            topLock.unlock();
        }
    }

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public long getHp() {
        return hp;
    }

    public void resetHp() {
        this.hp = 0;
    }

    public void initHp(long hp) {
        this.hpMax = hp;
        this.hp = this.hpMax;
    }

    public long getHpMax() {
        return hpMax;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDeadTime() {
        return deadTime;
    }

    public void setDeadTime(long deadTime) {
        this.deadTime = deadTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Map<Integer, BossBattlePlayer> getBattlePlayers() {
        return battlePlayers;
    }

    public BossBattlePlayer getBattlePlayer(int playerId) {
        return battlePlayers.get(playerId);
    }

//	public List<BossBattlePlayer> getRanks() {
//		return ranks;
//	}

    public Map<Integer, BossBattlePlayer> getRanks() {
        return ranks.ascendingMap();
    }

    public boolean isRankExisted(int id) {
        return ranks.containsKey(id);
    }

    public void addRank(BossBattlePlayer battlePlayer) {
        ranks.put(battlePlayer.getId(), battlePlayer);
    }

    public List<BossBattlePlayer> getHistory() {
        return history;
    }

    public void setHistory(List<BossBattlePlayer> history) {
        this.history = history;
    }

    public BossBattlePlayer getLastKiller() {
        return lastKiller;
    }

    public void setLastKiller(BossBattlePlayer lastKiller) {
        this.lastKiller = lastKiller;
    }

    public BossBattlePlayer getKiller() {
        return killer;
    }

    public void setKiller(BossBattlePlayer killer) {
        this.killer = killer;
    }

    public boolean isReward() {
        return reward;
    }

    public void setReward(boolean reward) {
        this.reward = reward;
    }

    public void setDamageMin(int damageMin) {
        this.damageMin = damageMin;
    }

    /**
     * 争夺归属权
     *
     * @param player
     * @return
     */
    public synchronized int[] fightForBelonging(Player player) {
        this.pkTime = System.currentTimeMillis();
        int[] state = new int[3];
        if (this.caller == null || this.caller.getId() == player.getId()) {
            state[0] = ErrorDefine.ERROR_OPERATION_FAILED;
            return state;
        }
        GameRole pkRole = GameWorld.getPtr().getOnlineRole(this.caller.getId());
        if (pkRole == null) {
            state[0] = ErrorDefine.ERROR_OPERATION_FAILED;
            return state;
        }

        byte result = FightCommon.playerVsPlayerFormula(player, pkRole.getPlayer());

        int lostId = player.getId();
        if (result == FightDefine.FIGHT_RESULT_SUCCESS) {
            lostId = this.caller.getId();
            //归属者
            resetCitizenCaller(player.getId());

            state[1] = player.getId();
            state[2] = lostId;
        } else {
            state[1] = this.caller.getId();
            state[2] = lostId;
        }
        //死亡时间
        BossBattlePlayer fighter = this.getBattlePlayer(lostId);
        fighter.setDeadTime(System.currentTimeMillis());
        //removeRank(lostId);

        state[0] = ErrorDefine.ERROR_NONE;
        return state;
    }

    public void removeRank(int id) {
        ranks.remove(id);
    }

    public void clearRanks() {
        ranks.clear();
    }

    public BossBattlePlayer getFirstRank() {
        Iterator<BossBattlePlayer> iterator = getRanks().values().iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public int getFirstRank(int id) {
        return isFirstRank(id) ? GameCommon.True : GameCommon.False;
    }

    public boolean isCaller(int id) {
        if (caller == null) {
            return false;
        }
        return caller.getId() == id;
    }

    public boolean isFirstRank(int id) {
        BossBattlePlayer firstPlayer = getFirstRank();
        if (firstPlayer == null) {
            return false;
        }
        return firstPlayer.getId() == id;
    }

    public BossBattlePlayer getCaller() {
        return caller;
    }

    /**
     * @param caller
     */
    public void setCaller(BossBattlePlayer caller) {
        this.caller = caller;
        //logger.info("Caller=" + StringUtil.obj2Gson(caller));
    }

    public void resetCitizenCaller(int playerId) {
        if (playerId == 0) {
            BossBattlePlayer caller = getFirstRank();
            this.setCaller(caller);
        } else {
            BossBattlePlayer bbp = this.getBattlePlayer(playerId);
            if (bbp != null)
                this.setCaller(bbp);
        }
    }

    public void removePlayer(int id) {
        //修改： 不要级联操作
        ranks.remove(id);
        battlePlayers.remove(id);
        if (caller != null && caller.getId() == id) {
            setCaller(getFirstRank());
        }
    }

    public long getPkTime() {
        return pkTime;
    }

    public void setPkTime(long pkTime) {
        this.pkTime = pkTime;
    }

}
