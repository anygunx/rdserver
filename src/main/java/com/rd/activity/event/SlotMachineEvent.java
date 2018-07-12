package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.data.SlotMachineData;
import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;

import java.util.Map;

public class SlotMachineEvent implements IActivityEvent {

    @Override
    public boolean onStart() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onEnd() {
        //new ActivityDao().clearSlotMachine();
        return false;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        msg.setByte(activityData.getSlotMachine());
    }

    @Override
    public boolean isOpen(GameRole role) {
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        Map<String, SlotMachineData> model = ActivityService.getRoundData(EActivityType.SLOT_MACHINE, 0);

        if (activityData.getSlotMachine() >= model.size())
            return false;
        return true;
    }

}
