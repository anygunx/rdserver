package com.rd.game.event.type;

import com.rd.dao.EPlayerSaveType;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.provider.IGameRoleEventProvider;

import java.util.EnumSet;

public class GamePayEvent extends GameRoleEvent {
    /**
     * 首冲标志
     */
    private boolean isFirst;

    public static final IGameRoleEventProvider<GamePayEvent> provider = new IGameRoleEventProvider<GamePayEvent>() {
        @Override
        public GamePayEvent create(GameRole role, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
            return new GamePayEvent(role, type, data, enumSet);
        }

        @Override
        public GamePayEvent simulate(EGameEventType type, GameRole gameRole) {
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
    public GamePayEvent(GameRole gameRole, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
        super(gameRole, type, data, enumSet);
    }


    @Override
    public int getTotalData() {
        throw new UnsupportedOperationException("GamePayEvent.getTotalData() is unsupported.");
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

}
