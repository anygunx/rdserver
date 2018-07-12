package com.rd.bean.mission.handler;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.game.event.GameEvent;
import com.rd.model.DragonBallModel;
import com.rd.model.MissionModel;
import com.rd.model.data.mission.MissionDailyModelData;

import java.util.EnumSet;

/**
 * Created by XingYun on 2017/12/6.
 */
public class DragonBallMissionHandler implements IMissionHandler {
    private static final DragonBallMissionHandler instance = new DragonBallMissionHandler();

    private DragonBallMissionHandler() {
    }

    public static final DragonBallMissionHandler getInstance() {
        return instance;
    }

    @Override
    public void handleEvent(GameRole gameRole, GameEvent event) {
        final short missionId = event.getType().getDragonballMissionId();
        if (missionId == 0) {
            return;
        }
        Player player = gameRole.getPlayer();
        MissionDailyModelData data = MissionModel.getDragonballMission(missionId);
        short process = player.getDayData().getDragonballProcess(data.getId());
        if (process < data.getCount()) {
            process += 1;
            player.getDayData().setDragonballProcess(data.getId(), process);
            int addition = (int) (DragonBallModel.MOTH_CARD_ADDITION * data.getScore());
            int finalValue = data.getScore();
            if (gameRole.getActivityManager().isMonthlyCard()) {
                finalValue += addition;
            } else {
                player.getDragonBall().addMothCardAddition(addition);
            }
            DropData reward = new DropData(EGoodsType.DRAGONBALL_PIECE, 0, finalValue);
            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
            gameRole.getPackManager().addGoods(reward, EGoodsChangeType.DRAGONBALL_BOX_ADD, enumSet);

            gameRole.putMessageQueue(gameRole.getMissionManager().getDragonBallMissionMessage(data.getId()));
            event.addPlayerSaveType(enumSet);
            event.addPlayerSaveType(EPlayerSaveType.DAYDATA);
        }
    }
}
