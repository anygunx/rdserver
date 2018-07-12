package com.rd.game.event.type;

import com.rd.dao.EPlayerSaveType;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.provider.IGameRoleEventProvider;

import java.util.EnumSet;

/**
 * 经脉升级事件
 * Created by XingYun on 2017/11/8.
 */
public class GameMerdianUpEvent extends GameRoleEvent {
    public static final IGameRoleEventProvider<GameMerdianUpEvent> provider = new IGameRoleEventProvider<GameMerdianUpEvent>() {
        @Override
        public GameMerdianUpEvent create(GameRole role, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
            return new GameMerdianUpEvent(role, type, data, enumSet);
        }

        @Override
        public GameMerdianUpEvent simulate(EGameEventType type, GameRole gameRole) {
            int meridianLevel = 0;
//            for(Character character:gameRole.getPlayer().getCharacterList()){
//                if(character.getMeridian()>meridianLevel){
//                    meridianLevel=character.getMeridian();
//                }
//            }
            if (meridianLevel > 0) {
                return EGameEventType.MERIDIAN_UP.create(gameRole, meridianLevel, null);
            }
            return null;
        }
    };

    /**
     * 游戏事件
     *
     * @param role
     * @param type
     * @param data    事件数据
     * @param enumSet 事件发起者存储的数据(发起者不存储，填null)
     */
    private GameMerdianUpEvent(GameRole role, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
        super(role, type, data, enumSet);
    }

    /**
     * 需要单独实现以支持此方法
     *
     * @return
     */
    @Override
    public int getTotalData() {
        int total = 0;
//        for (Character character : gameRole.getPlayer().getCharacterList()) {
//            total += character.getMeridian();
//        }
        return total;
    }
}