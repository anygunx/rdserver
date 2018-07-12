package com.rd.bean.mission.handler;

import com.rd.bean.mission.RoleChainMission;
import com.rd.bean.player.Player;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.MissionDefine;
import com.rd.game.GameRole;
import com.rd.game.event.EMissionUpdateType;
import com.rd.game.event.GameEvent;
import com.rd.model.MissionModel;
import com.rd.model.data.mission.MissionModelData;

import java.util.Map;

/**
 * Created by XingYun on 2017/12/6.
 */
public class AchievementMissionHandler implements IMissionHandler {
    private static final AchievementMissionHandler instance = new AchievementMissionHandler();

    private AchievementMissionHandler() {
    }

    public static final AchievementMissionHandler getInstance() {
        return instance;
    }

    @Override
    public void handleEvent(GameRole gameRole, GameEvent event) {
        // TODO FSM
        Player player = gameRole.getPlayer();
        final Map<Short, EMissionUpdateType> eventMissions = event.getType().getAchievementMissions();
        Map<Short, RoleChainMission> currentMission = player.getAchievementMission();

        Map<Short, RoleChainMission> updates =
                gameRole.getMissionManager().getUpdateMissions(event, currentMission, eventMissions);


        for (RoleChainMission mission : updates.values()) {
            updateState(mission);
            player.updateAchievemntMission(mission.getId(), mission);
            gameRole.putMessageQueue(gameRole.getMissionManager().getAchievementMissionMessage(mission.getId()));
            event.addPlayerSaveType(EPlayerSaveType.ACHIEVEMENT_MISSION);
        }
    }


    private boolean updateState(RoleChainMission mission) {
        if (mission.isCompleted()) {
            return false;
        }
        if (mission.isReceived()) {
            return false;
        }
        MissionModelData resData = MissionModel.getAchievementMission(mission.getId());
        if (resData == null) {
            return false;
        }
        if (mission.getC() >= resData.getParam()) {
            mission.setS(MissionDefine.MISSION_STATE_COMPLETED);
        }
        return true;
    }
}
