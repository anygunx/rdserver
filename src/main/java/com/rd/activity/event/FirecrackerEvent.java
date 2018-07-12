package com.rd.activity.event;

import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

public class FirecrackerEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(FirecrackerEvent.class);

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        logger.info("幸运鞭炮活动结束！");
        ActivityDao dao = new ActivityDao();
        dao.updateActivityFirecracker();
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
