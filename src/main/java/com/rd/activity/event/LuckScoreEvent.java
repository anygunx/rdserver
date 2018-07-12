package com.rd.activity.event;

import com.rd.game.GameRole;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

public class LuckScoreEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(LuckScoreEvent.class);

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        logger.info("幸运鉴宝结束");
        return false;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        //TODO 活动轮训的数据结构
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
