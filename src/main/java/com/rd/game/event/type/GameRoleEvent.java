package com.rd.game.event.type;

import com.rd.dao.EPlayerSaveType;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;

import java.util.EnumSet;

/**
 * 玩家事件
 * 为了不修改GameEvent基类 11.08添加
 * Created by XingYun on 2017/11/8.
 */
public abstract class GameRoleEvent extends GameEvent {
    protected GameRole gameRole;

    /**
     * 游戏事件
     *
     * @param gameRole
     * @param type     事件类型
     * @param data     事件数据
     * @param enumSet  事件发起者存储的数据(发起者不存储，填null)
     */
    public GameRoleEvent(GameRole gameRole, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
        super(type, data, enumSet);
        this.gameRole = gameRole;
    }

    @Override
    public abstract int getTotalData();

    public GameRole getGameRole() {
        return gameRole;
    }
}
