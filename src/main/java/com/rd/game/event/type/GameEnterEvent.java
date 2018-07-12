package com.rd.game.event.type;

import com.rd.dao.EPlayerSaveType;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.provider.IGameRoleEventProvider;

import java.util.EnumSet;

/**
 * 进入游戏事件
 */
public class GameEnterEvent extends GameRoleEvent {
    public static final IGameRoleEventProvider<GameEnterEvent> provider = new IGameRoleEventProvider<GameEnterEvent>() {
        @Override
        public GameEnterEvent create(GameRole role, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
            return new GameEnterEvent(role, type, data, enumSet);
        }

        @Override
        public GameEnterEvent simulate(EGameEventType type, GameRole gameRole) {
            return null;
        }
    };

    /**
     * 游戏事件
     *
     * @param gameRole
     * @param type     事件类型
     * @param data     事件数据
     * @param enumSet  事件发起者存储的数据(发起者不存储，填null)
     */
    public GameEnterEvent(GameRole gameRole, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
        super(gameRole, type, data, enumSet);
    }


    @Override
    public int getTotalData() {
        throw new UnsupportedOperationException("GameEnterEvent.getTotalData() is unsupported.");
    }
}
