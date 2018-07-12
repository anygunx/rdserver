package com.rd.game;

import com.rd.bean.player.Player;
import com.rd.bean.relationship.NRelatedPlayer;
import com.rd.dao.NRelationshipDao;
import com.rd.game.manager.NRelationManager;
import com.rd.net.message.Message;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import com.rd.util.SimpleLRUCache;
import org.apache.log4j.Logger;

import java.util.LinkedHashMap;

/**
 * 游戏亲密管理器
 *
 * @author MyPC
 */
public class NGameRelationshipManager {
    private static Logger logger = Logger.getLogger(NGameRelationshipManager.class);

    private static NGameRelationshipManager instance = new NGameRelationshipManager();
    private LinkedHashMap<Integer, NRelatedPlayer> playerMap = new LinkedHashMap<>();

    public static NGameRelationshipManager getInstance() {
        return instance;
    }

    /**
     *
     **/
    private SimpleLRUCache<Integer, NRelationManager> cache = new SimpleLRUCache<>(1024);

    public void addGuanZhu(NRelationManager nimeManager, final NRelatedPlayer mine, final int otherId, Message request) {

        TaskManager.getInstance().scheduleTask(ETaskType.COMMON, new Task() {
            @Override
            public void run() {
                try {

                    NRelationManager otherManager = getNRelationshipManager(otherId);
                    NRelatedPlayer other = getNRelatedPlayer(otherId);
                    if (otherManager == null) {
                        return;
                    }
                    nimeManager.guanzhu(otherManager, other, mine, request);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

            }

            @Override
            public String name() {
                return "GameRelationshipManager.addFriendTask";
            }
        });
    }


    public NRelatedPlayer getNRelatedPlayer(int playerId) {
        IGameRole targetRole = GameWorld.getPtr().getGameRole(playerId);
        if (targetRole == null) {
            return null;
        }
        NRelatedPlayer pr = new NRelatedPlayer();
        pr.init(targetRole.getPlayer());
        return pr;
    }


    /**
     * 添加友情币 这个是所有玩家向某个玩家赠送友情币需要按照顺序执行的
     *
     * @param playerId
     * @param role
     * @param guanzhu
     */
    public void addYouqingBi(int playerId, GameRole role, NRelatedPlayer guanzhu) {
        TaskManager.getInstance().scheduleTask(ETaskType.COMMON, new Task() {
            @Override
            public void run() {
                try {
                    NRelationManager other = getNRelationshipManager(playerId);
                    if (other == null) {
                        return;
                    }
                    //添加友情币
                    other.addFriendCoin(role);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

            @Override
            public String name() {
                return "GameRelationshipManager.addYouQingBi";
            }
        });


    }

    public NRelationManager getNRelationshipManager(int playerId) {
        IGameRole targetRole = GameWorld.getPtr().getGameRole(playerId);
        if (targetRole == null) {
            return null;
        }
        if (targetRole.isOnline()) {
            removeCache(playerId);//防止并发产生的缓存
            return targetRole.getGameRole().getNRelationManager();
        }

        return getCache(targetRole.getPlayer());
    }


    private NRelationManager getCache(Player player) {
        NRelationManager manager = cache.get(player.getId());
        if (manager != null) {
            return manager;
        }
        synchronized (cache) {
            manager = cache.get(player.getId());
            if (manager == null) {
                manager = new NRelationManager(player);
                manager.init();
                manager.initGuanZhu(null, null);
                cache.put(player.getId(), manager);
            }
            return manager;
        }
    }

    public NRelationManager removeCache(int id) {
        if (!cache.containsKey(id)) {
            return null;
        }
        synchronized (cache) {
            if (!cache.containsKey(id)) {
                return null;
            }
            return cache.remove(id);
        }
    }


    public void init() {
        playerMap = NRelationshipDao.getInstance().selectPlayer();
    }

    /**
     * 某些玩家上线了就要清除了
     */
    public void removePlayerMap(int id) {
        if (!playerMap.containsKey(id)) {
            return;
        }
        synchronized (playerMap) {
            if (!playerMap.containsKey(id)) {
                return;
            }
            playerMap.remove(id);
        }

    }


    /**
     * 玩家下线   要进行筛选  满足被关注的条件 下线会添加到列表中的
     */
    public void addPlayerMap(Player player) {
        if (playerMap.containsKey(player.getId())) {
            return;
        }
        synchronized (playerMap) {
            if (playerMap.containsKey(player.getId())) {
                return;
            }
            if (playerMap.size() >= 100) {
                removeOldData();
            }
            NRelatedPlayer nRe = new NRelatedPlayer();
            nRe.init(player);
            playerMap.put(player.getId(), nRe);
        }
    }


    private void removeOldData() {
        NRelatedPlayer remove = null;
        for (NRelatedPlayer pr : playerMap.values()) {
            if (remove == null) {
                remove = pr;
            } else if (remove.getLoginOutTime() > pr.getLoginOutTime()) {
                remove = pr;
            }
        }
        if (remove == null) {
            return;
        }
        playerMap.remove(remove.getId());

    }

    /**
     * 获取离线玩家可以被关注的数据
     *
     * @return
     */
    public LinkedHashMap<Integer, NRelatedPlayer> getPlayerMap() {
        return playerMap;
    }

}
