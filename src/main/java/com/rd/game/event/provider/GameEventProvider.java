package com.rd.game.event.provider;

import com.rd.dao.EPlayerSaveType;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;

import java.util.EnumSet;

/**
 * Created by XingYun on 2017/11/8.
 */
public class GameEventProvider implements IGameRoleEventProvider<GameEvent> {
    @Override
    public GameEvent create(GameRole role, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
        return new GameEvent(type, data, enumSet);
    }

    @Override
    public GameEvent simulate(EGameEventType type, GameRole gameRole) {
        return null;
    }
}
