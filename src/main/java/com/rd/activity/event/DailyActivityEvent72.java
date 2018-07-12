package com.rd.activity.event;

import com.rd.game.GameRole;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

public class DailyActivityEvent72 implements IActivityEvent {

    private static Logger logger = Logger.getLogger(DailyActivityEvent72.class);

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        logger.info("日常(指引)72活动结束！");
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
