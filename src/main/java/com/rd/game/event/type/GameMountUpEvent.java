package com.rd.game.event.type;

import com.rd.dao.EPlayerSaveType;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.provider.IGameRoleEventProvider;

import java.util.EnumSet;

/**
 * 坐骑/羽翼升阶事件
 * Created by XingYun on 2017/11/8.
 */
public class GameMountUpEvent extends GameRoleEvent {
    public static final IGameRoleEventProvider<GameMountUpEvent> provider = new IGameRoleEventProvider<GameMountUpEvent>() {
        @Override
        public GameMountUpEvent create(GameRole role, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
            return new GameMountUpEvent(role, type, data, enumSet);
        }

        @Override
        public GameMountUpEvent simulate(EGameEventType type, GameRole gameRole) {
            int lv = 0;
//            for(Character character:gameRole.getPlayer().getCharacterList()){
//                if(character.getMountStage()>lv){
//                    lv=character.getMountStage();
//                }
//            }
//            if(lv>0){
//                return this.create(gameRole, type, lv,null);
//            }
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
    private GameMountUpEvent(GameRole role, EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
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
//            total += character.getMountStage();
//        }
        return total;
    }
}