package com.rd.common;

import com.google.common.base.Preconditions;
import com.rd.bean.chat.*;
import com.rd.bean.player.Player;
import com.rd.dao.ChatDao;
import com.rd.define.EBroadcast;
import com.rd.define.NChatDefine;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.rd.define.ChatDefine.*;

/*****
 * 聊天 服务器管理
 * @author MyPC
 *
 */
public class NChatService {

    //缓存的聊天队列
    private static Queue<Chat> chatQueue = new ConcurrentLinkedQueue<>();

    //缓存的工会聊天
    private static Map<Integer, Queue<Chat>> gangChat = new HashMap<>();

    //玩家广播队列
    private static Queue<PlayerBroadcast> bcPlayer = new ConcurrentLinkedQueue<>();
    //系统广播队列
    private static Queue<BaseBroadcast> bcSystem = new ConcurrentLinkedQueue<>();

    /**
     * 缓存聊天消息
     *
     * @param chat
     */
    public static void addChatMsg(Chat chat) {
        if (chatQueue.size() >= NChatDefine.CHAT_CACHE_CAPACITY)
            chatQueue.poll();
        chatQueue.offer(chat);
    }

    public static void addFactionChatMsg(Chat chat) {
        int gangId = chat.getPlayer().getGangId();
        if (!gangChat.containsKey(gangId))
            gangChat.put(gangId, new ConcurrentLinkedQueue<>());
        Queue<Chat> queue = gangChat.get(gangId);
        if (queue.size() >= NChatDefine.CHAT_CACHE_CAPACITY)
            queue.poll();
        queue.offer(chat);
    }

    /**
     * 获得消息列表信息
     *
     * @return
     */
    public static Message getChatListMsg(Message msg) {

        msg.setByte(CHAT_TYPE_PLAYER);
        msg.setByte(chatQueue.size());
        for (Chat chat : chatQueue) {
            chat.getMessage(msg);
        }
        return msg;
    }

    public static Message getGangChatListMsg(Message msg, int gangId) {
        Queue<Chat> chats = gangChat.get(gangId);
        if (chats == null || chats.size() == 0)
            return null;
        msg.setByte(CHAT_TYPE_GANG);
        msg.setByte(chats.size());
        for (Chat chat : chats) {
            chat.getMessage(msg);
            //msg.setByte(chat.getPlayer().getGangPos());
        }
        return msg;
    }


    public static Message getListMsg(Message msg, int gangId) {

        Queue<Chat> chats = gangChat.get(gangId);
        int num = 0;
        if (chats != null) {
            num = chats.size();
        }
        msg.setByte(chatQueue.size() + num);
        for (Chat chat : chatQueue) {
            msg.setByte(NChatDefine.CHAT_TYPE_PLAYER);
            msg.setByte(NChatDefine.CHAT_TYPE_NORMER);
            chat.getMessage(msg);
        }

        if (chats == null || chats.size() == 0) {

        } else {
            for (Chat chat : chats) {
                msg.setByte(NChatDefine.CHAT_TYPE_FACTION);
                msg.setByte(NChatDefine.CHAT_TYPE_NORMER);
                chat.getMessage(msg);
            }
        }

        return msg;
    }

    /**
     * 广播玩家消息
     *
     * @param player
     * @param type
     * @param contents
     */
    public static void broadcastPlayerMsg(Player player, EBroadcast type, String... contents) {
        PlayerBroadcast broadcast = PlayerBroadcast.build(player, type, contents);
        if (bcPlayer.size() >= BROADCAST_PLAYER_CAPACITY)
            bcPlayer.poll();
        bcPlayer.offer(broadcast);
    }

    /**
     * 广播系统消息
     *
     * @param broadcast
     */
    public static void broadcastSystemMsg(BaseBroadcast broadcast) {
        if (bcSystem.size() >= BROADCAST_SYSTEM_CAPACITY)
            bcSystem.poll();
        bcSystem.offer(broadcast);
    }

    /**
     * 创建玩家广播消息
     *
     * @param player
     * @param type
     * @param params
     * @return
     */
    public static Message createBroadcastMsg(Player player, EBroadcast type, String... params) {
        Message message = new Message(MessageCommand.BROADCAST_MESSAGE);
        byte channel = (player == null) ? BROADCAST_TYPE_SYSTEM : BROADCAST_TYPE_PLAYER;
        message.setByte(channel);
        message.setByte(type.getId());
        switch (channel) {
            case BROADCAST_TYPE_PLAYER:
                ChatPlayer chatPlayer = new ChatPlayer(player);
                chatPlayer.getBroadcastMessage(message);
                break;
            default:
                break;
        }
        message.setByte(params.length);
        for (String param : params) {
            message.setString(param);
        }
        return message;
    }

    /**
     * 向玩家推送广播消息
     *
     * @param role
     */
    public static void onTick(GameRole role) {
        long currTime = System.currentTimeMillis();
        //推送全部系统广播
        for (BaseBroadcast bc : bcSystem) {
            if (!bc.isNeedBroadcast(currTime))
                continue;
            role.putMessageQueue(bc.getMsg());
            bc.setDirty(true);
        }
        int push = 0;
        //推送玩家广播
        for (PlayerBroadcast bc : bcPlayer) {
            if (push > PUSH_BROADCAST_MAX)
                break;
            if (!bc.isNeedBroadcast(currTime))
                continue;
            role.putMessageQueue(bc.getMsg());
            bc.setDirty(true);
            push++;
        }
    }

    /**
     * 清除推送过的广播
     */
    public static void clearDirty() {
        long currTime = System.currentTimeMillis();
        if (bcSystem.isEmpty() && bcPlayer.isEmpty())
            return;
        List<BaseBroadcast> removeList = new ArrayList<>();
        for (BaseBroadcast bbc : bcSystem) {
            if (bbc.isDirty()) {
                bbc.countDown(currTime);
                bbc.setDirty(false);
            }
            if (bbc.isDone(currTime)) {
                removeList.add(bbc);
            }
        }
        if (!removeList.isEmpty()) {
            bcSystem.removeAll(removeList);
            removeList.clear();
        }
        for (BaseBroadcast bbc : bcPlayer) {
            if (bbc.isDirty()) {
                bbc.countDown(currTime);
                bbc.setDirty(false);
            }
            if (bbc.isDone(currTime)) {
                removeList.add(bbc);
            }
        }
        if (!removeList.isEmpty()) {
            bcPlayer.removeAll(removeList);
        }
    }


    /*************************************** 私聊记录 *****************************************/
    /**
     * 聊天记录缓存
     * 策略：
     * 缓存一小时内的所有玩家的聊天记录。
     * 每小时脏数据入库。
     * 移除不活跃的同步数据。
     * 服务器关闭时脏数据入库。
     **/
    private static final Map<Integer, ChatHistory> cache = new HashMap<>(512);

    private static ChatHistory getHistory(int playerId) {
        IGameRole targetRole = GameWorld.getPtr().getGameRole(playerId);
        if (targetRole == null) {
            return null;
        }
        return getCache(targetRole.getPlayer());
    }

    private static ChatHistory getCache(Player player) {
        int playerId = player.getId();
        ChatHistory history = cache.get(playerId);
        if (history != null) {
            return history;
        }
        synchronized (cache) {
            history = cache.get(playerId);
            if (history == null) {
                history = new ChatDao().getPrivateHistory(playerId);
                if (history == null) {
                    history = new ChatHistory(playerId);
                    new ChatDao().createPrivateHistory(playerId);
                }
                cache.put(player.getId(), history);
            }
            return history;
        }
    }

    /**
     * 更新聊天缓存
     * 单位时间内
     * 1.脏数据入库
     * 2.非活跃(非脏)移除
     *
     * @param currentTime
     */
    public static void update(long currentTime) {
        ChatDao chatDao = new ChatDao();
        Set<Integer> clearSet = new HashSet<>();
        for (ChatHistory history : cache.values()) {
            if (currentTime - history.getLastUpdateTime() < CHAT_CACHE_SAVE_INTERVAL) {
                continue;
            }
            if (history.isDirty()) {
                chatDao.updateData(history);
                history.setDirty(false);
                history.setLastUpdateTime(currentTime);
            } else {
                clearSet.add(history.getPlayerId());
            }
        }

        if (!clearSet.isEmpty()) {
            synchronized (cache) {
                for (Integer key : clearSet) {
                    cache.remove(key);
                }
            }
        }
    }

//	public static ChatHistory removeCache(int id) {
//		if (!cache.containsKey(id)){
//			return null;
//		}
//		synchronized (cache) {
//			if (!cache.containsKey(id)){
//				return null;
//			}
//			return cache.remove(id);
//		}
//	}

    /**
     * 所有脏数据入库
     */
    public static void saveAllChat() {
        long currentTime = System.currentTimeMillis();
        ChatDao chatDao = new ChatDao();
        for (ChatHistory history : cache.values()) {
            if (!history.isDirty()) {
                continue;
            }
            chatDao.updateData(history);
            history.setDirty(false);
            history.setLastUpdateTime(currentTime);
        }

    }

    public static void removePrivateHistory(int fromPlayerId, int targetPlayerId) {
        ChatHistory fromHistory = getHistory(fromPlayerId);
        fromHistory.removeAll(targetPlayerId);
    }

    /**
     * 添加私聊记录
     *
     * @param fromPlayerId
     * @param toPlayerId
     * @param chat
     */
    public static void addPrivateChat(int fromPlayerId, int toPlayerId, ChatPrivate chat) {
        Preconditions.checkArgument(fromPlayerId != toPlayerId, "ChatService.addPrivateChat() failed. fromPlayer=toPlayer");

        ChatHistory fromHistory = getHistory(fromPlayerId);
        fromHistory.add(chat);
        ChatHistory toHistory = getHistory(toPlayerId);
        toHistory.add(chat);
    }

    public static void processChatListMsg(Message request, GameRole role) {
        byte type = request.readByte();
        if (type == CHAT_TYPE_PRIVATE) {
            Message message = new Message(MessageCommand.CHAT_LIST_MESSAGE, request.getChannel());
            message.setByte(type);
            ChatHistory.getMessage(getHistory(role.getPlayerId()), message);
            role.sendMessage(message);
        }
    }

}
