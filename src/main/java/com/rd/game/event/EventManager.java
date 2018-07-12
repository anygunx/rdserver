package com.rd.game.event;

import com.rd.game.BroadcastService;
import com.rd.game.GameRole;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家事件管理
 * 目前只用于管理自己的事件
 * Created by XingYun on 2016/5/24.
 */
public class EventManager {
    private static final Logger logger = Logger.getLogger(EventManager.class);
    private final GameRole gameRole;
    private List<IEventListener> listenerList = new ArrayList<>();

    public EventManager(GameRole gameRole) {
        this.gameRole = gameRole;
        init();
    }

    private void init() {
        registerListener(gameRole);

        registerListener(gameRole.getSkillManager());
        registerListener(gameRole.getNTaskManager());
        registerListener(gameRole.getNFactionManager());
        //registerListener(gameRole.getMissionManager());
        //registerListener(gameRole.getGangManager());
        //registerListener(gameRole.getAuctionManager());
        registerListener(BroadcastService.listener);
    }

    public void registerListener(IEventListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void unregisterListener(IEventListener listener) {
        listenerList.remove(listener);
    }

    public void notifyEvent(GameEvent event) {
        if (event == null) {
            logger.error("EventManager.notifyEvent() failed. Unexpected event=null.");
            return;
        }
        for (IEventListener listener : listenerList) {
            listener.handleEvent(event);
        }
    }
}
