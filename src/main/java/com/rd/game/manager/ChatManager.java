package com.rd.game.manager;

import com.rd.bean.chat.Chat;
import com.rd.bean.chat.ChatPlayer;
import com.rd.bean.chat.ChatPrivate;
import com.rd.bean.gang.Gang;
import com.rd.bean.gang.GangMember;
import com.rd.common.ChatService;
import com.rd.define.ErrorDefine;
import com.rd.define.GameDefine;
import com.rd.game.GameRelationshipManager;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.model.WordSensitiveModel;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;

import static com.rd.define.ChatDefine.*;

/**
 * 聊天管理器
 *
 * @author Created by U-Demon on 2016年11月8日 下午1:36:21
 * @version 1.0.0
 */
public class ChatManager {

    private GameRole role;

    public ChatManager(GameRole gameRole) {
        this.role = gameRole;
    }

    public void init() {
    }

    /**
     * 聊天
     *
     * @param request
     */
    public void processChatMsg(Message request) {
        //聊天类型
        byte type = request.readByte();
        //聊天内容
        String content = request.readString();
        long ts = System.currentTimeMillis();

        //判断禁言状态
        if (role.getPlayer().getState() == GameDefine.PLAYER_STATE_SHUTUP) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_STATE_SHUT_UP);
            return;
        }
        //判断长度
        if (content.length() > CHAT_CONTENT_LENGTH_LIMIT) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_CHAT_LENGTH_LONG);
            return;
        }
        //替换敏感字
        content = WordSensitiveModel.replaceSensitive(content);

        //玩家聊天
        if (CHAT_TYPE_PLAYER == type) {
            ChatPlayer chatPlayer = new ChatPlayer(role.getPlayer());
            Chat chat = new Chat(chatPlayer, content, ts);
            Message msg = new Message(MessageCommand.CHAT_MESSAGE, request.getChannel());
            msg.setByte(type);
            chat.getMessage(msg);
            GameWorld.getPtr().broadcast(msg, chatPlayer);
            ChatService.addChatMsg(chat);
            role.sendTick(request);
        } else if (CHAT_TYPE_GANG == type) {
            Gang gang = role.getPlayer().getGang();
            if (gang != null) {
                ChatPlayer chatPlayer = new ChatPlayer(role.getPlayer());
                Chat chat = new Chat(chatPlayer, content, ts);
                Message msg = new Message(MessageCommand.CHAT_MESSAGE, request.getChannel());
                msg.setByte(type);
                chat.getMessage(msg);
                ChatService.addGangChatMsg(chat);

                GangMember member = gang.getGangMember(role.getPlayer().getId());
                msg.setByte(member.getPosition());

                for (GangMember gangMember : gang.getMemberMap().values()) {
                    GameRole gameRole = GameWorld.getPtr().getOnlineRole(gangMember.getPlayerId());
                    if (gameRole != null) { //&& !gameRole.getRelationshipManager().isInBlackList(chatPlayer.getId())){
                        gameRole.putMessageQueue(msg);
                    }
                }
            }
            role.sendTick(request);
        } else if (CHAT_TYPE_PRIVATE == type) {
            int targetPlayerId = request.readInt();
            // 暂时关闭留言
//			IGameRole targetRole = GameWorld.getPtr().getGameRole(targetPlayerId);
//			if (targetRole == null){
//				role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//				return;
//			}
            IGameRole targetRole = GameWorld.getPtr().getOnlineRole(targetPlayerId);
            if (targetRole == null) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_ONLINE_LIMIT);
                return;
            }

            ChatPrivate chat = new ChatPrivate();
            chat.init(role.getPlayerId(), targetPlayerId, ts, content);
            byte errorCode = GameRelationshipManager.getInstance().checkAllFriend(role.getPlayerId(), targetPlayerId);
            if (errorCode != ErrorDefine.ERROR_NONE) {
                role.sendErrorTipMessage(request, errorCode);
                return;
            }

            if (targetRole.isOnline()) {
                Message msg = new Message(MessageCommand.CHAT_MESSAGE, request.getChannel());
                msg.setByte(type);
                chat.getMessage(msg, targetRole.getPlayer().getId());
                targetRole.getGameRole().putMessageQueue(msg);
            }

            Message msg = new Message(MessageCommand.CHAT_MESSAGE, request.getChannel());
            msg.setByte(type);
            chat.getMessage(msg, role.getPlayerId());
            role.sendMessage(msg);
            ChatService.addPrivateChat(role.getPlayerId(), targetPlayerId, chat);
        }
    }


}
