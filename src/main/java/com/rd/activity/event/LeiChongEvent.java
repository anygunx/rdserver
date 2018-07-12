package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.data.LeiChongLogicData;
import com.rd.game.GameRole;
import com.rd.net.message.Message;

import java.util.Map;
import java.util.Set;

public class LeiChongEvent implements IActivityEvent {

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        int keepDay = role.getPayManager().getPayDays();
        Set<Byte> data = role.getActivityManager().getActivityData().getPayCountData();
        msg.setByte(keepDay);
        msg.setByte(data.size());
        for (Byte day : data) {
            msg.setByte(day);
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        Map<String, LeiChongLogicData> logics = ActivityService.getRoundData(EActivityType.LEICHONG, 0);
        if (logics == null)
            return false;

        for (String day : logics.keySet()) {
            if (!role.getActivityManager().getActivityData().getPayCountData().contains(Byte.valueOf(day))) {
                return true;
            }
        }
        return false;
    }

}
