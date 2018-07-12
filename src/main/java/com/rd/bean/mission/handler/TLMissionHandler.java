package com.rd.bean.mission.handler;

import com.rd.bean.mission.RoleChainMission;
import com.rd.bean.mission.TLMissionData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.MissionDefine;
import com.rd.game.GameRole;
import com.rd.game.event.EMissionUpdateType;
import com.rd.game.event.GameEvent;
import com.rd.model.MissionModel;
import com.rd.model.data.mission.MissionModelData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;

import java.util.Map;

/**
 * Created by XingYun on 2017/12/6.
 */
public class TLMissionHandler implements IMissionHandler {
    private static final TLMissionHandler instance = new TLMissionHandler();

    private TLMissionHandler() {
    }

    public static final TLMissionHandler getInstance() {
        return instance;
    }

    @Override
    public void handleEvent(GameRole gameRole, GameEvent event) {
        gameRole.getMissionManager().updateTLMission();
        TLMissionData missionData = gameRole.getPlayer().getTLMissionData();
        if (missionData.isDead()) {
            return;
        }
        final Map<Short, EMissionUpdateType> eventMissions = event.getType().getTLMissions();

        Map<Short, RoleChainMission> updates =
                gameRole.getMissionManager().getUpdateMissions(event, missionData.getMissions(), eventMissions);

        if (updates.isEmpty()) {
            return;
        }
        for (RoleChainMission mission : updates.values()) {
            byte s = mission.getS();
            updateState(mission);
            missionData.updateMission(mission.getId(), mission);

            if (s != mission.getS() && mission.getS() == MissionDefine.MISSION_STATE_COMPLETED) {
                Message message = new Message(MessageCommand.TLMISSION_COMPLETE_MESSAGE);
                message.setShort(mission.getId());
                gameRole.putMessageQueue(message);
            }
            event.addPlayerSaveType(EPlayerSaveType.TLMISSION);
        }
    }


    private boolean updateState(RoleChainMission mission) {
        if (mission.isCompleted()) {
            return false;
        }
        if (mission.isReceived()) {
            return false;
        }
        MissionModelData resData = MissionModel.getTLMission(mission.getId());
        if (resData == null) {
            return false;
        }
        if (mission.getC() >= resData.getParam()) {
            mission.setC(resData.getParam());
            mission.setS(MissionDefine.MISSION_STATE_COMPLETED);
        }
        return true;
    }


}
