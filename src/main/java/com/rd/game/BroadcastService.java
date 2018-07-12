package com.rd.game;

import com.rd.common.ChatService;
import com.rd.define.EBroadcast;
import com.rd.define.ERankType;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.game.event.IEventListener;
import com.rd.game.event.type.GameEnterEvent;
import com.rd.game.event.type.GameGangCreateEvent;
import com.rd.game.event.type.GamePayEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BroadcastService {
    public static void handleEnterGame(GameEvent event) {
        GameEnterEvent enterEvent = (GameEnterEvent) event;
        GameRole enterRole = enterEvent.getGameRole();
        Map<ERankType, Integer> ranks = GameRankManager.getInstance().getRanks(enterRole.getPlayerId());
        if (ranks != null) {
            for (Map.Entry<ERankType, Integer> rank : ranks.entrySet()) {
                if (rank.getValue() == 1) {
                    // 排行榜第一登录广播
                    ChatService.broadcastPlayerMsg(enterRole.getPlayer(), EBroadcast.EnterGame, rank.getKey().getName());
                }
            }
        }
    }

    public static void handlePay(GameEvent event) {
        GamePayEvent payEvent = (GamePayEvent) event;
        GameRole payRole = payEvent.getGameRole();
        if (payEvent.isFirst()) {
            // 首充广播
            ChatService.broadcastPlayerMsg(payRole.getPlayer(), EBroadcast.FirstPay);
        }
    }

    public static void handleCreateGang(GameEvent event) {
        GameGangCreateEvent gangCreateEvent = (GameGangCreateEvent) event;
        GameRole gangOwner = gangCreateEvent.getGameRole();
        // 创建帮派广播
        ChatService.broadcastPlayerMsg(gangOwner.getPlayer(), EBroadcast.GangCreate, gangOwner.getPlayer().getGangName());
    }

    public static final Map<EGameEventType, Consumer<? extends GameEvent>> handlers = new HashMap<>();

    static {
        handlers.put(EGameEventType.ENTER_GAME, BroadcastService::handleEnterGame);
        handlers.put(EGameEventType.PAY, BroadcastService::handlePay);
        handlers.put(EGameEventType.GANG_CREATE, BroadcastService::handleCreateGang);
    }

    public static final BroadcastListener listener = new BroadcastListener();

    private static class BroadcastListener implements IEventListener {
        @Override
        public void handleEvent(GameEvent event) {
            Consumer consumer = handlers.get(event.getType());
            if (consumer == null) {
                return;
            }
            consumer.accept(event);
        }
    }


}
