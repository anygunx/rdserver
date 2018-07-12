package com.rd.bean.mission.handler;

import com.rd.bean.mission.CardMission;
import com.rd.bean.mission.CardMissionData;
import com.rd.bean.mission.RoleChainMission;
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

public class CardMissionHandler implements IMissionHandler {
    private static final CardMissionHandler instance = new CardMissionHandler();

    private CardMissionHandler() {
    }

    public static final CardMissionHandler getInstance() {
        return instance;
    }

    @Override
    public void handleEvent(GameRole gameRole, GameEvent event) {
        gameRole.getPlayer().getCardMission().updateMission();
        CardMission cardMission = gameRole.getPlayer().getCardMission();

        final Map<Short, EMissionUpdateType> eventMissions = event.getType().getCardMissions();

        for (CardMissionData missionData : cardMission.getMissions()) {
            for (RoleChainMission mission : missionData.getMissions()) {
                if (mission.getS() == MissionDefine.MISSION_STATE_RECEIVED || mission.getS() == MissionDefine.MISSION_STATE_COMPLETED) {
                    continue;
                }
                boolean update = false;
                EMissionUpdateType updateType = eventMissions.get(mission.getId());
                if (updateType == null) {
                    continue;
                }
                switch (updateType) {
                    case INCREMENT:
                        int c = mission.getC() + event.getData();
                        if (c > Short.MAX_VALUE) {
                            c = Short.MAX_VALUE;
                        }
                        mission.setC((short) c);
                        update = true;
                        break;
                    case BIGGER:
                        if (event.getData() > mission.getC()) {
                            mission.setC((short) event.getData());
                            update = true;
                        }
                        break;
                    case ONECE:
                        mission.setC((short) (mission.getC() + 1));
                        update = true;
                        break;
                    default:
                        throw new IllegalArgumentException("CardMissionHandler() failed. Unexpected updateType=" + updateType);
                }
                if (update) {
                    boolean stateChange = updateCardMissionState(mission);
                    if (stateChange) {
                        boolean isComple = true;
                        for (RoleChainMission checkMission : missionData.getMissions()) {
                            if (checkMission.getS() != MissionDefine.MISSION_STATE_COMPLETED) {
                                isComple = false;
                            }
                        }
                        if (isComple) {
                            missionData.setState(MissionDefine.MISSION_STATE_COMPLETED);
                        }
                    }
                    Message message = new Message(MessageCommand.CARD_MISSION_UPDATE_MESSAGE);
                    message.setByte(missionData.getId());
                    message.setByte(missionData.getState());
                    message.setByte(mission.getId());
                    message.setShort(mission.getC());
                    gameRole.putMessageQueue(message);
                    event.addPlayerSaveType(EPlayerSaveType.CARD_MISSION);
                }
            }
        }
    }

    private boolean updateCardMissionState(RoleChainMission mission) {
        if (mission.isCompleted()) {
            return false;
        }
        if (mission.isReceived()) {
            return false;
        }
        MissionModelData resData = MissionModel.getCardMission(mission.getId());
        if (resData == null) {
            return false;
        }
        if (mission.getC() >= resData.getParam()) {
            mission.setS(MissionDefine.MISSION_STATE_COMPLETED);
        }
        return true;
    }
}