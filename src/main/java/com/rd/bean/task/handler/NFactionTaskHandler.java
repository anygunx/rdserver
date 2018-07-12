package com.rd.bean.task.handler;

import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.model.NFactionModel;
import com.rd.model.data.faction.NFactionTaskData;

/**
 * Created by XingYun on 2017/12/6.
 */
public class NFactionTaskHandler implements ITaskHandler {
    private static final NFactionTaskHandler instance = new NFactionTaskHandler();

    private NFactionTaskHandler() {
    }

    public static final NFactionTaskHandler getInstance() {
        return instance;
    }

    @Override
    public void handleEvent(GameRole gameRole, GameEvent event) {
        EGameEventType eventType = event.getType();
        final byte missionId = eventType.getGangMissionId();
        if (missionId == 0) {
            return;
        }

        NFactionTaskData gangMissionData = NFactionModel.getNFactionTaskMap(missionId);
        if (gangMissionData != null) {
            gameRole.getNFactionManager().updateTask(gangMissionData, event);
        }
    }
}
