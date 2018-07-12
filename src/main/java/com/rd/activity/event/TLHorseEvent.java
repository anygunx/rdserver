package com.rd.activity.event;

import com.rd.game.GameRole;
import com.rd.net.message.Message;

import java.util.List;

/**
 * 限时坐骑
 * Created by XingYun on 2017/1/19.
 */
public class TLHorseEvent implements IActivityEvent {
    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        return false;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        List<Byte> horseList = role.getActivityManager().getActivityData().getTLHorseList();
        msg.setByte(horseList.size());
        for (Byte id : horseList) {
            msg.setByte(id);
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }
}
