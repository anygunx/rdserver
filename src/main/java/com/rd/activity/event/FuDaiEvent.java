package com.rd.activity.event;

import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.net.message.Message;

public class FuDaiEvent implements IActivityEvent {

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        new ActivityDao().cleaFuDai();
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {

    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
