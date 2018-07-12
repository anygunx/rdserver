package com.rd.bean.mission.handler;

import com.rd.bean.player.Player;
import com.rd.dao.EPlayerSaveType;
import com.rd.game.GameRole;
import com.rd.game.event.GameEvent;
import com.rd.model.MissionModel;
import com.rd.model.data.mission.MissionDailyModelData;

/**
 * Created by XingYun on 2017/12/6.
 */
public class DailyMissionHandler implements IMissionHandler {
    private static final DailyMissionHandler instance = new DailyMissionHandler();

    private DailyMissionHandler() {
    }

    public static final DailyMissionHandler getInstance() {
        return instance;
    }

    @Override
    public void handleEvent(GameRole gameRole, GameEvent event) {
        final short missionId = event.getType().getDailyMissionId();
        if (missionId == 0) {
            return;
        }
        Player player = gameRole.getPlayer();
        MissionDailyModelData data = MissionModel.getMissionDailyData(missionId);
        if (player.getDailyProgress()[data.getId()] < data.getCount()) {
            player.getDailyProgress()[data.getId()] += 1;

            gameRole.putMessageQueue(gameRole.getMissionManager().getDailyUpdateMessage(data.getId()));
            event.addPlayerSaveType(EPlayerSaveType.DAILYMISSION);
        }
    }
}
