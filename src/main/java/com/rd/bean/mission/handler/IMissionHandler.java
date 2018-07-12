package com.rd.bean.mission.handler;

import com.rd.game.GameRole;
import com.rd.game.event.GameEvent;

/**
 * 任务处理器
 * Created by XingYun on 2017/12/6.
 */
public interface IMissionHandler {
    void handleEvent(GameRole gameRole, GameEvent event);
}
