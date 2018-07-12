package com.rd.activity.event;

import com.rd.game.GameRole;
import com.rd.net.message.Message;

/**
 * 春节集字
 * Created by XingYun on 2017/1/18.
 */
public class SpringWordCollectionEvent implements IActivityEvent {
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
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }
}
