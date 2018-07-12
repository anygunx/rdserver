package com.rd.game.manager;

import com.rd.bean.relationship.IRelatedPlayer;
import com.rd.bean.relationship.RelatedPlayer;
import com.rd.common.ChatService;
import com.rd.dao.RelationshipDao;
import com.rd.define.ErrorDefine;
import com.rd.define.GameDefine;
import com.rd.game.GameRelationshipManager;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.*;

import static com.rd.define.RelationshipDefine.*;

/**
 * 亲密关系管理
 * 可用于管理不在线玩家的关系
 * Created by XingYun on 2017/6/18.
 */
public class NRelationshipManager {
    private static final Logger logger = Logger.getLogger(NRelationshipManager.class.getName());
    /**
     * 不在线玩家的gameRole为null
     **/
    private GameRole gameRole;
    private IRelatedPlayer player;

    /**
     * 各种关系的玩家map
     **/
    private Map<Byte, LinkedHashMap<Integer, RelatedPlayer>> relations = new HashMap<>();

    public NRelationshipManager(IRelatedPlayer player) {
        this.gameRole = null;
        this.player = player;
    }

    public NRelationshipManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.player = gameRole.getPlayer();
    }

    public void init() {
        relations = getRelationShipDao().getRelations(player.getId());
    }


    public Message getBlackListMessage() {
        return getListMessage(RELATIONSHIP_BLACK);
    }

    public Message getListMessage(byte type) {
        long currentTime = System.currentTimeMillis();
        Map<Integer, RelatedPlayer> players = relations.get(type);
        if (players != null) {
            updatePlayers(players);
        }
        return getRelationListMessage(type, players, currentTime);
    }

    private long lastGetListTime = -1;

    /**
     * 获取制定关系列表
     *
     * @param request
     */
    public void processGetListMessage(Message request) {
        long currentTime = System.currentTimeMillis();
        if (lastGetListTime != -1 && currentTime - lastGetListTime < GameDefine.OPERATION_INTERVAL) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_OVER_QUICK);
            return;
        }

        lastGetListTime = currentTime;
        byte type = request.readByte();
        sendList(request.getChannel(), type, currentTime);
    }

    /**
     * 更新玩家信息
     * 只从cache里获取
     *
     * @param players
     */
    private void updatePlayers(Map<Integer, RelatedPlayer> players) {
        if (players == null) {
            return;
        }
        for (RelatedPlayer relatedPlayer : players.values()) {
            IRelatedPlayer lastPlayer;
            IGameRole gameRole = GameWorld.getPtr().getOnlineRole(relatedPlayer.getId());
            if (gameRole == null) {
                lastPlayer = GameWorld.getPtr().getOfflinePlayer(relatedPlayer.getId());
            } else {
                lastPlayer = gameRole.getPlayer();
            }
            if (lastPlayer == null) {
                continue;
            }
            relatedPlayer.init(lastPlayer);
        }
    }


    private void sendList(Channel channel, byte type, long currentTime) {
        Map<Integer, RelatedPlayer> players = relations.get(type);
        if (players != null) {
            updatePlayers(players);
        }
        sendList(channel, type, players, currentTime);
    }

    private void sendList(Channel channel, byte type, Map<Integer, RelatedPlayer> players, long currentTime) {
        Message message = getRelationListMessage(type, players, currentTime);
        message.setChannel(channel);
        gameRole.sendMessage(message);
    }

    private Message getRelationListMessage(byte type, Map<Integer, RelatedPlayer> players, long currentTime) {
        Message message = new Message(MessageCommand.RELATIONSHIP_GET_LIST_MESSAGE);
        message.setByte(type);
        if (players == null) {
            message.setByte(0);
        } else {
            message.setByte(players.size());
            for (RelatedPlayer player : players.values()) {
                player.getMessage(message, currentTime);
            }
        }
        return message;
    }

    /**
     * 好友申请
     *
     * @param request
     */
    public void processApplyMessage(Message request) {
        int targetPlayerId = request.readInt();
        if (targetPlayerId <= 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (targetPlayerId == player.getId()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        Map friends = getFriends();
        if (friends.size() >= FRIEND_MAX) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PLAYER_FRIEND_MAX);
            return;
        }
        if (friends.containsKey(targetPlayerId)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_FRIEND_ALREADY_EXISTED);
            return;
        }
        RelatedPlayer mine = new RelatedPlayer();
        mine.init(player);

        byte error = GameRelationshipManager.getInstance().add2Application(mine, targetPlayerId);
        if (error != ErrorDefine.ERROR_NONE) {
            gameRole.sendErrorTipMessage(request, error);
            return;
        }

        Message message = new Message(MessageCommand.RELATIONSHIP_APPLY_MESSAGE);
        message.setChannel(request.getChannel());
        gameRole.sendMessage(message);
    }

    public void addFriend(RelatedPlayer other) {
        int otherId = other.getId();
        LinkedHashMap<Integer, RelatedPlayer> friends = getFriends();
        boolean checked = checkAddFriend(otherId);
        if (!checked) {
            return;
        }
        synchronized (friends) {
            checked = checkAddFriend(otherId);
            if (!checked) {
                return;
            }
            friends.put(otherId, other);
            getRelationShipDao().updateRelation(player.getId(), RELATIONSHIP_FRIEND, friends);
        }
        if (gameRole != null) {
            gameRole.putMessageQueue(getListMessage(RELATIONSHIP_FRIEND));
        }
    }

    private boolean checkAddFriend(int otherId) {
        LinkedHashMap<Integer, RelatedPlayer> friends = getFriends();
        if (friends != null) {
            // 好友列表满
            if (friends.size() >= FRIEND_MAX) {
                return false;
            }
            // 已加好友
            if (friends.containsKey(otherId)) {
                return false;
            }
        }
        // 黑名单
        Map<Integer, RelatedPlayer> blacks = getBlacks();
        if (blacks != null && blacks.containsKey(otherId)) {
            return false;
        }
        return true;
    }

    public byte addApplication(RelatedPlayer other) {
        int otherId = other.getId();
        Map<Integer, RelatedPlayer> friends = getFriends();
        if (friends != null) {
            // 好友列表满
            if (friends.size() >= FRIEND_MAX) {
                return ErrorDefine.ERROR_OTHER_FRIEND_MAX;
            }
            // 须要同移除一样是单向操作
//            // 已加好友
//            if (friends.containsKey(otherId)){
//                return ErrorDefine.ERROR_OTHER_FRIEND_ALREADY_EXISTED;
//            }
        }
        // 黑名单
        Map<Integer, RelatedPlayer> blacks = getBlacks();
        if (blacks != null && blacks.containsKey(otherId)) {
            return ErrorDefine.ERROR_OTHER_BLACK_LIST;
        }

        LinkedHashMap<Integer, RelatedPlayer> applications = getApplications();
        synchronized (applications) {
            // 已申请
            if (applications.containsKey(otherId)) {
                return ErrorDefine.ERROR_FRIEND_ALREADY_APPLY;
            }
            if (applications.size() >= APPLICATION_MAX) {
                Map.Entry<Integer, RelatedPlayer> firstOne = applications.entrySet().iterator().next();
                applications.remove(firstOne.getKey());
            }

            applications.put(otherId, other);
        }
        getRelationShipDao().updateRelation(player.getId(), RELATIONSHIP_APPLICATION, applications);

        if (gameRole != null) {
            Message message = new Message(MessageCommand.RELATIONSHIP_NEW_APPLICATION_MESSAGE);
            gameRole.putMessageQueue(message);
        }

        return ErrorDefine.ERROR_NONE;
    }

    /**
     * 为不在线的gameRole新建dao
     **/
    private RelationshipDao getRelationShipDao() {
        return gameRole == null ? new RelationshipDao() : gameRole.getDbManager().relationshipDao;
    }

    private LinkedHashMap<Integer, RelatedPlayer> createRelation(byte type) {
        synchronized (relations) {
            LinkedHashMap<Integer, RelatedPlayer> relation = relations.get(type);
            if (relation != null) {
                return relation;
            }
            relation = new LinkedHashMap<>();
            this.relations.put(type, relation);
            getRelationShipDao().insertRelation(player.getId(), type);
            return relation;
        }
    }


    public static final byte APPLY_PROCESS_ACCEPT = 1;
    public static final byte APPLY_PROCESS_REJECT = 2;

    public void processApplicationMessage(Message request) {
        byte process = request.readByte();
        int playerId = request.readInt();
        long currentTime = System.currentTimeMillis();

        byte errorCode;
        if (process != APPLY_PROCESS_ACCEPT) {
            errorCode = rejectApplication(playerId);
        } else {
            errorCode = acceptApplication(playerId);
        }
        if (errorCode != ErrorDefine.ERROR_NONE) {
            gameRole.sendErrorTipMessage(request, errorCode);
            return;
        } else {
            gameRole.putErrorMessage(ErrorDefine.ERROR_SUCCESS);
            if (process == APPLY_PROCESS_ACCEPT) {
                gameRole.putMessageQueue(getListMessage(RELATIONSHIP_APPLICATION));
                sendList(request.getChannel(), RELATIONSHIP_FRIEND, currentTime);
            } else {
                sendList(request.getChannel(), RELATIONSHIP_APPLICATION, currentTime);
            }
            return;
        }

    }

    /**
     * 接收好友申请
     *
     * @param playerId -1:一键
     * @return errorCode
     */
    private byte acceptApplication(int playerId) {
        LinkedHashMap<Integer, RelatedPlayer> friends = getFriends();
        if (friends.size() >= FRIEND_MAX) {
            return ErrorDefine.ERROR_PLAYER_FRIEND_MAX;
        }

        LinkedHashMap<Integer, RelatedPlayer> applications = getApplications();
        List<Integer> targetIdList = new ArrayList<>();
        if (playerId != -1) {
            if (!applications.containsKey(playerId)) {
                return ErrorDefine.ERROR_OPERATION_FAILED;
            }
            targetIdList.add(playerId);
        } else {
            targetIdList.addAll(applications.keySet());
            int capacityRest = FRIEND_MAX - friends.size();
            if (targetIdList.size() > capacityRest) {
                targetIdList = targetIdList.subList(0, capacityRest);
            }
        }

        for (Integer id : targetIdList) {
            RelatedPlayer relatedPlayer = applications.remove(id);
            if (relatedPlayer != null) {
                friends.put(id, relatedPlayer);
            }
        }
        getRelationShipDao().updateRelations(player.getId(), relations, RELATIONSHIP_APPLICATION, RELATIONSHIP_FRIEND);

        final RelatedPlayer mine = new RelatedPlayer();
        mine.init(player);
        GameRelationshipManager.getInstance().add2Friend(mine, targetIdList);
        return ErrorDefine.ERROR_NONE;
    }


    /**
     * 拒绝好友申请
     *
     * @param playerId -1:一键
     * @return errorCode
     */
    private byte rejectApplication(int playerId) {
        LinkedHashMap<Integer, RelatedPlayer> applications = getApplications();
        if (playerId != -1) {
            if (!applications.containsKey(playerId)) {
                return ErrorDefine.ERROR_OPERATION_FAILED;
            }
            applications.remove(playerId);
        } else {
            applications.clear();
        }
        getRelationShipDao().updateRelation(player.getId(), RELATIONSHIP_APPLICATION, applications);
        return ErrorDefine.ERROR_NONE;
    }


    /**
     * 处理拉黑请求
     *
     * @param request
     */
    public void processBlackMessage(Message request) {
        int playerId = request.readInt();
        if (playerId == player.getId()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        LinkedHashMap<Integer, RelatedPlayer> blacks = getBlacks();
        if (blacks.containsKey(playerId)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_IN_BLACK_LIST);
            return;
        }
        // 从其他所有关系中移除
        List<Byte> affectList = new ArrayList<>();
        for (Map.Entry<Byte, LinkedHashMap<Integer, RelatedPlayer>> entry : relations.entrySet()) {
            if (entry.getKey() == RELATIONSHIP_BLACK) {
                RelatedPlayer target = getRelatedPlayer(playerId);
                if (target != null) {
                    entry.getValue().put(playerId, target);
                }
                affectList.add(entry.getKey());
                continue;
            } else {
                if (removeRelation(entry.getKey(), playerId)) {
                    affectList.add(entry.getKey());
                }
            }
        }
        getRelationShipDao().updateRelations(player.getId(), relations, affectList.toArray(new Byte[]{}));
        gameRole.putErrorMessage(ErrorDefine.ERROR_SUCCESS);
        for (Byte type : affectList) {
            gameRole.putMessageQueue(getListMessage(type));
        }
        gameRole.sendTick(request);
    }

    private RelatedPlayer getRelatedPlayer(int playerId) {
        IGameRole gameRole = GameWorld.getPtr().getGameRole(playerId);
        if (gameRole.getPlayer() == null) {
            return null;
        }
        RelatedPlayer relatedPlayer = new RelatedPlayer();
        relatedPlayer.init(gameRole.getPlayer());
        return relatedPlayer;
    }

    /**
     * 移除关系
     *
     * @param request
     */
    public void processRemoveMessage(Message request) {
        byte type = request.readByte();
        int playerId = request.readInt();
        long currentTime = System.currentTimeMillis();
        boolean result = removeRelation(type, playerId);
        if (!result) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        getRelationShipDao().updateRelation(player.getId(), type, getRelation(type));
        sendList(request.getChannel(), type, currentTime);
    }

    private boolean removeRelation(byte type, int targetId) {
        LinkedHashMap<Integer, RelatedPlayer> relation = relations.get(type);
        if (relation == null) {
            return false;
        }
        if (!relation.containsKey(targetId)) {
            return false;
        }
        boolean result = relation.remove(targetId) != null;
        switch (type) {
            case RELATIONSHIP_FRIEND:
                // 好友关系只能单向主动移除
                ChatService.removePrivateHistory(player.getId(), targetId);
                break;
            default:
                break;
        }
        return result;
    }


    private final LinkedHashMap<Integer, RelatedPlayer> getApplications() {
        return getRelation(RELATIONSHIP_APPLICATION);
    }

    private final LinkedHashMap<Integer, RelatedPlayer> getFriends() {
        return getRelation(RELATIONSHIP_FRIEND);
    }

    private final LinkedHashMap<Integer, RelatedPlayer> getBlacks() {
        return getRelation(RELATIONSHIP_BLACK);
    }

    private final LinkedHashMap<Integer, RelatedPlayer> getRelation(byte type) {
        LinkedHashMap<Integer, RelatedPlayer> relation = relations.get(type);
        if (relation == null) {
            relation = createRelation(type);
        }
        return relation;
    }

    public boolean isInBlackList(int playerId) {
        if (!relations.containsKey(RELATIONSHIP_BLACK)) {
            return false;
        }
        Map blackList = relations.get(RELATIONSHIP_BLACK);
        if (blackList == null) {
            return false;
        }
        return blackList.containsKey(playerId);
    }

    public boolean isFriend(int playerId) {
        if (!relations.containsKey(RELATIONSHIP_FRIEND)) {
            return false;
        }
        Map friend = relations.get(RELATIONSHIP_FRIEND);
        if (friend == null) {
            return false;
        }
        return friend.containsKey(playerId);
    }


}
