package com.rd.bean.mission.handler;

import com.rd.bean.mission.RoleChainMission;
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
public class ChainMissionHandler implements IMissionHandler {
    private static final ChainMissionHandler instance = new ChainMissionHandler();

    private ChainMissionHandler() {
    }

    public static final ChainMissionHandler getInstance() {
        return instance;
    }

    @Override
    public void handleEvent(GameRole gameRole, GameEvent event) {
        final Map<Short, EMissionUpdateType> eventMissions = event.getType().getChainMissions();
        RoleChainMission currentMission = gameRole.getPlayer().getChainMission();

        boolean update = false;
        EMissionUpdateType updateType = eventMissions.get(currentMission.getId());
        if (updateType == null) {
            return;
        }
        if (MissionDefine.MISSION_STATE_UNCOMPLETED != currentMission.getS()) {
            return;
        }

        switch (updateType) {
            case INCREMENT:
                currentMission.setC((short) (currentMission.getC() + event.getData()));
                update = true;
                break;
            case BIGGER:
                if (event.getData() > currentMission.getC()) {
                    currentMission.setC((short) event.getData());
                    update = true;
                }
                break;
            default:
                throw new IllegalArgumentException("MissionManager.handleChainEvent() failed. Unexpected updateType=" + updateType);
        }
        if (update) {
            updateChainMissionState(currentMission);
            gameRole.putMessageQueue(gameRole.getMissionManager().getChainMessage());
            event.addPlayerSaveType(EPlayerSaveType.CHAINMISSION);
        }
    }

    private boolean updateChainMissionState(RoleChainMission mission) {
        if (mission.isCompleted()) {
            return false;
        }
        if (mission.isReceived()) {
            return false;
        }
        MissionModelData resData = MissionModel.getMissionChainData(mission.getId());
        if (resData == null) {
            return false;
        }
        if (mission.getC() >= resData.getParam()) {
            mission.setS(MissionDefine.MISSION_STATE_COMPLETED);
        }
        return true;

    }

}
