package com.rd.game.pvp;

import com.rd.bean.data.GameServer;
import com.rd.bean.player.BattlePlayer;
import com.rd.dao.LogonDao;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * PVP
 *
 * @author U-Demon Created on 2017年5月8日 下午5:19:15
 * @version 1.0.0
 */
public class PvPWorld {

    private static final Logger logger = Logger.getLogger(PvPWorld.class);

    //默认4个服务器一个PVP区
    public static final int PVP_AREA_NUM = 4;

    private static final PvPWorld instance = new PvPWorld();

    private PvPWorld() {
    }

    public static PvPWorld gi() {
        return instance;
    }

    //所有服务器
    private Map<Integer, GameServer> servers;
    private Map<Integer, Set<Integer>> pvpServers;
    private ReentrantLock lockServer;

    //所有玩家
    private Map<Integer, BattlePlayer> bps = null;

    //跨服战
    private Map<Integer, ArenaPvPManager> arenas = new HashMap<>();

    /**
     * 初始化PVP
     */
    public void init() {
        lockServer = new ReentrantLock();
        servers = new LogonDao().getAllGameServer();
        bps = new ConcurrentHashMap<>();
        pvpServers = new ConcurrentHashMap<>();
    }

    /**
     * 获取对应的管理器
     *
     * @param serverId
     * @param clazz
     * @return
     */
    public <T extends BasePvPManager> T getManager(int serverId, Class<T> clazz) {
        //获取对应的服务器
        if (!servers.containsKey(serverId)) {
            try {
                lockServer.lock();
                if (!servers.containsKey(serverId)) {
                    servers = new LogonDao().getAllGameServer();
                }
            } catch (Exception e) {
                logger.error("获取对应的服务器时发生异常", e);
            } finally {
                lockServer.unlock();
            }
        }
        GameServer server = servers.get(serverId);
        if (server == null)
            return null;

        //对应的管理器
        T mgr = checkAndCreateMgr(server, clazz);
        return mgr;
    }

    /**
     * 检查并创建管理器
     *
     * @param pvpId
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T extends BasePvPManager> T checkAndCreateMgr(GameServer server, Class<T> clazz) {
        //PVP分区
        int pvpId = server.getPvp();
        if (pvpId == 0) {
            pvpId = ((server.getId() - 1) / PVP_AREA_NUM) + 1;
        }
        //PVP大区下的所有游戏服
        if (!pvpServers.containsKey(pvpId))
            pvpServers.put(pvpId, new HashSet<>());
        if (!pvpServers.get(pvpId).contains(server.getId()))
            pvpServers.get(pvpId).add(server.getId());
        Map<Integer, T> map = null;
        //TODO 新加类型时在这添加
        //跨服战
        if (clazz == ArenaPvPManager.class) {
            map = (Map<Integer, T>) arenas;
        }
//		else if (clazz == ?) {
//			
//		}

        if (!map.containsKey(pvpId)) {
            try {
                synchronized (clazz.getSimpleName()) {
                    if (!map.containsKey(pvpId)) {
                        T mgr = clazz.newInstance();
                        mgr.setPvpId(pvpId);
                        mgr.init();
                        map.put(pvpId, mgr);
                    }
                }
            } catch (Exception e) {
                logger.error("创建PVP管理器时发生异常" + clazz.getName(), e);
            }
        }
        return map.get(pvpId);
    }

    /**
     * 玩家战斗数据1天失效
     */
    public void validBattlePlayer() {
        if (bps == null)
            return;
        List<Integer> delKeys = new ArrayList<>();
        long curr = System.currentTimeMillis();
        for (BattlePlayer bp : bps.values()) {
            if (curr - bp.getValidTime() >= DateUtil.DAY)
                delKeys.add(bp.getId());
        }
        for (int key : delKeys) {
            bps.remove(key);
        }
    }

    public void addBattlePlayer(BattlePlayer bp) {
        bp.setValidTime(System.currentTimeMillis());
        bps.put(bp.getId(), bp);
    }

    public BattlePlayer getBattlePlayer(int id) {
        return bps.get(id);
    }

    public Map<Integer, ArenaPvPManager> getArenas() {
        return arenas;
    }

    public Set<Integer> getServersByPvp(int pvpId) {
        return pvpServers.get(pvpId);
    }

}
