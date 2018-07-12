package com.rd.bean.fight.monstersiege;

import com.rd.action.GameAction;
import com.rd.bean.fight.monstersiege.state.GameMonsterState;
import com.rd.bean.fight.monstersiege.state.MonsterFSM;
import com.rd.bean.fight.monstersiege.state.MonsterSiegeDefine;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.Player;
import com.rd.common.MailService;
import com.rd.dao.PlayerMonsterSiegeDao;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.define.TextDefine;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.manager.MonsterSiegeManager;
import com.rd.model.MonsterSiegeModel;
import com.rd.model.data.MonsterSiegeModelData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 攻城怪 游戏数据
 */
public class GameMonsterData {
    private static final Logger logger = Logger.getLogger(GameMonsterData.class);

    private short id;
    private byte state;
    /**
     * 奖励结算标志 还没做成子状态
     **/
    private boolean rewardFlag;
    //--------------------------------- 以下是状态数据 ------------------------------------//
    private Map<Integer, GameMonsterAttacker> attackers;
    /**
     * 结束时间
     **/
    private long deadline;
    //--------------------------------- 以下是辅助数据------------------------------------//
    private MonsterFSM fsm;
    private long damageUpdateTime;

    public GameMonsterData(short id) {
        this(id, MonsterSiegeDefine.EMonsterSiegeState.Born, new HashMap<>(), 0, false);
    }

    public GameMonsterData(short id,
                           MonsterSiegeDefine.EMonsterSiegeState state,
                           Map<Integer, GameMonsterAttacker> attackers,
                           long deadline,
                           boolean rewardFlag) {
        this.id = id;
        this.state = state.getId();
        this.attackers = new ConcurrentHashMap<>(attackers);
        this.deadline = deadline;
        this.rewardFlag = rewardFlag;

        GameMonsterState currentState = state.build(this);
        this.fsm = new MonsterFSM(currentState);
        this.damageUpdateTime = System.currentTimeMillis();
    }

    public short getId() {
        return id;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public Map<Integer, GameMonsterAttacker> getAttackers() {
        return attackers;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public void getMessage(Message message) {
        message.setShort(id);
        message.setByte(state);
        int restTime = (int) ((deadline - System.currentTimeMillis()) / DateUtil.SECOND);
        message.setInt(restTime < 0 ? 0 : restTime);
        getAttackersMessage(message);
    }

    public void getAttackersMessage(Message message) {
        message.setByte(attackers.size());
        for (GameMonsterAttacker attacker : attackers.values()) {
            attacker.getMessage(message);
        }
    }

    /**
     * 挑战攻城怪
     *
     * @param gameRole
     * @return 错误码
     */
    public short startBattle(GameRole gameRole) {
        synchronized (fsm) {
            GameMonsterState currentState = fsm.getCurrentState();
            short error = currentState.checkAttacked(gameRole);
            if (error != ErrorDefine.ERROR_NONE) {
                return error;
            }
            Player player = gameRole.getPlayer();
            GameMonsterAttacker attacker = new GameMonsterAttacker(player.getId(), player.getName(), player.getHead(), 0);
            attackers.put(player.getId(), attacker);

            fsm.updateState();
            return ErrorDefine.ERROR_NONE;
        }
    }

    /**
     * 帧更新
     * 更新状态
     * 更新战斗
     *
     * @param ts
     * @return
     */
    public boolean update(long ts) {
        // 更新状态
        synchronized (fsm) {
            fsm.updateState();
        }

        // 更新战斗和奖励 只在此线程更新，不影响状态没有同步
        boolean dirtyBattle = updateBattle(ts);
        if (dirtyBattle) {
            broadcastSyncMessage();
        }
        boolean dirtyReward = !isRewardFlag() && updateReward();
        return dirtyBattle || dirtyReward;
    }

    /**
     * 伤害同步
     */
    private void broadcastSyncMessage() {
        Message message = new Message(MessageCommand.MONSTER_SIEGE_SYNC_MESSAGE);
        getAttackersMessage(message);
        for (GameMonsterAttacker attacker : attackers.values()) {
            GameRole gameRole = GameWorld.getPtr().getOnlineRole(attacker.getId());
            if (gameRole != null) {
                gameRole.putMessageQueue(message);
            }
        }
    }

    private boolean updateReward() {
        GameMonsterState currentState = fsm.getCurrentState();
        if (!currentState.checkReward()) {
            return false;
        }
        reward();
        return true;
    }

    private void reward() {
        setRewardFlag(true);
        // 排行 发奖
        MonsterSiegeModelData modelData = MonsterSiegeModel.getMonsterSiege(id);
        List<GameMonsterAttacker> attackerList = new ArrayList<>(attackers.values());
        Collections.sort(attackerList, GameMonsterAttacker.comparator);

        for (int rank = 1; rank <= attackerList.size(); rank++) {
            GameMonsterAttacker attacker = attackerList.get(rank - 1);
            int playerId = attacker.getId();
            int finalRank = rank;
            GameAction.submit(playerId, () -> {
                int score = modelData.getScore(finalRank);
                GameRole gameRole = GameWorld.getPtr().getOnlineRole(playerId);
                if (gameRole != null) {
                    gameRole.getMonsterSiegeManager().getPlayerData().addScore(score);
                }
                new PlayerMonsterSiegeDao().addScore(playerId, score);

                logger.info("怪物攻城战斗结算:playerId=" + playerId + ",monsterId=" + id + ",rank=" + finalRank + ",score=" + score);
                Mail mail = MailService.createMail(TextDefine.MONSTER_BATTLE_REWARD_TITLE,
                        TextDefine.MONSTER_BATTLE_REWARD_CONTENT,
                        EGoodsChangeType.MONSTER_SIEGE_BATTLE_ADD,
                        modelData.getRewardList(finalRank));
                MailService.sendSystemMail(playerId, mail);
            });
        }
    }


    public int getAttackTimes() {
        return getAttackers().size();
    }

    public boolean containsAttacker(int playerId) {
        return attackers.containsKey(playerId);
    }

    private long getDamageUpdateTime() {
        return damageUpdateTime;
    }

    private void setDamageUpdateTime(long damageUpdateTime) {
        this.damageUpdateTime = damageUpdateTime;
    }

    /**
     * 更新战斗
     * 计算伤害
     *
     * @param ts
     * @return 是否变更
     */
    private boolean updateBattle(long ts) {
        long lastTime = getDamageUpdateTime();
        // 没到更新时间
        if (ts - lastTime < MonsterSiegeModel.UPDATE_INTERVAL) {
            return false;
        }
        setDamageUpdateTime(ts);
        // 结算检查
        boolean dirty = false;
        for (GameMonsterAttacker attacker : attackers.values()) {
            GameRole gameRole = GameWorld.getPtr().getOnlineRole(attacker.getId());
            if (gameRole == null) {
                continue;
            }
            MonsterSiegeManager attackerManager = gameRole.getMonsterSiegeManager();
            // 不在这场战斗了
            if (!attackerManager.isInBattle(id)) {
                continue;
            }
            // 战斗结束
            if (!attackerManager.updateBattle(ts)) {
                continue;
            }
            // 在updateBattle中异步退出 这里获取不到结果
//            if (!attackerManager.isInBattle(id)) {
//                continue;
//            }

            // 普通伤害输出
            int dmg = (int) (gameRole.getPlayer().getFighting() * 0.125 * MonsterSiegeModel.UPDATE_INTERVAL / DateUtil.SECOND);
            // 技能伤害输出
            if (attackerManager.getSkillTime() > lastTime && attackerManager.getSkillTime() <= ts) {
                dmg += gameRole.getMonsterSiegeManager().getCombineSkillDamage();
            }
            attacker.addValue(dmg);
            dirty = true;
        }

        return dirty;
    }

    public String getAttackersJson() {
        return StringUtil.obj2Gson(attackers);
    }

    public GameMonsterAttacker getAttacker(int playerId) {
        return attackers.get(playerId);
    }

    public boolean isRewardFlag() {
        return rewardFlag;
    }

    public void setRewardFlag(boolean rewardFlag) {
        this.rewardFlag = rewardFlag;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id=").append(getId()).append(",")
                .append("deadline=").append(getDeadline()).append(",")
                .append("state=").append(getState()).append(",")
                .append("attackers=").append(StringUtil.obj2Gson(getAttackers()));
        return builder.toString();
    }
}
