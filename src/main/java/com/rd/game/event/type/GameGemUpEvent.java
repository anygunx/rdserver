package com.rd.game.event.type;

import com.rd.dao.EPlayerSaveType;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.provider.IGameRoleEventProvider;

import java.util.EnumSet;

/**
 * 宝石升级
 * Created by XingYun on 2017/11/8.
 */
public class GameGemUpEvent extends GameRoleEvent {
    public static final IGameRoleEventProvider<GameGemUpEvent> provider = new IGameRoleEventProvider<GameGemUpEvent>() {
        @Override
        public GameGemUpEvent create(GameRole role, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
            return new GameGemUpEvent(role, type, data, enumSet);
        }

        @Override
        public GameGemUpEvent simulate(EGameEventType type, GameRole gameRole) {
            int gemLevel = 0;
//            for(Character character:gameRole.getPlayer().getCharacterList()){
//                for(EquipSlot slot:character.getEquipSlotList()){
//                    if(slot.getJ()>gemLevel){
//                        gemLevel=slot.getJ();
//                    }
//                }
//            }
            if (gemLevel > 0) {
                return this.create(gameRole, type, gemLevel, null);
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
    private GameGemUpEvent(GameRole role, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
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
//            for (EquipSlot slot: character.getEquipSlotList()) {
//                total += slot.getJ();
//            }
//        }
        return total;
    }
}
