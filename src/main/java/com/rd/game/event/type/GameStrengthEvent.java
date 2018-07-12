package com.rd.game.event.type;

import com.rd.dao.EPlayerSaveType;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.provider.IGameRoleEventProvider;

import java.util.EnumSet;

/**
 * 强化事件
 * Created by XingYun on 2017/11/8.
 */
public class GameStrengthEvent extends GameRoleEvent {
    public static final IGameRoleEventProvider<GameStrengthEvent> provider = new IGameRoleEventProvider<GameStrengthEvent>() {
        @Override
        public GameStrengthEvent create(GameRole role, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
            return new GameStrengthEvent(role, type, data, enumSet);
        }

        @Override
        public GameStrengthEvent simulate(EGameEventType type, GameRole gameRole) {
            int str = 0;
//            for(Character character:gameRole.getPlayer().getCharacterList()){
//                for(EquipSlot slot:character.getEquipSlotList()){
//                    if(slot.getStr()>str){
//                        str=slot.getStr();
//                    }
//                }
//            }
            if (str > 0) {
                return this.create(gameRole, type, str, null);
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
    private GameStrengthEvent(GameRole role, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
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
//        for (Character character: gameRole.getPlayer().getCharacterList()){
//            for (EquipSlot slot: character.getEquipSlotList()) {
//                total += slot.getStr();
//            }
//        }
        return total;
    }


}
