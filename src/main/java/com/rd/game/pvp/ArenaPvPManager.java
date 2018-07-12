package com.rd.game.pvp;

import com.google.common.collect.ImmutableList;
import com.rd.bean.player.AppearCharacter;
import com.rd.bean.player.ArenaChallenge;
import com.rd.bean.player.BattlePlayer;
import com.rd.bean.rank.PlayerRank;
import com.rd.bean.rank.ServerRank;
import com.rd.common.GameCommon;
import com.rd.dao.jedis.JedisManager;
import com.rd.game.local.ArenaGameService;
import com.rd.game.local.GameHttpManager;
import com.rd.util.DateUtil;
import com.rd.util.GameUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 跨服战管理器，每个PVP区一个管理器
 *
 * @author U-Demon Created on 2017年5月9日 下午3:06:00
 * @version 1.0.0
 */
public class ArenaPvPManager extends BasePvPManager {

    private static final Logger logger = Logger.getLogger(ArenaPvPManager.class);

    //在REDIS中存储的KEY
    private static final String REDIS_ARENA_KEY = "ARENA";

    //竞技场总人数
    private static final int RANK_CAPACITY = 100;
    //竞技场刷新挑战者排名范围 范围-该范围数量限制
    private static final int[][] CHALLENGE_RANGE = {{50, -1},
            {60, -1},
            {70, -1},
            {80, -1},
            {90, 50},
            {100, -1}};

    //战斗交换排名的锁
    private ReentrantLock rankLock = null;

    //竞技场列表
    private List<PlayerRank> rankList = null;
    private long time = 0;
    //服务器排名
    private List<ServerRank> serverRank;

    /**
     * 初始化竞技场列表
     */
    @Override
    public void init() {
        super.init();
        rankLock = new ReentrantLock();
        //列表为空
        if (rankList == null) {
            //先从REDIS中读取
            Map<String, String> history = JedisManager.gi().hgetAll(REDIS_ARENA_KEY + GameHttpManager.SPLIT + pvpId);
            if (history != null && history.size() > 0) {
                //数据的时间搓
                String timeValue = history.get("time");
                if (timeValue != null) {
                    time = Long.valueOf(timeValue);
                }
                rankList = new ArrayList<>();
                for (int i = 1; i <= RANK_CAPACITY; i++) {
                    String value = history.get("" + i);
                    if (value == null) {
                        rankList.add(null);
                    } else {
                        rankList.add(StringUtil.gson2Obj(value, PlayerRank.class));
                    }
                }
            }
        }
        refreshRankList();
    }

    /**
     * 刷新竞技场列表
     */
    public void refreshRankList() {
        long curr = System.currentTimeMillis();
        //今天是周几:0-6
        int dayOfWeek = DateUtil.getWeek() - 1;
        //是否开放
        boolean isOpen = false;
        long dayStart = 0, dayEnd = 0;
        for (int i = 0; i < ArenaGameService.OPEN_DAY.length; i++) {
            int day = ArenaGameService.OPEN_DAY[i];
            //判断是否开放
            if (dayOfWeek == day) {
                long day0 = DateUtil.getDayStartTime(curr);
                dayStart = day0 + ArenaGameService.OPEN_STARTTIME;
                dayEnd = day0 + ArenaGameService.OPEN_ENDTIME;
                if (curr >= dayStart && curr <= dayEnd)
                    isOpen = true;
            }
        }
        if (rankList == null)
            rankList = new ArrayList<>();
        //计算服务器排名
        refreshServerRank();
        //空位补机器人
        for (int i = 0; i < rankList.size(); i++) {
            if (rankList.get(i) == null) {
                PlayerRank robot = createRobot(i + 1);
                rankList.set(i, robot);
                JedisManager.gi().hset(REDIS_ARENA_KEY + GameHttpManager.SPLIT + pvpId,
                        robot.getRank() + "", StringUtil.obj2Gson(robot));
            }
        }
        for (int i = rankList.size(); i < RANK_CAPACITY; i++) {
            PlayerRank robot = createRobot(i + 1);
            rankList.add(robot);
            JedisManager.gi().hset(REDIS_ARENA_KEY + GameHttpManager.SPLIT + pvpId,
                    robot.getRank() + "", StringUtil.obj2Gson(robot));
        }
        if (!isOpen)
            return;
        //数据失效
        if (time < dayStart) {
            rankList.clear();
            //生成机器人
            for (int i = 0; i < RANK_CAPACITY; i++) {
                PlayerRank robot = createRobot(i + 1);
                rankList.add(robot);
                JedisManager.gi().hset(REDIS_ARENA_KEY + GameHttpManager.SPLIT + pvpId,
                        robot.getRank() + "", StringUtil.obj2Gson(robot));
            }
            time = curr;
            JedisManager.gi().hset(REDIS_ARENA_KEY + GameHttpManager.SPLIT + pvpId,
                    "time", curr + "");
        }
    }

    private void refreshServerRank() {
        //计算服务器排名
        Map<Integer, ServerRank> sr = new HashMap<>();
        try {
            rankLock.lock();
            for (int i = 0; i < rankList.size(); i++) {
                PlayerRank pr = rankList.get(i);
                if (pr == null || pr.getId() <= 0)
                    continue;
                if (!sr.containsKey(pr.getValue2())) {
                    ServerRank server = new ServerRank();
                    server.setServerId(pr.getValue2());
                    server.setValue(0);
                    sr.put(server.getServerId(), server);
                }
                ServerRank ss = sr.get(pr.getValue2());
                ss.addValue(10 - (i / 10));
            }
        } catch (Exception e) {
            logger.error("计算服务器排名时发生异常", e);
        } finally {
            rankLock.unlock();
        }
        ArrayList<ServerRank> list = new ArrayList<>(sr.values());
        Collections.sort(list);
        serverRank = list;
    }

    /**
     * 获取服务器排名
     *
     * @return
     */
    public String getServerRankInfo() {
        if (serverRank == null || serverRank.size() == 0)
            return "NO";
        StringBuilder sb = new StringBuilder();
        for (ServerRank sr : serverRank) {
            sb.append("S").append(sr.getServerId()).append("服: ").append(sr.getValue()).append("分;");
        }
        return sb.toString();
    }

    /**
     * 获取个人排名
     *
     * @param id
     * @return
     */
    public int getArenaRankInfo(int id) {
        if (rankList == null)
            return 0;
        for (int i = 0; i < rankList.size(); i++) {
            PlayerRank pr = rankList.get(i);
            if (pr != null && pr.getId() == id) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * 竞技场战斗
     *
     * @param serverId
     * @param playerId
     * @param rank
     * @return
     */
    public String arenaBattleFight(int serverId, int playerId, int rank, int oldRank) {
        //自己的战斗数据
        BattlePlayer self = PvPWorld.gi().getBattlePlayer(playerId);
        //对手的战斗数据
        if (rank < 1 || rank > rankList.size())
            return "NoRank";
        //自己的排名
        int selfRankId = getArenaRankInfo(playerId);
        if (selfRankId != oldRank)
            return "NoSelfRank";
        //敌人的排行数据
        PlayerRank enemyRank = rankList.get(rank - 1);
        //敌人战斗力
        long enemyFight = enemyRank.getFighting();
        BattlePlayer enemy = null;
        if (enemyRank.getId() > 0) {
            enemy = PvPWorld.gi().getBattlePlayer(enemyRank.getId());
            if (enemy == null) {
                enemy = new BattlePlayer();
                enemy.init(self, enemyRank);
            }
            enemyFight = enemy.getFighting();
        }
        //战斗结果
        boolean succ = false;
        int newRankId = selfRankId;
        //胜利
        if (succ) {
            if (selfRankId == 0 || selfRankId > rank) {
                try {
                    rankLock.lock();
                    //自己改变位置
                    PlayerRank selfRank = new PlayerRank();
                    selfRank.init(self);
                    selfRank.setRank(rank);
                    selfRank.setValue2(serverId);
                    selfRank.encodeName();
                    rankList.set(rank - 1, selfRank);
                    JedisManager.gi().hset(REDIS_ARENA_KEY + GameHttpManager.SPLIT + pvpId,
                            rank + "", StringUtil.obj2Gson(selfRank));
                    newRankId = rank;
                    //对手改变位置
                    enemyRank.setRank(selfRankId);
                    if (selfRankId != 0) {
                        rankList.set(selfRankId - 1, enemyRank);
                        JedisManager.gi().hset(REDIS_ARENA_KEY + GameHttpManager.SPLIT + pvpId,
                                selfRankId + "", StringUtil.obj2Gson(enemyRank));
                    }
                    JedisManager.gi().hset(REDIS_ARENA_KEY + GameHttpManager.SPLIT + pvpId,
                            "time", System.currentTimeMillis() + "");
                } catch (Exception e) {
                    logger.error("交换竞技场排名时发生异常", e);
                } finally {
                    rankLock.unlock();
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(succ ? "succ" : "fail").append(GameHttpManager.SPLIT);
        sb.append(selfRankId).append(GameHttpManager.SPLIT).append(newRankId).append(GameHttpManager.SPLIT);
        //玩家
        if (enemy != null) {
            sb.append("1").append(GameHttpManager.SPLIT).append(StringUtil.obj2Gson(enemy));
        }
        //机器人
        else {
            sb.append("0").append(GameHttpManager.SPLIT).append(StringUtil.obj2Gson(enemyRank));
        }
        return sb.toString();
    }

    /**
     * 获取挑战列表
     *
     * @param rank
     * @return
     */
    public List<ArenaChallenge> getArenaChallengeList(int rank) {
        List<Integer> rankIds = new ArrayList<>();
        //榜单前几名
        if (rank < 0) {
            for (int i = 1; i <= -rank; i++) {
                rankIds.add(i);
            }
        }
        //正常寻找挑战列表
        else {
            if (rank == 0)
                rank = RANK_CAPACITY + 1;
            rankIds = getChallengeRankList(rank);
        }
        List<ArenaChallenge> challenges = new ArrayList<>();
        for (int rankId : rankIds) {
            PlayerRank pr = this.rankList.get(rankId - 1);
            if (pr == null) {
                pr = createRobot(rankId);
            }
            //获取ArenaChallenge对象
            ArenaChallenge ac = new ArenaChallenge();
            ac.init(pr);
            //获取玩家战斗数据
            BattlePlayer bp = PvPWorld.gi().getBattlePlayer(pr.getId());
            if (bp != null) {
                ac.setAppearCha(bp.getAp().getAppearCha(0));
                ac.setFighting(bp.getFighting());
            } else {
                //机器人
                ac.setAppearCha(createRobotFirstCha(pr.getValue()));
            }
            challenges.add(ac);
        }
        return challenges;
    }

    /**
     * 通过玩家排名获取挑战的排名
     *
     * @param playerRank
     * @return
     */
    private List<Integer> getChallengeRankList(int playerRank) {
        int challengerCount = CHALLENGE_RANGE.length - 1;
        List<Integer> rankList = new ArrayList<>(challengerCount);
        //这个值不太灵活,需要根据CHALLENGE_RANGE调整,保证不刷出重复玩家
        if (playerRank < 20) {
            //特殊处理, 前后的顺序N名
            int lowerOffset = 0;
            int highOffset = 0;
            for (int i = 0; i < CHALLENGE_RANGE.length; i++) {
                int range = CHALLENGE_RANGE[i][0];
                if (range == 100) {
                    continue;
                } else if (range > 100) {
                    int rank = playerRank + (++highOffset);
                    rankList.add(rank);
                } else {
                    int rank = playerRank - (lowerOffset + 1);
                    if (rank <= 0) {
                        rank = playerRank + (++highOffset);
                    } else {
                        rank = playerRank - (++lowerOffset);
                    }
                    rankList.add(rank);
                }
            }
            Collections.sort(rankList);
        } else {
            for (int i = 0; i < CHALLENGE_RANGE.length - 1; i++) {
                int lowRank = Math.round(playerRank * CHALLENGE_RANGE[i][0] / 100.f);
                // 避开自身排名
                lowRank = (lowRank == playerRank) ? (lowRank + 1) : lowRank;
                int highRank = Math.round(playerRank * CHALLENGE_RANGE[i + 1][0] / 100.f) - 1;
                // 根据范围限制重新调整起点
                int numLimit = CHALLENGE_RANGE[i + 1][1];
                lowRank = (numLimit != -1 && (highRank - lowRank) > numLimit) ? highRank - numLimit : lowRank;

                int randomRank = (lowRank >= highRank) ? lowRank : GameUtil.getRangedRandom(lowRank, highRank);
                rankList.add(randomRank);
            }
        }
        return rankList;
    }

    private PlayerRank createRobot(int rank) {
        PlayerRank robot = new PlayerRank();
        //基础信息
        robot.setId(-1);
        int serverId = GameUtil.getRangedRandom((pvpId - 1) * PvPWorld.PVP_AREA_NUM + 1, pvpId * PvPWorld.PVP_AREA_NUM);
        robot.setName(GameCommon.getRandomName(serverId));
        robot.setHead(GameCommon.getRandomHead());
        robot.setRein(0);
        robot.setLevel((short) 80);
        robot.setVip(0);
        robot.setFighting(12000000 - (rank - 1) * 100000);
        robot.setRank(rank);
        robot.setValue2(serverId);
        //机器人的职业
        robot.setValue(GameUtil.getRangedRandom(0, 2));
        robot.encodeName();
        return robot;
    }

    private AppearCharacter createRobotFirstCha(long occ) {
        AppearCharacter ac = new AppearCharacter();
        ac.occ = (byte) occ;
        return ac;
    }

    public String getArenaRankList(int serverId) {
        if (rankList == null)
            return "fail";
        String rankJson = StringUtil.obj2Gson(ImmutableList.copyOf(rankList));
        int server = 0;
        if (serverRank != null) {
            for (int i = 0; i < serverRank.size(); i++) {
                ServerRank sr = serverRank.get(i);
                if (sr.getServerId() == serverId) {
                    server = i + 1;
                    break;
                }
            }
        }
        return rankJson + GameHttpManager.SPLIT + server;
    }

}
