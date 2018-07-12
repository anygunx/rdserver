package com.rd.activity.event;

import com.rd.game.GameRole;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

public class DoubleEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(DoubleEvent.class);

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        logger.info("双倍活动结束！");
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
