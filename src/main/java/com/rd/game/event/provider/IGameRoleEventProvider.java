package com.rd.game.event.provider;

import com.rd.dao.EPlayerSaveType;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;

import java.util.EnumSet;

/**
 * 事件生成器标识
 * Created by XingYun on 2017/11/8.
 */
public interface IGameRoleEventProvider<T extends GameEvent> {
    /**
     * 生成事件
     *
     * @param role
     * @param type
     * @param data
     * @param enumSet FIXME 不可靠的依赖enumSet
     *                bug: null即可能丢失数据
     * @return
     */
    T create(GameRole role, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet);

    /**
     * 模拟事件
     * 用于兼容任务配置的修改
     **/
    T simulate(EGameEventType type, GameRole gameRole);
}
