package com.rd.bean.mission.handler;

import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.model.GangModel;
import com.rd.model.data.GangMissionData;

/**
 * Created by XingYun on 2017/12/6.
 */
public class GangMissionHandler implements IMissionHandler {
    private static final GangMissionHandler instance = new GangMissionHandler();

    private GangMissionHandler() {
    }

    public static final GangMissionHandler getInstance() {
        return instance;
    }

    @Override
    public void handleEvent(GameRole gameRole, GameEvent event) {
        EGameEventType eventType = event.getType();
        final byte missionId = eventType.getGangMissionId();
        if (missionId == 0) {
            return;
        }

        GangMissionData gangMissionData = GangModel.getGangMissionData(missionId);
        if (gangMissionData != null) {
            gameRole.getGangManager().updateMission(gangMissionData, event);
        }
    }
}
