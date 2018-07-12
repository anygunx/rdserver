package com.rd.game;

import com.rd.bean.relationship.IRelatedPlayer;
import com.rd.bean.relationship.RelatedPlayer;
import com.rd.define.ErrorDefine;
import com.rd.game.manager.RelationshipManager;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import com.rd.util.SimpleLRUCache;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 游戏亲密关系管理
 * 负责交互部分逻辑
 * Created by XingYun on 2017/5/5.
 */
public class GameRelationshipManager {
    private static Logger logger = Logger.getLogger(GameRelationshipManager.class);

    private static GameRelationshipManager instance = new GameRelationshipManager();

    public static GameRelationshipManager getInstance() {
        return instance;
    }

    /**
     * 离线玩家的亲密关系缓存
     * 当前策略未处理活跃
     **/
    private SimpleLRUCache<Integer, RelationshipManager> cache = new SimpleLRUCache<>(1024);

    public void add2Friend(final RelatedPlayer mine, final List<Integer> targetIdList) {
        if (targetIdList.isEmpty()) {
            return;
        }
        TaskManager.getInstance().scheduleTask(ETaskType.COMMON, new Task() {
            @Override
            public void run() {
                for (Integer targetId : targetIdList) {
                    try {
                        RelationshipManager manager = getRelationshipManager(targetId);
                        if (manager == null) {
                            return;
                        }
                        manager.addFriend(mine);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

            @Override
            public String name() {
                return "GameRelationshipManager.addFriendTask";
            }
        });
    }

    public byte add2Application(RelatedPlayer mine, int targetPlayerId) {
        RelationshipManager manager = getRelationshipManager(targetPlayerId);
        if (manager == null) {
            return ErrorDefine.ERROR_OPERATION_FAILED;
        }
        return manager.addApplication(mine);
    }

    private RelationshipManager getRelationshipManager(int playerId) {
        IGameRole targetRole = GameWorld.getPtr().getGameRole(playerId);
        if (targetRole == null) {
            return null;
        }
        if (targetRole.isOnline()) {
            // refresh cache
            removeCache(playerId);
            //return targetRole.getGameRole().getRelationshipManager();
            return null;
        } else {
            return getCache(targetRole.getPlayer());
        }
    }

    private RelationshipManager getCache(IRelatedPlayer player) {
        RelationshipManager manager = cache.get(player.getId());
        if (manager != null) {
            return manager;
        }
        synchronized (cache) {
            manager = cache.get(player.getId());
            if (manager == null) {
                manager = new RelationshipManager(player);
                manager.init();
                cache.put(player.getId(), manager);
            }
            return manager;
        }
    }

    public void removeCache(int id) {
        if (!cache.containsKey(id)) {
            return;
        }
        synchronized (cache) {
            if (!cache.containsKey(id)) {
                return;
            }
            cache.remove(id);
        }
    }

    /**
     * 检查是否好友关系(双向)
     *
     * @param fromPlayerId
     * @param toPlayerId
     * @return
     */
    public byte checkAllFriend(int fromPlayerId, int toPlayerId) {
        if (!isFriend(toPlayerId, fromPlayerId)) {
            return ErrorDefine.ERROR_PLAYER_FRIEND_LIMIT;
        }
        if (!isFriend(fromPlayerId, toPlayerId)) {
            return ErrorDefine.ERROR_OTHER_FRIEND_LIMIT;
        }
        return ErrorDefine.ERROR_NONE;
    }

    /**
     * 检查是否对方好友(单向)
     *
     * @param fromPlayerId
     * @param toPlayerId
     * @return
     */
    public boolean isFriend(int fromPlayerId, int toPlayerId) {
        RelationshipManager targetManager = getRelationshipManager(toPlayerId);
        if (!targetManager.isFriend(fromPlayerId)) {
            return false;
        }
        return true;
    }
}
