package com.rd.game;

import com.google.common.base.Preconditions;
import com.rd.bean.fight.monstersiege.GameMonsterAttacker;
import com.rd.bean.fight.monstersiege.GameMonsterData;
import com.rd.bean.fight.monstersiege.PlayerMonsterRecord;
import com.rd.dao.GameMonsterSiegeDao;
import com.rd.dao.PlayerMonsterSiegeDao;
import com.rd.define.ErrorDefine;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 怪物攻城
 * 战场服务
 */
public class GameMonsterSiegeService {
    private static final Logger logger = Logger.getLogger(GameMonsterSiegeService.class);
    /**
     * 战场怪物集合
     **/
    private static ConcurrentHashMap<Short, GameMonsterData> monsters;

    //---------------------------------------------- 分割线 以下临时数据 --------------------------------------------//
    /**
     * 当前周几
     **/
    private static int weekDay;
    /**
     * 计算值
     * 积分排行榜
     * 只在战斗结束/结算后 有限次数 插入数据的有序集合
     **/
    private static List<GameMonsterAttacker> rankList;

    private static ThreadLocal<GameMonsterSiegeDao> dao = new ThreadLocal<>();

    private static GameMonsterSiegeDao getDao() {
        GameMonsterSiegeDao curr = dao.get();
        if (curr == null) {
            curr = new GameMonsterSiegeDao();
            dao.set(curr);         //保存到线程局部变量中
        }
        return curr;
    }

    //--------------------------------------------------------------------------------------------------------------//
    public static void init() {
        weekDay = DateUtil.getWeekDay();
//        int monsterSize = MonsterSiegeModel.getMonsterCount(weekDay);
        monsters = new ConcurrentHashMap<>(getDao().getMonsters());
        loadRanks();
    }

    /**
     * 每日重置
     */
    public static void onDaily() {
        weekDay = DateUtil.getWeekDay();
        monsters = new ConcurrentHashMap<>();
        getDao().clear();
    }

    public static void onWeekly() {
        rankList.clear();
    }


    /**
     * 帧更新
     *
     * @param ts
     */
    public static void onTick(long ts) {
        // 更新伤害 从monster出发
        for (GameMonsterData monster : monsters.values()) {
            // 更新战斗
            if (monster.update(ts)) {
                // TODO 优化
                getDao().updateMonster(monster);
            }
        }
    }

    /**
     * 获取攻城数据
     *
     * @param message
     * @return
     */
    public static void getInfoMessage(Message message) {
        message.setByte(weekDay);
        message.setByte(monsters.size());
        for (GameMonsterData gameMonsterData : monsters.values()) {
            gameMonsterData.getMessage(message);
        }
    }

    public static Message getRankListMessage() {
        Message message = new Message(MessageCommand.MONSTER_SIEGE_RANK_MESSAGE);
        message.setByte(rankList.size());
        for (GameMonsterAttacker attacker : rankList) {
            attacker.getMessage(message);
        }
        return message;
    }

    /**
     * 开始进攻怪物
     *
     * @param gameRole
     * @param monsterId
     * @return errorCode
     */
    public static short startBattle(GameRole gameRole, short monsterId) {
        GameMonsterData monster = getMonster(monsterId);
        if (monster == null) {
            GameMonsterData previous = monsters.putIfAbsent(monsterId, new GameMonsterData(monsterId));
            monster = getMonster(monsterId);
            if (previous == null) {
                getDao().insertMonster(monster);
            }
        }
        short error = monster.startBattle(gameRole);
        if (error != ErrorDefine.ERROR_NONE) {
            return error;
        }
        getDao().updateMonster(monster);
        return ErrorDefine.ERROR_NONE;
    }

    public static void resetMonster(short monsterId) {
        GameMonsterData existData = monsters.get(monsterId);
        if (existData == null) {
            return;
        }
        GameMonsterData monsterData = new GameMonsterData(monsterId);
        monsters.put(monsterId, monsterData);
        getDao().updateMonster(monsterData);

        logger.info("重置攻城怪:" + existData.toString());
    }

    /**
     * 结束战斗
     * 其实没啥要做的
     *
     * @param playerId
     * @param monsterId
     * @param currentTime
     */
    public static PlayerMonsterRecord endBattle(int playerId, short monsterId, long currentTime) {
        GameMonsterData monster = getMonster(monsterId);
        Preconditions.checkNotNull(monster, "Cannot find GameMonsterData with id =" + monsterId);
        GameMonsterAttacker attacker = monster.getAttacker(playerId);
        Preconditions.checkNotNull(attacker, "Cannot find GameMonsterAttacker with id =" + playerId);
        return new PlayerMonsterRecord(monsterId, attacker.getValue(), currentTime);
    }

    public static void loadRanks() {
        //rankList = ImmutableList.copyOf(new PlayerMonsterSiegeDao().getRankList());
        rankList = new PlayerMonsterSiegeDao().getRankList();
    }

//    /**
//     * 更新排行
//     * @param attacker
//     * @param fromValue 原始积分
//     * @param toValue 新积分
//     */
//    public static void updateRank(IGameMonsterAttacker attacker, int fromValue, int toValue){
//        if (fromValue == toValue){
//            return;
//        }
//        if (rankList.size() >= MonsterSiegeModel.RANK_CAPACITY){
//            // 分布不均 大概率未上榜 最小值优化
//            GameMonsterAttacker last = rankList.get(rankList.size()-1);
//            if (last.getValue() > toValue){
//                return;
//            }
//        }
//        GameMonsterAttacker fromRank = new GameMonsterAttacker(attacker.getId(), attacker.getName(), attacker.getHead(), fromValue);
//        synchronized (rankList) {
//            int fromIdx = Collections.binarySearch(rankList, fromRank, GameMonsterAttacker.comparator);
//            // 找到原来的数据
//            if (fromIdx > 0) {
//                // 同分数查找玩家
//                for (; fromIdx < rankList.size(); fromIdx++) {
//                    GameMonsterAttacker currentRank = rankList.get(fromIdx);
//                    if (currentRank.getId() == attacker.getId()){
//                        // 移除原来的数据
//                        rankList.remove(fromIdx);
//                        break;
//                    }
//                }
//            }
//            // 插入新的数据
//            GameMonsterAttacker toRank = new GameMonsterAttacker(attacker.getId(), attacker.getName(), attacker.getHead(), toValue);
//            int toIdx = Collections.binarySearch(rankList, toRank, GameMonsterAttacker.comparator);
//            toIdx = toIdx >= 0 ? toIdx : -toIdx - 1;
//            rankList.add(toIdx, toRank);
//        }
//    }

    public static int getWeekDay() {
        return weekDay;
    }

    public static GameMonsterData getMonster(short monsterId) {
        return monsters.get(monsterId);
    }


}
