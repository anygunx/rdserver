package com.rd.activity.event;

import com.rd.game.GameRole;
import com.rd.net.message.Message;

import java.util.Map;
import java.util.Map.Entry;

public class SevenDayEvent implements IActivityEvent {

    @Override
    public boolean onStart() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onEnd() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        String[] record;

        Map<String, Byte> data = role.getActivityManager().getActivityData().getSevenDay();
        msg.setByte(data.size());
        for (Entry<String, Byte> entry : data.entrySet()) {
            record = entry.getKey().split("_");
            msg.setByte(Byte.parseByte(record[0]));
            msg.setByte(Byte.parseByte(record[1]));
            msg.setByte(entry.getValue());
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        // TODO Auto-generated method stub
        return true;
    }

}
