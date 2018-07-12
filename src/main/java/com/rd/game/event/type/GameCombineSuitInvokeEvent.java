package com.rd.game.event.type;

import com.rd.dao.EPlayerSaveType;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.provider.IGameRoleEventProvider;

import java.util.EnumSet;

/**
 * 激活合击符文套装
 */
public class GameCombineSuitInvokeEvent extends GameRoleEvent {
    public static final IGameRoleEventProvider<GameCombineSuitInvokeEvent> provider = new IGameRoleEventProvider<GameCombineSuitInvokeEvent>() {
        @Override
        public GameCombineSuitInvokeEvent create(GameRole role, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
            return new GameCombineSuitInvokeEvent(role, type, data, enumSet);
        }

        @Override
        public GameCombineSuitInvokeEvent simulate(EGameEventType type, GameRole gameRole) {
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
    public GameCombineSuitInvokeEvent(GameRole gameRole, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
        super(gameRole, type, data, enumSet);
    }


    @Override
    public int getTotalData() {
        throw new UnsupportedOperationException("GameCombineSuitInvokeEvent.getTotalData() is unsupported.");
    }
}
