package com.rd.game.manager;


import com.rd.bean.chat.Chat;
import com.rd.bean.chat.ChatPlayer;
import com.rd.bean.chat.ChatPrivate;
import com.rd.bean.faction.NFaction;
import com.rd.bean.faction.NFactionMember;
import com.rd.bean.grow.Grow;
import com.rd.bean.player.Player;
import com.rd.common.NChatService;
import com.rd.define.ErrorDefine;
import com.rd.define.GameDefine;
import com.rd.define.NChatDefine;
import com.rd.enumeration.EGrow;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.model.WordSensitiveModel;
import com.rd.model.data.GrowSeedData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;

public class NChatManager {
    private GameRole role;

    public NChatManager(GameRole gameRole) {
        this.role = gameRole;
    }

    public void init() {
    }

    private long time;

    /**
     * 聊天
     *
     * @param request
     */
    public void processChat(Message request) {
//		if(time==0) {
//			time=System.currentTimeMillis()+NChatDefine.CHAT_TIME;
//		}else if(this.time>System.currentTimeMillis()){ 
//			role.sendTick(request);
//			return;
//		}
        this.time = System.currentTimeMillis() + NChatDefine.CHAT_TIME;

        //聊天类型
        byte type = request.readByte();
        byte subTyp = request.readByte();
        //聊天内容
        long ts = System.currentTimeMillis();
        String content = "";
        short id = 0;
        if (NChatDefine.CHAT_TYPE_PLAYER_PET_SHOW != subTyp
                && NChatDefine.CHAT_TYPE_PLAYER_EQUIT_SHOW != subTyp) {
            content = request.readString();
            //判断禁言状态
            if (role.getPlayer().getState() == GameDefine.PLAYER_STATE_SHUTUP) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_STATE_SHUT_UP);
                return;
            }
            //判断长度f
            if (content.length() > NChatDefine.CHAT_CONTENT_LENGTH_LIMIT) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_CHAT_LENGTH_LONG);
                return;
            }
            //替换敏感字
            content = WordSensitiveModel.replaceSensitive(content);
        } else {
            id = request.readShort();

        }

        //玩家聊天
        if (NChatDefine.CHAT_TYPE_PLAYER == type) {
            ChatPlayer chatPlayer = new ChatPlayer(role.getPlayer());
            Chat chat = new Chat(chatPlayer, content, ts);
            Message msg = new Message(EMessage.CHAT_START.CMD(), request.getChannel());
            msg.setByte(type);
            msg.setByte(subTyp);
            chat.getMessage(msg);
            GameWorld.getPtr().broadcast(msg, chatPlayer);
            role.sendTick(request);
            NChatService.addChatMsg(chat);
        } else if (NChatDefine.CHAT_TYPE_FACTION == type) {
            NFaction faction = role.getPlayer().getFaction();
            if (faction == null) {
                role.sendTick(request);
                return;
            }

            ChatPlayer chatPlayer = new ChatPlayer(role.getPlayer());
            Chat chat = new Chat(chatPlayer, content, ts);
            Message msg = new Message(MessageCommand.CHAT_MESSAGE, request.getChannel());
            msg.setByte(type);
            msg.setByte(subTyp);
            chat.getMessage(msg);
            NChatService.addFactionChatMsg(chat);
            NFactionMember member = faction.getGangMember(role.getPlayer().getId());
            msg.setByte(member.getPosition());
            for (NFactionMember gangMember : faction.getMemberMap().values()) {
                GameRole gameRole = GameWorld.getPtr().getOnlineRole(gangMember.getPlayerId());
                if (gameRole != null) {
                    gameRole.putMessageQueue(msg);
                }
            }

        } else if (NChatDefine.CHAT_TYPE_PRIVATE == type) {
            int targetPlayerId = request.readInt();
            IGameRole targetRole = GameWorld.getPtr().getOnlineRole(targetPlayerId);
            if (targetRole == null) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_ONLINE_LIMIT);
                return;
            }

            ChatPrivate chat = new ChatPrivate();
            chat.init(role.getPlayerId(), targetPlayerId, ts, content);
            if (targetRole.isOnline()) {
                Message msg = new Message(request.getCmdId(), request.getChannel());
                msg.setByte(type);
                chat.getMessage(msg, targetRole.getPlayer().getId());
                targetRole.getGameRole().putMessageQueue(msg);
            }

            Message msg = new Message(request.getCmdId(), request.getChannel());
            msg.setByte(type);
            msg.setByte(subTyp);
            chat.getMessage(msg, role.getPlayerId());
            role.sendMessage(msg);
        } else if (NChatDefine.CHAT_TYPE_PLAYER == type
                && NChatDefine.CHAT_TYPE_PLAYER_PET_SHOW == subTyp) {
            ChatPlayer chatPlayer = new ChatPlayer(role.getPlayer());
            Chat chat = new Chat(chatPlayer, content, ts);
            Message msg = new Message(request.getCmdId(), request.getChannel());
            msg.setByte(type);
            msg.setByte(subTyp);
            chat.getMessage(msg);
            GameWorld.getPtr().broadcast(msg, chatPlayer);
            NChatService.addChatMsg(chat);
            role.sendTick(request);
        } else if (NChatDefine.CHAT_TYPE_PLAYER == type
                && NChatDefine.CHAT_TYPE_PLAYER_EQUIT_SHOW == subTyp) {
            ChatPlayer chatPlayer = new ChatPlayer(role.getPlayer());
            Chat chat = new Chat(chatPlayer, content, ts);
            Message msg = new Message(request.getCmdId(), request.getChannel());
            msg.setByte(type);
            msg.setByte(subTyp);
            chat.getMessage(msg);
            GameWorld.getPtr().broadcast(msg, chatPlayer);
            NChatService.addChatMsg(chat);
            role.sendTick(request);
        }

    }


    public void processShowInfo(Message request) {
        int targetPlayerId = request.readInt();
        byte type = request.readByte();
        short id = request.readShort();
        if (type != NChatDefine.CHAT_TYPE_PLAYER_PET_SHOW
                && type != NChatDefine.CHAT_TYPE_PLAYER_EQUIT_SHOW) {
            role.sendTick(request);
            return;
        }
        IGameRole targetRole = GameWorld.getPtr().getOnlineRole(targetPlayerId);
        if (targetRole == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ONLINE_LIMIT);
            return;
        }
        Player targetPlayer = targetRole.getPlayer();
        Message msg = new Message(request.getCmdId(), request.getChannel());
        if (type == NChatDefine.CHAT_TYPE_PLAYER_PET_SHOW) {
            EGrow egrow = EGrow.type(EGrow.PET.I());
            GrowSeedData data = egrow.getGrowDataMap().get(id);
            if (data == null) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
                return;
            }

            Grow grow = targetPlayer.getGrowList().get(egrow.I());
            if (!grow.getMap().containsKey(id)) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR__ALREADY_ACTIVATED);
                return;
            }

        } else if (type == NChatDefine.CHAT_TYPE_PLAYER_EQUIT_SHOW) {
            short[] wears = targetPlayer.getWearEquip();


        }
        role.sendMessage(msg);
    }


    /****
     *聊天历史记录
     */
    public void ProcessHistoryRecords(Message request) {
        Message msg = new Message(request.getCmdId(), request.getChannel());

        //NChatService.getChatListMsg(msg);

        NFaction faction = role.getPlayer().getFaction();
        int factionId = 0;
        if (faction != null) {
            factionId = faction.getId();
        }
        NChatService.getListMsg(msg, factionId);
        role.sendMessage(msg);
    }

}
