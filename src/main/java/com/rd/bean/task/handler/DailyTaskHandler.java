package com.rd.bean.task.handler;

import com.rd.bean.player.Player;
import com.rd.dao.EPlayerSaveType;
import com.rd.game.GameRole;
import com.rd.game.event.GameEvent;
import com.rd.model.NTaskModel;
import com.rd.model.data.task.NTaskLiLianData;

public class DailyTaskHandler implements ITaskHandler {
    private static final DailyTaskHandler instance = new DailyTaskHandler();

    private DailyTaskHandler() {
    }

    public static final DailyTaskHandler getInstance() {
        return instance;
    }

    @Override
    public void handleEvent(GameRole gameRole, GameEvent event) {
        final short missionId = event.getType().getDailyMissionId();
        if (missionId == 0) {
            return;
        }
        Player player = gameRole.getPlayer();
        NTaskLiLianData data = NTaskModel.getTaskLiLianDailyData(missionId);
        if (player.getDailyProgress()[data.getId()] < data.getTarget()) {
            short currCount = (short) (player.getDailyProgress()[data.getId()] + 1);
            player.getDailyProgress()[data.getId()] = currCount;
            //EnumSet<EPlayerSaveType> enumSet=EnumSet.noneOf(EPlayerSaveType.class);
            //  event.addPlayerSaveType(EPlayerSaveType.EXP);
            // gameRole.addExp(data.getLilianExp(),enumSet);
            //gameRole.getNTaskManager().addLiLianExp(data.getLilianExp());
            player.addLiLianExp(data.getLilianExp());
            //gameRole.putMessageQueue(gameRole.getMissionManager().getDailyUpdateMessage(data.getId()));
            event.addPlayerSaveType(EPlayerSaveType.DAILYMISSION);
            event.addPlayerSaveType(EPlayerSaveType.LILIAN_EXP);

        }
    }
}